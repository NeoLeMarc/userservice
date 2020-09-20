package net.xcore.usermanagement.userservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.util.Optional;
import javax.annotation.PostConstruct;
import lombok.val;
import net.xcore.usermanagement.userservice.dao.UserRepository;
import net.xcore.usermanagement.userservice.domain.User;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;

@RunWith(MockitoJUnitRunner.class)
public class UserserviceApplicationUnitTest {

  public static final String TESTUSER_USERNAME = "testuser";
  public static final String TESTUSER_PASSWORT = "testpasswort";
  public static final String TESTUSER_ROLE = "testrole";
  @Mock
  private UserRepository repository;

  @Before
  public void initMocks() {
    User user = new User();
    user.setPassword(TESTUSER_PASSWORT);
    user.setUsername(TESTUSER_USERNAME);
    user.setRole(TESTUSER_ROLE);

    Optional<User> ouser = Optional.of(user);
    Mockito.when(repository.findById(Mockito.eq(TESTUSER_USERNAME))).thenReturn(ouser);
  }

  @Test
  public void testApplicationCallsRepositoryWhenCallingGetUser(){
    UserserviceApplication application = new UserserviceApplication(repository);
    val user = application.getUser(TESTUSER_USERNAME);
    verify(repository, times(1)).findById(TESTUSER_USERNAME);
    assertTrue(user.isPresent());
    assertThat(user.get().getUsername()).isEqualTo(TESTUSER_USERNAME);
  }
}
