package com.maple;

import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class ManyAssignmentRequest{
    private String username;
    private List<Assignment> value;
}
