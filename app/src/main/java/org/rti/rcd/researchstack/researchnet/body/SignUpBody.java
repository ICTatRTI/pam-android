package org.rti.rcd.researchstack.researchnet.body;


import com.google.gson.annotations.SerializedName;

public class SignUpBody {

    /**
     * User's email address, cannot be change once created
     */
    private String email;

    private String dob;

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    /**
     * User's username
     */
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * User's password. Constraints for an acceptable password can be set per study.
     */
    private String password;

    private String type = "SignUp";

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    private String gender;

    public String getFirstName() {
        return firstName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public SignUpBody(String study, String email, String username, String password, String[] roles, String[] dataGroups)
    {
        this.email = email;
        this.username = username;
        this.password = password;

    }

    public void setFirstNameFromName(String name) {

        String[] tokens = name.split(" ");
        if (tokens.length == 2){
            this.firstName = tokens[0];
        } else {
            this.firstName = name;
        }
    }


    public void setLastNameFromName(String name) {

        String[] tokens = name.split(" ");
        if (tokens.length == 2){
            this.lastName = tokens[1];
        } else {
            this.lastName = name;
        }

    }
}
