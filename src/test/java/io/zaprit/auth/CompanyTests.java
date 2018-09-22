/**
 * 
 */
package io.zaprit.auth;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * @author vaibhav.singh
 */
@ExtendWith({ RestDocumentationExtension.class, SpringExtension.class })
@WebAppConfiguration
@TestInstance(Lifecycle.PER_CLASS)
@ContextConfiguration(classes = { TestWebConfig.class })
@TestPropertySource(locations = "classpath:application.properties")
public class CompanyTests
{
	@Autowired
	private WebApplicationContext	applicationContext;

	@Autowired
	private FilterChainProxy		springSecurityFilterChain;

	private MockMvc					mockMvc;
	private static String			accessToken;
	private static String			companyId;

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
		params.add("client_id", "oauth2-read-write-client");
		params.add("username", "admin");
		params.add("password", "admin1234");

		ResultActions result = mockMvc
				.perform(post("/oauth/token").params(params)
						.with(httpBasic("oauth2-read-write-client", "spring-security-oauth2-read-write-client-password1234"))
						.accept("application/json;charset=UTF-8"))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"));

		String response = result.andReturn().getResponse().getContentAsString();
		accessToken = JsonPath.parse(response).read("$.access_token").toString();
	}

	@Test
	public void addCompany() throws Exception
	{
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("authorization", "Bearer " + accessToken);
		RequestBuilder requestBuilder = post(EndPoint.Company.V1 + EndPoint.ADD).headers(httpHeaders)
				.param("companyName", "GoldenWings" + Math.random()).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.characterEncoding("UTF-8");

		ResultActions result = mockMvc.perform(requestBuilder).andDo(print()).andExpect(status().isCreated())
				.andExpect(content().contentType("application/json;charset=UTF-8"));
		String response = result.andReturn().getResponse().getContentAsString();
		companyId = JsonPath.parse(response).read("$.id").toString();
	}

	@Test
	public void getAllCompany() throws Exception
	{
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("authorization", "Bearer " + accessToken);
		RequestBuilder requestBuilder = get(EndPoint.Company.V1 + EndPoint.GET).headers(httpHeaders)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE).characterEncoding("UTF-8");

		mockMvc.perform(requestBuilder).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"));
	}

	@Test
	public void getCompany() throws Exception
	{
		addCompany();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("authorization", "Bearer " + accessToken);
		RequestBuilder requestBuilder = get(EndPoint.Company.V1 + EndPoint.GET_ID, companyId).headers(httpHeaders)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE).characterEncoding("UTF-8");

		mockMvc.perform(requestBuilder).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"));
	}

	@Test
	public void updateCompany() throws Exception
	{
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("authorization", "Bearer " + accessToken);
		RequestBuilder requestBuilder = put(EndPoint.Company.V1 + EndPoint.UPDATE).headers(httpHeaders).param("id", companyId)
				.param("companyName", "GoldenWings_" + Math.random()).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.characterEncoding("UTF-8");

		mockMvc.perform(requestBuilder).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"));
	}

	@Test
	public void deleteCompany() throws Exception
	{
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("authorization", "Bearer " + accessToken);
		RequestBuilder requestBuilder = delete(EndPoint.Company.V1 + EndPoint.DELETE, companyId).headers(httpHeaders)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE).characterEncoding("UTF-8");

		mockMvc.perform(requestBuilder).andDo(print()).andExpect(status().isOk());
	}
}
