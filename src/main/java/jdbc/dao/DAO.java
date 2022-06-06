package jdbc.dao;

import java.util.List;

public interface DAO<T> {
	T get(int id);
	List<T> getAll();
    int save(T t);
    int update(T t);
    int delete(T t);
}
