package net.xcore.usermanagement.userservice.domain;

import lombok.Data;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("user")
@Data
public class User {

  @PrimaryKey
  private String username;
  private String password;
  private String role;
}
