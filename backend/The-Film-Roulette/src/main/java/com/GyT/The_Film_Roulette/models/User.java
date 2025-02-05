package com.GyT.The_Film_Roulette.models;

import org.hibernate.annotations.NaturalId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    @NaturalId(mutable = true)
    private String email;
    private String username;
    private String password;

}