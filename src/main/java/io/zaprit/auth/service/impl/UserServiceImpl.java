package io.zaprit.auth.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.zaprit.auth.bo.User;
import io.zaprit.auth.dao.UserDao;
import io.zaprit.auth.service.UserService;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * UserService class. Needs to implement UserDetailsService since it is injected
 * into OAuth2 configuration.
 * </p>
 * 
 * @author vaibhav.singh
 */
@Slf4j
@Service("userService")
public class UserServiceImpl implements UserService
{
	@Autowired
	private UserDao userDao;

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username)
	{
		log.debug("Loading user details for username: {}", username);
		Optional<User> optionalUser = userDao.getUserByUserName(username);
		if (!optionalUser.isPresent())
		{
			optionalUser = getUserByEmail(username);
			if (!optionalUser.isPresent())
			{
				optionalUser = getUserByEmail(username);
				log.error("User not found!");
				throw new UsernameNotFoundException("Username not found.");
			}
		}
		return optionalUser.get();
	}

	@Override
	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('COMPANY_READ') or hasAuthority('USER_READ')")
	public Optional<User> getUserByEmail(String email)
	{
		log.debug("Getting user for email: {}", email);
		Optional<User> optionalUser = userDao.getUserByEmail(email);
		return optionalUser;
	}

	@Override
	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('USER_READ')")
	public Optional<User> getUserByConfirmationToken(String confirmationToken)
	{
		log.debug("Getting user by confirmation token: {}", confirmationToken);
		Optional<User> optionalUser = userDao.getUserByConfirmationToken(confirmationToken);
		if (!optionalUser.isPresent())
		{
			log.error("User not found!");
			throw new UsernameNotFoundException("ConfirmationToken not found.");
		}
		return optionalUser;
	}

	@Override
	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('USER_READ')")
	public Optional<User> getUser(String userId)
	{
		log.debug("Getting user by id: {}", userId);
		Optional<User> optionalUser = userDao.getUser(userId);
		if (!optionalUser.isPresent())
		{
			log.error("User not found!");
			throw new UsernameNotFoundException("User not found.");
		}
		return optionalUser;
	}

	@Override
	@Transactional
	@PreAuthorize("hasAuthority('USER_DELETE')")
	public void delete(User user)
	{
		log.debug("delete user by id: {}", user.getId());
		userDao.delete(user);
	}

	@Override
	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('USER_READ')")
	public List<User> getAll()
	{
		log.debug("Getting All User.");
		Optional<List<User>> optionalUser = userDao.getAll();
		if (!optionalUser.isPresent())
		{
			log.error("User not found!");
			throw new UsernameNotFoundException("User not found.");
		}
		return optionalUser.get();
	}

	@Override
	@Transactional
	@PreAuthorize("hasAuthority('USER_CREATE')")
	public void save(User user)
	{
		log.debug("save user by id: {}", user.getId());
		userDao.save(user);
	}
}
