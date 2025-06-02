package com.major.k1.resturant.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SlotPush {
    int availableSeats;
    String time;
}
