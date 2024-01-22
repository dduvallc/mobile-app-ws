package com.dduvall.developerblog.apps.ws.ui.controller;

import com.dduvall.developerblog.apps.ws.exceptions.UserServiceException;
import com.dduvall.developerblog.apps.ws.service.AddressService;
import com.dduvall.developerblog.apps.ws.service.UserService;
import com.dduvall.developerblog.apps.ws.shared.dto.AddressDTO;
import com.dduvall.developerblog.apps.ws.shared.dto.UserDto;
import com.dduvall.developerblog.apps.ws.ui.model.request.UserDetailsRequestModel;
import com.dduvall.developerblog.apps.ws.ui.model.response.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/users") // http://localhost:8080/users
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    AddressService addressService;

    @Autowired
    AddressService addressesService;

    @GetMapping (path="/{id}",
            produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})       // bind method to http get request
    public UserRest getUser (@PathVariable String id) {

        UserRest returnValue = new UserRest();
        UserDto userDto = userService.getUserByUserId(id);
//        BeanUtils.copyProperties(userDto, returnValue);
        ModelMapper modelMapper = new ModelMapper();
        returnValue = modelMapper.map(userDto, UserRest.class);
        return returnValue;
//        return "get user was called";
    }

    //  by adding request body and user details request model class,
    //  we made our Java method able to convert
    //  incoming Json into a Java object.
    @PostMapping(
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},  // what the web services endpoint can consume
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}   // what the web services endpoint can respond back with
            ) // bind method to http post request
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {

        UserRest returnValue = new UserRest();

        if (userDetails.getFirstName().isEmpty())
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

//        UserDto userDto = new UserDto();
//        BeanUtils.copyProperties(userDetails, userDto);
        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);

        UserDto createdUser = userService.createUser(userDto);
//        BeanUtils.copyProperties(createdUser, returnValue);
        returnValue = modelMapper.map(createdUser, UserRest.class);

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

    // http://localhost:8080/mobile-app-ws/users/<user-id> like jsjajdkfgh/addresses
    @GetMapping (path="/{id}/addresses",
            produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})       // bind method to http get request
    public List<AddressesRest> getUserAddresses (@PathVariable String id) {

        List<AddressesRest> returnValue = new ArrayList<>();

        List<AddressDTO> addressesDTO = addressesService.getAddresses(id);
//        BeanUtils.copyProperties(userDto, returnValue);
        Type listType = new TypeToken<List<AddressesRest>>() {}.getType();
        returnValue = new ModelMapper().map(addressesDTO, listType);

        return returnValue;
    }

    // http://localhost:8080/mobile-app-ws/users/<user-id> like jsjajdkfgh/addresses/address-id
    @GetMapping (path="/{userId}/addresses/{addressId}",
            produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})       // bind method to http get request
    public EntityModel<AddressesRest> getUserAddress (@PathVariable String userId, @PathVariable String addressId) {  // if @PathVariable different then path need, need (@PathVariable("id")

        AddressDTO addressesDto = addressesService.getAddress(addressId);

        ModelMapper modelMapper = new ModelMapper();
        AddressesRest returnValue = modelMapper.map(addressesDto, AddressesRest.class);

        // http://localhost:8080/users/<userId>
        Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(userId).withRel("user"); // "user" used as jason key to a link object in the response
        // http://localhost:8080/users/<userId>/addresses
        Link userAddressesLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(userId))
//                .slash(userId)
//                .slash("addresses")
                .withRel("addresses");
        // http://localhost:8080/users/<userId>/addresses/{addressId}
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddress(userId, addressId))
//                .slash(userId)
//                .slash("addresses")
//                .slash(addressId)
                .withSelfRel();

//        returnValue.add(userLink);
//        returnValue.add(userAddressesLink);
//        returnValue.add(selfLink);


        return EntityModel.of(returnValue, Arrays.asList(userLink, userAddressesLink, selfLink));

    }

}
