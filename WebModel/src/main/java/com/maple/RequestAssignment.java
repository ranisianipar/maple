package com.maple;

import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class RequestAssignment extends Request{
    private List<Assignment> value;
}
