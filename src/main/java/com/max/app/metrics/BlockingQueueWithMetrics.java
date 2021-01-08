package com.max.app.metrics;

import io.prometheus.client.Gauge;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public final class BlockingQueueWithMetrics extends AbstractQueue<Runnable> implements BlockingQueue<Runnable> {

    private static final Gauge asyncQueueSize = Gauge.build().
            name("async_queue_size").
            help("Async worker queue size.").
            register();

    private final BlockingQueue<Runnable> original;

    public BlockingQueueWithMetrics(BlockingQueue<Runnable> original) {
        this.original = Objects.requireNonNull(original, "null 'original' queue passed");
    }

    public void printMetrics() {
        System.out.printf("Queue size: %.0f%n", asyncQueueSize.get());
    }

    @Override
    public Iterator<Runnable> iterator() {
        return original.iterator();
    }

    @Override
    public int size() {
        return original.size();
    }

    @Override
    public void put(Runnable task) throws InterruptedException {
        original.put(task);
        asyncQueueSize.inc();
    }

    @Override
    public boolean offer(Runnable task, long timeout, TimeUnit unit) throws InterruptedException {
        boolean wasAdded = original.offer(task, timeout, unit);
        if (wasAdded) {
            asyncQueueSize.inc();
        }
        return wasAdded;
    }

    @Override
    public Runnable take() throws InterruptedException {
        Runnable task = original.take();
        asyncQueueSize.dec();
        return task;
    }

    @Override
    public Runnable poll(long timeout, TimeUnit unit) throws InterruptedException {
        Runnable task = original.poll(timeout, unit);
        if (task != null) {
            asyncQueueSize.dec();
        }
        return task;
    }

    @Override
    public int remainingCapacity() {
        return original.remainingCapacity();
    }

    @Override
    public int drainTo(Collection<? super Runnable> other) {
        int movedElementsCount = original.drainTo(other);
        asyncQueueSize.dec(movedElementsCount);
        return movedElementsCount;
    }

    @Override
    public int drainTo(Collection<? super Runnable> other, int maxElements) {
        int movedElementsCount = original.drainTo(other, maxElements);
        asyncQueueSize.dec(movedElementsCount);
        return movedElementsCount;
    }

    @Override
    public boolean offer(Runnable task) {
        boolean wasInserted = original.offer(task);

        if (wasInserted) {
            asyncQueueSize.inc();
        }
        return wasInserted;
    }

    @Override
    public Runnable poll() {
        Runnable task = original.poll();

        if (task != null) {
            asyncQueueSize.dec();
        }

        return task;
    }

    @Override
    public Runnable peek() {
        return original.peek();
    }
}
