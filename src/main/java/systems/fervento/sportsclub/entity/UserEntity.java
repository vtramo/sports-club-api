package systems.fervento.sportsclub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Setter
@Getter
@jakarta.persistence.Entity
public class UserEntity {
    @Id
    @GeneratedValue(generator = "ID_GENERATOR")
    private Long id;

    @OneToMany(mappedBy = "owner", cascade = {CascadeType.PERSIST})
    @org.hibernate.annotations.OnDelete(
        action = org.hibernate.annotations.OnDeleteAction.CASCADE
    )
    private Set<BillingDetailsEntity> allBillingDetails = new HashSet<>();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.PERSIST)
    private Set<SportsFacilityEntity> sportsFacilities = new HashSet<>();

    @NotBlank
    @NotNull
    @Column(unique = true)
    private String fiscalCode;

    @NotBlank
    @NotNull
    @Email(message = "Email should be valid", regexp = "^[\\w.\\.]+@([\\w.]+\\.)+[\\w.]{2,4}$")
    @Column(unique = true)
    private String email;

    @NotBlank
    @NotNull
    @Column(unique = true)
    private String username;

    @NotBlank
    @NotNull
    private String password;

    @NotBlank
    @NotNull
    private String firstName;

    @NotBlank
    @NotNull
    private String lastName;

    @NotNull
    @Embedded
    private Address homeAddress;

    @CreationTimestamp
    private LocalDateTime registeredOn;

    public boolean addBillingDetails(BillingDetailsEntity billingDetails) {
        Objects.requireNonNull(billingDetails);
        billingDetails.setOwner(this);
        return allBillingDetails.add(billingDetails);
    }

    public boolean addSportsFacility(SportsFacilityEntity sportsFacility) {
        Objects.requireNonNull(sportsFacility);
        sportsFacility.setOwner(this);
        return sportsFacilities.add(sportsFacility);
    }
}
