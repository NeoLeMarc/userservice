package net.xcore.usermanagement.userservice;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import net.xcore.usermanagement.userservice.dao.UserRepository;
import net.xcore.usermanagement.userservice.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class UserserviceApplication {

    private static final Logger logger = LoggerFactory.getLogger(UserserviceApplication.class);

  @Autowired
  UserRepository repository;

  public static void main(String[] args) {
    if (args != null) {
      logger.info("main({})", (Object) args);
    } else {
      logger.info("main() called without arguments");
    }

    String propertyPath = null;
    for(String arg : args) {
      if(arg != null && !arg.isEmpty() && arg.charAt(0) != '-'){
        propertyPath = arg;
        break;
      }
    }

    if (propertyPath != null && !propertyPath.isEmpty()) {
      logger.info("Loading bootstrap properties from {}", propertyPath);
      loadBootstrapConfiguration(propertyPath);
    } else {
      loadBootstrapConfiguration("/home/marcel/properties/userservice/dev.properties");
    }
    SpringApplication.run(UserserviceApplication.class, args);
  }

  private static void loadBootstrapConfiguration(String path) {
    if (Files.exists(Paths.get(path))) {
      logger.info("Found bootstrap properties {} - updating spring.cloud.bootstrap.location", path);
      System.setProperty("spring.cloud.bootstrap.location", path);
    } else {
      logger.info("Failed to load bootstrap properties");
    }
  }

  @GetMapping("/hello")
  public String hello(@RequestParam(value = "name", defaultValue = "World") String name){
    return "Hello from userservice" + name + '!';
  }

  @GetMapping("/user")
  public Optional<User> getUser(@RequestParam(value = "username") String username){
    Optional<User> user = repository.findById(username);
    return user;
  }

  @PostMapping(value = "/user", consumes = "application/json", produces = "application/json")
  public Optional<User> setUser(@RequestBody User user){
    logger.info(user.toString());
    String cryptedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
    user.setPassword(cryptedPassword);
    repository.save(user);
    return repository.findById(user.getUsername());
  }

  @PostMapping(value = "/user/verify", produces = "application/json")
  public User verifyUserPassword(@RequestParam("username") String username, @RequestParam("password") String password){
    Optional<User> ouser = repository.findById(username);
    if(ouser.isEmpty()){
      BCrypt.checkpw(password, BCrypt.gensalt()+"asdf");
      return null;
    }

    User user = ouser.get();
    if(BCrypt.checkpw(password, user.getPassword())){
      return user;
    }
    return null;
  }
}
