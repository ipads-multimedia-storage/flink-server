package flink.operator;

import flink.config.CONFIG;
import flink.types.Information;
import flink.types.Output;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

import java.util.HashSet;

public class InformationToOutput implements FlatMapFunction<Information, Output> {
    private Information start;
    private boolean startFlag = false;
//    private boolean outputFlag = false;
    static HashSet noticeObj = new HashSet<>();

    @Override
    public void flatMap(Information value, Collector<Output> out) throws Exception {
        if (!startFlag) {
            start = value;
            startFlag = true;
        } else {
            if (value.getEventTime() - start.getEventTime() > CONFIG._output_interval && !noticeObj.contains(value.getObjectID())){
                Output output = new Output();
                output.setSpeed((value.getPosY() - start.getPosY()) / (value.getEventTime() - start.getEventTime()));
                output.setInfo(value);

                noticeObj.add(value.getObjectID());

                out.collect(output);
            }
        }
    }
}
