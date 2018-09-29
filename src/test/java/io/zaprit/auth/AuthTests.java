/**
 * 
 */
package io.zaprit.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

/**
 * @author vaibhav.singh
 */
@ExtendWith({ RestDocumentationExtension.class, SpringExtension.class })
@WebAppConfiguration
@TestInstance(Lifecycle.PER_CLASS)
@ContextConfiguration(classes = { TestWebConfig.class })
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("OAuth TestCases")
public class AuthTests
{

	@Autowired
	private WebApplicationContext	applicationContext;

	@Autowired
	private FilterChainProxy		springSecurityFilterChain;

	private MockMvc					mockMvc;
	private static String			accessToken;

	private static String			refreshToken;
	private static final String		oAuthClientId		= "web-test-read-write-client";
	private static final String		oAuthClientPassword	= "spring-security-oauth2-read-write-client-password1234";
	private static final String		invalidRefreshToken	= UUID.randomUUID().toString();

	@BeforeEach
	public void setUp(RestDocumentationContextProvider restDocumentation) throws Exception
	{
		this.mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext).addFilter(springSecurityFilterChain)
				.apply(documentationConfiguration(restDocumentation))
				.alwaysDo(document("{method-name}", Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
						Preprocessors.preprocessResponse(Preprocessors.prettyPrint())))
				.build();
		Thread.sleep(1000);
	}

	@Test
	public void getAccessTokenByPasswordGrant() throws Exception
	{
		System.out.println("-------------------------------------------------------------------------------------------------");
		System.out.println(new Object()
		{}.getClass().getEnclosingMethod().getName());
		System.out.println("-------------------------------------------------------------------------------------------------");
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "password");
		params.add("client_id", oAuthClientId);
		params.add("username", "admin");
		params.add("password", "admin1234");

		ResultActions result = mockMvc
				.perform(post("/oauth/token").params(params).with(httpBasic(oAuthClientId, oAuthClientPassword))
						.accept("application/json;charset=UTF-8"))
				.andDo(print()).andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"));

		String response = result.andReturn().getResponse().getContentAsString();
		DocumentContext documentContext = JsonPath.parse(response);
		accessToken = documentContext.read("$.access_token").toString();
		refreshToken = documentContext.read("$.refresh_token").toString();
	}

	@Test
	public void getAccessTokenByClientCredentials() throws Exception
	{
		System.out.println("-------------------------------------------------------------------------------------------------");
		System.out.println(new Object()
		{}.getClass().getEnclosingMethod().getName());
		System.out.println("-------------------------------------------------------------------------------------------------");
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "client_credentials");
		params.add("client_id", oAuthClientId);

		ResultActions result = mockMvc
				.perform(post("/oauth/token").params(params).with(httpBasic(oAuthClientId, oAuthClientPassword))
						.accept("application/json;charset=UTF-8"))
				.andDo(print()).andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"));

		String response = result.andReturn().getResponse().getContentAsString();
		DocumentContext documentContext = JsonPath.parse(response);
		String myAccessToken = documentContext.read("$.access_token").toString();

		assertTrue(myAccessToken != null && !myAccessToken.trim().isEmpty());
	}

	@RepeatedTest(value = 1, name = RepeatedTest.LONG_DISPLAY_NAME)
	public void refreshAccessToken() throws Exception
	{
		if (refreshToken == null)
			getAccessTokenByPasswordGrant();
		System.out.println("-------------------------------------------------------------------------------------------------");
		System.out.println(new Object()
		{}.getClass().getEnclosingMethod().getName());
		System.out.println("-------------------------------------------------------------------------------------------------");
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "refresh_token");
		params.add("client_id", oAuthClientId);
		params.add("username", "admin");
		params.add("password", "admin1234");
		params.add("refresh_token", refreshToken);

		ResultActions result = mockMvc
				.perform(post("/oauth/token").params(params).with(httpBasic(oAuthClientId, oAuthClientPassword))
						.accept("application/json;charset=UTF-8"))
				.andDo(print()).andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"));

		String response = result.andReturn().getResponse().getContentAsString();
		String newAccessToken = JsonPath.parse(response).read("$.access_token").toString();

		assertNotEquals(newAccessToken, accessToken);
	}

	@Test
	public void validateRefreshToken() throws Exception
	{
		System.out.println("-------------------------------------------------------------------------------------------------");
		System.out.println(new Object()
		{}.getClass().getEnclosingMethod().getName());
		System.out.println("-------------------------------------------------------------------------------------------------");
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "refresh_token");
		params.add("client_id", oAuthClientId);
		params.add("username", "admin");
		params.add("password", "admin1234");
		params.add("refresh_token", invalidRefreshToken);

		MvcResult result = mockMvc.perform(
				post("/oauth/token").with(httpBasic(oAuthClientId, oAuthClientPassword)).params(params).accept("application/json;charset=UTF-8"))
				.andDo(print()).andExpect(status().is4xxClientError()).andReturn();

		String response = result.getResponse().getContentAsString();

		String error = JsonPath.parse(response).read("$.error").toString();
		String errorDescription = JsonPath.parse(response).read("$.error_description").toString();
		assertEquals(error, "invalid_grant");
		assertEquals(errorDescription, "Invalid refresh token: " + invalidRefreshToken);
	}
}
