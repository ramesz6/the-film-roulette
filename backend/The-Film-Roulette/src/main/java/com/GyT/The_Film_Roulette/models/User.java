package com.GyT.The_Film_Roulette.models;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;
    private String password;

}