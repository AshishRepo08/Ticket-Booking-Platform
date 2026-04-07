package com.RollNo8.bookingservice.response;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VenueResponse {

    private Long id;
    private String name;
    private String address;
    private Long totalCapacity;
}
