package com.maple;

import lombok.Data;

import java.util.List;

@Data
public class DeleteRequest {
    String username;
    List<String > ids;
}
