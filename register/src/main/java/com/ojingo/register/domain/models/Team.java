package com.ojingo.register.domain.models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;


@Entity
@Table(name="teams")
public class Team extends PanacheEntityBase {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;	
	
	public String name;
	
	@Transient
	public List<User> users;
	
	@Transient
	public List<Todo> todos;

}
