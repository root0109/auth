package io.zaprit.auth.service;

import java.util.List;
import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetailsService;
import io.zaprit.auth.bo.User;

/**
 * <p>
 * TokenService interface.
 * </p>
 * 
 * @author vaibhav.singh
 */
public interface UserService extends UserDetailsService
{
	/**
	 * Find a user by email.
	 *
	 * @param email
	 *            the user's email
	 * @return user which contains the user with the given email or null.
	 */
	public Optional<User> getUserByEmail(String email);

	/**
	 * Find a user by confirmation token.
	 * 
	 * @param confirmationToken
	 *            generated for registration confirmation
	 * @return user associated with this confirmation token
	 */
	public Optional<User> getUserByConfirmationToken(String confirmationToken);

	/**
	 * Find a user by ID.
	 *
	 * @param id
	 *            the user's ID
	 * @return User returns an Optional User object which contains the user or null.
	 */
	public Optional<User> getUser(String id);

	/**
	 * Delete user
	 * 
	 * @param user
	 */
	public void delete(User user);

	/**
	 * Get all Users
	 * 
	 * @return
	 */
	public List<User> getAllCompanyUsers(String companyId);

	/**
	 * Get all Users
	 * 
	 * @return
	 */
	public List<User> getAll();

	/**
	 * Save User Object
	 * 
	 * @param user
	 */
	public void save(User user);

	/**
	 * update User Object
	 * 
	 * @param user
	 */
	public void update(User user);
}
