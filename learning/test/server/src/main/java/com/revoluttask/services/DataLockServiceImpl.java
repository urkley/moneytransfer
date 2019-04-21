package com.revoluttask.services;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DataLockServiceImpl<T> implements DataLockService<T> {

    private final Set<T> lockSet = ConcurrentHashMap.newKeySet();

    @Override
    public boolean tryLock(T object) {
       return lockSet.add(object);
    }

    @Override
    public void unlock(T object) {
        lockSet.remove(object);
    }
}
