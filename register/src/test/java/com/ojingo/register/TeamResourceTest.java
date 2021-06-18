package com.ojingo.register;

import javax.ws.rs.core.Response.Status;

import org.approvaltests.Approvals;
import org.junit.jupiter.api.Test;

import com.github.database.rider.cdi.api.DBRider;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.ojingo.register.domain.models.Team;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import junit.framework.Assert;


@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
@QuarkusTest
@QuarkusTestResource(DatabaseLifecycleManager.class)
@SuppressWarnings("deprecation")
public class TeamResourceTest {
	  
	@Test
	@DataSet("teams-cenary-1.yml")
	public void testGetAllTeams() {
 		String result = given()
				.when().get("/teams")
				.then()
				.statusCode(200)
				.extract().asString();
		Approvals.verifyJson(result);
	}
	
	private RequestSpecification given() {
		return RestAssured.given().contentType(ContentType.JSON);
	}
	
	@Test
	@DataSet("teams-cenary-1.yml")
	public void testUpdateTeam() {
		Team dto = new Team();
		dto.name = "new Name";
		Long parameterValue = 123L;		
		given()
			.with().pathParam("id", parameterValue)
				.body(dto)
				.when().put("/teams/{id}")
				.then()
				.statusCode(200)
				.extract().asString();
		
		Team findById = Team.findById(parameterValue);
		
		Assert.assertEquals(dto.name, findById.name);
	}
}
