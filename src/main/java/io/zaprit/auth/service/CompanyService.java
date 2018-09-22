package io.zaprit.auth.service;

import java.util.List;
import io.zaprit.auth.bo.Company;

public interface CompanyService
{
	public Company getCompany(String companyId);

	public List<Company> getAll();

	public void create(Company company);

	public Company update(Company company);

	public void delete(String companyId);

	public void delete(Company company);
}
