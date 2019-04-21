package com.revoluttask.services;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DataLockService<T> {

    private final Set<T> lockSet = ConcurrentHashMap.newKeySet();

    public boolean tryLock(T object) {
       return lockSet.add(object);
    }

    public void unlock(T object) {
        lockSet.remove(object);
    }
}
