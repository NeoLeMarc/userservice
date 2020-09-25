package net.xcore.usermanagement.userservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.util.Optional;
import lombok.val;
import net.xcore.usermanagement.userservice.UserserviceApplication.FilesHelper;
import net.xcore.usermanagement.userservice.controller.UserController;
import net.xcore.usermanagement.userservice.dao.UserRepository;
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
public class UserserviceApplicationAndControllerUnitTest {

  public static final String TESTUSER_USERNAME = "testuser";
  public static final String TESTUSER_UNHASHED_PASSWORD = "testpasswort";
  public static final String TESTUSER_PASSWORD =  BCrypt.hashpw(TESTUSER_UNHASHED_PASSWORD, BCrypt.gensalt());
  public static final String TESTUSER_ROLE = "testrole";
  public static final String SPRING_BOOT_PROPERTY_LOCATION = "/does/not/exist.properties";
  @Mock
  private UserRepository repositoryMock;

  @Mock
  private UserserviceApplication.Runner runnerMock;
  private UserController controller;
  private User user;
  private UserService userService;

  @SuppressWarnings("NewClassNamingConvention")
  private static class FilesHelperWrapperMock extends FilesHelper {
    boolean wasCalled;
    int callCount;

    @Override
    public boolean doesFileExist(String path) {
      wasCalled = true;
      callCount++;
      return true;
    }
  }

  private final FilesHelperWrapperMock filesHelperWrapperMock = new FilesHelperWrapperMock();
  private final UserService.BCryptHelper bcryptHelperMock = Mockito.mock(UserService.BCryptHelper.class);

  @Before
  public void initMocks() {
    user = new User();
    user.setPassword(TESTUSER_PASSWORD);
    user.setUsername(TESTUSER_USERNAME);
    user.setRole(TESTUSER_ROLE);

    Optional<User> ouser = Optional.of(user);
    Mockito.when(repositoryMock.findById(Mockito.eq(TESTUSER_USERNAME))).thenReturn(ouser);

    UserserviceApplication.setRunner(runnerMock);
    UserserviceApplication.setFilesHelper(filesHelperWrapperMock);

    userService = new UserService(repositoryMock);
    controller = new UserController(userService);
  }

  @Test
  public void testApplicationCallsRepositoryWhenCallingGetUser(){
    val user = controller.getUser(TESTUSER_USERNAME);
    verify(repositoryMock, times(1)).findById(TESTUSER_USERNAME);
    assertTrue(user.isPresent());
    assertThat(user.get().getUsername()).isEqualTo(TESTUSER_USERNAME);
  }

  @Test
  public void testPublicStaticVoidMainWithoutArguments(){
    String[] args = new String[0];
    UserserviceApplication.main(args);
    verify(runnerMock, times(1)).run(args);
  }

  @Test
  public void testPublicStaticVoidMainWithArgumentsSetsPropertiesLocationIfFileExists(){
    String[] args = {SPRING_BOOT_PROPERTY_LOCATION};
    UserserviceApplication.main(args);
    assertThat(filesHelperWrapperMock.wasCalled).isTrue();
    assertThat(filesHelperWrapperMock.callCount).isEqualTo(1);
    assertThat(System.getProperty("spring.cloud.bootstrap.location")).isEqualTo(
        SPRING_BOOT_PROPERTY_LOCATION);
  }

  @Test
  public void testApplicationCallsRepositoryWhenCallingSetUser(){
    User user = new User();
    controller.postUser(user);
    verify(repositoryMock, times(1)).save(user);
    assertIsBcryptHash(user);
    System.out.println(user);

  }

  private static void assertIsBcryptHash(User user) {
    assertThat(user.getPassword().length()).isGreaterThanOrEqualTo(60);
    assertThat(user.getPassword()).startsWith("$");
    assertThat(user.getPassword().charAt(3)).isEqualTo('$');
    assertThat(user.getPassword().charAt(6)).isEqualTo('$');
  }

  @Test
  public void testVerifyUserPasswordCallsBcryptWhenUserExists(){
    UserService.setBcryptHelper(bcryptHelperMock);
    controller.verifyUserPassword(TESTUSER_USERNAME, TESTUSER_PASSWORD);
    verify(bcryptHelperMock, Mockito.times(1)).checkpw(TESTUSER_PASSWORD, user.getPassword());
  }

  @Test
  public void testVerifyUserPasswordCorrectlyVerifiesCorrectPassword(){
    UserService.setBcryptHelper(new UserService.BCryptHelper());
    val ret = controller.verifyUserPassword(TESTUSER_USERNAME, TESTUSER_UNHASHED_PASSWORD);
    assertThat(ret).isNotNull();
  }

  @Test
  public void testVerifyUserPasswordCorrectlyRejectsWrongPassword(){
    UserService.setBcryptHelper(new UserService.BCryptHelper());
    val ret = controller.verifyUserPassword(TESTUSER_USERNAME, TESTUSER_UNHASHED_PASSWORD + "12");
    assertThat(ret).isNull();
  }
}
