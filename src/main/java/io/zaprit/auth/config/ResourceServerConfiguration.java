package io.zaprit.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import io.zaprit.auth.constants.EndPoint;

@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter
{
	@Value("${security.oauth2.resource.id}")
	private static String			RESOURCE_ID;
	private static final String		SECURED_READ_SCOPE	= "#oauth2.hasScope('read')";
	private static final String		SECURED_WRITE_SCOPE	= "#oauth2.hasScope('write')";
	private static final String[]	SECURED_PATTERN		= new String[] { EndPoint.V1 + "/**", EndPoint.V2 + "/**" };	// "/secured/**";

	@Override
	public void configure(ResourceServerSecurityConfigurer resources)
	{
		resources.resourceId(RESOURCE_ID);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception
	{
		http.requestMatchers().antMatchers(SECURED_PATTERN).and().authorizeRequests().antMatchers(SECURED_PATTERN).access(SECURED_WRITE_SCOPE)
				.anyRequest().access(SECURED_READ_SCOPE);
	}
}
