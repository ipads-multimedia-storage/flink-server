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

import flink.operator.LocationAggregate;
import flink.operator.ObjectIdSelector;
import flink.sink.MessageSerialize;
import flink.source.OpenCVSocketSource;
import flink.operator.TransferImage;
import flink.types.Output;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

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
    static { System.load("C:\\Windows\\System32\\opencv.dll"); };

    public static void main(String[] args) throws Exception {
        // set up the streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // load data from camera
        DataStream<Tuple2<Long, byte[]>> source = env
                .addSource(new OpenCVSocketSource(8002));

        // show the raw image video by this
        // source.flatMap(new ShowImage());

        // process the data
        DataStream<Output> output = source
                .flatMap(new TransferImage())
                .keyBy(new ObjectIdSelector())
                .countWindow(5, 2)
                .aggregate(new LocationAggregate());

        // sink data to arm
        output.writeToSocket("192.168.11.138", 8003, new MessageSerialize());

        // execute program
        env.execute("Flink Streaming Java API Skeleton");
    }
}
