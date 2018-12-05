package com.maple;

import lombok.Data;

import java.util.List;

@Data
public class DeleteRequest {
    private String username;
    private List<String > ids;
}
