package me.authmedical.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import me.authmedical.model.UserAuth;
import me.authmedical.persistence.UserAuthDao;

@Component
public class AppUserDetailsService implements UserDetailsService {

	@Autowired
	private UserAuthDao userauthDao;
	
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        UserAuth user = userauthDao.findByLoginAndActiveTrue(s);

        if(user == null) {
            throw new UsernameNotFoundException(String.format("The username %s doesn't exist", s));
        }

		List<GrantedAuthority> authorities = new ArrayList<>();
        user.getRolesArray().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role));
        });

        return new User(user.getLogin(), user.getPassword(), authorities);
    }
}
