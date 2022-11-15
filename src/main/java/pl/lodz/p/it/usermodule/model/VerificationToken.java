package pl.lodz.p.it.usermodule.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    //TODO think about expiration date for verification token and resend verification email functionality
    public VerificationToken(User user) {
        this.user = user;
        this.token = UUID.randomUUID().toString();
        //this.expiresAt = Instant.now().plusSeconds(3600);
    }

    //private Instant expiresAt;
}
