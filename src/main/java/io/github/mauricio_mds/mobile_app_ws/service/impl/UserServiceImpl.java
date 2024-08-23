package io.github.mauricio_mds.mobile_app_ws.service.impl;

import io.github.mauricio_mds.mobile_app_ws.UserRepository;
import io.github.mauricio_mds.mobile_app_ws.io.entity.UserEntity;
import io.github.mauricio_mds.mobile_app_ws.service.UserService;
import io.github.mauricio_mds.mobile_app_ws.shared.Utils;
import io.github.mauricio_mds.mobile_app_ws.shared.dto.UserDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    Utils utils;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDto createUser(UserDto user) {
        if (userRepository.findByEmail(user.getEmail()) != null) throw  new RuntimeException("Record already exists");

        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(user, userEntity);
        userEntity.setUserId(utils.generateUserId(30));
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        UserEntity storedUser = userRepository.save(userEntity);
        UserDto returnUser = new UserDto();
        BeanUtils.copyProperties(storedUser, returnUser);
        return returnUser;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
