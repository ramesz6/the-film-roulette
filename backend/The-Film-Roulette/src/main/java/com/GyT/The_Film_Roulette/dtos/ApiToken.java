package com.GyT.The_Film_Roulette.dtos;

public record ApiToken(
    boolean success,
    String expired_at,
    String request_token) {
}
