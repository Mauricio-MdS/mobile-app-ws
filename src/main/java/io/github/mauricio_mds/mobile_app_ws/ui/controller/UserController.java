package io.github.mauricio_mds.mobile_app_ws.ui.controller;

import io.github.mauricio_mds.mobile_app_ws.service.UserService;
import io.github.mauricio_mds.mobile_app_ws.shared.dto.UserDto;
import io.github.mauricio_mds.mobile_app_ws.ui.model.request.UserDetailsRequestModel;
import io.github.mauricio_mds.mobile_app_ws.ui.model.response.UserRest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping
    public String getUser() {
        return "get user was called";
    }

    @PostMapping
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) {
        UserRest userRest = new UserRest();
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);
        UserDto createdUser = userService.createUser(userDto);
        BeanUtils.copyProperties(createdUser, userRest);
        return userRest;
    }

    @PutMapping
    public String updateUser() {
        return "update user was called";
    }

    @DeleteMapping
    public String deleteUser() {
        return "delete user was called";
    }
}
