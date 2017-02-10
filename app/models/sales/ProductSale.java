package models.sales;


import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ProductSale extends Model {

    @Id
    @GeneratedValue
    private Long id;

    private Long quantity;

    private Product product;

}
