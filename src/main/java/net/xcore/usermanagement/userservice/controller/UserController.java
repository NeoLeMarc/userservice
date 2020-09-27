package net.xcore.usermanagement.userservice.controller;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import net.xcore.usermanagement.userservice.domain.User;
import net.xcore.usermanagement.userservice.service.UserService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

  @GetMapping("/helloadmin")
  @Secured("ROLE_ADMIN")
  public String helloAdmin(@RequestParam(value = "name", defaultValue = "World") String name){
    return "Hello admin - from userservice" + name + '!';
  }

  @GetMapping("/hellouser")
  @Secured("ROLE_USER")
  public String helloUser(@RequestParam(value = "name", defaultValue = "World") String name){
    return "Hello user - from userservice" + name + '!';
  }

  @GetMapping("/user/{username}")
  @Secured("ROLE_USER")
  public Optional<User> getUser(@PathVariable String username){
    log.info("GET /user/" + username);
    Optional<User> user = userService.getUser(username);
    return user;
  }

  @Secured("ROLE_ADMIN")
  @PostMapping(value = "/user", consumes = "application/json", produces = "application/json")
  public Optional<User> postUser(@RequestBody User user){
    return userService.createUser(user);
  }

  @PostMapping(value = "/user/verify", produces = "application/json")
  @Secured("ROLE_USER")
  public User verifyUserPassword(@RequestParam("username") String username, @RequestParam("password") String password) {
    log.info("POST /verify/" + username);
    return userService.verifyUserPassword(username, password);
  }
}
