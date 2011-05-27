package com.baker.vm;

/**
 * @author baker
 *
 */
public class UsernamePassword
{
	public UsernamePassword(final String iUser, final String iPass)
	{
		user = iUser;
		pass = iPass;
	}

	public final String user;
	public final String pass;

	@Override
	public String toString()
	{
		return user + " (" + pass + ")";
	}
}
