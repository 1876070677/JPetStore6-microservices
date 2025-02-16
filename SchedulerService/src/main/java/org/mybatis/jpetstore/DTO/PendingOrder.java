package org.mybatis.jpetstore.DTO;

import java.io.Serializable;

public class PendingOrder implements Serializable {
    private int orderId;
    private int pendingTime;

    public PendingOrder() {};

    public PendingOrder(int orderId, int pendingTime) {
        this.orderId = orderId;
        this.pendingTime = pendingTime;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setPendingTime(int pendingTime) {
        this.pendingTime = pendingTime;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getPendingTime() {
        return pendingTime;
    }
}
