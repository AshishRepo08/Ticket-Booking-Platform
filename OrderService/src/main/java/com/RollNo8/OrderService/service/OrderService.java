package com.RollNo8.OrderService.service;

import com.RollNo8.OrderService.client.InventoryServiceClient;
import com.RollNo8.OrderService.entity.Order;
import com.RollNo8.OrderService.repository.OrderRepository;
import com.RollNo8.bookingservice.kafkaEvent.BookingEventForKafka;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private InventoryServiceClient inventoryServiceClient;

    @KafkaListener(topics = "booking", groupId = "order-service")
    public void orderEventListenerMethod(BookingEventForKafka bookingEventForKafka) {
        log.info("Received order event: {}", bookingEventForKafka);

        //Two things we want to accomplish in this service:
            //1. Create Order object based on Kafka Event and save it to the DB
            //2  Update the inventory to reflect the new value of tickets left.

        //Step 1
        Order order = createOrder(bookingEventForKafka);
        orderRepository.saveAndFlush(order);

        //Step 2
        inventoryServiceClient.updateInventory(order.getEventId(),order.getTicketCount());
        log.info("Inventory updated for event: {}, less tickets: {}",order.getEventId(),order.getTicketCount());

    }

    //Helper Method
    private Order createOrder(BookingEventForKafka bookingEventForKafka) {
        return Order.builder()
                .customerId(bookingEventForKafka.getUserId())
                .eventId(bookingEventForKafka.getEventId())
                .ticketCount(bookingEventForKafka.getTicketCount())
                .totalPrice(bookingEventForKafka.getTotalPrice())
                .build();
    }

}
