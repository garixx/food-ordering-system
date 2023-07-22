package com.food.ordering.system.order.service.messaging.listener.kafka;

import com.food.ordering.system.kafka.consumer.KafkaConsumer;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.ports.input.message.listener.restaurantapproval.RestaurantApprovalResponseMessageListener;
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.food.ordering.system.kafka.order.avro.model.OrderApprovalStatus;
import org.food.ordering.system.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class RestaurantApprovalResponseKafkaListener implements KafkaConsumer<RestaurantApprovalResponseAvroModel> {
    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final RestaurantApprovalResponseMessageListener restaurantApprovalResponseMessageListener;

    public RestaurantApprovalResponseKafkaListener(OrderMessagingDataMapper orderMessagingDataMapper,
                                                   RestaurantApprovalResponseMessageListener restaurantApprovalResponseMessageListener) {
        this.orderMessagingDataMapper = orderMessagingDataMapper;
        this.restaurantApprovalResponseMessageListener = restaurantApprovalResponseMessageListener;
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.restaurant-approval-consumer-group-id}",
            topics = "${order-service.restaurant-approval-response-topic-name}")
    public void receive(@Payload() List<RestaurantApprovalResponseAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {

        log.info("{} number of restaurant approval responses received with keys:{}, partitions: {}, offsets: {}",
                messages.size(),
                keys.toString(),
                partitions.toString(),
                offsets.toString());

        messages.forEach(restaurantApprovalResponseAvroModel -> {
            if (OrderApprovalStatus.APPROVED == restaurantApprovalResponseAvroModel.getOrderApprovalStatus()) {
                log.info("Processing approved order for order id : {}", restaurantApprovalResponseAvroModel.getOrderId());
                restaurantApprovalResponseMessageListener.orderApproved(
                        orderMessagingDataMapper.approvalResponseAvroModelToApprovalResponse(restaurantApprovalResponseAvroModel));
            } else if (OrderApprovalStatus.REJECTED == restaurantApprovalResponseAvroModel.getOrderApprovalStatus()) {
                log.info("Processing rejected order for order with id: {}, with fail messages: {}",
                        restaurantApprovalResponseAvroModel.getOrderId(),
                        String.join(Order.FAILURE_MESSAGE_DELIMITER, restaurantApprovalResponseAvroModel.getFailureMessages()));
                restaurantApprovalResponseMessageListener.orderRejected(
                        orderMessagingDataMapper.approvalResponseAvroModelToApprovalResponse(restaurantApprovalResponseAvroModel));
            };
        });
    }
}
