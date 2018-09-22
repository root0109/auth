package io.zaprit.auth.utils;

import org.springframework.stereotype.Service;

/**
 * <p>
 * MailContentBuilder class.
 * </p>
 * 
 * @author vaibhav.singh
 */
@Service
public class MailContentBuilder
{
	/**
	 * <p>
	 * Populate email template with custom message.
	 * </p>
	 */
	public String build(String message, String link)
	{
		return link;
	}

}
