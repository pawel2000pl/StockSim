package zti.entities;

import jakarta.persistence.*;

import org.eclipse.persistence.annotations.ReadOnly;

@Entity(name = "wallets")
@Table(name = "wallets")
@ReadOnly
public class Wallet {
    @Id
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "copartnership_id")
    private Copartnership copartnership;

    @Column(name = "aver_price")
    private Double averPrice;

    @Column(name = "count")
    private Long count;

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Copartnership getCopartnership() {
        return copartnership;
    }

    public void setCopartnership(Copartnership copartnership) {
        this.copartnership = copartnership;
    }

    public Double getAverPrice() {
        return averPrice;
    }

    public void setAverPrice(Double averPrice) {
        this.averPrice = averPrice;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


}
