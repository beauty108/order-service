package com.example.orders;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest{
    private OrderService service;
    private List<OrderItem> items;

    @BeforeEach
    void setUp(){
        service = new OrderService();
        items = List.of(
                new OrderItem("Keyboard", 2, new BigDecimal("10.50")),
                new OrderItem("Mouse", 1, new BigDecimal("5.00"))
        );
    }

    @Test
    void createOrder_calculatesCorrectTotal(){
        Order order = service.createOrder("Alice", items);

        assertEquals(new BigDecimal("26.00"), order.total());
        assertEquals(OrderStatus.CREATED, order.getStatus());
    }

    @Test
    void findById_returnsExistingOrder(){
        Order order = service.createOrder("Alice", items);

        Optional<Order> result = service.findById(order.getId());

        assertTrue(result.isPresent());
        assertEquals(order, result.get());
    }

    @Test
    void findById_returnsEmptyForUnknownId(){
        Optional<Order> result = service.findById(UUID.randomUUID());

        assertTrue(result.isEmpty());
    }

    @Test
    void listAll_returnsAllCreatedOrders() {
        service.createOrder("Alice", items);
        service.createOrder("Bob", items);

        assertEquals(2, service.listAll().size());
    }

    @Test
    void cancelOrder_setsStatusToCancelled(){
        Order order = service.createOrder("Alice", items);

        service.cancelOrder(order.getId());

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void cancelOrder_throwsWhenOrderNotFound() {
        assertThrows(OrderNotFoundException.class,
                () -> service.cancelOrder(UUID.randomUUID()));
    }

    @Test
    void cancelOrder_throwsWhenShipped() {
        Order order = service.createOrder("Alice", items);
        order.setStatus(OrderStatus.SHIPPED);

        assertThrows(OrderNotCancellableException.class,
                () -> service.cancelOrder(order.getId()));
    }

    @Test
    void cancelOrder_throwsWhenAlreadyCancelled() {
        Order order = service.createOrder("Alice", items);
        service.cancelOrder(order.getId());

        assertThrows(OrderNotCancellableException.class,
                () -> service.cancelOrder(order.getId()));
    }


}
