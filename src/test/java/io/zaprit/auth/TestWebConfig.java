/**
 * 
 */
package io.zaprit.auth;

import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author vaibhav.singh
 */
@Configuration
@EnableConfigurationProperties
@TestPropertySource(locations = "classpath:application-test.properties")
@ComponentScan(basePackages = { "io.zaprit.auth" })
public class TestWebConfig implements WebMvcConfigurer, ApplicationContextAware
{
	@SuppressWarnings("unused")
	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
	{
		this.applicationContext = applicationContext;
	}
}