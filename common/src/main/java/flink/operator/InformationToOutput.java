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
    static private final HashMap<Long, Double> speed_list = new HashMap<>();
    static private boolean _speed_set_flag = false;
    static private double speed;

    @Override
    public void flatMap(Information value, Collector<Output> out) throws Exception {
        //当前的object id
        Long objectID = value.getObjectID();
        // 如果是第一个到达的，记录它的信息到starts集合
        if (!starts.containsKey(objectID)) {
            starts.put(objectID, value);
            _not_output_set.add(objectID); //标记他作为即将要输出的
        } else {
            // 获取起始的信息
            Information start = starts.get(objectID);

            // 若目前时延已经超过我们设置的阈值，并且是还没有输出的
            if (value.getEventTime() - start.getEventTime() > CONFIG._output_interval
                    && _not_output_set.contains(value.getObjectID())){
                // 首个物体因为没有速度信息所以需要设置一下
                if (!_speed_set_flag){
                    speed = (value.getPosX() - start.getPosX()) /
                            (value.getEventTime() - start.getEventTime());
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

            // 更新和记录物体的速度信息
            // 对前五个物体，当他们消失的时候更新速度
            if(speed_list.size() < 5) {
                if (value.getSkippedFrames() > 0
                        && !speed_list.containsKey(value.getObjectID())) {
                    double cur_speed = (value.getPosX() - start.getPosX()) /
                            (value.getEventTime() - start.getEventTime());
                    speed_list.put(value.getObjectID(), cur_speed);
                    if (speed_list.size() == 5) {
                        _speed_set_flag = true;
                    }

                    double tot_speed = 0;
                    for (double s : speed_list.values()) {
                        tot_speed += s;
                    }
                    speed = tot_speed / speed_list.size();
                }
            }
        }
    }
}
