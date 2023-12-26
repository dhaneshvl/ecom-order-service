package com.dw.orderservice.dto;

import com.dw.orderservice.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OrderInfoDTO {
    private String message;
    private Order order;
    private PaymentDTO paymentDTO;
    private UserDTO userDTO;
}
