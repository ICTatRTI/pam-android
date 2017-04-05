package org.rti.rcd.researchstack.researchnet.body;


public class SignInBody
{
    /**
     * User's username or email address
     */
    private String username;

    /**
     * User's password
     */
    private String password;

    public SignInBody(String username, String password)
    {

        this.username = username;
        this.password = password;
    }
}
