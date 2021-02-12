package me.authmedical.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

@Configuration
@EnableAuthorizationServer
public class OAuthSecurityConfig extends AuthorizationServerConfigurerAdapter {
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private AuthenticationManagerCustom authenticationManagerCustom;
	
	@Autowired
	private AppUserDetailsService userDetailService;
	
	@Autowired
	private CustomTokenEnhancer tokenEnhancer;
	
	@Override 
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception { 
		oauthServer
		.tokenKeyAccess("permitAll()")
		.checkTokenAccess("permitAll()");
	}

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new SHA512PasswordEncoder();
    }

	
    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
        clients.jdbc(dataSource);
    }
	
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
    	endpoints
    	.tokenStore(tokenStore())
    	.authenticationManager(authenticationManagerCustom)
    	.userDetailsService(userDetailService)
    	.tokenEnhancer(tokenEnhancer());
    }
    
    @Bean
    public TokenStore tokenStore() {
        return new JdbcTokenStore(dataSource);
    }    

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return tokenEnhancer;
    }    
}
