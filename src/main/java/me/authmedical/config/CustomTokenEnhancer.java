package me.authmedical.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import me.authmedical.model.UserAuth;
import me.authmedical.persistence.UserAuthDao;

@Component
public class CustomTokenEnhancer implements TokenEnhancer {

	@Autowired
	private UserAuthDao userauthDao;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        Object userObj = authentication.getPrincipal();
        String user = "";
        
        if(userObj instanceof String) {
        	user = (String) userObj;
        }
        if(userObj instanceof User) {
        	user = ((User)userObj).getUsername();
        }
        
        UserAuth userAuth = userauthDao.findByLoginAndActiveTrue(user);
        if (user != null) {
			final Map<String, Object> additionalInfo = new HashMap<>();
			if(userAuth.getTenant() != null) {
				additionalInfo.put("tenant", userAuth.getTenant());
			}
			
			((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
		}
		return accessToken;
    }

}
