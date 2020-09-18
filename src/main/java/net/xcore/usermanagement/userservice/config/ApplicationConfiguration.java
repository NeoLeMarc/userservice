package net.xcore.usermanagement.userservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.CqlSessionFactoryBean;
import org.springframework.data.cassandra.core.AsyncCassandraTemplate;
import org.springframework.data.cassandra.core.cql.CqlTemplate;
import org.springframework.data.cassandra.core.cql.session.DefaultSessionFactory;

@EnableConfigurationProperties(VaultCassandraConfig.class)
@Configuration
public class ApplicationConfiguration {

  @Autowired
  private final VaultCassandraConfig config;

  public ApplicationConfiguration(VaultCassandraConfig config) {
    this.config = config;
  }

  @Bean
  public CqlSessionFactoryBean session() {
    CqlSessionFactoryBean session = new CqlSessionFactoryBean();
    session.setContactPoints(config.getContactpoints());
    session.setKeyspaceName(config.getKeyspace());
    session.setLocalDatacenter(config.getLocaldatacenter());
    return session;
  }

  @Bean
  public CqlTemplate cqlTemplate() {
    CqlTemplate template = new CqlTemplate();
    template.setSessionFactory(new DefaultSessionFactory(session().getObject()));
    return template;
  }

  @Bean
  public AsyncCassandraTemplate asyncCassandraTemplate(){
    AsyncCassandraTemplate template = new AsyncCassandraTemplate(session().getObject());
    return template;
  }
}
