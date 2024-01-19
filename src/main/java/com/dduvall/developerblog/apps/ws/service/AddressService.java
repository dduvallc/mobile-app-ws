package com.dduvall.developerblog.apps.ws.service;

import com.dduvall.developerblog.apps.ws.shared.dto.AddressDTO;

import java.util.List;

public interface AddressService {

    List<AddressDTO> getAddresses(String userId);

    AddressDTO getAddress(String addressId);

}
