package net.xcore.usermanagement.userservice;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.xcore.usermanagement.userservice.dao.UserRepository;
import net.xcore.usermanagement.userservice.domain.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequiredArgsConstructor
public class UserserviceApplication {

  private static final Logger logger = LoggerFactory.getLogger(UserserviceApplication.class);

  private final UserRepository repository;

  static class Runner {
    void run(String[] args) {
      SpringApplication.run(UserserviceApplication.class, args);
    }
  }

  @Setter private static Runner runner = new Runner();

  static class FilesHelper {
    public boolean doesFileExist(String path){
      return Files.exists(Paths.get(path));
    }
  }

  @Setter private static FilesHelper filesHelper = new FilesHelper();

  static class BCryptHelper {
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

  @Setter private static BCryptHelper bcryptHelper = new BCryptHelper();

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
    runner.run(args);
  }

  private static void loadBootstrapConfiguration(String path) {
    if (filesHelper.doesFileExist(path)) {
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
  public Optional<User> getUser(@RequestParam("username") String username){
    Optional<User> user = repository.findById(username);
    return user;
  }

  @PostMapping(value = "/user", consumes = "application/json", produces = "application/json")
  public Optional<User> setUser(@RequestBody User user){
    logger.info(user.toString());

    String password = user.getPassword();
    if(StringUtils.isEmpty(password)) {
      logger.warn("Warning: empty password");
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
