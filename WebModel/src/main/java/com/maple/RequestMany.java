package com.maple;

import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class RequestMany {
    private String username;
    private HashMap<String, Integer> value;
}
