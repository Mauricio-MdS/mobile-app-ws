package io.github.mauricio_mds.mobile_app_ws.ui.controller;

import io.github.mauricio_mds.mobile_app_ws.exceptions.UserServiceException;
import io.github.mauricio_mds.mobile_app_ws.service.AddressService;
import io.github.mauricio_mds.mobile_app_ws.service.UserService;
import io.github.mauricio_mds.mobile_app_ws.shared.dto.AddressDTO;
import io.github.mauricio_mds.mobile_app_ws.shared.dto.UserDto;
import io.github.mauricio_mds.mobile_app_ws.ui.model.request.UserDetailsRequestModel;
import io.github.mauricio_mds.mobile_app_ws.ui.model.response.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
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
@RequestMapping("users")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    AddressService addressService;

    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest getUser(@PathVariable String id) {
        UserDto userDto = userService.getUserByUserId(id);
        return new ModelMapper().map(userDto, UserRest.class);
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) {
        if (userDetails.getFirstName() == null || userDetails.getFirstName().isEmpty())
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

        ModelMapper mapper = new ModelMapper();
        UserDto userDto = mapper.map(userDetails, UserDto.class);
        UserDto createdUser = userService.createUser(userDto);
        return mapper.map(createdUser, UserRest.class);
    }

    @PutMapping(path = "/{id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
        if (userDetails.getFirstName() == null || userDetails.getFirstName().isEmpty())
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

        ModelMapper mapper = new ModelMapper();
        UserDto userDto = mapper.map(userDetails, UserDto.class);
        UserDto updatedUser = userService.updateUser(id, userDto);
        return mapper.map(updatedUser, UserRest.class);
    }

    @DeleteMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel deleteUser(@PathVariable String id) {
        OperationStatusModel statusModel = new OperationStatusModel();
        statusModel.setOperationName(RequestOperationName.DELETE.name());
        userService.deleteUser(id);
        statusModel.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return statusModel;
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "1") int page,
                                   @RequestParam(value="limit", defaultValue = "25") int limit) {
        List<UserRest> usersRest = new ArrayList<>();
        List<UserDto> users = userService.getUsers(page, limit);
        ModelMapper mapper = new ModelMapper();

        for (UserDto userDto : users) {
            usersRest.add(mapper.map(userDto, UserRest.class));
        }

        return usersRest;
    }

    @GetMapping(path = "/{id}/addresses",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public CollectionModel<AddressRest> getUserAddresses(@PathVariable String id) {
        List<AddressRest> addressesRest = new ArrayList<>();
        List<AddressDTO> addressesDTO = addressService.getAddresses(id);
        if (addressesDTO != null && !addressesDTO.isEmpty()) {
            Type listType = new TypeToken<List<AddressRest>>() {}.getType();
            addressesRest = new ModelMapper().map(addressesDTO, listType);
            for (AddressRest addressRest : addressesRest) {
                Link selfLink = WebMvcLinkBuilder
                        .linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
                                .getUserAddress(id, addressRest.getAddressId()))
                        .withSelfRel();
                addressRest.add(selfLink);
            }
        }
        Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(id).withRel("user");
        Link selfLink = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(id))
                .withSelfRel();
        return CollectionModel.of(addressesRest, userLink, selfLink);
    }

    @GetMapping(path = "/{userId}/addresses/{addressId}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public EntityModel<AddressRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId) {
        AddressDTO addressDTO = addressService.getAddress(addressId);
        ModelMapper mapper = new ModelMapper();
        AddressRest addressRest = mapper.map(addressDTO, AddressRest.class);
        Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(userId).withRel("user");
        Link userAddressesLink = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(userId))
                .withRel("addresses");
        Link selfLink = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddress(userId, addressId))
                .withSelfRel();
        return EntityModel.of(addressRest, Arrays.asList(userLink, userAddressesLink, selfLink));
    }

}
