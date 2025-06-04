package com.major.k1.resturant.DTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OtpCleanupScheduler {

    @Autowired
    private OtpUserStore otpUserStore;
    @Autowired
    private OtpStoreForgetPassword otpStoreForgetPassword;

    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void cleanupExpiredOtps() {
        long otpLifetime = 10 * 60 * 1000; // 10 minutes
        otpUserStore.removeExpiredUsers(otpLifetime);
        otpStoreForgetPassword.removeExpiredUsers(otpLifetime);
        System.out.println("Expired OTPs cleaned up");
    }
}
