package flink.operator;

import flink.types.Accumulator;
import flink.types.Information;
import flink.types.Output;
import org.apache.flink.api.common.functions.AggregateFunction;


public class LocationAggregate implements AggregateFunction<Information, Accumulator, Output> {
    @Override
    // 在一次新的aggregate发起时，创建一个新的Accumulator，Accumulator是我们所说的中间状态数据，简称ACC
    // 这个函数一般在初始化时调用
    public Accumulator createAccumulator() {
        return new Accumulator();
    }

    @Override
    // 当一个新元素流入时，将新元素与状态数据ACC合并，返回状态数据ACC
    public Accumulator add(Information value, Accumulator accumulator) {
        if (accumulator.count == 0) {
            accumulator.start = value;
        } else {
            accumulator.end = value;
        }
        accumulator.count += 1;
        return accumulator;
    }

    @Override
    // 将中间数据转成结果数据
    public Output getResult(Accumulator accumulator) {
        Double speed = 0.0;
        if (accumulator.end != null) {
            speed = (accumulator.end.getPosY() - accumulator.start.getPosY()) / (accumulator.end.getEventTime() - accumulator.start.getEventTime());
        }
        Output output = new Output();
        output.setInfo(accumulator.end);
        output.setSpeed(speed);
        return output;
    }

    @Override
    // 将两个Object合并
    public Accumulator merge(Accumulator a, Accumulator b) {
        a.count += b.count;
        a.end = b.end;
        return a;
    }
}
