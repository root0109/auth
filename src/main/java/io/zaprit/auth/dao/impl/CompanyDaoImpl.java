package io.zaprit.auth.dao.impl;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
import io.zaprit.auth.bo.Company;
import io.zaprit.auth.dao.CompanyDao;

@Repository
public class CompanyDaoImpl implements CompanyDao
{
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Company find(String companyId)
	{
		String query = "SELECT e FROM " + Company.class.getName() + " e WHERE e.id = :companyId";
		Query q = entityManager.createQuery(query);
		q.setParameter("companyId", companyId);
		return (Company) q.getSingleResult();
	}

	@Override
	public List<Company> findAll()
	{
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Company> query = builder.createQuery(Company.class);
		Root<Company> root = query.from(Company.class);
		query.select(root).distinct(true);
		TypedQuery<Company> allQuery = entityManager.createQuery(query);

		return allQuery.getResultList();
	}

	@Override
	public void create(Company company)
	{
		entityManager.persist(company);
	}

	@Override
	public Company update(Company company)
	{
		return entityManager.merge(company);
	}

	@Override
	public void delete(String id)
	{
		Company company = entityManager.find(Company.class, id);
		delete(company);
	}

	@Override
	public void delete(Company company)
	{
		entityManager.remove(company);
	}
}
