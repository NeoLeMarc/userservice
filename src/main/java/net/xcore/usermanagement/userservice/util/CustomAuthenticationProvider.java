package net.xcore.usermanagement.userservice.util;

import java.util.ArrayList;
import java.util.Optional;
import lombok.extern.java.Log;
import net.xcore.usermanagement.userservice.domain.User;
import net.xcore.usermanagement.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Log
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

  @Autowired
  private UserService userService;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String username = authentication.getName();
    String password = authentication.getCredentials().toString();

    Optional<User> oUser = userService.verifyUserPassword(username, password);
    if(oUser.isEmpty()) {
      log.warning("User " + username + " not found in database or password is invalid");
      return null;
    }
    log.info("User " + username + " found in database");

    User user = oUser.get();
    ArrayList<GrantedAuthority> roles = new ArrayList<>();
    if(user.getRole().equals("admin")){
      log.info("User " + username + " roles [USER][ADMIN]");
      roles.add(new SimpleGrantedAuthority("ROLE_USER"));
      roles.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
    } else if (user.getRole().equals("user")){
      log.info("User " + username + " roles [USER]");
      roles.add(new SimpleGrantedAuthority("ROLE_USER"));
    } else {
      log.warning("No role mapping found for role " + user.getRole());
    }

    return new UsernamePasswordAuthenticationToken(user.getUsername(), password, roles);
  }

  @Override
  public boolean supports(Class<?> aClass) {
    if(!aClass.equals(UsernamePasswordAuthenticationToken.class)){
      log.warning("Does not support " + aClass);
      return false;
    }
    return true;
  }
}
