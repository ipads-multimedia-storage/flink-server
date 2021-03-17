/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package flink;

import flink.operator.InformationToOutput;
import flink.sink.BandwidthSerialize;
import flink.sink.MessageSerialize;
import flink.sink.PravegaRouter;
import flink.sink.PravegaSerialize;
import flink.source.BandwidthReduce;
import flink.source.OpenCVSocketSource;
import flink.operator.TransferImage;
import flink.source.SourceData;
import flink.types.Information;
import flink.source.TimeAssigner;
import flink.types.Output;
import flink.utils.PravegaUtils;
import io.pravega.client.stream.Stream;
import io.pravega.connectors.flink.FlinkPravegaWriter;
import io.pravega.connectors.flink.PravegaConfig;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

import java.net.URI;
import java.time.Duration;

/**
 * Skeleton for a Flink Streaming Job.
 *
 * <p>For a tutorial how to write a Flink streaming application, check the
 * tutorials and examples on the <a href="http://flink.apache.org/docs/stable/">Flink Website</a>.
 *
 * <p>To package your application into a JAR file for execution, run
 * 'mvn clean package' on the command line.
 *
 * <p>If you change the name of the main class (with the public static void main(String[] args))
 * method, change the respective entry in the POM.xml file (simply search for 'mainClass').
 */
public class StreamingJob {
    // load opencv dll file
    static { System.loadLibrary("libopencv_java4"); }

    public static void main(String[] args) throws Exception {
        // build parameters
        ParameterTool paramsArg = ParameterTool.fromArgs(args);
        ParameterTool paramsFile = ParameterTool.fromPropertiesFile(StreamingJob.class.getClassLoader().getResourceAsStream("config.properties"));
        ParameterTool params = paramsFile.mergeWith(paramsArg);

        // set up the streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        // load data from camera
        DataStream<SourceData> source = env
                .addSource(new OpenCVSocketSource(params.getInt("socket.source.port")))
                .assignTimestampsAndWatermarks(WatermarkStrategy
                        .<SourceData>forBoundedOutOfOrderness(Duration.ofSeconds(3))
                        .withTimestampAssigner(new TimeAssigner()));

        // build Pravega writer
        if(params.getBoolean("enablePravega", false)) {
            PravegaConfig pravegaConfig = PravegaConfig
                    .fromParams(params)
                    .withControllerURI(URI.create(params.get("pravega.uri")))
                    .withDefaultScope(params.get("pravega.scope", "testScope"));
            Stream stream = PravegaUtils.createStream(pravegaConfig, params.get("pravega.stream", "testStream"));
            FlinkPravegaWriter<SourceData> pravegaWriter = FlinkPravegaWriter
                    .<SourceData>builder()
                    .forStream(stream)
                    .withPravegaConfig(pravegaConfig)
                    .withSerializationSchema(new PravegaSerialize())
                    .withEventRouter(new PravegaRouter())
                    .build();
            // write video data to Pravega
            // FlinkPravegaUtils.writeToPravegaInEventTimeOrder(source, pravegaWriter, 1);
            source.addSink(pravegaWriter);
        }

        // you can view the raw image video by this(for test only)
        // source.flatMap(new ShowImage());

        source.windowAll(TumblingProcessingTimeWindows.of(Time.seconds(10)))
                .reduce(new BandwidthReduce())
                .writeToSocket(params.get("socket.sink.hostname"), params.getInt("socket.bandwidth.port"), new BandwidthSerialize());

        // process the data
        DataStream<Output> output = source
                .flatMap(new TransferImage())
                .keyBy(Information::getObjectID)
                .flatMap(new InformationToOutput());

        // sink data to arm
        output.writeToSocket(params.get("socket.sink.hostname"), params.getInt("socket.sink.port"), new MessageSerialize());
        // sink to index
        if(params.getBoolean("enableIndex", false)) {
            output.addSink(JdbcSink.sink("INSERT INTO VIDEO_TEST (objectId, timestamp) values (?,?)",
                    (ps, t) -> {
                        ps.setLong(1, t.getInfo().getObjectID());
                        ps.setLong(2, t.getInfo().getEventTime());
                    },
                    new JdbcExecutionOptions.Builder().withBatchSize(1).build(),
                    new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                            .withUrl(params.get("jdbc.url"))
                            .withDriverName(params.get("jdbc.driver"))
                            .withUsername(params.get("jdbc.username"))
                            .withPassword(params.get("jdbc.password"))
                            .build()));
        }

        // execute program
        env.execute("Flink Streaming Java API Skeleton");
    }
}
