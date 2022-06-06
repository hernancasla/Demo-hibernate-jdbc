package hibernate.dao;

import hibernate.HibernateUtil;
import hibernate.model.Product;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;
public class ProductDao implements DAO<Product> {
    Session session = HibernateUtil.getSessionFactory().openSession();
    @Override
    public Product get(int id) {
        return  session.get(Product.class, id);
    }


    @Override
    public List<Product> getAll() {
        String hql = "FROM Product";
        Query query = session.createQuery(hql);
        return query.list();
    }

    @Override
    public int save(Product product) {
        session.getTransaction().begin();
        int id = (int) session.save(product);
        session.getTransaction().commit();
        return id;
    }

    public void update(Product product) {
        session.getTransaction().begin();
        session.saveOrUpdate(product);
        session.getTransaction().commit();

    }
    @Override
    public void delete(Product product) {
        session.getTransaction().begin();
        session.delete(product);
        session.getTransaction().commit();
    }
}
