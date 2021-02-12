package me.authmedical.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import me.authmedical.model.UserAuth;

@Repository
public interface UserAuthDao extends CrudRepository<UserAuth, String>{

	UserAuth findByLoginAndActiveTrue(String login);
	
}
