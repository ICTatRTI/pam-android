package org.rti.rcd.researchstack.researchnet.body;


public class SignUpBody {

    /**
     * User's email address, cannot be change once created
     */
    private String email;

    /**
     * User's username
     */
    private String username;

    /**
     * User's password. Constraints for an acceptable password can be set per study.
     */
    private String password;

    private String type = "SignUp";

    public SignUpBody(String study, String email, String username, String password, String[] roles, String[] dataGroups)
    {
        this.email = email;
        this.username = username;
        this.password = password;

    }
}
