/**
 * 
 */
package io.zaprit.auth.constants;

/**
 * @author vaibhav.singh
 */
public class EndPoint
{
	public static final String	V1		= "/v1";
	public static final String	V2		= "/v2";

	public static final String	ID		= "/{id}";
	public static final String	ADD		= "/add";
	public static final String	UPDATE	= "/update";
	public static final String	GET		= "/get";
	public static final String	SEARCH	= "/search";
	public static final String	DELETE	= "/delete" + ID;
	public static final String	GET_ID	= GET + ID;

	public static class Company
	{
		private static final String	ROOT	= "/company";
		public static final String	V1		= EndPoint.V1 + ROOT;
		public static final String	V2		= EndPoint.V2 + ROOT;
	}

	public static class Clients

	{
		private static final String	ROOT	= "/clients";
		public static final String	V1		= EndPoint.V1 + ROOT;
		public static final String	V2		= EndPoint.V2 + ROOT;
		public static final String	REVOKE	= "/approval/revoke";
	}

	public static class Account
	{
		private static final String	ROOT		= "/account";
		public static final String	REGISTER	= "/register";
		public static final String	CONFIRM		= "/confirm";
		public static final String	ALL			= "/all";
		public static final String	COMPANY_ALL	= "/company/{companyId}/users";
		public static final String	V1			= EndPoint.V1 + ROOT;
		public static final String	V2			= EndPoint.V2 + ROOT;
	}
}
