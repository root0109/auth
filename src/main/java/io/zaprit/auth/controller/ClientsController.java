/**
 * 
 */
package io.zaprit.auth.controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import io.zaprit.auth.constants.EndPoint;
import io.zaprit.auth.constants.EndPoint.Clients;
import io.zaprit.auth.utils.AuthorityPropertyEditor;
import io.zaprit.auth.utils.SplitCollectionEditor;

/**
 * @author vaibhav.singh
 */
@Controller
@RequestMapping(EndPoint.Clients.V1)
public class ClientsController
{
	@Autowired
	private JdbcClientDetailsService	clientDetailsService;

	@Autowired
	private ApprovalStore				approvalStore;

	@Autowired
	private TokenStore					tokenStore;

	@InitBinder
	public void initBinder(WebDataBinder binder)
	{
		binder.registerCustomEditor(Collection.class, new SplitCollectionEditor(Set.class, ","));
		binder.registerCustomEditor(GrantedAuthority.class, new AuthorityPropertyEditor());
	}

	@GetMapping(value = EndPoint.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> root(Principal principal)
	{
		List<ClientDetails> clientDetailList = clientDetailsService.listClientDetails();
		List<Approval> approvals = clientDetailList.stream()
				.map(clientDetails -> approvalStore.getApprovals(principal.getName(), clientDetails.getClientId())).flatMap(Collection::stream)
				.collect(Collectors.toList());
		Map<String, Object> model = new HashMap<>();
		model.put("approvals", approvals);
		model.put("clientDetails", clientDetailList);
		return new ResponseEntity<>(model, HttpStatus.OK);
	}

	@PostMapping(value = Clients.REVOKE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> revokApproval(@ModelAttribute Approval approval)
	{
		approvalStore.revokeApprovals(Arrays.asList(approval));
		tokenStore.findTokensByClientIdAndUserName(approval.getClientId(), approval.getUserId()).forEach(tokenStore::removeAccessToken);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping(value = EndPoint.GET_ID, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> showEditForm(@PathVariable(value = "id", required = false) String clientId)
	{
		ClientDetails clientDetails;
		if (clientId != null)
		{
			clientDetails = clientDetailsService.loadClientByClientId(clientId);
		}
		else
		{
			clientDetails = new BaseClientDetails();
		}
		return new ResponseEntity<>(clientDetails, HttpStatus.OK);
	}

	@PostMapping(value = EndPoint.ADD, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateClient(@ModelAttribute BaseClientDetails clientDetails,
			@RequestParam(value = "newClient", required = false) String newClient)
	{
		clientDetailsService.addClientDetails(clientDetails);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value = EndPoint.UPDATE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> editClient(@ModelAttribute BaseClientDetails clientDetails,
			@RequestParam(value = "newClient", required = false) String newClient)
	{
		clientDetailsService.updateClientDetails(clientDetails);

		if (!clientDetails.getClientSecret().isEmpty())
		{
			clientDetailsService.updateClientSecret(clientDetails.getClientId(), clientDetails.getClientSecret());
		}
		return new ResponseEntity<>(clientDetails, HttpStatus.OK);
	}

	@DeleteMapping(value = EndPoint.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> deleteClient(@ModelAttribute BaseClientDetails ClientDetails, @PathVariable("id") String id)
	{
		clientDetailsService.removeClientDetails(clientDetailsService.loadClientByClientId(id).toString());
		return new ResponseEntity<>(HttpStatus.OK);
	}
}