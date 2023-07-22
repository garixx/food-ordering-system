package com.food.ordering.system.order.service.messaging.mapper;

import com.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import com.food.ordering.system.order.service.domain.dto.message.RestaurantApprovalResponse;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import org.food.ordering.system.kafka.order.avro.model.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrderMessagingDataMapper {
    public PaymentRequestAvroModel orderCreatedEventToPaymentRequestAvroModel(OrderCreatedEvent orderCreatedEvent) {
        var order = orderCreatedEvent.getOrder();
        return PaymentRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId("todo")
                .setCustomerId(order.getCustomerId().getValue().toString())
                .setOrderId(order.getId().getValue().toString())
                .setPrice(order.getPrice().getAmount())
                .setCreatedAt(orderCreatedEvent.getCreatedAt().toInstant())
                .setPaymentOrderStatus(PaymentOrderStatus.PENDING)
                .build();
    }

    public PaymentRequestAvroModel orderCancelledEventToPaymentRequestAvroModel(OrderCancelledEvent orderCancelledEvent) {
        var order = orderCancelledEvent.getOrder();
        return PaymentRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId("todo")
                .setCustomerId(order.getCustomerId().getValue().toString())
                .setOrderId(order.getId().getValue().toString())
                .setPrice(order.getPrice().getAmount())
                .setCreatedAt(orderCancelledEvent.getCreatedAt().toInstant())
                .setPaymentOrderStatus(PaymentOrderStatus.CANCELLED)
                .build();
    }

    public RestaurantApprovalRequestAvroModel orderPaidEventToRestaurantApprovalRequestAvroModel(OrderPaidEvent orderPaidEvent) {
        var order = orderPaidEvent.getOrder();
        return RestaurantApprovalRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId("todo")
                .setOrderId(order.getId().getValue().toString())
                .setRestaurantId(order.getRestaurantId().toString())
                //.setRestaurantOrderStatus(RestaurantOrderStatus.valueOf(order.getOrderStatus().name()))
                .setProducts(order.getItems().stream()
                        .map(orderItem -> Product.newBuilder()
                                .setId(orderItem.getProduct().getId().getValue().toString())
                                .setQuantity(orderItem.getQuantity())
                                .build())
                        .toList())
                .setPrice(order.getPrice().getAmount())
                .setCreatedAt(orderPaidEvent.getCreatedAt().toInstant())
                .setRestaurantOrderStatus(RestaurantOrderStatus.PAID)
                .build();
    }

    public PaymentResponse paymentResponseAvroModelToPaymentResponse(PaymentResponseAvroModel paymentResponseAvroModel) {
        return PaymentResponse.builder()
                .id(paymentResponseAvroModel.getId())
                .sagaId(paymentResponseAvroModel.getSagaId())
                .paymentId(paymentResponseAvroModel.getPaymentId())
                .customerId(paymentResponseAvroModel.getCustomerId())
                .orderId(paymentResponseAvroModel.getOrderId())
                .price(paymentResponseAvroModel.getPrice())
                .createdAt(paymentResponseAvroModel.getCreatedAt())
                .paymentStatus(com.food.ordering.system.domain.valueobject.PaymentStatus.valueOf(paymentResponseAvroModel.getPaymentStatus().name()))
                .failureMessages(paymentResponseAvroModel.getFailureMessages())
                .build();
    }

    public RestaurantApprovalResponse approvalResponseAvroModelToApprovalResponse(RestaurantApprovalResponseAvroModel restaurantApprovalResponseAvroModel) {
        return RestaurantApprovalResponse.builder()
                .id(restaurantApprovalResponseAvroModel.getId())
                .sagaId(restaurantApprovalResponseAvroModel.getSagaId())
                .restaurantId(restaurantApprovalResponseAvroModel.getRestaurantId())
                .orderId(restaurantApprovalResponseAvroModel.getOrderId())
                .createAt(restaurantApprovalResponseAvroModel.getCreatedAt())
                .orderApprovalStatus(com.food.ordering.system.domain.valueobject.OrderApprovalStatus.valueOf(
                        restaurantApprovalResponseAvroModel.getOrderApprovalStatus().name())
                )
                .failureMessages(restaurantApprovalResponseAvroModel.getFailureMessages())
                .build();
    }
}
