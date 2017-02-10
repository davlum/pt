package models.sales;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;
import java.util.List;

@Entity
public class Sale extends Model {

    @Id
    @GeneratedValue
    private Long id;

    private Date date;

    private Client client;

    private Company company;

    private List<ProductSale> productSale;

    public static Model.Finder<Long, Sale> find = new Model.Finder<>(Sale.class);

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public List<ProductSale> getProductSale() {
        return productSale;
    }

    public void setProductSale(List<ProductSale> productSale) {
        this.productSale = productSale;
    }
}
