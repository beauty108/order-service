package com.example.orders;

import java.util.*;

public class OrderService{
    private final Map<UUID, Order> orders = new HashMap<>();

    public Order createOrder(String customerName, List<OrderItem> items){
        Order order = new Order(customerName, items);
        orders.put(order.getId(), order);
        return order;
    }

    public Optional<Order> findById(UUID id){
        return Optional.ofNullable(orders.get(id));
    }

    public List<Order> listAll(){
        return List.copyOf(orders.values());
    }

    public void cancelOrder(UUID id){
        Order order = findById(id).orElseThrow(()-> new OrderNotFoundException(id));

        if(order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.CANCELLED){
            throw new OrderNotCancellableException(id, order.getStatus());
        }
        order.setStatus(OrderStatus.CANCELLED);
    }
}