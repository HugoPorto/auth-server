package me.authmedical.controller;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.security.RolesAllowed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.authmedical.dto.PasswordChange;
import me.authmedical.dto.User;
import me.authmedical.model.AuthException;
import me.authmedical.model.AuthException.Type;
import me.authmedical.model.UserAuth;
import me.authmedical.persistence.UserAuthDao;

@RestController
@RequestMapping("/manager")
public class AuthManagerRest {

	@Autowired
	private UserAuthDao userAuthDao;

	public static final Pattern VALID_EMAIL_ADDRESS_REGEX =  Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

	//https://www.callicoder.com/spring-boot-spring-security-jwt-mysql-react-app-part-2/
	@GetMapping("/ok")
	@RolesAllowed("ROLE_ADMIN")
	public String ok(Principal principal) {
		return String.format("OK|%s|%s", principal.getName(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	}

	@GetMapping("/{login}")
	@RolesAllowed("ROLE_ADMIN")
	public User get(@PathVariable String login) {
		Optional<UserAuth> opt = userAuthDao.findById(login); 
		return opt.isPresent() ? new User(opt.get()) : null;
	}

	public static boolean validate(String emailStr) {
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
		return matcher.find();
	}

	@PostMapping
	@RolesAllowed("ROLE_ADMIN")
	public Map<String, Object> post(@RequestBody User user, Principal principal) {
		Map<String, Object> result = new HashMap<String, Object>();
		Optional<UserAuth> opt = userAuthDao.findById(user.getLogin().toLowerCase());
		if(opt.isPresent()) {
			throw new AuthException(String.format("%s already exists", user.getLogin()), Type.EMAIL_ALREADY_EXISTS);
		}else if(user.getLogin() != null && validate(user.getLogin())){
			Optional<UserAuth> admin = userAuthDao.findById(principal.getName());

			UserAuth userAuth = new UserAuth();
			userAuth.setPasswordRaw(user.getPassword());
			userAuth.setLogin(user.getLogin().toLowerCase());
			userAuth.setEmail(user.getLogin().toLowerCase());
			StringBuffer roles = new StringBuffer();
			boolean first = true;
			for(String s : user.getRoles()) {
				if(!first) {
					roles.append(",");
				}
				roles.append(s.toUpperCase());
			}
			userAuth.setRoles(roles.toString());
			userAuth.setTenant(admin.get().getTenant());
			
			userAuthDao.save(userAuth);
			result.put("result", "OK");
			result.put("message", "user " + user.getLogin() + " created successfully");
			return result;
		}else {
			throw new AuthException(String.format("%s: invalid email", user.getLogin()), Type.INVALID_EMAIL);
		}
	}

	@PostMapping("/change")
	@RolesAllowed("ROLE_ADMIN")
	public Map<String, Object> change(@RequestBody PasswordChange change, Principal principal) {
		Map<String, Object> result = new HashMap<String, Object>();
		Optional<UserAuth> opt = userAuthDao.findById(change.getLogin().toLowerCase());
		if(opt.isPresent()) {
			UserAuth user = opt.get();
			String actualHash = user.getPassword();
			String sendHash = Sha512DigestUtils.shaHex(change.getActual());
			if(actualHash.equals(sendHash)) {
				user.setPasswordRaw(change.getNewPass());
				user.setPassDate(new Date());
				result.put("result", "OK");
				result.put("message", "password for user " + user.getLogin() + " changed successfully");
				userAuthDao.save(user);
				return result;
			}else {
				throw new AuthException("wrong password has been informed", Type.WRONG_PASS);
			}
		}else {
			throw new AuthException("user not found", Type.USER_NOT_FOUND);
		}
	}

	@PostMapping("/changeWitoutPass")
	@RolesAllowed("ROLE_ADMIN")
	public Map<String, Object> changeWithoutPass(@RequestBody PasswordChange change, Principal principal) {
		Map<String, Object> result = new HashMap<String, Object>();
		Optional<UserAuth> opt = userAuthDao.findById(change.getLogin().toLowerCase());
		if(opt.isPresent()) {
			UserAuth user = opt.get();
			user.setPasswordRaw(change.getNewPass());
			result.put("result", "OK");
			result.put("message", "password for user " + user.getLogin() + " changed successfully");
			userAuthDao.save(user);
			user.setPassDate(new Date());
			return result;
		}else {
			throw new AuthException("user not found", Type.USER_NOT_FOUND);
		}
	}

}
