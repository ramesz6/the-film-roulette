package com.GyT.The_Film_Roulette.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.GyT.The_Film_Roulette.models.User;
import com.GyT.The_Film_Roulette.services.UserService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/users")
  public ResponseEntity<List<User>> getStudents() {
    return new ResponseEntity<>(userService.getUsers(), HttpStatus.FOUND);
  }

}