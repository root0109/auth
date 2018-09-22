package io.zaprit.auth.bo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.security.core.GrantedAuthority;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
// @Table(name = "ZAPRIT_AUTHORITY", uniqueConstraints = {
// @UniqueConstraint(columnNames = { "NAME" }) })
@Table(name = "ZAPRIT_AUTHORITY")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Authority implements GrantedAuthority
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 9163729969764436124L;

	@Id
	@Column(name = "ID", nullable = false, updatable = false, unique = true)
	private String				id;

	@Column(name = "NAME")
	private String				name;

	@Override
	public String getAuthority()
	{
		return getName();
	}
}