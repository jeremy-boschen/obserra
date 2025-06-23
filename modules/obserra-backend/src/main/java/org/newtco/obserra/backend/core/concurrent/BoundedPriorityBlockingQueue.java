package org.newtco.obserra.backend.core.concurrent;

import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class BoundedPriorityBlockingQueue<E> {
    private final PriorityBlockingQueue<E> queue;
    private final Semaphore                permits;
    private final Consumer<E>              rejectionHandler;

    public BoundedPriorityBlockingQueue(int capacity) {
        this(capacity, null);
    }

    public BoundedPriorityBlockingQueue(int capacity, Comparator<? super E> comparator) {
        this(capacity, comparator, null);
    }

    public BoundedPriorityBlockingQueue(int capacity, Comparator<? super E> comparator, Consumer<E> rejectionHandler) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be > 0");
        }

        this.queue            = new PriorityBlockingQueue<>(capacity, comparator);
        this.permits          = new Semaphore(capacity);
        this.rejectionHandler = rejectionHandler;
    }

    public void put(E element) throws InterruptedException {
        permits.acquire();
        queue.put(element);
    }

    public boolean offer(E element, long timeout, TimeUnit unit) throws InterruptedException {
        if (permits.tryAcquire(timeout, unit)) {
            queue.put(element);
            return true;
        } else {
            if (rejectionHandler != null) rejectionHandler.accept(element);
            return false;
        }
    }

    public boolean offer(E element) {
        if (permits.tryAcquire()) {
            queue.put(element);
            return true;
        } else {
            if (rejectionHandler != null) rejectionHandler.accept(element);
            return false;
        }
    }

    public E take() throws InterruptedException {
        E item = queue.take();
        permits.release();
        return item;
    }

    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        E item = queue.poll(timeout, unit);
        if (item != null) permits.release();
        return item;
    }

    public E poll() {
        E item = queue.poll();
        if (item != null) permits.release();
        return item;
    }

    public E peek() {
        return queue.peek();
    }

    public int drainTo(Collection<? super E> c, int maxElements) {
        int drained = queue.drainTo(c, maxElements);
        permits.release(drained);
        return drained;
    }

    public int size() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int remainingCapacity() {
        return permits.availablePermits();
    }
}
