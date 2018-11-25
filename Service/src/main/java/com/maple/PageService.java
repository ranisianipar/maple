package com.maple;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;



public class PageService {

    public static Pageable getPage (Integer page, Integer size, String sortBy)  {
        if (page == null) page = 0;
        if (size == null) size = 10;
        return PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortBy));
    }
}
