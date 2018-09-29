/**
 * 
 */
package io.zaprit.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import com.jayway.jsonpath.JsonPath;
import io.zaprit.auth.constants.EndPoint;
import io.zaprit.auth.constants.EndPoint.Account;

/**
 * @author vaibhav.singh
 */
@ExtendWith({ RestDocumentationExtension.class, SpringExtension.class })
@WebAppConfiguration
@TestInstance(Lifecycle.PER_CLASS)
@ContextConfiguration(classes = { TestWebConfig.class })
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("Account Related TestCases")
public class AccountTests
{
	@Autowired
	private WebApplicationContext	applicationContext;

	@Autowired
	private FilterChainProxy		springSecurityFilterChain;

	@Autowired
	private MessageSource			messageSource;

	private MockMvc					mockMvc;
	private static String			accessToken;
	private static String			userId;
	private static Locale			locale				= LocaleContextHolder.getLocale();
	private static String			email;

	private static final String		oauthClientId		= "web-test-read-write-client";
	private static final String		oAuthClientPassword	= "spring-security-oauth2-read-write-client-password1234";

	@BeforeEach
	public void setUp(RestDocumentationContextProvider restDocumentation) throws Exception
	{
		this.mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext).addFilter(springSecurityFilterChain)
				.apply(documentationConfiguration(restDocumentation))
				.alwaysDo(document("{method-name}", Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
						Preprocessors.preprocessResponse(Preprocessors.prettyPrint())))
				.build();

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "password");
		params.add("client_id", oauthClientId);
		params.add("username", "admin");
		params.add("password", "admin1234");

		ResultActions result = mockMvc
				.perform(post("/oauth/token").params(params).with(httpBasic(oauthClientId, oAuthClientPassword))
						.accept("application/json;charset=UTF-8"))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"));

		String response = result.andReturn().getResponse().getContentAsString();
		accessToken = JsonPath.parse(response).read("$.access_token").toString();
	}

	@Test
	public void registerUser() throws Exception
	{
		System.out.println("-------------------------------------------------------------------------------------------------");
		System.out.println(new Object()
		{}.getClass().getEnclosingMethod().getName());
		System.out.println("-------------------------------------------------------------------------------------------------");
		String randomString = Math.random() + "";
		email = "test" + randomString + "@test.com";
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("email", email);
		params.add("username", randomString);
		params.add("password", "test123");
		params.add("firstName", "firstname");
		params.add("lastName", "lastName");
		params.add("enabled", "false");
		params.add("accountNonExpired", "false");
		params.add("accountNonLocked", "false");
		params.add("credentialsNonExpired", "false");
		params.add("signUpType", "1");
		params.add("companyId", "TestCompanyId1");
		params.add("authorities[0].name", "USER_CREATE");
		params.add("authorities[1].name", "USER_READ");
		params.add("authorities[2].name", "USER_UPDATE");
		params.add("authorities[3].name", "USER_DELETE");
		params.add("authorities[0].id", "5");
		params.add("authorities[1].id", "6");
		params.add("authorities[2].id", "7");
		params.add("authorities[3].id", "8");

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("authorization", "Bearer " + accessToken);
		RequestBuilder requestBuilder = post(EndPoint.Account.V1 + Account.REGISTER).headers(httpHeaders)
				.contentType("application/json;charset=UTF-8").params(params);

		ResultActions result = mockMvc.perform(requestBuilder).andDo(print()).andExpect(status().isCreated())
				.andExpect(content().contentType("application/json;charset=UTF-8"));
		String response = result.andReturn().getResponse().getContentAsString();
		assertEquals(response, messageSource.getMessage("registration.confirmationEmail", new Object[] { email }, locale));
	}

	@Test
	public void getUserById() throws Exception
	{
		System.out.println("-------------------------------------------------------------------------------------------------");
		System.out.println(new Object()
		{}.getClass().getEnclosingMethod().getName());
		System.out.println("-------------------------------------------------------------------------------------------------");
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("authorization", "Bearer " + accessToken);
		RequestBuilder requestBuilder = get(EndPoint.Account.V1 + EndPoint.GET_ID, 2).headers(httpHeaders)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE).characterEncoding("UTF-8");

		ResultActions result = mockMvc.perform(requestBuilder).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"));

		String response = result.andReturn().getResponse().getContentAsString();
		assertEquals(JsonPath.parse(response).read("$.id").toString(), "2");
	}

	@Test
	public void getUserByIdNotFound() throws Exception
	{
		System.out.println("-------------------------------------------------------------------------------------------------");
		System.out.println(new Object()
		{}.getClass().getEnclosingMethod().getName());
		System.out.println("-------------------------------------------------------------------------------------------------");
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("authorization", "Bearer " + accessToken);
		RequestBuilder requestBuilder = get(EndPoint.Account.V1 + EndPoint.GET_ID, "notfound").headers(httpHeaders)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE).characterEncoding("UTF-8");

		ResultActions result = mockMvc.perform(requestBuilder).andDo(print()).andExpect(status().isUnauthorized())
				.andExpect(content().contentType("application/json;charset=UTF-8"));

		String response = result.andReturn().getResponse().getContentAsString();
		assertEquals(JsonPath.parse(response).read("$.error"), "unauthorized");
	}

	@Test
	public void getUserByEmailId() throws Exception
	{
		System.out.println("-------------------------------------------------------------------------------------------------");
		System.out.println(new Object()
		{}.getClass().getEnclosingMethod().getName());
		System.out.println("-------------------------------------------------------------------------------------------------");
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("authorization", "Bearer " + accessToken);
		RequestBuilder requestBuilder = get(EndPoint.Account.V1 + EndPoint.GET).param("emailId", email).headers(httpHeaders)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE).characterEncoding("UTF-8");

		ResultActions result = mockMvc.perform(requestBuilder).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"));

		String response = result.andReturn().getResponse().getContentAsString();
		String message = JsonPath.parse(response).read("$.email").toString();
		userId = JsonPath.parse(response).read("$.id");
		assertEquals(message, email);
	}

	@Test
	public void updateUser() throws Exception
	{
		System.out.println("-------------------------------------------------------------------------------------------------");
		System.out.println(new Object()
		{}.getClass().getEnclosingMethod().getName());
		System.out.println("-------------------------------------------------------------------------------------------------");

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("email", email);
		params.add("username", "New_Username");
		params.add("password", "test123");
		params.add("firstName", "New_firstname");
		params.add("lastName", "NEW_lastName");
		params.add("enabled", "false");
		params.add("accountNonExpired", "false");
		params.add("accountNonLocked", "false");
		params.add("credentialsNonExpired", "false");
		params.add("signUpType", "1");
		params.add("companyId", "TestCompanyId1");
		params.add("authorities[0].name", "USER_CREATE");
		params.add("authorities[1].name", "USER_READ");
		params.add("authorities[2].name", "USER_UPDATE");
		params.add("authorities[0].id", "5");
		params.add("authorities[1].id", "6");
		params.add("authorities[2].id", "7");

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("authorization", "Bearer " + accessToken);
		RequestBuilder requestBuilder = put(EndPoint.Account.V1 + EndPoint.UPDATE).params(params).headers(httpHeaders)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE).characterEncoding("UTF-8");

		ResultActions result = mockMvc.perform(requestBuilder).andDo(print()).andExpect(status().isUnauthorized())
				.andExpect(content().contentType("application/json;charset=UTF-8"));

		String response = result.andReturn().getResponse().getContentAsString();
		assertEquals(JsonPath.parse(response).read("$.error"), "unauthorized");
	}

	@Test
	public void getUserByEmailIdNotFound() throws Exception
	{
		System.out.println("-------------------------------------------------------------------------------------------------");
		System.out.println(new Object()
		{}.getClass().getEnclosingMethod().getName());
		System.out.println("-------------------------------------------------------------------------------------------------");
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("authorization", "Bearer " + accessToken);
		RequestBuilder requestBuilder = get(EndPoint.Account.V1 + EndPoint.GET).param("emailId", "notfound@test.com").headers(httpHeaders)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE).characterEncoding("UTF-8");

		ResultActions result = mockMvc.perform(requestBuilder).andDo(print()).andExpect(status().isUnauthorized())
				.andExpect(content().contentType("application/json;charset=UTF-8"));

		String response = result.andReturn().getResponse().getContentAsString();
		assertEquals(JsonPath.parse(response).read("$.error"), "unauthorized");
	}

	@Test
	public void getAllCompanyUsers() throws Exception
	{
		System.out.println("-------------------------------------------------------------------------------------------------");
		System.out.println(new Object()
		{}.getClass().getEnclosingMethod().getName());
		System.out.println("-------------------------------------------------------------------------------------------------");
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("authorization", "Bearer " + accessToken);
		RequestBuilder requestBuilder = get(EndPoint.Account.V1 + EndPoint.Account.COMPANY_ALL, "TestCompanyId1").headers(httpHeaders)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE).characterEncoding("UTF-8");

		mockMvc.perform(requestBuilder).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"));
	}

	@Test
	public void deleteUser() throws Exception
	{
		System.out.println("-------------------------------------------------------------------------------------------------");
		System.out.println(new Object()
		{}.getClass().getEnclosingMethod().getName());
		System.out.println("-------------------------------------------------------------------------------------------------");
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("authorization", "Bearer " + accessToken);
		RequestBuilder requestBuilder = delete(EndPoint.Account.V1 + EndPoint.DELETE, userId).headers(httpHeaders)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE).characterEncoding("UTF-8");

		ResultActions result = mockMvc.perform(requestBuilder).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"));

		String response = result.andReturn().getResponse().getContentAsString();
		assertEquals(response, messageSource.getMessage("delete.success", null, locale));
	}
}
