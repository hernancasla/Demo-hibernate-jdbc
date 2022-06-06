package jdbc.dao;

import jdbc.dbconn.ConnectionManager;
import jdbc.model.OrderDetail;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailDao implements DAO<OrderDetail>{
    private static final String selectById = "SELECT ID, PRODUCT_ID, QUANTITY FROM ORDER_DETAIL WHERE ID = ?";
    private static final String selectAll = "SELECT ID, PRODUCT_ID, QUANTITY FROM ORDER_DETAIL";
    private static final String insert = "INSERT INTO ORDER_DETAIL (ID,PRODUCT_ID,QUANTITY,ORDER_ID) VALUES (?,?,?,?)";
    private static final String deleteById = "DELETE FROM ORDER_DETAIL WHERE ID = ?";
    private static final String updateById = "UPDATE FROM DETAIL_ORDER SET PRODUCT_ID = ?, QUANTITY= ? WHERE ID = ?";
    private final ProductDao productDao = new ProductDao();


    @Override
    public OrderDetail get(int id) {
        OrderDetail detail = null;
        try(ConnectionManager cm = ConnectionManager.getInstance()){
            try(PreparedStatement statement = cm.getConnection().prepareStatement(selectById)){
                statement.setInt(1,id);
                ResultSet rs = statement.executeQuery();
                rs.getMetaData();
                detail = new OrderDetail();
                while (rs.next()) {
                    detail.setId(rs.getInt("ID"));
                    detail.setQuantity(rs.getInt("QUANTITY"));

                    /*
                    Atencion! llamamos a un dao dentro de un DAO se ve al menos raro...
                    Podriamos realizar un join en la consulta y armar directamente el objecto Product aca mismo
                    pero estariamos acoplando.. dado que si manana cambiamos la clase producto? que pasa con nuestro
                    DetailDao? ... hay que cambiarlo.
                     */
                    detail.setProduct(productDao.get(rs.getInt("PRODUCT_ID")));
                    continue;
                }
                return detail;
            }
        } catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public List<OrderDetail> getAll() {
        try(ConnectionManager cm = ConnectionManager.getInstance()){
            try(PreparedStatement statement = cm.getConnection().prepareStatement(selectAll)){
                List<OrderDetail> detailList = new ArrayList<>();
                ResultSet rs = statement.executeQuery();
                rs.getMetaData();

                while (rs.next()) {
                    OrderDetail detail = new OrderDetail();
                    detail.setId(rs.getInt("ID"));
                    detail.setQuantity(rs.getInt("QUANTITY"));
                    /*
                    Mismo problema que para el get pero multiplicado por la cantidad de registros
                    armar consultas anidadas a la base es una mala practica dado que por cada consulta que armamos
                    estamos abriendo y cerrando una conexion con la base, lo cual es costoso y si pensamos en un escenario
                    normal donde nuestros detalles podrian superar los millones de registros, esta consulta seria completamente
                    inviable.
                     */
                    detail.setProduct(productDao.get(rs.getInt("PRODUCT_ID")));
                    detailList.add(detail);
                }
                return detailList;
            }
        } catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public int save(OrderDetail detail) {
        try(ConnectionManager cm = ConnectionManager.getInstance()){
            try(PreparedStatement statement = cm.getConnection().prepareStatement(insert)){
                /*
                 tener en cuenta que a esta altura el producto seleccionado debe si o si existir en nuestra base de datos
                 sino se va a romper probablemente porque no acepta productos con id nulos, o peor aun, vamos a insertar
                 un detalle sin producto
                 */
                statement.setInt(1,detail.getId());
                statement.setInt(2,detail.getProduct().getId());
                statement.setInt(3,detail.getQuantity());
                statement.setInt(4,detail.getOrder_id());
                return statement.executeUpdate();
            }
        } catch(Exception ex){
            ex.printStackTrace();
            return -1;
        }
    }

    @Override
    public int update(OrderDetail detail) {
        try(ConnectionManager cm = ConnectionManager.getInstance()){
            try(PreparedStatement statement = cm.getConnection().prepareStatement(updateById)){
                statement.setInt(1,detail.getProduct().getId());
                statement.setInt(2,detail.getQuantity());
                statement.setInt(3,detail.getId());
                return statement.executeUpdate();
            }
        } catch(Exception ex){
            ex.printStackTrace();
            return -1;
        }
    }

    @Override
    public int delete(OrderDetail detail) {
        try(ConnectionManager cm =ConnectionManager.getInstance()){
            try(PreparedStatement statement = cm.getConnection().prepareStatement(deleteById)){
                statement.setInt(1,detail.getId());
                return statement.executeUpdate();
            }
        } catch(Exception ex){
            ex.printStackTrace();
            return -1;
        }
    }
}
