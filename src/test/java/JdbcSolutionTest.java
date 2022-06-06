import jdbc.dao.OrderDao;
import jdbc.dao.ProductDao;
import jdbc.dbconn.ConnectionManager;
import jdbc.model.OrderDetail;
import jdbc.model.Order;
import jdbc.model.Product;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JdbcSolutionTest {
    @Before
    public void cleanTables() throws SQLException {
        try(Statement st = ConnectionManager.getInstance().getConnection().createStatement()){
            st.execute("DELETE FROM ORDER_DETAIL");
            st.execute("DELETE FROM PRODUCT");

            st.execute("DELETE FROM ORDER_T");
        }
    }
    @Test
    public void test(){
        ProductDao prDao = new ProductDao();
        OrderDao orderDao = new OrderDao();

        Product product = new Product();
        product.setId(100);
        product.setDescription("YERBA");
        product.setPrice(200.0);
        prDao.save(product);

        Order order1 = new Order();
        order1.setDate(new Date());
        order1.setId(100);

        OrderDetail detail = new OrderDetail();
        detail.setId(999);
        detail.setProduct(product);
        detail.setQuantity(5);

        List<OrderDetail> orderDetails = new ArrayList<>();
        orderDetails.add(detail);
        order1.setOrderDetails(orderDetails);

        orderDao.save(order1);

        Order persisted_order = orderDao.get(100);
        persisted_order.getOrderDetails().stream()
                .map(od ->String.format("%d: %s $%.2f",od.getProduct().getId(),od.getProduct().getDescription(),od.getProduct().getPrice()))
                .forEach(System.out::println);
        Assert.assertFalse(persisted_order==null);
    }
}
