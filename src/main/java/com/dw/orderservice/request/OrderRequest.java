package com.dw.orderservice.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class OrderRequest {
    private String name;
    private String category;
    private Double price;
    private Long userId;
    private String paymentMode;
}