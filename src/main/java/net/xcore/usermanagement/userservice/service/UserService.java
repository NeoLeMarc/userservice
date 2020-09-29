package net.xcore.usermanagement.userservice.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;
import lombok.val;
import net.xcore.usermanagement.userservice.dao.UserRepository;
import net.xcore.usermanagement.userservice.domain.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log
public class UserService {
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
  private static UserService.BCryptHelper bcryptHelper = new UserService.BCryptHelper();

  public Optional<User> getUser(String username){
    val user = repository.findById(username);
    if(user.isEmpty()){
      log.warning("User with username " + username + " not found!");
    }
    return user;
  }

  public Optional<User> createUser(User user){
    log.info("Creating user: " + user.getUsername());

    String password = user.getPassword();
    if(StringUtils.isEmpty(password)) {
      log.warning("Warning: empty password");
      password = "";
    }

    String cryptedPassword = bcryptHelper.hashpw(password, bcryptHelper.gensalt());
    user.setPassword(cryptedPassword);
    repository.save(user);
    return repository.findById(user.getUsername());
  }

  public Optional<User> verifyUserPassword(String username, String password){
    Optional<User> ouser = repository.findById(username);
    if(ouser.isEmpty()){
      bcryptHelper.checkpw(password, BCrypt.gensalt()+"asdf");
      log.warning("User " + username + " not found");
      return Optional.empty();
    }

    User user = ouser.get();
    if(bcryptHelper.checkpw(password, user.getPassword())){
      log.info("Password successfully verified for user" + user.getUsername());
      return ouser;
    }
    log.warning("Password verification failed for user " + username);
    return Optional.empty();
  }
}
