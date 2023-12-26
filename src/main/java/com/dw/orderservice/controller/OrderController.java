package com.dw.orderservice.controller;

import com.dw.orderservice.dto.OrderInfoDTO;
import com.dw.orderservice.entity.Order;
import com.dw.orderservice.request.OrderRequest;
import com.dw.orderservice.service.OrderService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    //Validation
    //Logging
    //ExceptionHandling
    @PostMapping("/placeOrder")
    public String placeOrder(@RequestBody OrderRequest orderRequest) {
        return orderService.placeOrder(orderRequest);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderInfo(@PathVariable String orderId) {
        try {
            return new ResponseEntity<>(orderService.getOrderInfo(orderId), HttpStatusCode.valueOf(HttpStatus.SC_ACCEPTED));
        } catch (Exception ex) {
            return new ResponseEntity<>("Error: " + ex.getMessage(), HttpStatusCode.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR));
        }
    }

}
