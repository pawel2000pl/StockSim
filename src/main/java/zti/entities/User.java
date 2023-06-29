package zti.entities;

import jakarta.persistence.*;
import org.eclipse.persistence.annotations.Noncacheable;
import org.eclipse.persistence.annotations.SerializedObject;
import org.mindrot.jbcrypt.BCrypt;

import java.io.Serializable;
import java.util.List;

@Entity(name = "users")
@Table(name = "users")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "username", length = 64)
    private String username;
    @Column(name = "password", length = 60)
    private String password;
    @Column(name="cash")
    private Long cash;
    @OneToMany(mappedBy = "user")
    private List<AuthToken> tokens;
    @OneToMany(mappedBy = "user")
    @Noncacheable
    private List<Wallet> wallet;

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean checkPassword(String plainPassword) {
        try {
            return BCrypt.checkpw(plainPassword, this.password);
        } catch (Exception error) {
            return false;
        }
    }

    public List<Wallet> getWallet() {
        return wallet;
    }

    public void setWallet(List<Wallet> wallet) {
        this.wallet = wallet;
    }

    public void setPassword(String newPassword) {
        password = BCrypt.hashpw(newPassword, BCrypt.gensalt());
    }

    public Long getCash() {
        return cash;
    }

    public void setCash(Long cash) {
        this.cash = cash;
    }
}
