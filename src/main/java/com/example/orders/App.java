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
    }
}
