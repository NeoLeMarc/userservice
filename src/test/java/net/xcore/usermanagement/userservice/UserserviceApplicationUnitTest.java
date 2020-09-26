package net.xcore.usermanagement.userservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import net.xcore.usermanagement.userservice.UserserviceApplication.FilesHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UserserviceApplicationUnitTest {

  public static final String SPRING_BOOT_PROPERTY_LOCATION = "/does/not/exist.properties";

  @Mock
  private UserserviceApplication.Runner runnerMock;

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
    UserserviceApplication.setRunner(runnerMock);
    UserserviceApplication.setFilesHelper(filesHelperWrapperMock);
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
}
