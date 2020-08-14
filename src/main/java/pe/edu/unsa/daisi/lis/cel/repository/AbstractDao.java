package pe.edu.unsa.daisi.lis.cel.repository;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;

public abstract class AbstractDao<PK extends Serializable, T> {
	
	private final Class<T> persistentClass;
	
	@SuppressWarnings("unchecked")
	public AbstractDao(){
		this.persistentClass =(Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
	}
	
	
	@PersistenceContext
	private EntityManager entityManager;
	
	protected EntityManager getEntityManager(){
		return entityManager;
	}
	
	@SuppressWarnings("unchecked")
	public T getByKey(PK key) {
		return (T) getEntityManager().find(persistentClass, key);
	}

	public void persist(T entity) {
		getEntityManager().persist(entity);
	}

	public T merge(T entity) {
		return getEntityManager().merge(entity);		
	}

	public void flush() {
		getEntityManager().flush();
	}
	
	public void delete(T entity) {
		getEntityManager().remove(entity);
	}
	
	
	protected CriteriaBuilder createCriteriaBuilder(){
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		
		return  builder;
	}
	
	
}
