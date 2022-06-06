package jdbc.dao;

import jdbc.dbconn.ConnectionManager;
import jdbc.model.OrderDetail;
import jdbc.model.Order;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class OrderDao implements DAO<Order> {
    private static final String selectById = "SELECT O.ID AS ID, O.DATE_ORDER, D.ID AS DETAIL_ID FROM ORDER_T O" +
            " INNER JOIN DETAIL D ON O.ID = D.ORDER_ID WHERE O.ID = ?";
    private static final String selectAll = "SELECT O.ID AS ID, O.DATE_ORDER, D.ID AS DETAIL_ID FROM ORDER_T O" +
            " INNER JOIN DETAIL D ON O.ID = D.ORDER_ID";
    private static final String insert = "INSERT INTO ORDER_T (ID,DATE_ORDER) VALUES (?,?)";
    private static final String deleteById = "DELETE FROM ORDER_T WHERE ID = ?";
    private static final String updateById = "UPDATE FROM ORDER_T SET DATE_ORDER = ? WHERE ID = ?";
    private final DetailOrderDao detailDao = new DetailOrderDao();

    @Override
    public Order get(int id) {
        Order order = null;
        try(ConnectionManager cm = ConnectionManager.getInstance()){
            try(PreparedStatement statement = cm.getConnection().prepareStatement(selectById)){
                statement.setInt(1,id);
                ResultSet rs = statement.executeQuery();
                rs.getMetaData();
                order = new Order();
                List<OrderDetail> detailList = new ArrayList<>();
                while (rs.next()) {
                    order.setId(rs.getInt("ID"));
                    order.setDate(rs.getDate("ORDER_DATE"));
                    detailList.add(detailDao.get(rs.getInt("DETAIL_ID")));
                }
                order.setOrderDetails(detailList);
                return order;
            }
        } catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }


    @Override
    public List<Order> getAll() {
        List<Order> orderList = null;
        try(ConnectionManager cm = ConnectionManager.getInstance()){
            try(PreparedStatement statement = cm.getConnection().prepareStatement(selectById)){
                ResultSet rs = statement.executeQuery();
                rs.getMetaData();
                orderList = new ArrayList<>();
                while (rs.next()) {
                    Order order = new Order();
                    List<OrderDetail> detailList = new ArrayList<>();

                    order.setId(rs.getInt("ID"));
                    order.setDate(rs.getDate("ORDER_DATE"));
                    /*
                    Corte de control? ya nos fuimos...
                     */
                    while(rs.getInt("ID")==order.getId()){
                        detailList.add(detailDao.get(rs.getInt("DETAIL_ID")));
                        rs.next();
                    }
                    order.setOrderDetails(detailList);
                    orderList.add(order);
                }
                return orderList;
            }
        } catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public int save(Order order) {

        for(OrderDetail detail : order.getOrderDetails()){
            detailDao.save(detail);
        }
        try(ConnectionManager cm = ConnectionManager.getInstance()){
            try(PreparedStatement statement = cm.getConnection().prepareStatement(insert)){
                statement.setInt(1,order.getId());
                statement.setDate(2,new Date(order.getDate().getTime()));
                return statement.executeUpdate();
            }
        } catch(Exception ex){
            ex.printStackTrace();
            return -1;
        }
    }

    @Override
    public int update(Order product) {
        try(ConnectionManager cm = ConnectionManager.getInstance()){
            try(PreparedStatement statement = cm.getConnection().prepareStatement(updateById)){
                statement.setDate(1,new Date(product.getDate().getTime()));
                return statement.executeUpdate();
            }
        } catch(Exception ex){
            ex.printStackTrace();
            return -1;
        }
    }

    @Override
    public int delete(Order order) {
        try(ConnectionManager cm = ConnectionManager.getInstance()){
            try(PreparedStatement statement = cm.getConnection().prepareStatement(deleteById)){
                statement.setInt(1,order.getId());
                return statement.executeUpdate();
            }
        } catch(Exception ex){
            ex.printStackTrace();
            return -1;
        }
    }
}
