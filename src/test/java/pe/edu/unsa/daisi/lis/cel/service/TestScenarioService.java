package pe.edu.unsa.daisi.lis.cel.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.Scenario;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredContext;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredEpisode;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredAlternative;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredScenario;
import pe.edu.unsa.daisi.lis.cel.service.ScenarioServiceImpl;
import pe.edu.unsa.daisi.lis.cel.util.scenario.preprocess.ScenarioManipulation;


public class TestScenarioService {
	@Test
	public void testConvertToStructuredScenarioWithSuccess() {
		//TESTAR TRANSFORMACAO DE SCENARIO -> SCENARIO ESTRUTURADO
		Scenario scenario = new Scenario();
		scenario.setTitle("Submit Order");
		scenario.setGoal("Allow customers to find the best supplier for a given order.");
		scenario.setContext("PRE-CONDITION: The Broker System is online AND the Broker System welcome page is being displayed.");
		scenario.setActors("Customer, Broker System");
		scenario.setResources("Login page, Login information, Order");
		scenario.setEpisodes("1. The Customer loads the login page\r\n" + 
				"  2 The Broker System asks for the Customer's login information\r\n" + 
				"  3. The Customer enters her login information\r\n" + 
				"  4. The Broker System checks the provided login information\r\n" + 
				"  5. The Broker System displays an order page\r\n" + 
				"  6. The Customer creates a new Order\r\n" + 
				"  7. DO the Customer adds an item to the Order WHILE the Customer has more items to add to the order \n" + 
				"  8. The Customer submits the Order\r\n" + 
				"  9. The Broker System broadcast the Order to the Suppliers\r\n" + 
				"  10. # LOCAL SUPPLIER BID FOR ORDER\r\n" + 
				"  11. INTERNATIONAL SUPPLIER BID FOR ORDER #\r\n" + 
				"  POST-CONDITION: A OR B AND C\r\n" +
				"  DO the Customer adds an item to the Order WHILE the Customer has more items to add to the order \n" +
				"  PROCESS BIDS\r\n"+
				"  12. PROCESS BIDS\r\n"+
				"  PRE-CONDITION: A OR B AND C\r\n" +
				""
				);
		scenario.setAlternative("1.1 IF Customer is not registered THEN REGISTER CUSTOMER\r\n" + 
				"  2.1. IF after 60 seconds THEN The Broker System displays a login timeout page\r\n" + 
				"  4a. the Customer login information is not accurate\r\n" + 
				"  4a1. System go back to step 2 .\r\n" + 
				"  8.1 IF the order is empty THEN The Broker System displays an error message\r\n" +
				"  POST-CONDITION: A OR B AND C\r\n" +
				"  5.a. Administrator adds more channels. Proceed to step 7 \r\n" +
				"");
		
		StructuredScenario structuredScenario = new StructuredScenario();
		ScenarioServiceImpl service = new ScenarioServiceImpl();
		structuredScenario = service.convertToStructuredScenario(scenario) ;
		
		String texto = "$##ASJAJDHAKJDAHDK)_+,..";
		System.out.println(texto.replaceFirst("(^(\\p{Punct})*|(\\p{Punct})*$)", "---"));
		String episode = "# askldjsalkdlkas #" ;
				System.out.println((episode.substring(0, episode.length() - 1 )));
	}
	@Test
	public void testConvertToStructuredScenarioWithNonExplicitConstraintNonExplicitConditional() {
		//TESTAR TRANSFORMACAO DE SCENARIO -> SCENARIO ESTRUTURADO
		Scenario scenario = new Scenario();
		scenario.setTitle("Submit Order");
		scenario.setGoal("Allow customers to find the best supplier for a given order.");
		scenario.setContext("PRE-CONDITION: The Broker System is online AND the Broker System welcome page is being displayed.");
		scenario.setActors("Customer, Broker System");
		scenario.setResources("Login page, Login information, Order");
		scenario.setEpisodes(
				"  1. Scenario ends when user logs out or selects different option\r\n" + 
				"  2 IF new contract will be added THEN User select a client\r\n" + 
				"  3. system queries the database for news messages, whose expiry date and time have passed\r\n"
				);
		scenario.setAlternative("1.1 IF Customer is not registered THEN REGISTER CUSTOMER\r\n" + 
				"  2.1. IF after 60 seconds THEN The Broker System displays a login timeout page\r\n" + 
				"  4.1 IF the Customer login information is not accurate THEN The Broker System displays an alert message\r\n" + 
				"  8.1 IF the order is empty THEN The Broker System displays an error message\r\n" +
				"  POST-CONDITION: A OR B AND C\r\n" +
				"  IF EXCEPTION WITHOUT ID AND CAUSE THEN SOLUTION \r\n" +
				"");
		
		StructuredScenario structuredScenario = new StructuredScenario();
		ScenarioServiceImpl service = new ScenarioServiceImpl();
		structuredScenario = service.convertToStructuredScenario(scenario) ;
				
	}
	@Test
	public void testConvertToStructuredScenarioWithComplicatedvalidationStep() {
		//TESTAR TRANSFORMACAO DE SCENARIO -> SCENARIO ESTRUTURADO
		Scenario scenario = new Scenario();
		scenario.setTitle("Submit Order");
		scenario.setGoal("Allow customers to find the best supplier for a given order.");
		scenario.setContext("PRE-CONDITION: The Broker System is online AND the Broker System welcome page is being displayed.");
		scenario.setActors("Customer, Broker System");
		scenario.setResources("Login page, Login information, Order");
		scenario.setEpisodes(
				"  3. system queries the database for news messages, whose expiry date and time have passed\r\n"+
						"  2 System checks if a group with the given name has not been already defined and if so, inserts the name of a new group into a database\r\n"  
						
				);
		scenario.setAlternative("1.1 IF Customer is not registered THEN REGISTER CUSTOMER\r\n" + 
				"  2.1. IF after 60 seconds THEN The Broker System displays a login timeout page\r\n" + 
				"  4.1 IF the Customer login information is not accurate THEN The Broker System displays an alert message\r\n" + 
				"  8.1 IF the order is empty THEN The Broker System displays an error message\r\n" +
				"  POST-CONDITION: A OR B AND C\r\n" +
				"  IF EXCEPTION WITHOUT ID AND CAUSE THEN SOLUTION \r\n" +
				"");
		StructuredScenario structuredScenario = new StructuredScenario();
		ScenarioServiceImpl service = new ScenarioServiceImpl();
		structuredScenario = service.convertToStructuredScenario(scenario) ;
		System.out.println(structuredScenario);		
	}
	@Test
	public void testConvertToStructuredScenarioWithExceptionInMultipleLines2() {
		//TESTAR TRANSFORMACAO DE SCENARIO -> SCENARIO ESTRUTURADO
		Scenario scenario = new Scenario();
		scenario.setTitle("Submit Order");
		scenario.setGoal("Allow customers to find the best supplier for a given order.");
		scenario.setContext("PRE-CONDITION: The Broker System is online AND the Broker System welcome page is being displayed.");
		scenario.setActors("Customer, Broker System");
		scenario.setResources("Login page, Login information, Order");
		scenario.setEpisodes("1. The Customer loads the login page\r\n" + 
				"  2 The Broker System asks for the Customer's login information\r\n" + 
				"  3. The Customer enters her login information\r\n" + 
				"  4. The Broker System checks the provided login information\r\n" + 
				"  5. The Broker System displays an order page\r\n" + 
				"  6. The Customer creates a new Order\r\n" + 
				"  7. DO the Customer adds an item to the Order WHILE the Customer has more items to add to the order \n" + 
				"  8. The Customer submits the Order\r\n" + 
				"  9. The Broker System broadcast the Order to the Suppliers\r\n" + 
				"  10. # LOCAL SUPPLIER BID FOR ORDER\r\n" + 
				"  11. INTERNATIONAL SUPPLIER BID FOR ORDER #\r\n" + 
				"  POST-CONDITION: A OR B AND C\r\n" +
				"  DO the Customer adds an item to the Order WHILE the Customer has more items to add to the order \n" +
				"  PROCESS BIDS\r\n"+
				"  12. PROCESS BIDS\r\n"+
				"  PRE-CONDITION: A OR B AND C\r\n" +
				""
				);
		scenario.setAlternative("1.1 IF Customer is not registered THEN REGISTER CUSTOMER\r\n" + 
				"  2.1. after 60 seconds\r\n" +
				"  The Broker System displays a login timeout page\r\n" +
				"  4.1 IF the Customer login information is not accurate THEN\r\n" +
				"  The Broker System displays an alert message\r\n" + 
				"  8.1 IF the order is empty THEN The Broker System displays an error message\r\n" +
				"  POST-CONDITION: A OR B AND C\r\n" +
				"  IF EXCEPTION WITHOUT ID AND CAUSE THEN SOLUTION \r\n" +
				"");
		
		StructuredScenario structuredScenario = new StructuredScenario();
		ScenarioServiceImpl service = new ScenarioServiceImpl();
		structuredScenario = service.convertToStructuredScenario(scenario) ;
		System.out.println(structuredScenario);
	}
	
	@Test
	public void testConvertToStructuredScenarioWithExceptionInMultipleLines3() {
		//TESTAR TRANSFORMACAO DE SCENARIO -> SCENARIO ESTRUTURADO
		Scenario scenario = new Scenario();
		scenario.setTitle("Submit Order");
		scenario.setGoal("Allow customers to find the best supplier for a given order.");
		scenario.setContext("PRE-CONDITION: The Broker System is online AND the Broker System welcome page is being displayed.");
		scenario.setActors("Customer, Broker System");
		scenario.setResources("Login page, Login information, Order");
		scenario.setEpisodes("1. The Customer loads the login page\r\n" + 
				"  2 The Broker System asks for the Customer's login information\r\n" + 
				"  3. The Customer enters her login information\r\n" + 
				"  4. The Broker System checks the provided login information\r\n" + 
				"  5. The Broker System displays an order page\r\n" + 
				"  6. The Customer creates a new Order\r\n" + 
				"  7. DO the Customer adds an item to the Order WHILE the Customer has more items to add to the order \n" + 
				"  8. The Customer submits the Order\r\n" + 
				"  9. The Broker System broadcast the Order to the Suppliers\r\n" + 
				"  10. # LOCAL SUPPLIER BID FOR ORDER\r\n" + 
				"  11. INTERNATIONAL SUPPLIER BID FOR ORDER #\r\n" + 
				"  POST-CONDITION: A OR B AND C\r\n" +
				"  DO the Customer adds an item to the Order WHILE the Customer has more items to add to the order \n" +
				"  PROCESS BIDS\r\n"+
				"  12. PROCESS BIDS\r\n"+
				"  PRE-CONDITION: A OR B AND C\r\n" +
				""
				);
		scenario.setAlternative("1.1 IF Customer is not registered THEN REGISTER CUSTOMER\r\n" + 
				"  2.1. after 60 seconds\r\n" +
				"  The Broker System displays a login timeout page\r\n" +
				"  The Broker System finishes\r\n" +
				"  4.1 IF the Customer login information is not accurate THEN\r\n" +
				"  The Broker System displays an alert message\r\n" +
				"  The Broker System finishes\r\n" +
				"  \r\n" +
				"  POST-CONDITION: x OR y AND z\r\n" +
				"  SOLUTION POST-CONDITION: A OR B AND C\r\n" +
				"  IF EXCEPTION WITHOUT ID AND CAUSE THEN SOLUTION first \r\n" +
				"  8.1 IF the order is empty THEN The Broker System displays an error message\r\n" +
				"  POST-CONDITION: A OR B AND C\r\n" +
				"  IF EXCEPTION WITHOUT ID AND CAUSE THEN SOLUTION second \r\n" +
				"  POST-CONDITION: A OR B AND C\r\n" +
				"");
		
		StructuredScenario structuredScenario = new StructuredScenario();
		ScenarioServiceImpl service = new ScenarioServiceImpl();
		structuredScenario = service.convertToStructuredScenario(scenario) ;
		System.out.println(structuredScenario);
	}
	@Test
	public void testConvertToStructuredScenarioWithExceptionInMultipleLinesSolutionWithId() {
		//TESTAR TRANSFORMACAO DE SCENARIO -> SCENARIO ESTRUTURADO
		Scenario scenario = new Scenario();
		scenario.setTitle("Submit Order");
		scenario.setGoal("Allow customers to find the best supplier for a given order.");
		scenario.setContext("PRE-CONDITION: The Broker System is online AND the Broker System welcome page is being displayed.");
		scenario.setActors("Customer, Broker System");
		scenario.setResources("Login page, Login information, Order");
		scenario.setEpisodes("1. The Customer loads the login page\r\n" + 
				"  2 The Broker System asks for the Customer's login information\r\n" + 
				"  3. The Customer enters her login information\r\n" + 
				""
				);
		scenario.setAlternative("1.1 IF Customer is not registered THEN REGISTER CUSTOMER\r\n" + 
				"  2.1. after 60 seconds:\r\n" +
				"  The Broker System displays a login timeout page\r\n" +
				"  4.1 the Customer login information is not accurate\r\n" +
				"  4.1.1 The Broker System displays an alert message\r\n" +
				"  4.1.2 The Broker System ends\r\n" +
				"  POST-CONDITION: A OR B AND C\r\n" +
				"  8.1 the order is empty :\r\n" +
				"  1. The Broker System displays an error message\r\n" +
				"  2. The Broker System displays 1\r\n" +
				"  The Broker System displays 2\r\n" +
				"  POST-CONDITION: A OR B AND C\r\n" +
				"  EXCEPTION WITHOUT ID AND CAUSE\r\n" +
				"  SOLUTION 1\r\n" +
				"  SOLUTION 2\r\n" +
				"");
		
		StructuredScenario structuredScenario = new StructuredScenario();
		ScenarioServiceImpl service = new ScenarioServiceImpl();
		structuredScenario = service.convertToStructuredScenario(scenario) ;
		System.out.println(structuredScenario);
	}
	
	@Test
	public void testConvertToStructuredScenarioWithExceptionInMultipleLinesSolutionWithIdAndGOTO() {
		//TESTAR TRANSFORMACAO DE SCENARIO -> SCENARIO ESTRUTURADO
		Scenario scenario = new Scenario();
		scenario.setTitle("Submit Order");
		scenario.setGoal("Allow customers to find the best supplier for a given order.");
		scenario.setContext("PRE-CONDITION: The Broker System is online AND the Broker System welcome page is being displayed.");
		scenario.setActors("Customer, Broker System");
		scenario.setResources("Login page, Login information, Order");
		scenario.setEpisodes("1. The Customer loads the login page\r\n" + 
				"  2 The Broker System asks for the Customer's login information\r\n" + 
				"  3. The Customer enters her login information\r\n" + 
				"  4. The Broker System checks the provided login information\r\n" + 
				"  5. The Broker System displays an order page\r\n" + 
				"  6. The Customer creates a new Order\r\n" + 
				"  7. DO the Customer adds an item to the Order WHILE the Customer has more items to add to the order \n" + 
				"  8. The Customer submits the Order\r\n" + 
				"  9. The Broker System broadcast the Order to the Suppliers\r\n" + 
				"  10. # LOCAL SUPPLIER BID FOR ORDER\r\n" + 
				"  11. INTERNATIONAL SUPPLIER BID FOR ORDER #\r\n" + 
				"  12. PROCESS BIDS\r\n"+
				""
				);
		scenario.setAlternative("1.1 IF Customer is not registered THEN REGISTER CUSTOMER\r\n" + 
				"  2.1. after 60 seconds:\r\n" +
				"  2.1.1 The Broker System displays a login timeout page\r\n" +
				"  2.1.2 The broker System finishes\r\n" +
				"  4.1 the Customer login information is not accurate\r\n" +
				"  4.1.1 The Broker System displays an alert message\r\n" +
				"  4.1.2 The Broker System ends\r\n" +
				"  4.1.3 back to step 1\r\n" +
				"  POST-CONDITION: A OR B AND C\r\n" +
				"  8.1 the order is empty :\r\n" +
				"  1. The Broker System displays an error message\r\n" +
				"  2. The Broker System displays 1\r\n" +
				"  3. GO TO step 9\r\n" +
				"  POST-CONDITION: A OR B AND C\r\n" +
				"  EXCEPTION WITHOUT ID AND CAUSE\r\n" +
				"  SOLUTION 1\r\n" +
				"  SOLUTION 2\r\n" +
				"");
		
		StructuredScenario structuredScenario = new StructuredScenario();
		ScenarioServiceImpl service = new ScenarioServiceImpl();
		structuredScenario = service.convertToStructuredScenario(scenario) ;
		System.out.println(structuredScenario);
	}
	
	@Test
	public void testSortScenarios() {
		List<StructuredScenario> relatedScenarios = new ArrayList<StructuredScenario>();  
		StructuredScenario temp = new StructuredScenario();
		temp.setTitle("Submit Order");
		relatedScenarios.add(temp);
		
		temp = new StructuredScenario();
		temp.setTitle("Local Supplier bid for order");
		relatedScenarios.add(temp);
		
		temp = new StructuredScenario();
		temp.setTitle("International Supplier bid for order");
		relatedScenarios.add(temp);
		
		temp = new StructuredScenario();
		temp.setTitle("Register Customer");
		relatedScenarios.add(temp);
		
		temp = new StructuredScenario();
		temp.setTitle("Handle Payment");
		relatedScenarios.add(temp);
		
		temp = new StructuredScenario();
		temp.setTitle("Process Bids");
		relatedScenarios.add(temp);
		
		
		ScenarioServiceImpl service = new ScenarioServiceImpl();
		relatedScenarios = ScenarioManipulation.sortScenarios(relatedScenarios, 0, relatedScenarios.size()-1);
		System.out.println(relatedScenarios);
		for(StructuredScenario scenario: relatedScenarios)
			System.out.println(scenario.getTitle());
		
	}
	
	@Test
	public void testSortScenariosByEpisodes() {
		List<StructuredScenario> relatedScenarios = new ArrayList<StructuredScenario>();  
		StructuredScenario temp = new StructuredScenario();
		temp = createMainScenarioSubmitOrder();
		relatedScenarios.add(temp);
		
		temp = new StructuredScenario();
		temp = createScenarioLocalSupplier();
		relatedScenarios.add(temp);
		
		temp = new StructuredScenario();
		temp = createScenarioInternationalSupplier();
		relatedScenarios.add(temp);
		
		ScenarioServiceImpl service = new ScenarioServiceImpl();
		relatedScenarios = ScenarioManipulation.sortScenariosByEpisodes(relatedScenarios, 0, relatedScenarios.size() - 1);
		System.out.println(relatedScenarios);
		for(StructuredScenario scenario: relatedScenarios)
			System.out.println(scenario.getTitle());
		
	}
	
	@Test
	public void testFindNonSequentiallyRelatedScenariosExplicit() {
		ScenarioServiceImpl service = new ScenarioServiceImpl();
		StructuredScenario structuredScenario = createMainScenarioSubmitOrder();
		List<StructuredScenario> scenarios = new ArrayList<>();
		scenarios.add(createScenarioLocalSupplier());
		scenarios.add(createScenarioInternationalSupplier());
		scenarios.add(createMainScenarioSubmitOrder());
		HashMap<String, List<StructuredScenario>> sequentiallyRelatedScenarios = service.findSequentiallyRelatedScenarios(structuredScenario, scenarios);
		HashMap<String, List<StructuredScenario>> nonSequentiallyRelatedScenarios = service.findNonSequentiallyRelatedScenarios(structuredScenario, scenarios, sequentiallyRelatedScenarios);
		System.out.println(nonSequentiallyRelatedScenarios);
	}
	@Test
	public void testFindNonSequentiallyRelatedScenariosNonExplicit() {
		ScenarioServiceImpl service = new ScenarioServiceImpl();
		StructuredScenario structuredScenario = createScenarioLocalSupplier();
		List<StructuredScenario> scenarios = new ArrayList<>();
		scenarios.add(createScenarioLocalSupplier());
		scenarios.add(createScenarioInternationalSupplier());
		scenarios.add(createMainScenarioSubmitOrder());
		HashMap<String, List<StructuredScenario>> sequentiallyRelatedScenarios = service.findSequentiallyRelatedScenarios(structuredScenario, scenarios);
		HashMap<String, List<StructuredScenario>> nonSequentiallyRelatedScenarios = service.findNonSequentiallyRelatedScenarios(structuredScenario, scenarios, sequentiallyRelatedScenarios);
		System.out.println(nonSequentiallyRelatedScenarios);
	}
	

	public static void main(String[] args) {
		
		/*
		testConvertToStructuredScenarioWithExceptionInMultipleLines3();
		testConvertToStructuredScenarioWithSuccess();
		testConvertToStructuredScenarioWithExceptionInMultipleLines2();
		
		testFindNonSequentiallyRelatedScenariosExplicit();
		testFindNonSequentiallyRelatedScenariosNonExplicit();
		
		testSortScenarios();
		testSortScenariosByEpisodes();
		testLinkingVerbOrStateVerbInAlternativeCause();
		*/
	}
	
	@Test
	public void testLinkingVerbOrStateVerbInAlternativeCause() {
		
	}

	 public static StructuredScenario createMainScenarioSubmitOrder() {
	        StructuredScenario scenario = new StructuredScenario();
	        scenario.createScenario(1L, "Submit Order", "Allow customers to find the best supplier for a given order"
	                                                                , Calendar.getInstance(), false, 1L, "Online Broker System");

	        StructuredContext contexto = new StructuredContext();
	        contexto.createContext("");
	        contexto.addPreCondition("The Broker System is online");
	        contexto.addPreCondition("the Broker System welcome page is being displayed");
	        scenario.setContext(contexto);

	        scenario.addActor("Customer");
	        scenario.addActor("Broker System");

	        scenario.addResource("Login page");
	        scenario.addResource("Login information");
	        scenario.addResource("Order");

	        StructuredEpisode episodio1 = new StructuredEpisode();
	        episodio1.createSimpleEpisode("1", "The Customer loads the login page");
	        scenario.addEpisode(episodio1);

	        StructuredEpisode episodio2 = new StructuredEpisode();
	        episodio2.createSimpleEpisode("2", "The Broker System asks for the Customer's login information");
	        scenario.addEpisode(episodio2);

	        StructuredEpisode episodio3 = new StructuredEpisode();
	        episodio3.createSimpleEpisode("3", "The Customer enters her login information");
	        scenario.addEpisode(episodio3);

	        StructuredEpisode episodio4 = new StructuredEpisode();
	        episodio4.createSimpleEpisode("4", "The Broker System checks the provided login information");
	        scenario.addEpisode(episodio4);

	        StructuredEpisode episodio5 = new StructuredEpisode();
	        episodio5.createSimpleEpisode("5", "The Broker System displays an order page");
	        scenario.addEpisode(episodio5);

	        StructuredEpisode episodio6 = new StructuredEpisode();
	        episodio6.createSimpleEpisode("6", "The Customer creates a new Order");
	        scenario.addEpisode(episodio6);

	        StructuredEpisode episodio7 = new StructuredEpisode();
	        episodio7.createIterativeDoWhileEpisode("7", "The Customer adds a item to the Order");
	        episodio7.addCondition("the Customer has more items to add to the order");
	        scenario.addEpisode(episodio7);

	        StructuredEpisode episodio8 = new StructuredEpisode();
	        episodio8.createSimpleEpisode("8", "The Customer submits the Order");
	        scenario.addEpisode(episodio8);

	        StructuredEpisode episodio9 = new StructuredEpisode();
	        episodio9.createSimpleEpisode("9", "The Broker System broadcast the Order to the Suppliers");
	        scenario.addEpisode(episodio9);

	        StructuredEpisode episodio10 = new StructuredEpisode();
	        episodio10.createSimpleEpisode("10", "# LOCAL SUPPLIER BID FOR ORDER");
	        scenario.addEpisode(episodio10);

	        StructuredEpisode episodio11 = new StructuredEpisode();
	        episodio11.createSimpleEpisode("11", "INTERNATIONAL SUPPLIER BID FOR ORDER #");
	        scenario.addEpisode(episodio11);

	        StructuredEpisode episodio12 = new StructuredEpisode();
	        episodio12.createSimpleEpisode("12", "PROCESS BIDS");
	        scenario.addEpisode(episodio12);

	        StructuredAlternative alternative1_1 = new StructuredAlternative();
	        alternative1_1.createAlternative("1.1");
	        alternative1_1.addCause("Customer is not registered");
	        alternative1_1.addSolution("REGISTER CUSTOMER");
	        alternative1_1.setBranchingEpisode(episodio1);
	        scenario.addAlternative(alternative1_1);

	        StructuredAlternative alternative2_1 = new StructuredAlternative();
	        alternative2_1.createAlternative("2.1");
	        alternative2_1.addCause("after 60 seconds");
	        alternative2_1.addSolution("The Broker System displays a login timeout page");
	        alternative2_1.setBranchingEpisode(episodio2);
	        scenario.addAlternative(alternative2_1);

	        StructuredAlternative alternative4_1 = new StructuredAlternative();
	        alternative4_1.createAlternative("4.1");
	        alternative4_1.addCause("the Customer login information is not accurate");
	        alternative4_1.addSolution("The Broker System displays an alert message");
	        alternative4_1.setBranchingEpisode(episodio4);
	        scenario.addAlternative(alternative4_1);

	        StructuredAlternative alternative8_1 = new StructuredAlternative();
	        alternative8_1.createAlternative("8.1");
	        alternative8_1.addCause("the order is empty");
	        alternative8_1.addSolution("The Broker System displays an error message");
	        alternative8_1.setBranchingEpisode(episodio8);
	        scenario.addAlternative(alternative8_1);
	        return scenario;
	    }

	 public static StructuredScenario createScenarioLocalSupplier() {
	        
	        StructuredScenario scenario = new StructuredScenario();
	        scenario.createScenario(2L,"LOCAL SUPPLIER BID FOR ORDER","Submit a bid"
	                                , Calendar.getInstance(), false, 1L, "Online Broker System");
	        
	        StructuredContext contexto = new StructuredContext();
	        contexto.createContext("");
	        contexto.addPreCondition("An Order has been broadcasted");
	        contexto.addPostCondition("Local Supplier has bidden");
	        scenario.setContext(contexto);

	        scenario.addActor("Local Supplier");
	        scenario.addActor("Broker System");

	        scenario.addResource("Order");
	        scenario.addResource("Bid");
	        
	        StructuredEpisode episodio1 = new StructuredEpisode();
	        episodio1.createSimpleEpisode("1", "Local Supplier receives the Order and examines it");
	        scenario.addEpisode(episodio1);

	        StructuredEpisode episodio2 = new StructuredEpisode();
	        episodio2.createSimpleEpisode("2", "Local Supplier determines the applicable taxes to the order and creates a bid");
	        scenario.addEpisode(episodio2);

	        StructuredEpisode episodio3 = new StructuredEpisode();
	        episodio3.createSimpleEpisode("3", "Local Supplier submits a bid for the Order");
	        scenario.addEpisode(episodio3);

	        StructuredEpisode episodio4 = new StructuredEpisode();
	        episodio4.createSimpleEpisode("4", "The Broker System sends the Bid to the Customer");
	        scenario.addEpisode(episodio4);
	        
	        StructuredAlternative alternative1_1 = new StructuredAlternative();
	        alternative1_1.createAlternative("1.1");
	        alternative1_1.addCause("Local Supplier can not satisfy the Order");
	        alternative1_1.addSolution("Local Supplier passes on the Order");
	        scenario.addAlternative(alternative1_1);
	        
	        return scenario;
	    }
	    
	    public static StructuredScenario createScenarioInternationalSupplier() {
	        
	        StructuredScenario scenario = new StructuredScenario();
	        scenario.createScenario(3L,"INTERNATIONAL SUPPLIER BID FOR ORDER","Process a bid"
	                                , Calendar.getInstance(), false, 1L, "Online Broker System");
	        
	        StructuredContext contexto = new StructuredContext();
	        contexto.createContext("");
	        contexto.addPreCondition("An Order has been broadcasted");
	        contexto.addPostCondition("International Supplier has bidden");
	        scenario.setContext(contexto);

	        scenario.addActor("International Supplier");
	        scenario.addActor("Broker System");

	        scenario.addResource("Order");
	        scenario.addResource("Bid");
	        
	        StructuredEpisode episodio1 = new StructuredEpisode();
	        episodio1.createSimpleEpisode("1", "International Supplier receives the Order and examines it");
	        scenario.addEpisode(episodio1);

	        StructuredEpisode episodio2 = new StructuredEpisode();
	        episodio2.createSimpleEpisode("2", "International Supplier submits a Bid for de Order");
	        scenario.addEpisode(episodio2);

	        StructuredEpisode episodio3 = new StructuredEpisode();
	        episodio3.createSimpleEpisode("3", "The Broker systems sends the Bid to the Customer");
	        scenario.addEpisode(episodio3);
	        
	        StructuredAlternative alternative1_1 = new StructuredAlternative();
	        alternative1_1.createAlternative("1.1");
	        alternative1_1.addCause("The Order includes items restricted for exportation");
	        alternative1_1.addSolution("International Supplier passes on the Order");
	        alternative1_1.createAlternative("1.2");
	        alternative1_1.addCause("International Supplier can not satisfy the Order");
	        alternative1_1.addSolution("International Supplier passes on the Order");
	        scenario.addAlternative(alternative1_1);
	        
	        return scenario;
	    }
}
