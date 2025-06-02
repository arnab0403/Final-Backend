package com.major.k1.resturant.Entites;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
@AllArgsConstructor
public class FeedBack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long uid;
    private String name;
    private String email;
    private String messages;

    public FeedBack(Long uid, String name, String email, String messages) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.messages = messages;
    }
}
