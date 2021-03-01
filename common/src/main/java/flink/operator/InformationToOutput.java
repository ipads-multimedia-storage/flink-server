package flink.operator;

import flink.config.CONFIG;
import flink.types.Information;
import flink.types.Output;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

import java.util.HashMap;
import java.util.HashSet;

public class InformationToOutput implements FlatMapFunction<Information, Output> {
    static private final HashMap<Long, Information> starts = new HashMap<>();
    static private final HashSet<Long> _not_output_set = new HashSet<>();
    static private boolean _speed_set_flag = false;
    static private double speed;

    @Override
    public void flatMap(Information value, Collector<Output> out) throws Exception {
        Long objectID = value.getObjectID();
        if (!starts.containsKey(objectID)) {
            starts.put(objectID, value);
            _not_output_set.add(objectID);
        } else {
            Information start = starts.get(objectID);
            if (value.getEventTime() - objectID > CONFIG._output_interval
                    && _not_output_set.contains(value.getObjectID())){

                double current_speed = (value.getPosX() - start.getPosX()) /
                        (value.getEventTime() - start.getEventTime());

                if (!_speed_set_flag){
                    speed = current_speed;
                    _speed_set_flag = true;
                } else {
                    speed = .9 * speed + .1 * current_speed;
                }

                Output output = new Output();
                output.setSpeed(speed);
                output.setInfo(value);

                _not_output_set.remove(objectID);
                if (value.getSkippedFrames() >= CONFIG._maximum_allowed_skipped_frames - 1){
                    starts.remove(objectID);
                }

                out.collect(output);
            }
        }
    }
}
