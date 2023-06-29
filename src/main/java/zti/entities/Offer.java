package zti.entities;

import jakarta.persistence.*;

@Entity(name = "offers")
@Table(name = "offers")
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

    @Column(name = "price")
    private Long price;

    @Column(name = "count")
    private Long count;

    @ManyToOne
    @JoinColumn(name = "copartnership")
    private Copartnership copartnership;


    @Column(name = "is_sale_offer")
    private Boolean isSaleOffer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

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

    public Boolean getSaleOffer() {
        return isSaleOffer;
    }

    public void setSaleOffer(Boolean saleOffer) {
        isSaleOffer = saleOffer;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
