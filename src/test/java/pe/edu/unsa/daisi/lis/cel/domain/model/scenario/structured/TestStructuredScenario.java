package pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredContext;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredEpisode;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredAlternative;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredScenario;

public class TestStructuredScenario {

	public static void testCreateScenarioWithSuccess() {
		System.out.println("New Scenario");
		StructuredScenario structuredScenario = new StructuredScenario();
		structuredScenario.createScenario(1L, "Submit Order", "Allow customers to find the best supplier for a given order"
				, Calendar.getInstance(), false, 1L, "Online Broker System");

		StructuredContext contexto = new StructuredContext();
		contexto.createContext("");
		contexto.addPreCondition("The Broker System is online");
		contexto.addPreCondition("the Broker System welcome page is being displayed");
		structuredScenario.setContext(contexto);

		structuredScenario.addActor("Customer");
		structuredScenario.addActor("Broker System");

		structuredScenario.addResource("Login page");
		structuredScenario.addResource("Login information");
		structuredScenario.addResource("Order");

		StructuredEpisode episodio1 = new StructuredEpisode();
		episodio1.createSimpleEpisode("1", "The Customer loads the login page");
		structuredScenario.addEpisode(episodio1);

		StructuredEpisode episodio2 = new StructuredEpisode();
		episodio2.createSimpleEpisode("2", "The Broker System asks for the Customer's login information");
		structuredScenario.addEpisode(episodio2);

		StructuredEpisode episodio3 = new StructuredEpisode();
		episodio3.createSimpleEpisode("3", "The Customer enters her login information");
		structuredScenario.addEpisode(episodio3);

		StructuredEpisode episodio4 = new StructuredEpisode();
		episodio4.createSimpleEpisode("4", "The Broker System checks the provided login information");
		structuredScenario.addEpisode(episodio4);

		StructuredEpisode episodio5 = new StructuredEpisode();
		episodio5.createSimpleEpisode("5", "The Broker System displays an order page");
		structuredScenario.addEpisode(episodio5);

		StructuredEpisode episodio6 = new StructuredEpisode();
		episodio6.createSimpleEpisode("6", "The Customer creates a new Order");
		structuredScenario.addEpisode(episodio6);

		StructuredEpisode episodio7 = new StructuredEpisode();
		episodio7.createIterativeDoWhileEpisode("7", "The Customer creates a new Order");
		episodio7.addCondition("the Customer has more items to add to the order");
		structuredScenario.addEpisode(episodio7);

		StructuredEpisode episodio8 = new StructuredEpisode();
		episodio8.createSimpleEpisode("8", "The Customer submits the Order");
		structuredScenario.addEpisode(episodio8);

		StructuredEpisode episodio9 = new StructuredEpisode();
		episodio9.createSimpleEpisode("9", "The Broker System broadcast the Order to the Suppliers");
		structuredScenario.addEpisode(episodio9);

		StructuredEpisode episodio10 = new StructuredEpisode();
		episodio10.createSimpleEpisode("10", "# LOCAL SUPPLIER BID FOR ORDER");
		structuredScenario.addEpisode(episodio10);

		StructuredEpisode episodio11 = new StructuredEpisode();
		episodio11.createSimpleEpisode("11", "INTERNATIONAL SUPPLIER BID FOR ORDER #");

		StructuredEpisode episodio12 = new StructuredEpisode();
		episodio12.createSimpleEpisode("12", "PROCESS BIDS");
		structuredScenario.addEpisode(episodio12);


		StructuredAlternative alternative1_1 = new StructuredAlternative();
		alternative1_1.createAlternative("1.1");
		alternative1_1.addCause("Customer is not registered");
		alternative1_1.addSolution("REGISTER CUSTOMER");
		structuredScenario.addAlternative(alternative1_1);

		StructuredAlternative alternative2_1 = new StructuredAlternative();
		alternative2_1.createAlternative("2.1");
		alternative2_1.addCause("after 60 seconds");
		alternative2_1.addSolution("The Broker System displays a login timeout page");
		structuredScenario.addAlternative(alternative2_1);

		StructuredAlternative alternative4_1 = new StructuredAlternative();
		alternative4_1.createAlternative("4.1");
		alternative4_1.addCause("the Customer login information is not accurate");
		alternative4_1.addSolution("The Broker System displays an alert message");
		structuredScenario.addAlternative(alternative4_1);

		StructuredAlternative alternative8_1 = new StructuredAlternative();
		alternative8_1.createAlternative("8.1");
		alternative8_1.addCause("the order is empty");
		alternative8_1.addSolution("The Broker System displays an error message");
		structuredScenario.addAlternative(alternative8_1);

		System.out.println(structuredScenario.toString());
		


	}
	public static void main(String[] args) {
		testCreateScenarioWithSuccess();
		List<String> objects = new ArrayList<String>();
		objects.add("asja");
		objects.add("aghfdA");
		System.out.println(objects.toString());
	}

}
