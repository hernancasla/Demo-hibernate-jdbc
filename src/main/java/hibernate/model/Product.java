package hibernate.model;

import javax.persistence.*;

@Entity
@Table(name="PRODUCT")
public class Product {
    @Id
    @Column(name="ID")
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;


    @Column(name="DESCRIPTION")
    private String description;

    @Column(name="PRICE")
    private Double price;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
