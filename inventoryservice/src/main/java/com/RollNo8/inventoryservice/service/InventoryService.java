package com.RollNo8.inventoryservice.service;

import com.RollNo8.inventoryservice.entity.Event;
import com.RollNo8.inventoryservice.entity.Venue;
import com.RollNo8.inventoryservice.repository.EventRepository;
import com.RollNo8.inventoryservice.repository.VenueRepository;
import com.RollNo8.inventoryservice.response.EventInventoryResponse;
import com.RollNo8.inventoryservice.response.VenueInventoryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InventoryService {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private VenueRepository venueRepository;


    public List<EventInventoryResponse> getAllEvents() {
        final List<Event> events = eventRepository.findAll();

        return events.stream()
                .map(event -> EventInventoryResponse.builder()
                        .eventId(event.getId())
                        .event(event.getName())
                        .leftCapacity(event.getLeftCapacity())
                        .venue(event.getVenue())
                        .ticketPrice(event.getTicketPrice())
                        .build()
                )
                .collect(Collectors.toList());
    }

    public VenueInventoryResponse getVenueInformation(Long venueId) {
        final Venue venue = venueRepository.findById(venueId).orElse(null);

        return VenueInventoryResponse.builder()
                .venueId(venue.getId())
                .venueName(venue.getName())
                .totalCapacity(venue.getTotalCapacity())
                .build();
    }

    public EventInventoryResponse getEventInventory(final Long eventId) {
        final Event event = eventRepository.findById(eventId).orElse(null);

        return EventInventoryResponse.builder()
                .event(event.getName())
                .leftCapacity(event.getLeftCapacity())
                .venue(event.getVenue())
                .ticketPrice(event.getTicketPrice())
                .eventId(event.getId())
                .build();
    }

    public void updateEventCapacity(Long eventId, Long ticketBooked) {
        final Event event = eventRepository.findById(eventId).orElse(null);
        event.setLeftCapacity(event.getLeftCapacity() - ticketBooked);
        eventRepository.saveAndFlush(event);
        log.info("Updated event capacity for event id: {} with tickets booked: {}",eventId, ticketBooked);
    }
}
