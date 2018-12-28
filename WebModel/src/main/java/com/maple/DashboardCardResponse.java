package com.maple;

import lombok.Data;

@Data
public class DashboardCardResponse {
    private int pending;
    private int approved;
    private int received;

    public DashboardCardResponse() {}
}
