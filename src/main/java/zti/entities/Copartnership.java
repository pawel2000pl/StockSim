package zti.entities;

import jakarta.persistence.*;
import org.eclipse.persistence.annotations.ReadOnly;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Entity(name = "copartnerships")
@Table(name = "copartnerships")
@NamedQuery(name="findAll", query="SELECT c FROM copartnerships c ORDER BY c.name")
public class Copartnership implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "name", length = 64)
    private String name;

    public Long getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Long salePrice) {
        this.salePrice = salePrice;
    }

    public Long getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Long purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    @Column(name = "sale_price")
    private Long salePrice;
    @Column(name = "purchase_price")
    private Long purchasePrice;

    @OneToMany(mappedBy = "copartnership")
    private List<Stock> stocks;

    public Copartnership() {}

    public Long getStockCount() {
        return (long)stocks.size();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
}
