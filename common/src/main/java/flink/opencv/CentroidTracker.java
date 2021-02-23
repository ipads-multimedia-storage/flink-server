package flink.opencv;

import flink.operator.TransferImage;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Comparator.comparingDouble;

public class CentroidTracker implements Serializable {
    Integer maxDisappeared;
    Integer threshold;
    ConcurrentHashMap<Long, RotatedRect> objects = new ConcurrentHashMap<>();
    ConcurrentHashMap<Long, Integer> disappeared = new ConcurrentHashMap<>();

    Logger LOG = LoggerFactory.getLogger("CentroidTracker");

    public CentroidTracker(Integer maxDisappeared, Integer threshold) {
        this.threshold = threshold;
        this.maxDisappeared = maxDisappeared;
    }

    public void register(Long objectID, RotatedRect centroid) {
        // 注册对象时，我们使用下一个可用的对象ID来存储质心
        LOG.info("register object {}", objectID.toString());
        objects.put(objectID, centroid);
        disappeared.put(objectID, 0);
    }

    public void deregister(Long objectID){
        // 要注销注册对象ID，我们从两个字典中都删除了该对象ID
        LOG.info("deregister object {}", objectID.toString());
        objects.remove(objectID);
        disappeared.remove(objectID);
    }

    public ConcurrentHashMap<Long, RotatedRect> update(List<RotatedRect> list, Long eventTime){
        // 检查输入边界框矩形的列表是否为空
        if (list.isEmpty()){
            // 遍历任何现有的跟踪对象并将其标记为消失
            for(Map.Entry<Long, Integer> entry: disappeared.entrySet()){
                Long objectID = entry.getKey();
                disappeared.replace(objectID, entry.getValue() + 1);
                // 如果达到给定对象被标记为丢失的最大连续帧数，请取消注册
                if (disappeared.get(objectID) > maxDisappeared){
                    deregister(objectID);
                }
            }
            // 由于没有质心或跟踪信息要更新，请尽早返回
            return objects;
        }

        // 如果我们当前未跟踪任何对象，请输入输入质心并注册每个质心
        if(objects.isEmpty()){
            for (RotatedRect rotatedRect: list)
                register(eventTime, rotatedRect);
        }
        // 否则，当前正在跟踪对象，因此我们需要尝试将输入质心与现有对象质心进行匹配
        else {
            // 抓取一组对象ID和相应的质心
            Set<Long> objectIDs = objects.keySet();
            Set<Integer> usedInputCtds = new HashSet<Integer>();

            // 更新object中的每一个
            for (Long ID: objectIDs){
                List<Double> distance = list.stream()
                        .map(rect -> distance(objects.get(ID).center, rect.center))
                        .collect(Collectors.toList());
                int minIndex = IntStream.range(0, distance.size())
                        .boxed()
                        .min(comparingDouble(distance::get))
                        .get();  // or throw if empty list

                LOG.info("ID {} min distance is {}", ID, distance.get(minIndex).toString());

                if (distance.get(minIndex) > threshold) {
                    deregister(ID);
                } else {
                    objects.replace(ID, list.get(minIndex));
                    usedInputCtds.add(minIndex);
                }
            }
            // 注册新输入的没有对应的
            for (Integer index = 0; index < list.size(); index += 1){
                if(!usedInputCtds.contains(index))
                    register(eventTime, list.get(index));
            }
        }

        return objects;
    }

    private Double distance(Point x, Point y){
        return Point2D.distance(x.x, x.y, y.x, y.y);
    }
}
