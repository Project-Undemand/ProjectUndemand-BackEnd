package PU.pushop.address;


import PU.pushop.profile.Profile;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "address")
public class Address {

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
    private Profile profile;
}
