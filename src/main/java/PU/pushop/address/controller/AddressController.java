package PU.pushop.address.controller;

import PU.pushop.address.entity.Addresses;
import PU.pushop.address.model.AddressesRequstDto;
import PU.pushop.address.service.AddressService;
import PU.pushop.global.authorization.MemberAuthorizationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/address/{memberId}")
    public ResponseEntity<List<Addresses>> fetchAddressList(@PathVariable Long memberId) {
        MemberAuthorizationUtil.verifyUserIdMatch(memberId);
        return addressService.fetchAddressList(memberId);
    }

    @PostMapping("/address/{memberId}")
    public ResponseEntity<?> createAddress(@PathVariable Long memberId, @RequestBody AddressesRequstDto addressDto) {
        MemberAuthorizationUtil.verifyUserIdMatch(memberId);

        return addressService.createAddress(memberId, addressDto);
    }

    @GetMapping("/address/{memberId}/{addressId}")
    public ResponseEntity<?> fetchDetailAddress(@PathVariable Long memberId, @PathVariable Long addressId) {
        MemberAuthorizationUtil.verifyUserIdMatch(memberId);
        return addressService.fetchDetailAddress(memberId, addressId);

    }

    @PutMapping("/address/{memberId}/{addressId}")
    public ResponseEntity<?> updateDetailAddress(@PathVariable Long memberId, @PathVariable Long addressId, @RequestBody AddressesRequstDto addressDto) {
        MemberAuthorizationUtil.verifyUserIdMatch(memberId);
        return addressService.updateDetailAddress(memberId, addressId, addressDto);
    }

    @DeleteMapping("/address/{memberId}/{addressId}")
    public ResponseEntity<?> deleteDetailAddress(@PathVariable Long memberId, @PathVariable Long addressId) {
        MemberAuthorizationUtil.verifyUserIdMatch(memberId);
        return addressService.deleteDetailAddress(memberId, addressId);
    }

    @PutMapping("/address/default/{memberId}/{addressId}")
    public ResponseEntity<?> updateIsDefaultAddress(@PathVariable Long memberId, @PathVariable Long addressId) {
        MemberAuthorizationUtil.verifyUserIdMatch(memberId);
        return addressService.setDefaultAddress(memberId, addressId);
    }



}
