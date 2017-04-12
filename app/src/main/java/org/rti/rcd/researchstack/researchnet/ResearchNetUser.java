package org.rti.rcd.researchstack.researchnet;

import com.google.gson.annotations.SerializedName;

import org.researchstack.skin.model.User;

import java.io.Serializable;

/**
 * Created by apreston on 4/10/17.
 */

public class ResearchNetUser extends User implements Serializable {


    @SerializedName("last_name")
    private String lastName;

    @SerializedName("first_name")
    private String firstName;

    private String password;

    public String getFirstName()
    {
        String[] tokens = this.getName().split(" ");
        return tokens[0];
    }



    public String getLastName(){

        return lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
