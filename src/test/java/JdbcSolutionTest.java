import jdbc.dao.OrderDao;
import jdbc.dao.ProductDao;
import jdbc.model.OrderDetail;
import jdbc.model.Order;
import jdbc.model.Product;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JdbcSolutionTest {
    @Test
    public void test(){
        ProductDao prDao = new ProductDao();
        OrderDao orderDao = new OrderDao();

        Product product = new Product();
        product.setId(1);
        product.setDescription("YERBA");
        prDao.save(product);

        Order order1 = new Order();
        order1.setDate(new Date());
        order1.setId(1);

        OrderDetail detail = new OrderDetail();
        detail.setId(1);
        detail.setProduct(product);
        detail.setQuantity(5);

        List<OrderDetail> orderDetails = new ArrayList<>();
        orderDetails.add(detail);
        order1.setOrderDetails(orderDetails);

        System.out.println(orderDao.save(order1));
    }
}
