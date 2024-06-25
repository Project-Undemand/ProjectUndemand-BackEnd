package PU.pushop.address.model;


import lombok.Data;

@Data
public class AddressesRequstDto {

    private String addressName;

    private String recipient;

    private String postCode;

    private String address;

    private String detailAddress;

    private boolean isDefaultAddress;

    private String recipientPhone;

}
