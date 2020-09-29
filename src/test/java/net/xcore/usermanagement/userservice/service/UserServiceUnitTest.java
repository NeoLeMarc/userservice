package net.xcore.usermanagement.userservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.util.Optional;
import lombok.val;
import net.xcore.usermanagement.userservice.dao.UserRepository;
import net.xcore.usermanagement.userservice.domain.User;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCrypt;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceUnitTest {

  public static final String TESTUSER_USERNAME = "testuser";
  public static final String TESTUSER_UNHASHED_PASSWORD = "testpasswort";
  public static final String TESTUSER_PASSWORD =  BCrypt.hashpw(TESTUSER_UNHASHED_PASSWORD, BCrypt.gensalt());
  public static final String TESTUSER_ROLE = "testrole";
  public static final int BCRYPT_HASH_MIN_LENGTH = 60;

  @Mock
  private UserRepository repositoryMock;

  private User user;
  private UserService userService;

  private final UserService.BCryptHelper bcryptHelperMock = Mockito.mock(UserService.BCryptHelper.class);

  @Before
  public void initMocks() {
    user = new User();
    user.setPassword(TESTUSER_PASSWORD);
    user.setUsername(TESTUSER_USERNAME);
    user.setRole(TESTUSER_ROLE);

    Optional<User> ouser = Optional.of(user);
    Mockito.when(repositoryMock.findById(Mockito.eq(TESTUSER_USERNAME))).thenReturn(ouser);

    userService = new UserService(repositoryMock);
  }

  @Test
  public void userServiceCallsReposiotryWhenCallingGetUser(){
    val user = userService.getUser(TESTUSER_USERNAME);
    verify(repositoryMock, times(1)).findById(TESTUSER_USERNAME);
    assertTrue(user.isPresent());
    AssertionsForClassTypes.assertThat(user.get().getUsername()).isEqualTo(TESTUSER_USERNAME);
  }

  @Test
  public void testUserServiceCallsRepositoryWhenCallingCreateUser(){
    User user = new User();
    userService.createUser(user);
    verify(repositoryMock, times(1)).save(user);
    assertIsBcryptHash(user);
    System.out.println(user);
  }

  private static void assertIsBcryptHash(User user) {
    assertThat(user.getPassword().length()).isGreaterThanOrEqualTo(BCRYPT_HASH_MIN_LENGTH);
    assertThat(user.getPassword()).startsWith("$");
    assertThat(user.getPassword().charAt(3)).isEqualTo('$');
    assertThat(user.getPassword().charAt(6)).isEqualTo('$');
  }

  @Test
  public void testVerifyUserPasswordCallsBcryptWhenUserExists(){
    UserService.setBcryptHelper(bcryptHelperMock);
    userService.verifyUserPassword(TESTUSER_USERNAME, TESTUSER_PASSWORD);
    verify(bcryptHelperMock, Mockito.times(1)).checkpw(TESTUSER_PASSWORD, user.getPassword());
  }

  @Test
  public void testVerifyUserPasswordCorrectlyVerifiesCorrectPassword(){
    UserService.setBcryptHelper(new UserService.BCryptHelper());
    val ret = userService.verifyUserPassword(TESTUSER_USERNAME, TESTUSER_UNHASHED_PASSWORD);
    assertThat(ret).isNotEmpty();
  }

  @Test
  public void testVerifyUserPasswordCorrectlyRejectsWrongPassword(){
    UserService.setBcryptHelper(new UserService.BCryptHelper());
    val ret = userService.verifyUserPassword(TESTUSER_USERNAME, TESTUSER_UNHASHED_PASSWORD + "12");
    assertThat(ret).isEmpty();
  }
}
