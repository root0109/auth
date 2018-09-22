package io.zaprit.auth.dao;

import java.util.List;
import java.util.Optional;
import io.zaprit.auth.bo.User;

/**
 * <p>
 * UserRepository interface. Uses Spring JPA repository
 * </p>
 * 
 * @author vaibhav.singh
 */
public interface UserDao
{
	/**
	 * Find a user by username.
	 *
	 * @param userName
	 *            the user's userName
	 * @return user which contains the user with the given username or null.
	 */
	public Optional<User> getUserByUserName(String userName);

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
	public Optional<User> getUser(String userId);

	/**
	 * Delete User
	 * 
	 * @param user
	 */
	public void delete(User user);

	/**
	 * Find all users
	 *
	 * @return User List.
	 */
	public Optional<List<User>> getAll();

	/**
	 * Save the User object in DB
	 *
	 * @param id
	 *            the user's ID
	 */
	public void save(User user);
}
