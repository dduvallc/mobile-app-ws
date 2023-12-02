package com.dduvall.developerblog.apps.ws.ui.model.request;

    // When user signs in into our application, they will need to send a
    //Json document providing their username and password so that our API can authenticate them.
    // And this Json document will need to be converted into a Java class so that we can work with it.

public class UserLoginRequestModel {
    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }




}
