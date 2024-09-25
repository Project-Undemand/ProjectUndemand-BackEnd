package PU.pushop.address.service;

import PU.pushop.address.entity.Addresses;
import PU.pushop.address.model.AddressMemberResponseDto;
import PU.pushop.address.model.AddressesRequstDto;
import PU.pushop.address.model.AddressesResponseDto;
import PU.pushop.address.repository.AddressRepository;
import PU.pushop.members.entity.Member;
import PU.pushop.members.repository.MemberRepositoryV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static PU.pushop.global.ResponseMessageConstants.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AddressService {

    private final AddressRepository addressRepository;
    private final MemberRepositoryV1 memberRepositoryV1;

    private void setDefaultAddressToFalseExcept(Long memberId, Long addressIdToKeepDefault) {
        // 주어진 memberId로 모든 주소를 조회.
        List<Addresses> allAddresses = addressRepository.findAllByMemberId(memberId);
        // 기본 배송지로 설정된 주소 외의 모든 주소의 기본 배송지 상태를 false로 변경.
        for (Addresses addr : allAddresses) {
            if (!addr.getAddressId().equals(addressIdToKeepDefault)) {
                addr.setDefaultAddress(false);
                addressRepository.save(addr);
            }
        }
    }

    public ResponseEntity<?> updateDetailAddress(Long memberId, Long addressId, AddressesRequstDto addressDto) {
        // 주어진 addressId로 주소를 조회. 주소가 없으면 예외 발생.
        Addresses address = addressRepository.findByAddressId(addressId)
                .orElseThrow(() -> new NoSuchElementException(ADDRESS_NOT_FOUND));

        // 조회된 주소의 회원 ID가 주어진 memberId와 일치하는지 확인.
        if (address.getMember().getId().equals(memberId)) {
            // 주소를 업데이트.
            Addresses updatedAddress = address.updateAddress(addressDto);
            log.info("addressDto = {}", addressDto.isDefaultAddress());
            // 업데이트된 주소가 기본 배송지로 설정된 경우, 다른 모든 주소의 기본 배송지 상태를 false로 변경.
            log.info("updatedAddress = {}", updatedAddress.isDefaultAddress());
            if (updatedAddress.isDefaultAddress()) {
                setDefaultAddressToFalseExcept(memberId, updatedAddress.getAddressId());
            }

            // 업데이트된 주소를 저장.
            addressRepository.save(updatedAddress);

            // 엔티티를 DTO로 변환.
            AddressesResponseDto responseDto = convertToAddressesResponseDto(updatedAddress);

            return ResponseEntity.status(HttpStatus.OK).body(responseDto);
        }

        // 회원 ID가 일치하지 않으면 접근 거부 응답을 반환.
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ACCESS_DENIED);
    }

    public ResponseEntity<AddressesResponseDto> createAddress(Long memberId, AddressesRequstDto addressDto) {
        // 주어진 memberId로 회원을 조회. 회원이 없으면 예외 발생.
        Member member = memberRepositoryV1.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException(MEMBER_NOT_FOUND));
        log.info("addressDto = {}", addressDto);
        log.info("addressDto = {}", addressDto.isDefaultAddress());

        // 회원과 Address request 입력값을 통해, Address를 새로 생성.
        Addresses addresses = Addresses.createAddress(addressDto, member);

        // 새로 생성된 주소가 기본 배송지로 설정된 경우, 다른 모든 주소의 기본 배송지 상태를 false로 변경.
        if (addresses.isDefaultAddress()) {
            setDefaultAddressToFalseExcept(memberId, addresses.getAddressId());
        }

        // 새로 생성된 주소를 저장.
        addressRepository.save(addresses);
        log.info("addresses = {}", addresses.isDefaultAddress());

        // 엔티티를 DTO로 변환.
        AddressesResponseDto responseDto = convertToAddressesResponseDto(addresses);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }


    public ResponseEntity<List<Addresses>> fetchAddressList(Long memberId) {
        Member member = memberRepositoryV1.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException(MEMBER_NOT_FOUND));

        List<Addresses> memberAddressList = addressRepository.findAllByMember(member)
                .orElseThrow(() -> new NoSuchElementException(ADDRESS_NOT_FOUND));

        return ResponseEntity.status(HttpStatus.OK).body(memberAddressList);
    }

    public ResponseEntity<?> fetchDetailAddress(Long memberId, Long addressId) {
        Addresses address = addressRepository.findByAddressId(addressId)
                .orElseThrow(() -> new NoSuchElementException(ADDRESS_NOT_FOUND));

        if (address.getMember().getId().equals(memberId)) {
            return ResponseEntity.status(HttpStatus.OK).body(address);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ACCESS_DENIED);
    }

    public ResponseEntity<?> deleteDetailAddress(Long memberId, Long addressId) {
        Addresses address = addressRepository.findByAddressId(addressId)
                .orElseThrow(() -> new NoSuchElementException(ADDRESS_NOT_FOUND));

        if (address.getMember().getId().equals(memberId)) {
            addressRepository.delete(address);
            return ResponseEntity.status(HttpStatus.OK).body(ADDRESS_DELETE_SUCCESS);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ACCESS_DENIED);
    }

    public ResponseEntity<?> setDefaultAddress(Long memberId, Long addressId) {
        Addresses address = addressRepository.findByAddressId(addressId)
                .orElseThrow(() -> new NoSuchElementException("해당 주소를 찾을 수 없습니다."));

        if (address.getMember().getId().equals(memberId)) {
            // 모든 주소의 기본 설정 해제
            List<Addresses> allAddresses = addressRepository.findAllByMemberId(memberId);
            for (Addresses addr : allAddresses) {
                if (addr.isDefaultAddress()) {
                    addr.setDefaultAddress(false);
                    addressRepository.save(addr);
                }
            }

            // 해당 주소를 기본 주소로 설정
            address.setDefaultAddress(true);
            addressRepository.save(address);
            return ResponseEntity.status(HttpStatus.OK).body(address);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("요청하신 주소의 Member id 와 Address Member 의 Id 가 일치하지 않습니다.");
    }

    public AddressMemberResponseDto convertToAddressMemberDto(Member member) {
        return new AddressMemberResponseDto(
                member.getId(),
                member.getEmail(),
                member.getIsActive(),
                member.getMemberRole().toString(),
                member.getSocialType().toString()
        );
    }

    public AddressesResponseDto convertToAddressesResponseDto(Addresses addresses) {
        AddressMemberResponseDto memberDto = convertToAddressMemberDto(addresses.getMember());
        return new AddressesResponseDto(addresses, memberDto);
    }
}
