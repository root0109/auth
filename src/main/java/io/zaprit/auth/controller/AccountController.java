package io.zaprit.auth.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import io.zaprit.auth.bo.User;
import io.zaprit.auth.constants.EndPoint;
import io.zaprit.auth.constants.EndPoint.Account;
import io.zaprit.auth.service.AccountService;
import io.zaprit.auth.service.UserService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author vaibhav.singh
 */
@Slf4j
@Controller
@RequestMapping(EndPoint.Account.V1)
public class AccountController
{
	@Autowired
	private AccountService	accountService;

	@Autowired
	private UserService		userService;
	@Autowired
	private MessageSource	messageSource;

	@PostMapping(value = Account.REGISTER, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> processRegistrationForm(ModelAndView modelAndView, @Valid User user, Locale locale)
	{
		String message = "";
		if (accountService.isUserRegistered(user))
		{
			message = messageSource.getMessage("registration.emailExists", new Object[] { user.getEmail() }, locale);
		}
		else
		{
			accountService.registerUser(user, locale);
			message = messageSource.getMessage("registration.confirmationEmail", new Object[] { user.getEmail() }, locale);
		}
		log.info("Account Registration recieved: " + user.toString());
		return new ResponseEntity<>(message, HttpStatus.OK);
	}

	@PostMapping(value = Account.CONFIRM, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> processConfirmationForm(@RequestParam("token") String token, @RequestParam("password") String password,
			Locale locale)
	{
		Map<String, Object> data = new HashMap<>();
		data.put("token", token);
		Optional<User> optionalUser = accountService.getUserForToken(token);
		if (!optionalUser.isPresent())
		{
			log.debug("No user found for this token: {}", token);
			data.put("message", messageSource.getMessage("registration.invalidToken", null, locale));
		}
		else if (!password.equals(optionalUser.get().getPassword()))
		{
			log.debug("Passwords are not matching!");
			data.put("message", messageSource.getMessage("password.notMatching", null, locale));
		}
		else
		{
			accountService.confirmUser(token, password);
			data.put("message", messageSource.getMessage("registration.passwordSuccess", null, locale));
		}
		return new ResponseEntity<>(data, HttpStatus.OK);
	}

	@GetMapping(value = EndPoint.GET_ID, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> getUser(@PathVariable("id") String id)
	{
		Optional<User> optionalUser = userService.getUser(id);
		if (!optionalUser.isPresent())
		{
			throw new UsernameNotFoundException("User for principal not found!");
		}
		User user = optionalUser.get();
		// override email (in Principal username) with ID - only used at this endpoint
		user.setEmail(user.getId());
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	@GetMapping(value = EndPoint.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> deleteUser(@PathVariable("id") String id, Locale locale)
	{
		Optional<User> optionalUser = userService.getUser(id);
		if (!optionalUser.isPresent())
		{
			optionalUser = userService.getUserByEmail(id);
			if (!optionalUser.isPresent())
			{
				throw new UsernameNotFoundException("User for principal not found!");
			}
		}
		userService.delete(optionalUser.get());
		return new ResponseEntity<>(messageSource.getMessage("delete.warning", null, locale), HttpStatus.OK);
	}

	@GetMapping(value = EndPoint.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<User> getAllUsers()
	{
		log.debug("Accessing list of users");
		return userService.getAll();
	}

}
