package zti.entities;

import jakarta.persistence.*;

import java.security.SecureRandom;
import java.util.Base64;

@Entity(name="auth_tokens")
@Table(name="auth_tokens")
public class AuthToken {

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public final static long TOKEN_EXPIRE_TIME = 6*3600;

    @Id
    @Column(name = "hash", length = 128)
    private String hash;
    @ManyToOne
    @JoinColumn(name = "user")
    private User user;
    @Column(name = "expire_time")
    private Long expireTime;

    public AuthToken() {};

    public static AuthToken createNew(User user) {
        AuthToken result = new AuthToken();
        result.user = user;

        byte[] randomBytes = new byte[96];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(randomBytes);
        result.hash = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        result.expireTime = System.currentTimeMillis() / 1000L + AuthToken.TOKEN_EXPIRE_TIME;
        return result;
    }


}
