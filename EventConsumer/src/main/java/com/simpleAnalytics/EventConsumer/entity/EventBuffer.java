package com.simpleAnalytics.EventConsumer.entity;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class EventBuffer {

    private final List<Event> events = new ArrayList<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static final int MAX_BATCH_SIZE = 1000;

    public void add(Event event) {
        lock.writeLock().lock();
        try {
            events.add(event);
        } finally {
            lock.writeLock().unlock();
        }
    }public void addAll(List<Event> eventList) {
        lock.writeLock().lock();
        try {
            events.addAll(eventList);
        } finally {
            lock.writeLock().unlock();
        }
    }


    public boolean isEmpty() {
        lock.readLock().lock();
        try {
            return events.isEmpty();
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Event> drain() {
        lock.writeLock().lock();
        try {
            if (events.isEmpty()) {
                return List.of();
            }

            List<Event> batch = new ArrayList<>(events);
            events.clear();
            return batch;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<Event> drainBatch(int maxSize) {
        lock.writeLock().lock();
        try {
            if (events.isEmpty()) {
                return List.of();
            }

            int batchSize = Math.min(maxSize, events.size());
            List<Event> batch = new ArrayList<>(events.subList(0, batchSize));
            events.subList(0, batchSize).clear();
            return batch;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void clear() {
        lock.writeLock().lock();
        try {
            events.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int size() {
        lock.readLock().lock();
        try {
            return events.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean shouldFlush() {
        lock.readLock().lock();
        try {
            return events.size() >= MAX_BATCH_SIZE;
        } finally {
            lock.readLock().unlock();
        }
    }
}
