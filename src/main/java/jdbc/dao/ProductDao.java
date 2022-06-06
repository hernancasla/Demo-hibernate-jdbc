package jdbc.dao;

import jdbc.dbconn.ConnectionManager;
import jdbc.model.Product;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProductDao implements DAO<Product> {
    private static final String selectById = "SELECT ID, DESCRIPTION, PRICE FROM PRODUCT WHERE ID = ?";
    private static final String selectAll = "SELECT ID, DESCRIPTION, PRICE  FROM PRODUCT";
    private static final String insert = "INSERT INTO PRODUCT (ID,DESCRIPTION,PRICE ) VALUES (?,?)";
    private static final String deleteById = "DELETE FROM PRODUCT WHERE ID = ?";
    private static final String updateById = "UPDATE FROM PRODUCT SET DESCRIPTION = ? , PRICE = ? WHERE ID = ?";

    @Override
    public Product get(int id) {
        Product product = null;
        try(ConnectionManager cm = ConnectionManager.getInstance()){
            try(PreparedStatement statement = cm.getConnection().prepareStatement(selectById)){
                statement.setInt(1,id);
                ResultSet rs = statement.executeQuery();
                rs.getMetaData();
                product = new Product();
                while (rs.next()) {
                    product.setDescription(rs.getString("DESCRIPTION"));
                    product.setId(rs.getInt("ID"));
                    product.setPrice(rs.getDouble("PRICE"));
                }
                return product;
            }
        } catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }


    @Override
    public List<Product> getAll() {
        List<Product> list = null;
        try(ConnectionManager cm = ConnectionManager.getInstance()){
            try(PreparedStatement statement = cm.getConnection().prepareStatement(selectAll)){
                ResultSet rs = statement.executeQuery();
                rs.getMetaData();
                list = new ArrayList<>();
                while (rs.next()) {
                    Product product = new Product();
                    product.setDescription(rs.getString("DESCRIPTION"));
                    product.setPrice(rs.getDouble("PRICE"));
                    product.setId(rs.getInt("ID"));
                    list.add(product);
                }
                return list;
            }
        } catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public int save(Product product) {
        try(ConnectionManager cm = ConnectionManager.getInstance()){
            try(PreparedStatement statement = cm.getConnection().prepareStatement(insert)){
                statement.setInt(1,product.getId());
                statement.setString(2,product.getDescription());
                statement.setDouble(2,product.getPrice());
                return statement.executeUpdate();
            }
        } catch(Exception ex){
            ex.printStackTrace();
            return -1;
        }
    }

    @Override
    public int update(Product product) {
        try(ConnectionManager cm = ConnectionManager.getInstance()){
            try(PreparedStatement statement = cm.getConnection().prepareStatement(updateById)){
                statement.setString(1,product.getDescription());
                statement.setDouble(2,product.getPrice());
                statement.setInt(3,product.getId());
                return statement.executeUpdate();
            }
        } catch(Exception ex){
            ex.printStackTrace();
            return -1;
        }
    }

    @Override
    public int delete(Product product) {
        try(ConnectionManager cm = ConnectionManager.getInstance()){
            try(PreparedStatement statement = cm.getConnection().prepareStatement(deleteById)){
                statement.setInt(1,product.getId());
                return statement.executeUpdate();
            }
        } catch(Exception ex){
            ex.printStackTrace();
            return -1;
        }
    }
}
