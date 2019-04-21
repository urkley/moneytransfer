package com.revoluttask.services;

public interface DataLockService<T> {

    boolean tryLock(T object);

    void unlock(T object);
}
