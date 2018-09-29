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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	public ResponseEntity<?> processRegistrationForm(@Valid User user, BindingResult bindingResult, Locale locale)
	{
		String message = "";
		if (bindingResult.hasErrors())
		{
			message = "Invalid Params" + bindingResult.toString();
			return new ResponseEntity<>(message, HttpStatus.CREATED);
		}
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
		return new ResponseEntity<>(message, HttpStatus.CREATED);
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
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	@GetMapping(value = EndPoint.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> getUserByEmail(@RequestParam("emailId") String emailId)
	{
		Optional<User> optionalUser = userService.getUserByEmail(emailId);
		if (!optionalUser.isPresent())
		{
			throw new UsernameNotFoundException("User for principal not found!");
		}
		User user = optionalUser.get();
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	@PutMapping(value = EndPoint.UPDATE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(@Valid User user, BindingResult bindingResult)
	{
		Optional<User> optionalUser = userService.getUserByEmail(user.getEmail());
		if (!optionalUser.isPresent() || optionalUser.get().getEmail().equalsIgnoreCase(user.getEmail()))
		{
			throw new UsernameNotFoundException("User for principal not found!");
		}
		userService.update(user);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping(value = EndPoint.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
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
		return new ResponseEntity<>(messageSource.getMessage("delete.success", null, locale), HttpStatus.OK);
	}

	@GetMapping(value = EndPoint.Account.COMPANY_ALL, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<User>> getAllCompanyUsers(@PathVariable("companyId") String companyId)
	{
		log.debug("Accessing list of users for companyId: " + companyId);
		return new ResponseEntity<>(userService.getAllCompanyUsers(companyId), HttpStatus.OK);
	}

	@GetMapping(value = EndPoint.Account.ALL, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<User>> getAllUsers(@PathVariable("companyId") String companyId)
	{
		log.debug("Accessing list of users");
		return new ResponseEntity<>(userService.getAll(), HttpStatus.OK);
	}
}
