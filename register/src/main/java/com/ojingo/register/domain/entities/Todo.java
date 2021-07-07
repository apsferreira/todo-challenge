package com.ojingo.register.domain.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
@Table(name="todos")
public class Todo extends PanacheEntityBase {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;	

	public String description;
	
	@OneToOne(optional = true)
	public Team team;
	
	@OneToOne(optional = true)
	public User user;
	
}
