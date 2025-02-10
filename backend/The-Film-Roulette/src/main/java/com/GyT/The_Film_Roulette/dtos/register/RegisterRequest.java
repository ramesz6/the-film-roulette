package com.GyT.The_Film_Roulette.dtos.register;

public record RegisterRequest(
    String username,
    String email,
    String password) {
}