package com.dduvall.developerblog.apps.ws.ui.model.response;
/*
    Classes that are being used as a response model, postfix with Rest
    So this class being used on the UI level & used to communicate
    information back to a calling client (like this mobile-app-ws client)
 */

public class UserRest {

    private String userId;
    private String firstName;
    private String lastName;
    private String email;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
