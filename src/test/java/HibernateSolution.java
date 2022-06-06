import hibernate.dao.OrderDao;
import hibernate.dao.ProductDao;
import hibernate.model.Order;
import hibernate.model.OrderDetail;
import hibernate.model.Product;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class HibernateSolution {
    @Before
    public void setup(){
        System.out.println("before");
    }
    @Test
    public void testSaveDao(){
        ProductDao productDao = new ProductDao();

        OrderDetail orderDetail1 = new OrderDetail();
        orderDetail1.setId(1);
        orderDetail1.setProduct(productDao.get(1));
        orderDetail1.setQuantity(5);

        OrderDetail orderDetail2 = new OrderDetail();
        orderDetail2.setId(2);
        orderDetail2.setProduct(productDao.get(2));
        orderDetail2.setQuantity(3);

        OrderDetail orderDetail3 = new OrderDetail();
        orderDetail3.setId(3);
        orderDetail3.setProduct(productDao.get(3));
        orderDetail3.setQuantity(10);

        Order order = new Order();
        //order.setDate(new Date());
        order.setOrderDetails(Arrays.asList(orderDetail1,orderDetail2,orderDetail3));

        OrderDao orderDao = new OrderDao();
        int id = orderDao.save(order);
        System.out.println("Nuevo Order ID: "+id);
        orderDao.get(id).getOrderDetails().stream().forEach(e->{
            System.out.println("detalle: "+e.getId());
            System.out.println("producto: "+e.getProduct().getDescription());
            System.out.println("cantidad: "+e.getQuantity());
        });
        Assert.assertTrue("El ID deberia tener valor", id>0);
    }
    @Test
    public void testSaveProduct(){
        Product yerba = new Product();
        yerba.setDescription("YERBA");
        yerba.setPrice(200.0);

        Product azucar = new Product();
        azucar.setDescription("AZUCAR");
        azucar.setPrice(100.0);

        Product fideos = new Product();
        fideos.setDescription("FIDEOS");
        fideos.setPrice(50.5);

        ProductDao productDao = new ProductDao();
        productDao.save(yerba);
        productDao.save(azucar);
        productDao.save(fideos);

        List<Product> productList = productDao.getAll();
        productList.stream().map(p ->String.format("%d: %s $%.2f",p.getId(),p.getDescription(),p.getPrice()))
                .forEach(System.out::println);
        Assert.assertFalse(productList.isEmpty());
    }
    @Test
    public void testGetAll(){
        ProductDao productDao = new ProductDao();
        List<Product> productList = productDao.getAll();
        productList.stream().map(product -> product.getId() +": "+product.getDescription()).forEach(System.out::println);
        Assert.assertFalse(productList.isEmpty());
    }

    @After
    public  void close(){
        System.out.println("after");
    }
}
