package io.zaprit.auth.bo;

import java.util.Collection;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * User class.
 * </p>
 * 
 * @author vaibhav.singh
 */
@Entity
@Table(name = "ZAPRIT_USER")
@Setter
@Getter
public class User implements UserDetails
{
	/**
	 * 
	 */
	private static final long		serialVersionUID	= 8200547333131343057L;
	@Id
	@Column(name = "ID", nullable = false, updatable = false, unique = true)
	private String					id;
	@Email
	@Column(name = "EMAIL", nullable = false, unique = true)
	private String					email;
	@Column(name = "USER_NAME", nullable = false, unique = true)
	private String					username;
	@Email
	@Column(name = "PENDING_EMAIL")
	private String					pendingEmail;
	@Column(name = "PASSWORD", nullable = false)
	private String					password;
	@Column(name = "FIRST_NAME", nullable = false)
	private String					firstName;
	@Column(name = "LAST_NAME", nullable = false)
	private String					lastName;
	@Column(name = "ENABLED", nullable = false)
	private boolean					enabled;
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "USERS_AUTHORITIES", joinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "AUTHORITY_ID", referencedColumnName = "ID"))
	@OrderBy
	@JsonIgnore
	private Collection<Authority>	authorities;
	@Column(name = "ACCOUNT_NON_EXPIRED")
	private boolean					accountNonExpired;
	@Column(name = "ACCOUNT_NON_LOCKED")
	private boolean					accountNonLocked;
	@Column(name = "CREDENTIALS_NON_EXPIRED")
	private boolean					credentialsNonExpired;
	@Column(name = "CONFIRMATION_TOKEN")
	private String					confirmationToken;
	@Column(name = "SIGNUP_TYPE")
	private int						signUpType;
	@Column(name = "COMPANY_ID")
	private String					companyId;

	@PrePersist
	private void ensureId()
	{
		this.setId(UUID.randomUUID().toString().replace("-", ""));
	}

	@Override
	public boolean isAccountNonExpired()
	{
		return accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked()
	{
		return accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired()
	{
		return credentialsNonExpired;
	}

	@Override
	public String getUsername()
	{
		return username;
	}

	@Override
	@JsonIgnore
	public String getPassword()
	{
		return password;
	}

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}

	@Override
	public String toString()
	{
		return "User [id=" + id + ", email=" + email + ", username=" + username + ", pendingEmail=" + pendingEmail + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", enabled=" + enabled + ", accountNonExpired=" + accountNonExpired + ", accountNonLocked="
				+ accountNonLocked + ", credentialsNonExpired=" + credentialsNonExpired + ", confirmationToken=" + confirmationToken
				+ ", signUpType=" + signUpType + ", companyId=" + companyId + "]";
	}
}
