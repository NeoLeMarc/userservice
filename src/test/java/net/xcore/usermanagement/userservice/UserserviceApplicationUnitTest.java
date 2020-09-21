package net.xcore.usermanagement.userservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.util.Optional;
import lombok.val;
import net.xcore.usermanagement.userservice.UserserviceApplication.FilesHelper;
import net.xcore.usermanagement.userservice.dao.UserRepository;
import net.xcore.usermanagement.userservice.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UserserviceApplicationUnitTest {

  public static final String TESTUSER_USERNAME = "testuser";
  public static final String TESTUSER_PASSWORT = "testpasswort";
  public static final String TESTUSER_ROLE = "testrole";
  public static final String SPRING_BOOT_PROPERTY_LOCATION = "/does/not/exist.properties";
  @Mock
  private UserRepository repositoryMock;

  @Mock
  private UserserviceApplication.Runner runnerMock;
  private UserserviceApplication application;

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

  @Before
  public void initMocks() {
    User user = new User();
    user.setPassword(TESTUSER_PASSWORT);
    user.setUsername(TESTUSER_USERNAME);
    user.setRole(TESTUSER_ROLE);

    Optional<User> ouser = Optional.of(user);
    Mockito.when(repositoryMock.findById(Mockito.eq(TESTUSER_USERNAME))).thenReturn(ouser);

    UserserviceApplication.setRunner(runnerMock);
    UserserviceApplication.setFilesHelper(filesHelperWrapperMock);
    application = new UserserviceApplication(repositoryMock);
  }

  @Test
  public void testApplicationCallsRepositoryWhenCallingGetUser(){
    val user = application.getUser(TESTUSER_USERNAME);
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
    application.setUser(user);
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
}
