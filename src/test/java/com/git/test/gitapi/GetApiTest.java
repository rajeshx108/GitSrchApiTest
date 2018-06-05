package com.git.test.gitapi;
import static io.restassured.RestAssured.given;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.git.test.common.RestUtilities;
import com.git.test.constants.EndPoints;
import com.git.test.constants.Path;

import com.gitapi.bin.Gitresponse;
import com.gitapi.bin.Item;

import io.restassured.response.ResponseBody;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import static org.hamcrest.Matchers.*;
import java.util.ArrayList;
import java.util.List;



//I have used couple of different ways of assertion 
//to illustrate you can utilize mutliple approaches
//depneding on needs


public class GetApiTest {
	
	RequestSpecification reqSpec;
	ResponseSpecification resSpec;
	
	@BeforeClass
	public void setup() {
		reqSpec = RestUtilities.getRequestSpecification();
	
		reqSpec.basePath(Path.PATH);
		
		resSpec = RestUtilities.getResponseSpecification();
	}
      /*Scenario:
       * Given I have correct uri and path
       * When I call Git Search Repository API with valid body and header
       * Then status code is 200
       * And a response body with relevant result is returned
       */
	@Test
	public void verifyCallToSearchRepo() {
		given()
			.spec(RestUtilities.createQueryParam(reqSpec, "q","{Sangroula}"))
		.when()
			.get(EndPoints.SearchRepo)
		.then()
		    .statusCode(200)
			.log().all();
		
			
	}
	
	/*Scenario:
     * Given I have correct uri and path
     * When I send a query param for order desc
     * Then status code 200 is returned
     * And the response body is ordered in descending order 
     */
	@Test
	public void verifySearchRepositorySortFeature() {
		given()
		.spec(RestUtilities.createQueryParam(reqSpec, "q","{Sangroula&&sort=stars&order=desc}"))
	.when()
		.get(EndPoints.SearchRepo)
	.then()
	    .statusCode(200)
		.log().all()
		.spec(resSpec)
		.body("items.id", everyItem(hasItem(98363823)));
		
		
		
	}
	/*Scenario:
     * Given I have correct uri and path
     * When I send a query param for sorting with stars
     * Then status code 200 is returned
     * And the response body is sorted based on stars received
     */
	@Test
	public void verifySearchRepositoryByLanguage() {
		RestUtilities.setEndPoint(EndPoints.SearchRepo);
		Response res = RestUtilities.getResponse(
				RestUtilities.createQueryParam(reqSpec, "q", "bitcoin+language:java"), "get");
		Gitresponse result = res.as(Gitresponse.class, ObjectMapperType.GSON);
		Assert.assertTrue(res.asString().contains("java"));
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertAll();
		
			
		}
		
	/*Scenario:
     * Given I have correct uri and path
     * When I send a query param with a particular topic
     * Then status code 200 is returned
     * And the response body contains the repos related to my topic 
     */
	@Test
	public void verifySearchRepositoryByTopic() {
		RestUtilities.setEndPoint(EndPoints.SearchRepo);
		Response res = RestUtilities.getResponse(
				RestUtilities.createQueryParam(reqSpec, "q", "topic:blockchain"), "get");
		
       
		ArrayList<String> contents = res.path("items.name");
		Assert.assertTrue(contents.contains("go-ethereum"));
		
		
	}
	/*Scenario:
     * Given I have correct uri and path
     * When I send a query param for a repo with a particular user
     * Then status code 200 is returned
     * And the response body only contains repos by the queried user
     */
	@Test
	public void verifySearchRepositoryByUser() {
		given()
		.spec(RestUtilities.createQueryParam(reqSpec, "q","user:blockchain"))
	.when()
		.get(EndPoints.SearchRepo)
	.then()
	    
	    .statusCode(200)
		.log().all()
	    .spec(resSpec)
	    .body("items.owner.login", hasItem("blockchain"));
		
		
	}
	/*Scenario:
     * Given I have correct uri and path
     * When I send a query param with # of pages I want to view
     * Then status code 200 is returned
     * And header Link attribute provides the page information
     */
	@Test
	public void verifySearchRepositoryUsingPagination() {
		RestUtilities.setEndPoint(EndPoints.SearchRepo);
		Response res = RestUtilities.getResponse(
				RestUtilities.createQueryParam(reqSpec, "q", "bitcoin&page=2"), "get");
		
		String headercontent = res.header("Link");
		System.out.println(headercontent);
	
		
		
		
	    
	}
	/*Scenario:
     * Given I have correct uri and path
     * When I send a query param with created date and programmign language
     * Then status code 200 is returned
     * And only results created on that date is returned
     */
	@Test
	public void verifySearchRepositoryUsingCreatedDate() {
		given()
		.spec(RestUtilities.createQueryParam(reqSpec, "q","blockchain&created:>=2017-01-01"))
	.when()
		.get(EndPoints.SearchRepo)
	    .then()
	    .statusCode(200)
		.log().all()
		.spec(resSpec)
		.body("items.created_at", (hasItem("2017-09-24T19:36:36Z")));
	 
	}
	/*Scenario:
     * Given I have correct uri and path
     * When I send a query param with License Key word
     * Then status code 200 is returned
     * And result set is based on the license type
     */
	@Test
	public void verifySearchRepositoryUsingLicenseKeyword() {
		given()
		.spec(RestUtilities.createQueryParam(reqSpec, "q","google&license:mit"))
	.when()
		.get(EndPoints.SearchRepo)
	.then()
	    .statusCode(200)
		.log().all()
		.spec(resSpec)
		.body("items.license.url",(hasItem("https://api.github.com/licenses/mit")));
		
		
	
	}
	/*Scenario:
     * Given I have correct uri and path
     * When I send a query param with size criteria
     * Then status code 200 is returned
     * And returned repos reflect size criteria
     */
	@Test
	public void verifySearchRepositoryBySizes() {
		
		given()
		.spec(RestUtilities.createQueryParam(reqSpec, "q","Sangroula@size:>100"))
	.when()
		.get(EndPoints.SearchRepo)
	.then()
	    .statusCode(200)
		.log().all()
		.spec(resSpec);
		
	}
	/*Scenario:
     * Given I have correct uri and path
     * When I send a query param with a date criteria
     * Then status code 200 is returned
     * And the repos returned will reflect the date critera
     */
	@Test
	public void verifySearchRepositoryByPushedDate() {
		//JsonPath jsonPathEvaluator = response.jsonPath();
		Response response = 
		 given()
		
		.spec(RestUtilities.createQueryParam(reqSpec, "q","Electron&pushed:>2016-04-29"))
	    .when()
		.get(EndPoints.SearchRepo)
		
	    .then()
	    .statusCode(200)
	    .extract().response();
		
	}
	  
		
		
	}


