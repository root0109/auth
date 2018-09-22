package io.zaprit.auth.service.impl;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import io.zaprit.auth.AuthProperties;
import io.zaprit.auth.bo.User;
import io.zaprit.auth.service.AccountService;
import io.zaprit.auth.service.EmailService;
import io.zaprit.auth.service.UserService;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * AccountServiceImpl class.
 * </p>
 * 
 * @author vaibhav.singh
 */
@Slf4j
@Service("accountService")
public class AccountServiceImpl implements AccountService
{
	@Autowired
	private UserService				userService;
	@Autowired
	private EmailService			emailService;
	@Autowired
	private BCryptPasswordEncoder	userPasswordEncoder;
	@Autowired
	private AuthProperties			properties;
	@Autowired
	private MessageSource			messages;

	@Override
	@PreAuthorize("hasAuthority('USER_CREATE')")
	public void registerUser(User user, Locale locale)
	{
		log.debug("Registering new user...");
		Optional<User> loaded = userService.getUserByEmail(user.getUsername());
		if (loaded.isPresent())
		{
			user = loaded.get();
		}
		// Disable user until they click on confirmation link in email
		user.setEnabled(false);
		// Generate random 36-character string token for confirmation link
		user.setConfirmationToken(UUID.randomUUID().toString().replace("-", ""));
		if (user.getCompanyId() == null)
		{
			String companyId = UUID.randomUUID().toString();
			user.setCompanyId(companyId);
		}
		userService.save(user);
		log.debug("Sending confirmation token to the selected email: {}", user.getEmail());
		String message = messages.getMessage("email.registration", null, locale);
		String link = properties.getRedirectionUrl() + "/confirm?token=" + user.getConfirmationToken() + "&password=" + user.getPassword();
		emailService.prepareAndSend(user.getEmail(), properties.getEmailFrom(), "Registration confirmation", message, link);
	}

	@Override
	@PreAuthorize("hasAuthority('COMPANY_READ') and hasAuthority('USER_UPDATE')")
	public void confirmUser(String token, String password)
	{
		log.debug("Confirming user with token {}", token);
		Optional<User> optionalUser = userService.getUserByConfirmationToken(token);
		if (!optionalUser.isPresent())
		{
			throw new UsernameNotFoundException("NO user found for token!");
		}
		User user = optionalUser.get();
		user.setPassword(userPasswordEncoder.encode((CharSequence) password));
		user.setEnabled(true);
		user.setConfirmationToken("");
		userService.save(user);
	}

	@Override
	@PreAuthorize("hasAuthority('COMPANY_READ') or hasAuthority('USER_READ')")
	public boolean isUserRegistered(User user)
	{
		Optional<User> loaded = userService.getUserByEmail(user.getEmail());
		if (loaded.isPresent())
		{
			return loaded.get().isEnabled();
		}
		return false;
	}

	@Override
	@PreAuthorize("hasAuthority('COMPANY_READ') and hasAuthority('USER_READ')")
	public Optional<User> getUserForToken(String token)
	{
		return userService.getUserByConfirmationToken(token);
	}

	@Override
	@PreAuthorize("hasAuthority('COMPANY_READ') and hasAuthority('USER_UPDATE')")
	public void resetPassword(User user, Locale locale)
	{
		log.debug("Resetting password for user: {}", user.getEmail());
		Optional<User> optionalUser = userService.getUserByEmail(user.getEmail());
		if (!optionalUser.isPresent())
		{
			log.error("Cannot find user with this e-mail!");
			return;
		}
		user = optionalUser.get();
		user.setConfirmationToken(UUID.randomUUID().toString().replace("-", ""));
		log.debug("Sending confirmation token to the selected email: {}", user.getEmail());
		String message = messages.getMessage("email.resetPassword", null, locale);
		String link = properties.getRedirectionUrl() + "/confirmRedirect?token=" + user.getConfirmationToken();
		emailService.prepareAndSend(user.getEmail(), properties.getEmailFrom(), "Password reset", message, link);
		userService.save(user);
	}

	@Override
	@PreAuthorize("hasAuthority('COMPANY_READ') and hasAuthority('USER_UPDATE')")
	public boolean changePassword(String email, String oldPassword, String newPassword)
	{
		log.debug("Changing password for user: {}", email);
		Optional<User> optionalUser = userService.getUserByEmail(email);
		if (!optionalUser.isPresent())
		{
			log.error("Cannot find user with this e-mail!");
			return false;
		}
		User user = optionalUser.get();
		boolean passwordMatch = userPasswordEncoder.matches(oldPassword, user.getPassword());
		log.debug("Current password matches: {}", passwordMatch);
		if (passwordMatch)
		{
			user.setPassword(userPasswordEncoder.encode(newPassword));
			userService.save(user);
			return true;
		}
		return false;
	}

	@Override
	@PreAuthorize("hasAuthority('COMPANY_READ') and hasAuthority('USER_UPDATE')")
	public boolean changeEmail(String email, String password, String newEmail, Locale locale)
	{
		log.debug("Changing e-mail for user: {}", email);
		Optional<User> optionalUser = userService.getUserByEmail(email);
		if (!optionalUser.isPresent())
		{
			log.error("Cannot find user with this e-mail!");
			return false;
		}
		User user = optionalUser.get();
		if (userService.getUserByEmail(newEmail).isPresent())
		{
			log.warn("User with email {} already exists.", newEmail);
			return false;
		}
		boolean passwordMatch = userPasswordEncoder.matches(password, user.getPassword());

		log.debug("Current password matches: {}", passwordMatch);
		if (passwordMatch)
		{
			user.setPendingEmail(newEmail);
			user.setConfirmationToken(UUID.randomUUID().toString().replace("-", ""));
			log.debug("Sending verification token {} to the selected email: {}", user.getConfirmationToken(), newEmail);
			String message = messages.getMessage("email.verification", null, locale);
			String link = properties.getRedirectionUrl() + "/verifyEmail?token=" + user.getConfirmationToken();
			emailService.prepareAndSend(newEmail, properties.getEmailFrom(), "E-mail change", message, link);
			userService.save(user);
			return true;
		}
		return false;
	}

	@Override
	@PreAuthorize("hasAuthority('COMPANY_READ') and hasAuthority('USER_UPDATE')")
	public void verifyEmail(User user)
	{
		log.debug("Verifying e-mail {}", user.getPendingEmail());
		user.setEmail(user.getPendingEmail());
		user.setPendingEmail(null);
		user.setConfirmationToken("");
		userService.save(user);
	}
}
