package PU.pushop.address.model;

import PU.pushop.address.entity.Addresses;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AddressesResponseDto {
    private Long addressId;
    private String addressName;
    private String recipient;
    private String postCode;
    private String address;
    private String detailAddress;
    private String recipientPhone;
    private AddressMemberResponseDto member;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean defaultAddress;

    public AddressesResponseDto(Addresses addresses, AddressMemberResponseDto memberDto) {
        this.addressId = addresses.getAddressId();
        this.addressName = addresses.getAddressName();
        this.recipient = addresses.getRecipient();
        this.postCode = addresses.getPostCode();
        this.address = addresses.getAddress();
        this.detailAddress = addresses.getDetailAddress();
        this.recipientPhone = addresses.getRecipientPhone();
        this.member = memberDto;
        this.createdAt = addresses.getCreatedAt();
        this.updatedAt = addresses.getUpdatedAt();
        this.defaultAddress = addresses.isDefaultAddress();
    }
}
