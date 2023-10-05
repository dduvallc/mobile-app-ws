package com.dduvall.developerblog.apps.ws.service.impl;

import com.dduvall.developerblog.apps.ws.UserRepository;
import com.dduvall.developerblog.apps.ws.io.entity.UserEntity;
import com.dduvall.developerblog.apps.ws.service.UserService;
import com.dduvall.developerblog.apps.ws.shared.Utils;
import com.dduvall.developerblog.apps.ws.shared.dto.UserDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(user, userEntity);

        String publicUserId = utils.generateUserId(30);

        userEntity.setUserId(publicUserId);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        UserEntity storedUserDetails = userRepository.save(userEntity);

        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(storedUserDetails, returnValue);

        return returnValue;

    }
}
