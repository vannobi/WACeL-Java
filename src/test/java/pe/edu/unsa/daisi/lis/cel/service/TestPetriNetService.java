package pe.edu.unsa.daisi.lis.cel.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import pe.edu.unsa.daisi.lis.cel.domain.model.petrinet.PetriNet;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredContext;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredEpisode;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredAlternative;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredScenario;

public class TestPetriNetService {

	public static void main(String[] args) {
		//testTransformScenarioTopetriNet();

	}
	
	@Test
	public void testTransformScenarioTopetriNet() {
		IPetriNetService petrinetService = new PetriNetServiceImpl();
		PetriNet petriNet = petrinetService.transformScenario(createMainScenarioSubmitOrder(), 300, 50);
		System.out.println(petriNet.toString());
		//assertEquals(49, petriNet.getNodes().size());
		//assertEquals(61, petriNet.getNodes().size());
		//System.out.println(petrinetService.createPNMLFile(petriNet));
	}
	@Test 
	public void testExplicitNonSeqIntegratePetriNetsFromMainScenario() {
		IPetriNetService petrinetService = new PetriNetServiceImpl();
		
		StructuredScenario mainScenario = createMainScenarioSubmitOrder();
		HashMap<String, List<StructuredScenario>> seqRelatedScenarioHashMap = new HashMap<String, List<StructuredScenario>>(); //Sequentially related scenarios by type of relationship (EXPLICIT and NON_EXPLICIT)
		List<StructuredScenario> seqScenarios = new ArrayList<>();
		seqScenarios.add(createScenarioLocalSupplier());
		seqScenarios.add(createScenarioInternationalSupplier());
		seqRelatedScenarioHashMap.put("SUB_SCENARIO", seqScenarios);
		
		HashMap<String, List<StructuredScenario>> nonSeqRelatedScenarioHashMap = new HashMap<String, List<StructuredScenario>>(); //Non-Sequentially related scenarios by type of relationship (EXPLICIT and NON_EXPLICIT)
		List<StructuredScenario> nonSeqScenarios = new ArrayList<>();
		nonSeqScenarios.add(createScenarioLocalSupplier());
		nonSeqScenarios.add(createScenarioInternationalSupplier());
		
		nonSeqRelatedScenarioHashMap.put("EXPLICIT", nonSeqScenarios);
		
		PetriNet mainPetriNet = petrinetService.integratePetriNetsFromMainScenario(mainScenario, seqRelatedScenarioHashMap, nonSeqRelatedScenarioHashMap);
		
		System.out.println(mainPetriNet.toString());
		//assertEquals(49, mainPetriNet.getNodes().size());
		System.out.println(petrinetService.createPNML(mainPetriNet));
	}
	
	@Test 
	public void testNonExplicitNonSeqIntegratePetriNetsFromMainScenario() {
		IPetriNetService petrinetService = new PetriNetServiceImpl();
		StructuredScenario mainScenario = createScenarioLocalSupplier();
		HashMap<String, List<StructuredScenario>> seqRelatedScenarioHashMap = new HashMap<String, List<StructuredScenario>>(); //Sequentially related scenarios by type of relationship (EXPLICIT and NON_EXPLICIT)
		List<StructuredScenario> seqScenarios = new ArrayList<>();
		
		HashMap<String, List<StructuredScenario>> nonSeqRelatedScenarioHashMap = new HashMap<String, List<StructuredScenario>>(); //Non-Sequentially related scenarios by type of relationship (EXPLICIT and NON_EXPLICIT)
		List<StructuredScenario> nonSeqScenarios = new ArrayList<>();
		nonSeqScenarios.add(createScenarioInternationalSupplier());
		
		nonSeqRelatedScenarioHashMap.put("NON_EXPLICIT", nonSeqScenarios);
		
		PetriNet mainPetriNet = petrinetService.integratePetriNetsFromMainScenario(mainScenario, seqRelatedScenarioHashMap, nonSeqRelatedScenarioHashMap);
		
		System.out.println(mainPetriNet.toString());
		//assertEquals(49, mainPetriNet.getNodes().size());
		System.out.println(petrinetService.createPNML(mainPetriNet));
	}
	
	@Test
	public void testDepthFirstSearchRecursiveAllPathsMainPetriNet() {
		IPetriNetService petrinetService = new PetriNetServiceImpl();
		PetriNet petriNet = petrinetService.transformScenario(createMainScenarioSubmitOrder(), 300, 50);
		petrinetService.depthFirstSearchRecursiveAllPaths(petriNet, null, petriNet.getStartPlace(), petriNet.getFinalTransition());
		System.out.println(petriNet.toString());
		
		//assertEquals(49, petriNet.getNodes().size());
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
	        alternative8_1.addSolution("The Broker System thinks");
	        alternative8_1.addSolution("The Broker System finishes");
	        alternative8_1.addPostCondition("a");
	        alternative8_1.addPostCondition("b");
	        alternative8_1.addPostCondition("c");
	        alternative8_1.setBranchingEpisode(episodio8);
	        alternative8_1.setScenarioFinish(true);
	        scenario.addAlternative(alternative8_1);
	        
	        StructuredAlternative alternative8_2 = new StructuredAlternative();
	        alternative8_2.createAlternative("8.2");
	        alternative8_2.addCause("the order is overflow");
	        alternative8_2.addSolution("The Broker System displays an error message");
	        alternative8_2.addSolution("The Broker System thinks");
	        alternative8_2.addSolution("System finishes");
	        alternative8_2.addPostCondition("a");
	        alternative8_2.addPostCondition("b");
	        alternative8_2.addPostCondition("c");
	        alternative8_2.setBranchingEpisode(episodio8);
	        scenario.addAlternative(alternative8_2);
	        
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
	        alternative1_1.setBranchingEpisode(episodio1);
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
	        scenario.addAlternative(alternative1_1);
	        alternative1_1.setBranchingEpisode(episodio1);
	        StructuredAlternative alternative1_2 = new StructuredAlternative();
	        alternative1_2.createAlternative("1.2");
	        alternative1_2.addCause("International Supplier can not satisfy the Order");
	        alternative1_2.addSolution("International Supplier passes on the Order");
	        alternative1_2.setBranchingEpisode(episodio1);
	        scenario.addAlternative(alternative1_2);
	        
	        return scenario;
	    }
	    
}
