package com.example.orders;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Map;


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

    //Stream test
    @Test
    void findByStatus_returnsOnlyMatchingOrders() {
        Order paid = service.createOrder("Alice", items);
        service.createOrder("Bob", items);
        service.createOrder("Carol", items);
        paid.setStatus(OrderStatus.PAID);

        List<Order> result = service.findByStatus(OrderStatus.PAID);

        assertEquals(1, result.size());
        assertEquals(paid, result.get(0));
    }

    @Test
    void totalRevenue_excludesCancelledOrders() {
        service.createOrder("Alice", items);
        Order toCancel = service.createOrder("Bob", items);
        service.cancelOrder(toCancel.getId());

        BigDecimal revenue = service.totalRevenue();

        assertEquals(new BigDecimal("26.00"), revenue);
    }

    @Test
    void orderByCustomer_groupsOrdersByName() {
        service.createOrder("Alice", items);
        service.createOrder("Alice", items);
        service.createOrder("Bob", items);

        Map<String, List<Order>> grouped = service.ordersByCustomer();

        assertEquals(2, grouped.size());
        assertEquals(2, grouped.get("Alice").size());
        assertEquals(1, grouped.get("Bob").size());
    }

    @Test
    void topCustomers_returnsHighestSpendersFirst() {
        List<OrderItem> cheap = List.of(new OrderItem("Pen", 1, new BigDecimal("1.00")));
        List<OrderItem> mid = List.of(new OrderItem("Book", 1, new BigDecimal("10.00")));

        service.createOrder("Alice", cheap);   // 1.00
        service.createOrder("Bob", items);     // 26.00
        service.createOrder("Carol", mid);     // 10.00

        List<String> top = service.topCustomers(2);

        assertEquals(List.of("Bob", "Carol"), top);
    }

    @Test
    void createOrder_throwsWhenCustomerNameBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> service.createOrder("   ", items));
    }

    @Test
    void createOrder_throwsWhenCustomerNameNull() {
        assertThrows(IllegalArgumentException.class,
                () -> service.createOrder(null, items));
    }

    @Test
    void createOrder_throwsWhenItemsEmpty() {
        assertThrows(IllegalArgumentException.class,
                () -> service.createOrder("Alice", List.of()));
    }

    @Test
    void createOrder_throwsWhenQuantityNotPositive() {
        List<OrderItem> bad = List.of(new OrderItem("Pen", 0, new BigDecimal("1.00")));

        assertThrows(IllegalArgumentException.class,
                () -> service.createOrder("Alice", bad));
    }

    @Test
    void createOrder_throwsWhenPriceNegative() {
        List<OrderItem> bad = List.of(new OrderItem("Pen", 1, new BigDecimal("-1.00")));

        assertThrows(IllegalArgumentException.class,
                () -> service.createOrder("Alice", bad));
    }

    @Test
    void createOrder_doesNotStoreOrderWhenInvalid() {
        assertThrows(IllegalArgumentException.class,
                () -> service.createOrder("", items));

        assertTrue(service.listAll().isEmpty());
    }


}
