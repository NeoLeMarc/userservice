package net.xcore.usermanagement.userservice;

import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RequiredArgsConstructor
public class UserserviceApplication {

  private static final Logger logger = LoggerFactory.getLogger(UserserviceApplication.class);

  static class Runner {
    void run(String[] args) {
      SpringApplication.run(UserserviceApplication.class, args);
    }
  }

  @Setter private static Runner runner = new Runner();

  static class FilesHelper {
    public boolean doesFileExist(String path){
      return Files.exists(Paths.get(path));
    }
  }

  @Setter private static FilesHelper filesHelper = new FilesHelper();

  public static void main(String[] args) {
    if (args != null) {
      logger.info("main({})", (Object) args);
    } else {
      logger.info("main() called without arguments");
    }

    String propertyPath = null;
    for(String arg : args) {
      if(arg != null && !arg.isEmpty() && arg.charAt(0) != '-'){
        propertyPath = arg;
        break;
      }
    }

    if (propertyPath != null && !propertyPath.isEmpty()) {
      logger.info("Loading bootstrap properties from {}", propertyPath);
      loadBootstrapConfiguration(propertyPath);
    } else {
      loadBootstrapConfiguration("/home/marcel/properties/userservice/dev.properties");
    }
    runner.run(args);
  }

  private static void loadBootstrapConfiguration(String path) {
    if (filesHelper.doesFileExist(path)) {
      logger.info("Found bootstrap properties {} - updating spring.cloud.bootstrap.location", path);
      System.setProperty("spring.cloud.bootstrap.location", path);
    } else {
      logger.info("Failed to load bootstrap properties");
    }
  }
}
