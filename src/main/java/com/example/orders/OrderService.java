package com.example.orders;

import java.util.*;
import java.math.BigDecimal;
import java.util.stream.Collectors;

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

    //streams
    public List<Order> findByStatus(OrderStatus status){
        return orders.values().stream()
                .filter(o->o.getStatus() == status)
                .toList();
    }

    public BigDecimal totalRevenue(){
        return orders.values().stream()
                .filter(o->o.getStatus() != OrderStatus.CANCELLED)
                .map(Order::total)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<String, List<Order>> ordersByCustomer(){
        return orders.values().stream()
                .collect(Collectors.groupingBy(Order::getCustomerName));
    }

    public List<String> topCustomers(int n){
        return orders.values().stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELLED)
                .collect(Collectors.groupingBy(
                        Order::getCustomerName,
                        Collectors.reducing(BigDecimal.ZERO, Order::total, BigDecimal::add)))
                .entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .limit(n)
                .map(Map.Entry::getKey)
                .toList();
    }
}