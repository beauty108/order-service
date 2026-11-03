package com.example.orders;

import java.util.UUID;

public class OrderNotCancellableException extends RuntimeException {
    public OrderNotCancellableException(UUID id, OrderStatus status){
        super("Order " + id + " cannot be cancelled; status is " + status);
    }
}