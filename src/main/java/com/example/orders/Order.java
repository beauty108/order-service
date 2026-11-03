package com.example.orders;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class Order{
    private final UUID id;
    private final String customerName;
    private final List<OrderItem> items;
    private final Instant createdAt;
    private OrderStatus status;

    public Order(String customerName, List<OrderItem> items){
        this.id = UUID.randomUUID();
        this.customerName = customerName;
        this.items = List.copyOf(items);
        this.createdAt = Instant.now();
        this.status = OrderStatus.CREATED;
    }

    public UUID getId(){
        return id;
    }

    public String getCustomerName(){
        return customerName;
    }

    public List<OrderItem> getItems(){
        return items;
    }

    public Instant getCreatedAt(){
        return createdAt;
    }

    public OrderStatus getStatus(){
        return status;
    }

    public void setStatus(OrderStatus status){
        this.status = status;
    }

    public BigDecimal total(){
        BigDecimal sum = BigDecimal.ZERO;
        for (OrderItem item : items){
            sum = sum.add(item.lineTotal());
        }
        return sum;
    }


}