package com.dduvall.developerblog.apps.ws.service.impl;

import com.dduvall.developerblog.apps.ws.io.repository.UserRepository;
import com.dduvall.developerblog.apps.ws.exceptions.UserServiceException;
import com.dduvall.developerblog.apps.ws.io.entity.UserEntity;
import com.dduvall.developerblog.apps.ws.service.UserService;
import com.dduvall.developerblog.apps.ws.shared.Utils;
import com.dduvall.developerblog.apps.ws.shared.dto.AddressDTO;
import com.dduvall.developerblog.apps.ws.shared.dto.UserDto;
import com.dduvall.developerblog.apps.ws.ui.model.response.ErrorMessages;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired // autowired on properties eliminates the need for getters & setters. Injected into property userRepository at runtime.
    UserRepository userRepository;

    @Autowired
    Utils utils;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDto createUser(UserDto user) {

//        UserEntity storedUserDetails = userRepository.findByEmail(user.getEmail());

        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("Record already exists");
        }

        for (int i = 0; i < user.getAddresses().size(); i++) {
            AddressDTO address = user.getAddresses().get(i);
            address.setUserDetails(user);
            address.setAddressId(utils.generateAddressId(30));
            user.getAddresses().set(i, address);
        }

//        UserEntity userEntity = new UserEntity();
//        BeanUtils.copyProperties(user, userEntity);
        ModelMapper modelMapper = new ModelMapper();
        UserEntity userEntity = modelMapper.map(user, UserEntity.class);

        String publicUserId = utils.generateUserId(30);
        userEntity.setUserId(publicUserId);
//        userEntity.setEncryptedPassword("test");
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        UserEntity storedUserDetails = userRepository.save(userEntity);

//        UserDto returnValue = new UserDto();
//        BeanUtils.copyProperties(storedUserDetails, returnValue);
        UserDto returnValue = modelMapper.map(storedUserDetails, UserDto.class);

        return returnValue;

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username);

        if (userEntity == null) throw new UsernameNotFoundException(username);
        return new User(username, userEntity.getEncryptedPassword(), new ArrayList<>()); // User from Spring Framework
    }

    @Override
    public UserDto getUser(String email)
    {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) throw new UsernameNotFoundException(email);

        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userEntity, returnValue);  //purpose is to get userId

        return returnValue;
    }

    @Override
    public UserDto getUserByUserId(String userId) {

        UserDto returnValue = new UserDto();
        UserEntity userEntity = userRepository.findByUserId(userId); //query DB

        if (userEntity == null) {
            throw new UsernameNotFoundException("User with ID: " + userId + " not found");  //this exception from Spring
        }

//        BeanUtils.copyProperties(userEntity, returnValue);
        ModelMapper modelMapper = new ModelMapper();
        returnValue = modelMapper.map(userEntity, UserDto.class);

        return returnValue;
    }

    @Override
    public UserDto updateUser(String userId, UserDto user) {

        UserDto returnValue = new UserDto();
        UserEntity userEntity = userRepository.findByUserId(userId);  // read user from DB

        if (userEntity == null) {
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());  // if null throw Exception
        }

        // modify the fields
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());

        UserEntity updatedUserDetails = userRepository.save(userEntity); // update the DB

//        BeanUtils.copyProperties(updatedUserDetails, returnValue);
        ModelMapper modelMapper = new ModelMapper();
        returnValue = modelMapper.map(updatedUserDetails, UserDto.class);

        return returnValue;
    }

    @Override
    public void deleteUser(String userId) {

        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null) {
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        }

        userRepository.delete(userEntity);

    }

    @Override
    public List<UserDto> getUsers(int page, int limit) {

        List<UserDto> returnValue = new ArrayList<>();

        if (page > 0) {
            page-= 1;
        }

        Pageable pageableRequest = PageRequest.of(page, limit);

        Page<UserEntity> usersPage = userRepository.findAll(pageableRequest);
        List<UserEntity> users = usersPage.getContent();

        for (UserEntity userEntity : users) {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(userEntity, userDto);

            returnValue.add(userDto);
        }

        return returnValue;
    }
}
