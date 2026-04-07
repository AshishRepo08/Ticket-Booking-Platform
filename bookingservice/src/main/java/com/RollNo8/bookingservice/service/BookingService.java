package com.RollNo8.bookingservice.service;

import com.RollNo8.bookingservice.client.InventoryServiceClient;
import com.RollNo8.bookingservice.entity.Customer;
import com.RollNo8.bookingservice.kafkaEvent.BookingEventForKafka;
import com.RollNo8.bookingservice.repository.CustomerRepository;
import com.RollNo8.bookingservice.request.BookingRequest;
import com.RollNo8.bookingservice.response.BookingResponse;
import com.RollNo8.bookingservice.response.InventoryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class BookingService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private InventoryServiceClient inventoryServiceClient;

    @Autowired
    private KafkaTemplate<String, BookingEventForKafka> kafkaTemplate;
    //K = Key Type
    //V = Value Type

    public BookingResponse createBooking(final BookingRequest request) {
        /*
        Checks to complete
            1. Check if user exists
            2. Check if there is enough inventory. And, get Event Information, ticket price to also get Venue Information
            3. create booking event
            4. send booking event to Order Service on a Kafka Topic
         */

        //1st Check : Check if user exists
        final Customer customer = customerRepository.findById(request.getUserId()).orElse(null);
        if(customer == null) {
            throw new RuntimeException("User not found");
        }

        //2nd Check : Check if there is enough inventory
        final InventoryResponse inventoryResponse = inventoryServiceClient.getInventory(request.getEventId());
       log.info("Inventory Service Response: {}", inventoryResponse);
        if (inventoryResponse.getLeftCapacity() < request.getTicketCount()) {
            throw new RuntimeException("Not enough inventory");
        }

        //3rd Step : create booking event. Which will be sent through Kafka to OrderService
        final BookingEventForKafka bookingEventForKafka = createBookingEvent(request,customer, inventoryResponse);

        //4th Step : send booking event to Order Service through Kafka
        kafkaTemplate.send("booking",bookingEventForKafka);
        log.info("Booking sent to Kafka: {}", bookingEventForKafka);

        return BookingResponse.builder()
                .userId(bookingEventForKafka.getUserId())
                .eventId(bookingEventForKafka.getEventId())
                .ticketCount(bookingEventForKafka.getTicketCount())
                .totalPrice(bookingEventForKafka.getTotalPrice())
                .build();
    }

    private BookingEventForKafka createBookingEvent(final BookingRequest request, final Customer customer, final InventoryResponse inventoryResponse) {
        return BookingEventForKafka.builder()
                .userId(customer.getId())
                .eventId(request.getEventId())
                .ticketCount(request.getTicketCount())
                .totalPrice(inventoryResponse.getTicketPrice().multiply(BigDecimal.valueOf(request.getTicketCount())))
                .build();
    }
}
