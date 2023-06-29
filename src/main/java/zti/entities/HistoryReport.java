package zti.entities;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity(name="history_report")
@Table(name="history_report")
public class HistoryReport implements Serializable {

    @Id
    @Column(name = "id")
    private Long id;
    
    @Column(name = "price")
    private Long price;

    @Column(name = "count")
    private Long count;

    @Column(name = "time")
    private Long time;

    @ManyToOne
    @JoinColumn(name = "copartnership")
    private Copartnership copartnership;

    public final static long HISTORY_REPORT_TIME = 6*3600;

    public HistoryReport() {}

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

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Copartnership getCopartnership() {
        return copartnership;
    }

    public void setCopartnership(Copartnership copartnership) {
        this.copartnership = copartnership;
    }
}
