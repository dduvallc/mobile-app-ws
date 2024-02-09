package com.dduvall.developerblog.apps.ws.security;

import com.dduvall.developerblog.apps.ws.SpringApplicationContext;
import org.springframework.core.env.Environment;

import javax.crypto.SecretKey;

public class SecurityConstants {

    public static final long EXPIRATION_TIME=864000000; // 10 days
    public static final String TOKEN_PREFIX="Bearer ";
    public static final String HEADER_STRING="Authorization";
    public static final String SIGN_UP_URL="/users";
    public static final String VERIFICATION_EMAIL_URL="/users/email-verification";


    // best to make TOKEN_SECRET 64 chars are greater
//    public static final String TOKEN_SECRET="jf9i4jgu83nfl0jf9i4jgu83nfl0jf9i4jgu83nfl0jf9i4jgu83nfl0jf9i4jgu83nfl0";

    public static String getTokenSecret() {
        Environment environment = (Environment) SpringApplicationContext.getBean("environment");
        return environment.getProperty("tokenSecret");
    }

}
