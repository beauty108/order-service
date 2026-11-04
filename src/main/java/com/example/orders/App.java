package com.example.orders;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


public class App 
{
    public static void main( String[] args )
    {
        OrderService service = new OrderService();

        List<OrderItem> items = List.of(
                new OrderItem("Keyboard", 2, new BigDecimal("10.50")),
                new OrderItem("Mouse", 1, new BigDecimal("5.00"))
        );
        Order order = service.createOrder("Alice", items);
        System.out.println("Created: " + order.getId() + ", total = " + order.total());

        System.out.println("FOund: " + service.findById(order.getId()).isPresent());

        System.out.println("All orders: " + service.listAll().size());

        service.cancelOrder(order.getId());

        System.out.println("Status after cancel: " + order.getStatus());

        try{
            service.cancelOrder(UUID.randomUUID());
        }catch (OrderNotFoundException e){
            System.out.println("Caught: " + e.getMessage());
        }

        try{
            service.cancelOrder(order.getId());
        }catch (OrderNotCancellableException e){
            System.out.println("Caught: " + e.getMessage());
        }

        // Stream queries
        service.createOrder("Alice", List.of(new OrderItem("Monitor", 1, new BigDecimal("150.00"))));
        service.createOrder("Bob", List.of(new OrderItem("Cable", 3, new BigDecimal("4.00"))));

        System.out.println("CREATED orders: " + service.findByStatus(OrderStatus.CREATED).size());
        System.out.println("Total revenue: " + service.totalRevenue());
        System.out.println("Customers: " + service.ordersByCustomer().keySet());
        System.out.println("Top 2 customers: " + service.topCustomers(2));
    }
}
