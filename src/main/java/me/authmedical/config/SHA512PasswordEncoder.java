package me.authmedical.config;

import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

public class SHA512PasswordEncoder implements PasswordEncoder {

	@Override
	public String encode(CharSequence rawPassword) {
		return Sha512DigestUtils.shaHex(rawPassword.toString());
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		return Sha512DigestUtils.shaHex(rawPassword.toString()).equals(encodedPassword);
	}

}
