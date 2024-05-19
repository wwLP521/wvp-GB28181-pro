package com.genersoft.iot.vmp.utils.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionDemo {

    private static final Logger log = LoggerFactory.getLogger(ConditionDemo.class);

    static ReentrantLock lock = new ReentrantLock();

    static Condition waitCigaretteQueue = lock.newCondition();

    static Condition waitBreakfastQueue = lock.newCondition();

    static Condition waitQueue = lock.newCondition();

    static volatile boolean hasCigarette= false;

    static volatile boolean hasBreakfast = false;

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            try {
                lock.lock();
                while (!hasCigarette) {
                    try {
                        waitCigaretteQueue.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                log.debug("等到了他的烟");
            } finally {
                lock.unlock();
            }
        }).start();
        new Thread(() -> {
            try {
                lock.lock();
                while (!hasBreakfast) {
                    try {
                        waitBreakfastQueue.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                log.debug("等到了他的早餐");
            } finally {
                lock.unlock();
            }
        }).start();

        TimeUnit.SECONDS.sleep(1);
        sendCigarette();
        TimeUnit.SECONDS.sleep(1);
        sendBreakfast();
    }

    private static void sendCigarette() {
        lock.lock();
        try {
            log.debug("来送烟了");
            hasCigarette = true;
            waitCigaretteQueue.signal();
        } finally {
            lock.unlock();
        }
    }

    private static void sendBreakfast() {
        lock.lock();
        try {
            log.debug("来送早餐了");
            hasBreakfast = true;
            waitBreakfastQueue.signal();
        } finally {
            lock.unlock();
        }
    }
}
