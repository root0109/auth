package io.zaprit.auth;

import java.util.Locale;
import java.util.Properties;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import com.zaxxer.hikari.HikariDataSource;

@SpringBootApplication
@ServletComponentScan
public class AuthApplication
{
	@Autowired
	private AuthProperties properties;

	public static void main(String[] args)
	{
		SpringApplication.run(AuthApplication.class, args);
	}

	@Bean
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource dataSource()
	{
		return DataSourceBuilder.create().type(HikariDataSource.class).build();
	}

	@Bean
	public PasswordEncoder oauthClientPasswordEncoder()
	{
		return new BCryptPasswordEncoder(4);
	}

	@Bean
	public PasswordEncoder userPasswordEncoder()
	{
		return new BCryptPasswordEncoder(8);
	}

	@Bean
	public JavaMailSender getJavaMailSender()
	{
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(properties.getHost());
		mailSender.setUsername(properties.getUsername());
		mailSender.setPassword(properties.getPassword());
		mailSender.setProtocol(properties.getMailTransferProtocol());
		mailSender.setDefaultEncoding(properties.getDefaultEncoding());

		Properties props = mailSender.getJavaMailProperties();
		props.setProperty("mail.smtp.auth", properties.isSMTPAuth() + "");
		// props.put("mail.transport.protocol", "smtp");
		props.put("mail.transfer.protocol", "smtp");
		props.put("mail.debug", "true");
		return mailSender;
	}

	@Bean
	public LocaleResolver localeResolver()
	{
		SessionLocaleResolver slr = new SessionLocaleResolver();
		slr.setDefaultLocale(Locale.US);
		return slr;
	}

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor()
	{
		LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
		lci.setParamName("lang");
		return lci;
	}
}
