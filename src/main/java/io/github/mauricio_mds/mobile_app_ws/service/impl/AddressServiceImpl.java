package io.github.mauricio_mds.mobile_app_ws.service.impl;

import io.github.mauricio_mds.mobile_app_ws.io.entity.AddressEntity;
import io.github.mauricio_mds.mobile_app_ws.io.entity.UserEntity;
import io.github.mauricio_mds.mobile_app_ws.io.repository.AddressRepository;
import io.github.mauricio_mds.mobile_app_ws.io.repository.UserRepository;
import io.github.mauricio_mds.mobile_app_ws.service.AddressService;
import io.github.mauricio_mds.mobile_app_ws.shared.dto.AddressDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    AddressRepository addressRepository;

    @Override
    public List<AddressDTO> getAddresses(String userId) {
        List<AddressDTO> addressesDTO = new ArrayList<>();
        ModelMapper mapper = new ModelMapper();
        UserEntity userEntity = userRepository.findByUserId(userId);
        if (userEntity == null) return addressesDTO;

        Iterable<AddressEntity> addressEntities = addressRepository.findAllByUserDetails(userEntity);
        for (AddressEntity addressEntity : addressEntities) {
            addressesDTO.add(mapper.map(addressEntity, AddressDTO.class));
        }

        return addressesDTO;
    }

    @Override
    public AddressDTO getAddress(String addressId) {
        AddressEntity entity = addressRepository.findByAddressId(addressId);
        if (entity == null) return null;

        return new ModelMapper().map(entity, AddressDTO.class);
    }
}
