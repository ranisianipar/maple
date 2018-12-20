package com.maple;

import lombok.Data;

import java.util.List;

@Data
public class DeleteRequest{
    public String username;
    private List<String> ids;
}
