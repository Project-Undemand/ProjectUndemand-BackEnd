package PU.pushop.profile;


import lombok.Data;

@Data
public class MemberDTO {

    private Long id;
    private String email;
    private String username;
    private String nickname;
    private String phone;
    private Boolean active;
    private Boolean certifyByMail;
    private Boolean admin;
    private String member_role;
    private String social_type;
    private String joined_at;
}
