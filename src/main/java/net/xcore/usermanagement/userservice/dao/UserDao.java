package net.xcore.usermanagement.userservice.dao;

import net.xcore.usermanagement.userservice.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class UserDao {

  Logger logger = LoggerFactory.getLogger(UserDao.class);

  @Autowired
  private CassandraTemplate cassandraTemplate;

}
