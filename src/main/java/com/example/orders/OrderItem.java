package com.example.orders;
import java.math.BigDecimal;

public record OrderItem(String productName, int quantity, BigDecimal unitPrice){
    public BigDecimal lineTotal(){
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}