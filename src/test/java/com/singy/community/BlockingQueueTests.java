package com.singy.community;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 对于BlockingQueue：
 *      方式             抛出异常     有返回值，不抛出异常      阻塞等待      超时等待
 *      添加             add         offer()                 put()        offer(E e, long timeout, TimeUnit unit)
 *      移除             remove      poll()                  take()       poll(long timeout, TimeUnit unit)
 *      检测队首元素      element     peek                    -            -
 *
 * 同步队列：java.util.concurrent.SynchronousQueue<E> implements BlockingQueue<E> 一种阻塞队列，其中每个插入操作必须等待另一个线程的对应移除操作 ，反之亦然。
 */
public class BlockingQueueTests {

    public static void main(String[] args) {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);
        new Thread(new Producer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
    }
}

class Producer implements Runnable { // 生产者线程

    private BlockingQueue<Integer> queue; // 阻塞队列

    public Producer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 100; i++) {
                Thread.sleep(20); // 生产每个整数间隔20ms
                queue.put(i);
                System.out.println(Thread.currentThread().getName() + "生产：" + queue.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Consumer implements Runnable { // 消费者线程

    private BlockingQueue<Integer> queue; // 阻塞队列

    public Consumer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(new Random().nextInt(1000)); // 消费的速度比生产的速度慢
                queue.take();
                System.out.println(Thread.currentThread().getName() + "消费：" + queue.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
