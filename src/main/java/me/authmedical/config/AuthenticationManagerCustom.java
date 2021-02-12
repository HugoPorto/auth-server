package me.authmedical.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import me.authmedical.model.UserAuth;
import me.authmedical.persistence.UserAuthDao;

@Component
public class AuthenticationManagerCustom implements AuthenticationManager{

	@Autowired
	private UserAuthDao userauthDao;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		UserAuth user = userauthDao.findByLoginAndActiveTrue(authentication.getName());
		String password = passwordEncoder.encode(authentication.getCredentials().toString());
		
		if(user != null && user.getPassword().equals(password)) {
			List<GrantedAuthority> authorities = new ArrayList<>();
	        user.getRolesArray().forEach(role -> {
	            authorities.add(new SimpleGrantedAuthority(role));
	        });
			Authentication auth = new UsernamePasswordAuthenticationToken(user.getLogin(), "***", authorities);
			return auth;
		}else {
			throw new AuthenticationExceptionImpl("Username or password not found!");
		}
	}

	private class AuthenticationExceptionImpl extends AuthenticationException{

		private static final long serialVersionUID = 1L;

		public AuthenticationExceptionImpl(String msg) {
			super(msg);
		}
		
	}
}
