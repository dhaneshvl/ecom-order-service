package com.dw.orderservice.service;

import com.dw.orderservice.dto.OrderInfoDTO;
import com.dw.orderservice.dto.PaymentDTO;
import com.dw.orderservice.dto.UserDTO;
import com.dw.orderservice.entity.Order;
import com.dw.orderservice.repository.OrderRepository;
import com.dw.orderservice.request.OrderRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    @Lazy
    RestTemplate restTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @Value("${order.producer.topic.name}")
    private String topicName;

    private static final String PAYMENT_SERVICE_URL = "http://PAYMENT-SERVICE/api/v1/payment";

    private static final String USER_SERVICE_URL = "http://USER-SERVICE/api/v1/user";
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public String placeOrder(OrderRequest orderRequest) {

        Order order = new Order();
        order.setName(orderRequest.getName());
        order.setCategory(orderRequest.getCategory());
        order.setPrice(orderRequest.getPrice());
        order.setOrderId(UUID.randomUUID().toString().split("-")[0]);
        order.setUserId(orderRequest.getUserId());
        order.setPaymentMode(orderRequest.getPaymentMode());
        order.setPurchaseDate(new Date());

        Order _order = orderRepository.save(order);
        try {
            kafkaTemplate.send(topicName, new ObjectMapper().writeValueAsString(_order));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return "Your order with (" + _order.getOrderId() + ") has been placed, soon you will receive the confirmation. Thank you.";
    }

    @CircuitBreaker(name = "orderService", fallbackMethod = "getOrderDetails")
    public OrderInfoDTO getOrderInfo(String orderId) throws Exception {
        Order order = orderRepository.findByOrderId(orderId);
        if (order == null)
            throw new RuntimeException("Invalid order id, please provide the valid order id.");
        PaymentDTO paymentDTO = restTemplate.getForObject(PAYMENT_SERVICE_URL + "/" + orderId, PaymentDTO.class);
        UserDTO userDTO = restTemplate.getForObject(USER_SERVICE_URL + "/" + order.getUserId(), UserDTO.class);
        return OrderInfoDTO.builder()
                .order(order)
                .paymentDTO(paymentDTO)
                .userDTO(userDTO)
                .build();
    }

    public OrderInfoDTO getOrderDetails(Exception ex) {
        return new OrderInfoDTO("Failed:" + ex.getMessage(), null, null, null);
    }

}
