package me.authmedical.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;
import org.springframework.security.core.token.Sha512DigestUtils;

@Entity
public class UserAuth extends BaseEntity{
	
	@Id
	private String login;
	
	@Column(unique=true)
	private String email;
	
	@Enumerated(EnumType.STRING)
	private EnumStatus status;
	
	private String password;
	
	private String roles;
	
	private String tenant;
	
	private Boolean active = true;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date date = new Date();
	
	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb")
	private Map<String, Object> extra = new HashMap<String, Object>();
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date passDate = new Date();

	public void setPasswordRaw(String password) {
		if(password != null) {
			this.password = Sha512DigestUtils.shaHex(password);
		}else {
			throw new AuthException("password required", me.authmedical.model.AuthException.Type.PASS_REQUIRED);
		}
	}

	public List<String> getRolesArray() {
		List<String> result = new ArrayList<>();
		if(roles != null) {
			String [] r = roles.split(",");
			for (int i = 0; i < r.length; i++) {
				result.add(r[i].trim().toUpperCase());
			}
		}
		return result;
	}
	
	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public String getTenant() {
		return tenant;
	}

	public void setTenant(String tenant) {
		this.tenant = tenant;
	}
	
	public Map<String, Object> getExtra() {
		return extra;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public EnumStatus getStatus() {
		return status;
	}

	public void setStatus(EnumStatus status) {
		this.status = status;
	}

	public Date getPassDate() {
		return passDate;
	}

	public void setPassDate(Date passDate) {
		this.passDate = passDate;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
}
