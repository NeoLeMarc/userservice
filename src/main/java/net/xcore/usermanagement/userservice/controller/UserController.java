package net.xcore.usermanagement.userservice.controller;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import net.xcore.usermanagement.userservice.domain.User;
import net.xcore.usermanagement.userservice.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log
public class UserController {

  private final UserService userService;

  @GetMapping("/hello")
  public String hello(@RequestParam(value = "name", defaultValue = "World") String name){
    return "Hello from userservice" + name + '!';
  }

  @GetMapping("/user")
  public Optional<User> getUser(@RequestParam("username") String username){
    Optional<User> user = userService.getUser(username);
    return user;
  }

  @PostMapping(value = "/user", consumes = "application/json", produces = "application/json")
  public Optional<User> postUser(@RequestBody User user){
    return userService.createUser(user);
  }

  @PostMapping(value = "/user/verify", produces = "application/json")
  public User verifyUserPassword(@RequestParam("username") String username, @RequestParam("password") String password) {
    return userService.verifyUserPassword(username, password);
  }
}
