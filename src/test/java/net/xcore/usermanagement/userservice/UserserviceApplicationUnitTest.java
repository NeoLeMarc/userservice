package net.xcore.usermanagement.userservice;

import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.util.Optional;
import net.xcore.usermanagement.userservice.dao.UserRepository;
import net.xcore.usermanagement.userservice.domain.User;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

public class UserserviceApplicationUnitTest {

  @Mock
  private UserRepository repository;

  @Test
  public void firstTest(){
    repository = Mockito.mock(UserRepository.class);
    UserserviceApplication application = new UserserviceApplication(repository);
    Optional<User> empty = Optional.empty();
    Mockito.when(repository.findById(Mockito.anyString())).thenReturn(empty);
    application.getUser("testuser");
    verify(repository, times(1)).findById("testuser");
  }

}
