package com.ojingo.register;

import javax.inject.Inject;

import org.approvaltests.Approvals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.database.rider.cdi.api.DBRider;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.ojingo.register.data.repositories.TeamRepository;
import com.ojingo.register.domain.dto.UpdateTeamDTO;
import com.ojingo.register.util.TokenUtils;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import junit.framework.Assert;


@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
@QuarkusTest
@QuarkusTestResource(DatabaseLifecycleManager.class)
@SuppressWarnings("deprecation")
public class TeamResourceTest {
	
	@Inject
	private TeamRepository teamRepository;
	
	private String token;
	
	@BeforeEach
	public void generateToken() throws Exception {
		token = TokenUtils.generateTokenString("/JWTRegisterClaims.json", null);
	}
	
	private RequestSpecification given() {
		return RestAssured.given().contentType(ContentType.JSON)
				.header(new Header("Authorization", "Bearer " + token));
	}

	@Test
	@DataSet("teams-cenary-1.yml")
	public void testGetAll() {
 		String result = given()
				.when().get("/api/v1/teams")
				.then()
				.statusCode(200)
				.extract().asString();
		Approvals.verifyJson(result);
	}
	
	@Test
	@DataSet("teams-cenary-1.yml")
	public void testGetTeam() {
		Long parameterValue = 123L;
		
 		String result = given()
 				.with().pathParam("idTeam", parameterValue)
				.when().get("/api/v1/teams/{idTeam}")
				.then()
				.statusCode(200)
				.extract().asString();
 		
		Approvals.verifyJson(result);
	}
	
	@Test
	@DataSet("teams-cenary-2.yml")
	public void testGetUsersFromTeam() {
		Long parameterValue = 123L;
		
 		String result = given()
 				.with().pathParam("idTeam", parameterValue)
				.when().get("/api/v1/teams/{idTeam}/users")
				.then()
				.statusCode(200)
				.extract().asString();
 		
		Approvals.verifyJson(result);
	}
	
	@Test
	@DataSet("teams-cenary-1.yml")
	public void testUpdateTeam() {
		UpdateTeamDTO updateTeamDTO = new UpdateTeamDTO();
		
		updateTeamDTO.name = "new Name";
		Long parameterValue = 123L;
		
		given()
			.with().pathParam("id", parameterValue)
				.body(updateTeamDTO)
				.when().put("/api/v1/teams/{id}")
				.then()
				.statusCode(200)
				.extract().asString();
		
		Assert.assertEquals(updateTeamDTO.name, teamRepository.findById(parameterValue).name);
	}
	
	
}
