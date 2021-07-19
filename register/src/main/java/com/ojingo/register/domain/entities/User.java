package com.ojingo.register.domain.entities;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
@Table(name = "users")
public class User extends PanacheEntityBase {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;

	@OneToOne
	public Team team;

	@Column(name = "original_id")
	public UUID originalId;

	public String username;

	public String email;

	public static User of(UUID originalId, String username, String email) {
		User user = new User();

		user.originalId = originalId;
		user.username = username;
		user.email = email;

		return user;
	}
}
