package com.major.k1.resturant.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PendingOTPForget {
    private String username;
    private String otp;
    private long timestamp;
}
