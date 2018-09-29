package io.zaprit.auth.dao.impl;

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;
import io.zaprit.auth.bo.User;
import io.zaprit.auth.dao.UserDao;
import lombok.extern.slf4j.Slf4j;

/**
 * @author vaibhav.singh
 */
@Slf4j
@Repository("userDao")
public class UserDaoImpl implements UserDao
{
	@PersistenceContext
	private EntityManager entityManager;

	public Optional<User> getUserByUserName(String userName)
	{
		String query = "SELECT DISTINCT user FROM " + User.class.getName() + " user INNER JOIN FETCH user.authorities AS authorities "
				+ "WHERE user.username = :username";
		Query q = entityManager.createQuery(query);
		q.setParameter("username", userName);
		try
		{
			return Optional.of((User) q.getSingleResult());
		}
		catch (Exception e)
		{
			return Optional.empty();
		}
	}

	@Override
	public Optional<User> getUserByEmail(String email)
	{
		String query = "SELECT DISTINCT user FROM " + User.class.getName() + " user INNER JOIN FETCH user.authorities AS authorities "
				+ "WHERE user.email = :email";
		Query q = entityManager.createQuery(query);
		q.setParameter("email", email);
		try
		{
			return Optional.of((User) q.getSingleResult());
		}
		catch (Exception e)
		{
			return Optional.empty();
		}
	}

	@Override
	public Optional<User> getUserByConfirmationToken(String confirmationToken)
	{
		Query q = entityManager.createQuery("SELECT e FROM " + User.class.getName() + " e WHERE e.confirmationToken = :confirmationToken");
		q.setParameter("confirmationToken", confirmationToken);
		try
		{
			return Optional.of((User) q.getSingleResult());
		}
		catch (Exception e)
		{
			return Optional.empty();
		}
	}

	@Override
	public Optional<User> getUser(String id)
	{
		String query = "SELECT DISTINCT user FROM " + User.class.getName() + " user INNER JOIN FETCH user.authorities AS authorities "
				+ "WHERE user.id = :id";
		Query q = entityManager.createQuery(query);
		q.setParameter("id", id);
		try
		{
			return Optional.of((User) q.getSingleResult());
		}
		catch (Exception e)
		{
			return Optional.empty();
		}
	}

	@Override
	public void delete(User user)
	{
		entityManager.remove(user);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Optional<List<User>> getAllCompanyUsers(String companyId)
	{
		String query = "SELECT DISTINCT user FROM " + User.class.getName()
				+ " user INNER JOIN FETCH user.authorities AS authorities where user.companyId=:companyId";

		Query q = entityManager.createQuery(query);
		q.setParameter("companyId", companyId);
		try
		{
			return Optional.of((List<User>) q.getResultList());
		}
		catch (Exception e)
		{
			return Optional.empty();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Optional<List<User>> getAll()
	{
		String query = "SELECT DISTINCT user FROM " + User.class.getName() + " user INNER JOIN FETCH user.authorities AS authorities ";

		Query q = entityManager.createQuery(query);
		try
		{
			return Optional.of((List<User>) q.getResultList());
		}
		catch (Exception e)
		{
			return Optional.empty();
		}
	}

	@Override
	public void save(User user)
	{
		entityManager.persist(user);
		log.debug(user.toString());
	}

	@Override
	public void update(User user)
	{
		entityManager.merge(user);
	}
}
