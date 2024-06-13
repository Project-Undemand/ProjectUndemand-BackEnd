package PU.pushop.address.controller;

import PU.pushop.address.repository.AddressRepository;
import PU.pushop.address.entity.Addresses;
import PU.pushop.address.model.AddressListResponseDto;
import PU.pushop.address.model.AddressesRequstDto;
import PU.pushop.global.authorization.MemberAuthorizationUtil;
import PU.pushop.members.entity.Member;
import PU.pushop.members.repository.MemberRepositoryV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

import static PU.pushop.global.ResponseMessageConstants.ADDRESS_NOT_FOUND;
import static PU.pushop.global.ResponseMessageConstants.MEMBER_NOT_FOUND;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class AddressController {

    private final AddressRepository addressRepository;
    private final MemberRepositoryV1 memberRepositoryV1;

    @GetMapping("/address/{memberId}")
    public ResponseEntity<List<Addresses>> fetchAddressList(@PathVariable Long memberId) {
        MemberAuthorizationUtil.verifyUserIdMatch(memberId);
        Member member = memberRepositoryV1.findById(memberId).orElseThrow(() -> new NoSuchElementException(MEMBER_NOT_FOUND));
        // 저장한 상품의 pk
        List<Addresses> memberAddressList = addressRepository.findAllByMember(member).orElseThrow(() -> new NoSuchElementException(ADDRESS_NOT_FOUND));

        return ResponseEntity.status(HttpStatus.OK).body(memberAddressList);
    }

    @PostMapping("/address/{memberId}")
    public ResponseEntity<Addresses> createAddress(@PathVariable Long memberId, @RequestBody AddressesRequstDto addressDto) {
        MemberAuthorizationUtil.verifyUserIdMatch(memberId);

        Member member = memberRepositoryV1.findById(memberId).orElseThrow(() -> new NoSuchElementException(MEMBER_NOT_FOUND));
        Addresses addresses = Addresses.createAddress(addressDto, member);
        addressRepository.save(addresses);

        AddressListResponseDto addressListResponseDto = new AddressListResponseDto();
        return ResponseEntity.status((HttpStatus.CREATED)).body(addresses);
    }

    @GetMapping("/address/{memberId}/{addressId}")
    public ResponseEntity<?> fetchDetailAddress(@PathVariable Long memberId, @PathVariable Long addressId) {
        MemberAuthorizationUtil.verifyUserIdMatch(memberId);
        Addresses address = addressRepository.findByAddressId(addressId).orElseThrow(() -> new NoSuchElementException(ADDRESS_NOT_FOUND));
        if (address.getMember().getId().equals(memberId)) {
            return ResponseEntity.status(HttpStatus.OK).body(address);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("요청하신 주소의 Member id 와 Address Member 의 Id 가 일치하지 않습니다. ");
    }

    @PutMapping("/address/{memberId}/{addressId}")
    public ResponseEntity<?> updateDetailAddress(@PathVariable Long memberId, @PathVariable Long addressId, @RequestBody AddressesRequstDto addressDto) {
        MemberAuthorizationUtil.verifyUserIdMatch(memberId);
        Addresses address = addressRepository.findByAddressId(addressId).orElseThrow(() -> new NoSuchElementException(ADDRESS_NOT_FOUND));
        if (address.getMember().getId().equals(memberId)) {
            Addresses updatedAddress = address.updateAddress(addressDto);
            addressRepository.save(updatedAddress);
            return ResponseEntity.status(HttpStatus.OK).body(updatedAddress);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("요청하신 주소의 Member id 와 Address Member 의 Id 가 일치하지 않습니다. ");
    }

    @DeleteMapping("/address/{memberId}/{addressId}")
    public ResponseEntity<?> deleteDetailAddress(@PathVariable Long memberId, @PathVariable Long addressId) {
        MemberAuthorizationUtil.verifyUserIdMatch(memberId);
        Addresses address = addressRepository.findByAddressId(addressId).orElseThrow(() -> new NoSuchElementException(ADDRESS_NOT_FOUND));
        if (address.getMember().getId().equals(memberId)) {
            addressRepository.delete(address);
            return ResponseEntity.status(HttpStatus.OK).body("Address deleted.");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("요청하신 주소의 Member id 와 Address Member 의 Id 가 일치하지 않습니다. ");
    }
}
