package com.helium.nettymq.broker.utils;

public interface PutMessageLock {
    void lock();

    void unlock();
}
