package PU.pushop.address.model;

import lombok.Data;

@Data
public class AddressMemberResponseDto {
    private Long id;
    private String email;
    private boolean isActive;
    private String memberRole;
    private String socialType;

    public AddressMemberResponseDto(Long id, String email, boolean isActive, String memberRole, String socialType) {
        this.id = id;
        this.email = email;
        this.isActive = isActive;
        this.memberRole = memberRole;
        this.socialType = socialType;
    }
}
