package net.xcore.usermanagement.userservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("cassandra")
public class VaultCassandraConfig {

  private String localdatacenter;
  private String contactpoints;
  private String keyspace;

  public String getLocaldatacenter() {
    return localdatacenter;
  }

  public void setLocaldatacenter(String localdatacenter) {
    this.localdatacenter = localdatacenter;
  }

  public String getContactpoints() {
    return contactpoints;
  }

  public void setContactpoints(String contactpoints) {
    this.contactpoints = contactpoints;
  }

  public String getKeyspace() {
    return keyspace;
  }

  public void setKeyspace(String keyspace) {
    this.keyspace = keyspace;
  }
}

