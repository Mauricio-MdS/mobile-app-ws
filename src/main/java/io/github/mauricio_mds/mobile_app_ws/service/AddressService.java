package io.github.mauricio_mds.mobile_app_ws.service;

import io.github.mauricio_mds.mobile_app_ws.shared.dto.AddressDTO;

import java.util.List;

public interface AddressService {
    List<AddressDTO> getAddresses(String userId);

    AddressDTO getAddress(String addressId);
}
