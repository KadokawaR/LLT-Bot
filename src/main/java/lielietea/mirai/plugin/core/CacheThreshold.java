package lielietea.mirai.plugin.core;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class CacheThreshold {
    final Map<Long, Integer> data = new HashMap<>();
    final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    final Lock readLock = readWriteLock.readLock();
    final Lock writeLock = readWriteLock.writeLock();
    final int limit;

    CacheThreshold(int limit) {
        this.limit = limit;
    }

    public void clearCache() {
        writeLock.lock();
        try {
            data.clear();
        } finally {
            writeLock.unlock();
        }
    }

    public void count(long id) {
        writeLock.lock();
        try {
            data.put(id, data.getOrDefault(id, 0) + 1);
        } finally {
            writeLock.unlock();
        }
    }

    public int get(long id) {
        readLock.lock();
        try {
            return data.getOrDefault(id, 0);
        } finally {
            readLock.unlock();
        }
    }

    public boolean reachLimit(long id) {
        readLock.lock();
        try {
            return data.getOrDefault(id, 0) >= limit;
        } finally {
            readLock.unlock();
        }
    }


}
