package com.ojingo.register.data.repository;

import javax.enterprise.context.ApplicationScoped;

import com.ojingo.register.domain.models.User;

import io.quarkus.hibernate.orm.panache.PanacheRepository;


@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

}
