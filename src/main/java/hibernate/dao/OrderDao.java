package hibernate.dao;

import hibernate.HibernateUtil;
import hibernate.model.Order;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class OrderDao implements DAO<Order> {
    Session session = HibernateUtil.getSessionFactory().openSession();

    @Override
    public Order get(int id) {
        return session.get(Order.class, id);
    }

    @Override
    public List<Order> getAll() {
        String hql = "FROM Order";
        Query query = session.createQuery(hql);
        return query.list();
    }

    @Override
    public int save(Order order) {
        session.getTransaction().begin();
        int id = (int) session.save(order);
        session.getTransaction().commit();
        return id;
    }

    @Override
    public void update(Order order) {
        session.getTransaction().begin();
        session.saveOrUpdate(order);
        session.getTransaction().commit();
    }

    @Override
    public void delete(Order order) {
        session.getTransaction().begin();
        session.delete(order);
        session.getTransaction().commit();
    }
}
