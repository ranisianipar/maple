package com.maple;

import lombok.Data;

@Data
public class DashboardCardResponse {
    private long pending;
    private long approved;
    private long received;

    public DashboardCardResponse() {}
}
