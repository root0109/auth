package io.zaprit.auth.controller;

import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import io.zaprit.auth.bo.Company;
import io.zaprit.auth.constants.EndPoint;
import io.zaprit.auth.service.CompanyService;

/**
 * @author vaibhav.singh
 */
@RestController
@RequestMapping(EndPoint.Company.V1)
public class CompanyController
{
	@Autowired
	private CompanyService companyService;

	@GetMapping(value = EndPoint.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody List<Company> getAll()
	{
		return companyService.getAll();
	}

	@GetMapping(value = EndPoint.GET_ID, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody Company get(@PathVariable String id)
	{
		return companyService.getCompany(id);
	}

	@PostMapping(value = EndPoint.ADD, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> create(@Valid Company company)
	{
		companyService.create(company);
		return new ResponseEntity<>(company, HttpStatus.CREATED);
	}

	@PutMapping(value = EndPoint.UPDATE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(@Valid Company company)
	{
		companyService.update(company);
		return new ResponseEntity<>(company, HttpStatus.OK);
	}

	@DeleteMapping(value = EndPoint.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> delete(@PathVariable String id)
	{
		companyService.delete(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
