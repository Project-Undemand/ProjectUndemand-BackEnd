package PU.pushop.address.service;

import PU.pushop.address.entity.Addresses;
import PU.pushop.address.model.AddressesRequstDto;
import PU.pushop.address.repository.AddressRepository;
import PU.pushop.members.entity.Member;
import PU.pushop.members.repository.MemberRepositoryV1;
import lombok.RequiredArgsConstructor;
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
public class AddressService {

    private final AddressRepository addressRepository;
    private final MemberRepositoryV1 memberRepositoryV1;

    public ResponseEntity<?> updateDetailAddress(Long memberId, Long addressId, AddressesRequstDto addressDto) {
        Addresses address = addressRepository.findByAddressId(addressId)
                .orElseThrow(() -> new NoSuchElementException(ADDRESS_NOT_FOUND));

        if (address.getMember().getId().equals(memberId)) {
            Addresses updatedAddress = address.updateAddress(addressDto);

            if (updatedAddress.isDefaultAddress()) {
                List<Addresses> allAddresses = addressRepository.findAllByMemberId(memberId);
                for (Addresses addr : allAddresses) {
                    if (!addr.getAddressId().equals(updatedAddress.getAddressId())) {
                        addr.setDefaultAddress(false);
                        addressRepository.save(addr);
                    }
                }
            }
            addressRepository.save(updatedAddress);
            return ResponseEntity.status(HttpStatus.OK).body(updatedAddress);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ACCESS_DENIED);
    }

    public ResponseEntity<Addresses> createAddress(Long memberId, AddressesRequstDto addressDto) {

        Member member = memberRepositoryV1.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException(MEMBER_NOT_FOUND));
        Addresses addresses = Addresses.createAddress(addressDto, member);
        addressRepository.save(addresses);

        return ResponseEntity.status(HttpStatus.CREATED).body(addresses);
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
}
