package io.zaprit.auth.bo;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * @author vaibhav.singh
 */
@Entity
@Table(name = "ZAPRIT_COMPANY")
@Getter
@Setter
public class Company implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -3763699439388311704L;
	@Id
	@Column(name = "ID", nullable = false, updatable = false, unique = true)
	private String				id;
	@Column(name = "NAME", nullable = false, unique = true)
	private String				companyName			= null;
}