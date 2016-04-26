package br.com.saude.api.social.dao;

import java.io.Serializable;
import java.util.List;

public interface GenericDAO<T,Type extends Serializable> {
	
	void beginTransaction();
	
	void commitTransaction();
	
	void save(T entity);
	
	void delete (T entity);
	
	List<T> listAll();

}
