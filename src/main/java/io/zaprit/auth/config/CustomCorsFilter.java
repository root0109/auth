package io.zaprit.auth.config;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import lombok.extern.slf4j.Slf4j;

/**
 * Servlet Filter implementation class CustomCorsFilter
 */
@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class CustomCorsFilter extends OncePerRequestFilter
{
	@Value("${custom.cors.allowOrigin:*}")
	private String	allowOrigin;

	@Value("${custom.cors.allowMethods:GET, POST, PUT, DELETE, OPTIONS}")
	private String	allowMethods;

	@Value("${custom.cors.allowHeaders:Content-Type}")
	private String	allowHeaders;

	@Value("${custom.cors.allowCredentials:true}")
	private String	allowCredentials;

	@Value("${custom.cors.maxAge:3600}")
	private String	maxAge;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException
	{
		response.addHeader("Access-Control-Allow-Origin", allowOrigin);

		if (request.getMethod().equalsIgnoreCase(HttpMethod.OPTIONS.toString()))
		{
			response.addHeader("Access-Control-Allow-Methods", allowMethods);
			response.addHeader("Access-Control-Allow-Headers", allowHeaders);
			response.addHeader("Access-Control-Allow-Credentials", allowCredentials);
			response.addHeader("Access-Control-Max-Age", maxAge);
			response.setStatus(HttpServletResponse.SC_OK);
		}
		else
		{
			filterChain.doFilter(request, response);
		}
		log.debug("CORS Filter Applied");
	}
}
