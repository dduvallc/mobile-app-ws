package com.dduvall.developerblog.apps.ws.ui.controller;

import com.dduvall.developerblog.apps.ws.service.UserService;
import com.dduvall.developerblog.apps.ws.shared.dto.UserDto;
import com.dduvall.developerblog.apps.ws.ui.model.request.UserDetailsRequestModel;
import com.dduvall.developerblog.apps.ws.ui.model.response.UserRest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("users") // http://localhost:8080/users
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping // bind method to http get request
    public String getUser() {
        return "get user was called";
    }

    //  by adding request body and user details request model class,
    //  we made our Java method able to convert
    //  incoming Json into a Java object.
    @PostMapping  // bind method to http post request
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) {

        UserRest returnValue = new UserRest();

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);

        UserDto createdUser = userService.createUser(userDto);
        BeanUtils.copyProperties(createdUser, returnValue);


        return returnValue;
    }

    @PutMapping // bind method to http put request
    public String updateUser() {
        return "update user was called";
    }

    @DeleteMapping  // bind method to http delete request
    public String deleteUser() {
        return "delete user was called";
    }


}
