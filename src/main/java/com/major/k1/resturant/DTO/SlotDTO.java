package com.major.k1.resturant.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
//For fetching time slots for restaurant list
@Data
@AllArgsConstructor
public class SlotDTO {

    private Long id;
    private String time;
    private boolean available;
    private int availableSeats;

    // Constructor
    public SlotDTO(Long id, String time, boolean available) {
        this.id = id;
        this.time = time;
        this.available = available;
    }
    public SlotDTO(String time) {
        this.time = time;

    }
    public SlotDTO(String time , int availableSeats) {
        this.time = time;

    }

    public SlotDTO(String time, boolean available) {
        this.time = time;
        this.available = available;
    }

    // Getters and setters

}
