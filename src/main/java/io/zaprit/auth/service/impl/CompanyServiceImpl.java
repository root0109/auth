package io.zaprit.auth.service.impl;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.zaprit.auth.bo.Company;
import io.zaprit.auth.dao.CompanyDao;
import io.zaprit.auth.service.CompanyService;

@Service("companyService")
public class CompanyServiceImpl implements CompanyService
{
	@Autowired
	private CompanyDao companyDao;

	@Override
	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('COMPANY_READ') and hasAuthority('USER_READ')")
	public Company getCompany(String id)
	{
		return companyDao.find(id);
	}

	@Override
	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('COMPANY_READ')")
	public List<Company> getAll()
	{
		return companyDao.findAll();
	}

	@Override
	@Transactional
	@PreAuthorize("hasAuthority('COMPANY_CREATE')")
	public void create(Company company)
	{
		company.setId(UUID.randomUUID().toString());
		companyDao.create(company);
	}

	@Override
	@Transactional
	@PreAuthorize("hasAuthority('COMPANY_UPDATE')")
	public Company update(Company company)
	{
		return companyDao.update(company);
	}

	@Override
	@Transactional
	@PreAuthorize("hasAuthority('COMPANY_DELETE')")
	public void delete(String id)
	{
		companyDao.delete(id);
	}

	@Override
	@Transactional
	@PreAuthorize("hasAuthority('COMPANY_DELETE')")
	public void delete(Company company)
	{
		companyDao.delete(company);
	}
}
