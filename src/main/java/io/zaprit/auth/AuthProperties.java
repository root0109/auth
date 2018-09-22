/**
 * 
 */
package io.zaprit.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;

/**
 * @author vaibhav.singh
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties("auth")
public class AuthProperties
{
	private String	redirectionUrl;
	private String	emailFrom;
	private String	corsAllowedOrigins;

	private String	host;
	private String	username;
	private String	password;
	private String	port;
	private String	defaultEncoding;
	private String	mailTransferProtocol;
	private boolean	isSMTPAuth;
}