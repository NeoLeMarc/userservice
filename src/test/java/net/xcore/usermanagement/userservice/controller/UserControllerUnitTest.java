package net.xcore.usermanagement.userservice.controller;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.util.Optional;
import lombok.val;
import net.xcore.usermanagement.userservice.domain.User;
import net.xcore.usermanagement.userservice.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCrypt;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerUnitTest {

  public static final String TESTUSER_USERNAME = "testuser";
  public static final String TESTUSER_UNHASHED_PASSWORD = "testpasswort";
  public static final String TESTUSER_PASSWORD =  BCrypt.hashpw(TESTUSER_UNHASHED_PASSWORD, BCrypt.gensalt());
  public static final String TESTUSER_ROLE = "testrole";

  @Mock private UserService userServiceMock;
  private User user;
  private UserController controller;

  @Before
  public void initMocks(){
    user = new User();
    user.setUsername(TESTUSER_USERNAME);
    user.setPassword(TESTUSER_PASSWORD);
    user.setRole(TESTUSER_ROLE);

    Optional<User> ouser = Optional.of(user);
    Mockito.when(userServiceMock.getUser(Mockito.eq(TESTUSER_USERNAME))).thenReturn(ouser);

    controller = new UserController(userServiceMock);
  }

  @Test
  public void userControllerCallsUserServiceWhenCallingGetUser(){
    val user = controller.getUser(TESTUSER_USERNAME);
    verify(userServiceMock, times(1)).getUser(TESTUSER_USERNAME);
    assertTrue(user.isPresent());
    assertThat(user.get().getUsername()).isEqualTo(TESTUSER_USERNAME);
  }

  @Test
  public void userControllerCallsUserServiceWhenCallingPostUser(){
    User user = new User();
    controller.postUser(user);
    verify(userServiceMock, times(1)).createUser(user);
  }

  @Test
  public void userControllerCallsUserServiceWhenCallingVerifyPassword(){
    controller.verifyUserPassword(TESTUSER_USERNAME, TESTUSER_UNHASHED_PASSWORD);
    verify(userServiceMock, times(1)).verifyUserPassword(TESTUSER_USERNAME, TESTUSER_UNHASHED_PASSWORD);
  }


}
