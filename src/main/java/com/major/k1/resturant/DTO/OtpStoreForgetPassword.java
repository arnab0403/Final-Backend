package com.major.k1.resturant.DTO;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class OtpStoreForgetPassword {
    private final Map<String,PendingOTPForget> store= new ConcurrentHashMap<>();

    public void add(String username , PendingOTPForget pendingOtp){
        store.put(username,pendingOtp);
    }
    public PendingOTPForget get(String username){
        return store.get(username);
    }
    public void remove(String username) {
        store.remove(username);
    }

    public void removeExpiredUsers(long maxAgeMillis) {
        long now = System.currentTimeMillis();
        store.entrySet().removeIf(entry ->
                (now - entry.getValue().getTimestamp()) > maxAgeMillis
        );
    }
}
