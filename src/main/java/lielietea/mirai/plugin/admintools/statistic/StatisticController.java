package lielietea.mirai.plugin.admintools.statistic;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


@SuppressWarnings("ConstantConditions")
public class StatisticController {
    //还要多考虑一下这个统计应该怎么做
    static Table<Long,UUID,Integer> data = HashBasedTable.create();
    static Lock lock = new ReentrantLock();

    public static void countIn(long groupID, UUID serviceID){
        lock.lock();
        try {
            if (data.contains(groupID,serviceID)) {
                data.put(groupID,serviceID, data.get(groupID,serviceID)+1);
            } else {
                data.put(groupID,serviceID,1);
            }
        } finally {
            lock.unlock();
        }
    }
}
