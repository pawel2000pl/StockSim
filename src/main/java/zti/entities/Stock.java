package zti.entities;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity(name="stocks")
@Table(name="stocks")
public class Stock implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @ManyToOne
    @JoinColumn(name="copartnership")
    private Copartnership copartnership;

    public Stock() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Copartnership getCopartnership() {
        return copartnership;
    }

    public void setCopartnership(Copartnership copartnership) {
        this.copartnership = copartnership;
    }

}
