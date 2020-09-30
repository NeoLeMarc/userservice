package net.xcore.usermanagement.userservice.util;

import java.util.Collection;
import java.util.Optional;
import net.xcore.usermanagement.userservice.domain.User;
import net.xcore.usermanagement.userservice.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

@RunWith(MockitoJUnitRunner.class)
public class CustomAuthenticationProviderUnitTest {

  public static final String USER_ROLE = "user";
  public static final String USER_USERNAME = "username";
  public static final String USER_PASSWORD = "userpassword";
  public static final String ADMIN_ROLE = "admin";
  public static final String ADMIN_USERNAME = "adminusername";
  public static final String ADMIN_PASSWORD = "adminpassword";
  private CustomAuthenticationProvider customAuthenticationProvider;

  @Mock
  private UserService mockUserService;
  private User user;
  private User adminUser;

  @Before
  public void initMocks(){
    customAuthenticationProvider = new CustomAuthenticationProvider();
    customAuthenticationProvider.setUserService(mockUserService);

    user = new User();
    user.setRole(USER_ROLE);
    user.setUsername(USER_USERNAME);
    user.setPassword(USER_PASSWORD);

    adminUser = new User();
    user.setRole(ADMIN_ROLE);
    user.setUsername(ADMIN_USERNAME);
    user.setPassword(ADMIN_PASSWORD);

    Mockito.when(mockUserService.getUser(Mockito.eq(USER_USERNAME))).thenReturn(Optional.of(user));
    Mockito.when(mockUserService.getUser(Mockito.eq(ADMIN_USERNAME))).thenReturn(Optional.of(adminUser));
    Mockito.when(mockUserService.verifyUserPassword(Mockito.eq(USER_USERNAME), Mockito.eq(USER_PASSWORD))).thenReturn(Optional.of(user));
    Mockito.when(mockUserService.verifyUserPassword(Mockito.eq(ADMIN_USERNAME), Mockito.eq(ADMIN_PASSWORD))).thenReturn(Optional.of(adminUser));
  }

  @Test
  public void testAdminAuth(){
    Authentication authentication = new PreAuthenticatedAuthenticationToken(USER_USERNAME, USER_PASSWORD);
    customAuthenticationProvider.authenticate(authentication);
  }
}
