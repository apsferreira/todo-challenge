package com.ojingo.register.data.repository;

import javax.enterprise.context.ApplicationScoped;

import com.ojingo.register.domain.models.Team;

import io.quarkus.hibernate.orm.panache.PanacheRepository;


@ApplicationScoped
public class TeamRepository implements PanacheRepository<Team> {

}
