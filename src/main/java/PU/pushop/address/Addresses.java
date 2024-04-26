package PU.pushop.address;


import PU.pushop.profile.MemberProfile;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "addresses")
public class Addresses {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long addressId;

    private String zipCode;

    private String address;

    private String addressDetail;

    @Column(name = "is_default_address")
    private boolean isDefaultAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private MemberProfile profile;
}
