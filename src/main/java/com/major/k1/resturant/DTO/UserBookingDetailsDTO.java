package com.major.k1.resturant.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
public class UserBookingDetailsDTO {
    private String name;
    private double amount;
    private int seats;
    private String location;
     private LocalDateTime bookingTime;
}
