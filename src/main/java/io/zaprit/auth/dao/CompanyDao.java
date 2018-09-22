package io.zaprit.auth.dao;

import java.util.List;
import io.zaprit.auth.bo.Company;

public interface CompanyDao
{
	public Company find(String companyId);

	public List<Company> findAll();

	public void create(Company company);

	public Company update(Company company);

	public void delete(String companyId);

	public void delete(Company company);
}
