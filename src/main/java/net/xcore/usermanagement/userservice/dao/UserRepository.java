package net.xcore.usermanagement.userservice.dao;

import net.xcore.usermanagement.userservice.domain.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String> {

}
