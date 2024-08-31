package io.github.mauricio_mds.mobile_app_ws.io.repository;

import io.github.mauricio_mds.mobile_app_ws.io.entity.AddressEntity;
import io.github.mauricio_mds.mobile_app_ws.io.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends CrudRepository<AddressEntity, Long> {
    List<AddressEntity> findAllByUserDetails(UserEntity userEntity);
    AddressEntity findByAddressId(String addressId);
}
