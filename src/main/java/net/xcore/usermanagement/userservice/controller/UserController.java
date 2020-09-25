package net.xcore.usermanagement.userservice.controller;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;
import net.xcore.usermanagement.userservice.dao.UserRepository;
import net.xcore.usermanagement.userservice.domain.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log
public class UserController {
  private final UserRepository repository;

  public static class BCryptHelper {
    public String hashpw(String password, String salt){
      return BCrypt.hashpw(password, salt);
    }

    public String gensalt(){
      return BCrypt.gensalt();
    }

    public boolean checkpw(String password, String hashed){
      return BCrypt.checkpw(password, hashed);
    }
  }

  @Setter
  private static UserController.BCryptHelper bcryptHelper = new UserController.BCryptHelper();

  @GetMapping("/hello")
  public String hello(@RequestParam(value = "name", defaultValue = "World") String name){
    return "Hello from userservice" + name + '!';
  }

  @GetMapping("/user")
  public Optional<User> getUser(@RequestParam("username") String username){
    Optional<User> user = repository.findById(username);
    return user;
  }

  @PostMapping(value = "/user", consumes = "application/json", produces = "application/json")
  public Optional<User> setUser(@RequestBody User user){
    log.info(user.toString());

    String password = user.getPassword();
    if(StringUtils.isEmpty(password)) {
      log.warning("Warning: empty password");
      password = "";
    }

    String cryptedPassword = bcryptHelper.hashpw(password, BCrypt.gensalt());
    user.setPassword(cryptedPassword);
    repository.save(user);
    return repository.findById(user.getUsername());
  }

  @PostMapping(value = "/user/verify", produces = "application/json")
  public User verifyUserPassword(@RequestParam("username") String username, @RequestParam("password") String password){
    Optional<User> ouser = repository.findById(username);
    if(ouser.isEmpty()){
      bcryptHelper.checkpw(password, BCrypt.gensalt()+"asdf");
      return null;
    }

    User user = ouser.get();
    if(bcryptHelper.checkpw(password, user.getPassword())){
      return user;
    }
    return null;
  }
}
