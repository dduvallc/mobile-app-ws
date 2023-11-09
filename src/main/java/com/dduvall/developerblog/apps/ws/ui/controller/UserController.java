package com.dduvall.developerblog.apps.ws.ui.controller;

import com.dduvall.developerblog.apps.ws.exceptions.UserServiceException;
import com.dduvall.developerblog.apps.ws.service.UserService;
import com.dduvall.developerblog.apps.ws.shared.dto.UserDto;
import com.dduvall.developerblog.apps.ws.ui.model.request.UserDetailsRequestModel;
import com.dduvall.developerblog.apps.ws.ui.model.response.ErrorMessages;
import com.dduvall.developerblog.apps.ws.ui.model.response.OperationStatusModel;
import com.dduvall.developerblog.apps.ws.ui.model.response.RequestOperationStatus;
import com.dduvall.developerblog.apps.ws.ui.model.response.UserRest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("users") // http://localhost:8080/users
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping (path="/{id}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})       // bind method to http get request
    public UserRest getUser (@PathVariable String id) {

        UserRest returnValue = new UserRest();
        UserDto userDto = userService.getUserByUserId(id);
        BeanUtils.copyProperties(userDto, returnValue);

        return returnValue;
//        return "get user was called";
    }

    //  by adding request body and user details request model class,
    //  we made our Java method able to convert
    //  incoming Json into a Java object.
    @PostMapping(
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
            ) // bind method to http post request

    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {

        UserRest returnValue = new UserRest();

        if (userDetails.getFirstName().isEmpty())
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
//            throw new NullPointerException("The object is null");
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);

        UserDto createdUser = userService.createUser(userDto);
        BeanUtils.copyProperties(createdUser, returnValue);


        return returnValue;
    }

    @PutMapping(path="/{id}",
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
            ) // bind method to http put request
    public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
        UserRest returnValue = new UserRest();

        if (userDetails.getFirstName().isEmpty())
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);

        UserDto updateUser = userService.updateUser(id, userDto);
        BeanUtils.copyProperties(updateUser, returnValue);


        return returnValue;
    }

    @DeleteMapping(path="/{id}",
                produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })// bind method to http delete request
    public OperationStatusModel deleteUser(@PathVariable String id) {

        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.DELETE.name());

        userService.deleteUser(id);

        returnValue.setOperationalResult(RequestOperationStatus.SUCCESS.name());

        return returnValue;
    }

    @GetMapping( produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE } )
    public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "limit", defaultValue = "2")  int limit) {

        List<UserRest> returnValue = new ArrayList<>();

        List<UserDto> users = userService.getUsers(page, limit);

        for (UserDto userDto : users) {
            UserRest userModel = new UserRest();
            BeanUtils.copyProperties(userDto, userModel);
            returnValue.add(userModel);
        }

        return returnValue;

    }


}
