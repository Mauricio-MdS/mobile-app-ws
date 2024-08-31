package io.github.mauricio_mds.mobile_app_ws.service.impl;

import io.github.mauricio_mds.mobile_app_ws.exceptions.UserServiceException;
import io.github.mauricio_mds.mobile_app_ws.io.entity.UserEntity;
import io.github.mauricio_mds.mobile_app_ws.io.repository.UserRepository;
import io.github.mauricio_mds.mobile_app_ws.service.UserService;
import io.github.mauricio_mds.mobile_app_ws.shared.Utils;
import io.github.mauricio_mds.mobile_app_ws.shared.dto.AddressDTO;
import io.github.mauricio_mds.mobile_app_ws.shared.dto.UserDto;
import io.github.mauricio_mds.mobile_app_ws.ui.model.response.ErrorMessages;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

        List<AddressDTO> addresses = user.getAddresses();
        for (int i = 0; i < addresses.size(); i++) {
            AddressDTO addressDTO = addresses.get(i);
            addressDTO.setUserDetails(user);
            addressDTO.setAddressId(utils.generateAddressId(30));
            addresses.set(i, addressDTO);
        }

        ModelMapper mapper = new ModelMapper();
        UserEntity userEntity = mapper.map(user, UserEntity.class);
        userEntity.setUserId(utils.generateUserId(30));
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        UserEntity storedUser = userRepository.save(userEntity);
        return mapper.map(storedUser, UserDto.class);
    }

    @Override
    public UserDto getUser(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) throw new UsernameNotFoundException(email);

        ModelMapper mapper = new ModelMapper();
        return mapper.map(userEntity, UserDto.class);
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity =  userRepository.findByUserId(userId);
        if (userEntity == null) throw new UsernameNotFoundException("User with id " + userId + " not found.");

        ModelMapper mapper = new ModelMapper();
        return mapper.map(userEntity, UserDto.class);
    }

    @Override
    public UserDto updateUser(String userId, UserDto userDto) {
        UserEntity userEntity =  userRepository.findByUserId(userId);
        if (userEntity == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        userEntity.setFirstName(userDto.getFirstName());
        userEntity.setLastName(userDto.getLastName());
        UserEntity updatedUser = userRepository.save(userEntity);

        return new ModelMapper().map(updatedUser, UserDto.class);
    }

    @Override
    public void deleteUser(String userId) {
        UserEntity userEntity =  userRepository.findByUserId(userId);
        if (userEntity == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        userRepository.delete(userEntity);
    }

    @Override
    public List<UserDto> getUsers(int page, int limit) {
        if (page > 0) page--;
        List<UserDto> userDtos = new ArrayList<>();
        Pageable pageableRequest = PageRequest.of(page, limit);
        Page<UserEntity> usersPage = userRepository.findAll(pageableRequest);
        List<UserEntity> users = usersPage.getContent();

        ModelMapper mapper = new ModelMapper();
        for(UserEntity user : users) {
            userDtos.add(mapper.map(user, UserDto.class));
        }

        return userDtos;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username);
        if (userEntity == null) throw new UsernameNotFoundException(username);

        return new User(username, userEntity.getEncryptedPassword(), new ArrayList<>());
    }
}
