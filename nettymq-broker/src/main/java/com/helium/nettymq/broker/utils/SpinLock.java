package com.helium.nettymq.broker.utils;

import java.util.concurrent.atomic.AtomicInteger;

public class SpinLock implements PutMessageLock {

    AtomicInteger atomicInteger = new AtomicInteger(0);

    @Override
    public void lock() {
        do {
            int result = atomicInteger.getAndIncrement();
            if (result == 1) {
                return;
            }
        } while (true);
    }

    @Override
    public void unlock() {
        atomicInteger.decrementAndGet();
    }
}
