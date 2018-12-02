package com.maple;

import org.springframework.data.mongodb.repository.MongoRepository;

public class SimpleUtils {

    public static long getTotalObject(MongoRepository repo) {
        return repo.count();
    }

    public static long getTotalPages(long size, long totalObject) {
        if (totalObject % size==0)
            return totalObject/size;
        return (totalObject/size) + 1;
    }
}
