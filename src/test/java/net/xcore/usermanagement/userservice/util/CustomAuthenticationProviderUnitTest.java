package net.xcore.usermanagement.userservice.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Optional;
import javax.annotation.PostConstruct;
import net.xcore.usermanagement.userservice.domain.User;
import net.xcore.usermanagement.userservice.service.UserService;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

@RunWith(MockitoJUnitRunner.class)
public class CustomAuthenticationProviderUnitTest {

  public static final String USER_ROLE = "user";
  public static final String USER_USERNAME = "username";
  public static final String USER_PASSWORD = "userpassword";
  public static final String ADMIN_ROLE = "admin";
  public static final String ADMIN_USERNAME = "adminusername";
  public static final String ADMIN_PASSWORD = "adminpassword";
  public static final String WRONG_USERNAME = "wrongusername";
  public static final String WRONG_PASSWORD = "wrongpassword";

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
    adminUser.setRole(ADMIN_ROLE);
    adminUser.setUsername(ADMIN_USERNAME);
    adminUser.setPassword(ADMIN_PASSWORD);

    Mockito.when(mockUserService.getUser(Mockito.eq(USER_USERNAME))).thenReturn(Optional.of(user));
    Mockito.when(mockUserService.getUser(Mockito.eq(ADMIN_USERNAME))).thenReturn(Optional.of(adminUser));
    Mockito.when(mockUserService.verifyUserPassword(Mockito.eq(ADMIN_USERNAME), Mockito.eq(ADMIN_PASSWORD))).thenReturn(Optional.of(adminUser));
    Mockito.when(mockUserService.verifyUserPassword(Mockito.eq(USER_USERNAME), Mockito.eq(USER_PASSWORD))).thenReturn(Optional.of(user));
  }

  @Test
  public void testNormalUserIsVerifiedAndAssignedOnlyUserRole(){
    Authentication authentication = new PreAuthenticatedAuthenticationToken(USER_USERNAME, USER_PASSWORD);
    Authentication token = customAuthenticationProvider.authenticate(authentication);
    Mockito.verify(mockUserService, Mockito.times(1)).verifyUserPassword(USER_USERNAME, USER_PASSWORD);

    assertThat(token.isAuthenticated()).isTrue();
    Collection<? extends GrantedAuthority> authorities = token.getAuthorities();
    assertThat(authorities.size()).isEqualTo(1);
    assertThat(authorities.contains(new SimpleGrantedAuthority("USER")));
  }

  @Test
  public void testAdminUserIsVerifiedAndAssignedAdminAndUserRole(){
    Authentication authentication = new PreAuthenticatedAuthenticationToken(ADMIN_USERNAME, ADMIN_PASSWORD);
    Authentication token = customAuthenticationProvider.authenticate(authentication);
    Mockito.verify(mockUserService, Mockito.times(1)).verifyUserPassword(ADMIN_USERNAME, ADMIN_PASSWORD);

    assertThat(token.isAuthenticated()).isTrue();
    Collection<? extends GrantedAuthority> authorities = token.getAuthorities();
    assertThat(authorities.size()).isEqualTo(2);
    assertThat(authorities.contains(new SimpleGrantedAuthority("USER")));
    assertThat(authorities.contains(new SimpleGrantedAuthority("ADMIN")));
  }

  @Test
  public void testUserWithIncorrectPasswordIsNotVerified(){
    Authentication authentication = new PreAuthenticatedAuthenticationToken(ADMIN_USERNAME, WRONG_PASSWORD);
    Authentication token = customAuthenticationProvider.authenticate(authentication);
    Mockito.verify(mockUserService, Mockito.times(1)).verifyUserPassword(ADMIN_USERNAME, WRONG_PASSWORD);
    assertThat(token).isNull();
  }

  @Test
  public void testNotExistingUserWitCorrectPasswordIsNotVerified(){
    Authentication authentication = new PreAuthenticatedAuthenticationToken(WRONG_USERNAME, ADMIN_PASSWORD);
    Authentication token = customAuthenticationProvider.authenticate(authentication);
    Mockito.verify(mockUserService, Mockito.times(1)).verifyUserPassword(WRONG_USERNAME, ADMIN_PASSWORD);
    assertThat(token).isNull();
  }
}
