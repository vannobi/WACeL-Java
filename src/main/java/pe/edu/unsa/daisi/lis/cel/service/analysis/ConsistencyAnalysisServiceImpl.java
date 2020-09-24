package pe.edu.unsa.daisi.lis.cel.service.analysis;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.edu.unsa.daisi.lis.cel.domain.model.analysis.Defect;
import pe.edu.unsa.daisi.lis.cel.domain.model.analysis.DefectCategoryEnum;
import pe.edu.unsa.daisi.lis.cel.domain.model.analysis.DefectIndicatorEnum;
import pe.edu.unsa.daisi.lis.cel.domain.model.analysis.QualityPropertyEnum;
import pe.edu.unsa.daisi.lis.cel.domain.model.analysis.ScenarioElement;
import pe.edu.unsa.daisi.lis.cel.domain.model.petrinet.PetriNet;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredContext;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredEpisode;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredAlternative;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredScenario;
import pe.edu.unsa.daisi.lis.cel.service.IPetriNetService;
import pe.edu.unsa.daisi.lis.cel.service.PetriNetServiceImpl;
import pipe.modules.stateSpace.StateSpace;

@Service("consistencyAnalysisService")
public class ConsistencyAnalysisServiceImpl implements IConsistencyAnalysisService {
	@Autowired
	IPetriNetService petrinetService;
	
	@Override
	public List<Defect> analyze(StructuredScenario mainScenario, HashMap<String, List<StructuredScenario>> sequentiallyRelatedScenariosHashMap, HashMap<String, List<StructuredScenario>> nonSeqRelatedScenarioHashMap) {
		//@Episode 1: Derive Integrated Petri-Net from a main scenario
		PetriNet petriNet = petrinetService.integratePetriNetsFromMainScenario(mainScenario, sequentiallyRelatedScenariosHashMap, nonSeqRelatedScenarioHashMap);
		StreamSource streamPN = petrinetService.createPNMLStreamSource(petriNet);
		
		//@Episode 2: Generate reachability Graph for Integrated Petri-Net
		StateSpace stateSpace = new StateSpace();
		String pnAnalysisresult = stateSpace.stateSpaceAnalysis(streamPN);
		
		Boolean bounded = true;
		Boolean safe = true;
		Boolean live = true;
		
		List<Defect> defects = new ArrayList<Defect>();
		//Episode 3: Check the absence of non-determinism: A non-deterministic behavior occurs when a set of operations are simultaneously enabled 
		//indicator: Petri-Net with simultaneously enabled operations
		if(pnAnalysisresult.contains("<br/><b>Simultaneously enabled transitions:</b>") ) {
			String enabledTransitions = StringUtils.substringBetween(pnAnalysisresult, "<br/><b>Simultaneously enabled transitions:</b>", "</ul>");
			Defect defect = new Defect();
			defect.setQualityProperty(QualityPropertyEnum.NON_INTERFERENTIAL.getQualityProperty());
			defect.setScenarioId(mainScenario.getId());
			defect.setScenarioElement(ScenarioElement.TITLE.getScenarioElement());
			defect.setIndicator(DefectIndicatorEnum.NON_INTERFERENTIAL_SIMULTANEOUS_ENABLED_OPERATIONS_INDICATOR.getDefectIndicator());
			if(enabledTransitions != null)
				defect.setIndicator(defect.getIndicator().replace("<indicator>", enabledTransitions));
			else
				defect.setIndicator(defect.getIndicator().replace("<indicator>", pnAnalysisresult));
			defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
			defect.setFixRecomendation(DefectIndicatorEnum.NON_INTERFERENTIAL_SIMULTANEOUS_ENABLED_OPERATIONS_INDICATOR.getFixRecomendation());
			defects.add(defect);

		}
		
		//Episode 4: Check the absence of overflow: An executable model is overflowed when the number of elements in some communication channel or resource exceeds a finite capacity.
		//indicator: Petri-Net is not bounded, i.e, It contains overflowed resources
		if(pnAnalysisresult.contains("<b>Bounded:</b> false")) {
			String boundedPlaces = StringUtils.substringBetween(pnAnalysisresult, "<br/><b>Places to overflow: </b>", "<br/>");
			bounded = false;
			Defect defect = new Defect();
			defect.setQualityProperty(QualityPropertyEnum.BOUNDEDNESS.getQualityProperty());
			defect.setScenarioId(mainScenario.getId());
			defect.setScenarioElement(ScenarioElement.TITLE.getScenarioElement());
			defect.setIndicator(DefectIndicatorEnum.BOUNDEDNESS_RESOURCE_OVERFLOW_INDICATOR.getDefectIndicator());
			defect.setIndicator(defect.getIndicator().replace("<indicator>", boundedPlaces));
			defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
			defect.setFixRecomendation(DefectIndicatorEnum.BOUNDEDNESS_RESOURCE_OVERFLOW_INDICATOR.getFixRecomendation());
			defects.add(defect);

		} else {
			//indicator: Petri-Net is not safe, i.e, It is not 1-bounded
			if(pnAnalysisresult.contains("<b>Safe: </b>false")) {
				safe = false;
				Defect defect = new Defect();
				defect.setQualityProperty(QualityPropertyEnum.SAFENESS.getQualityProperty());
				defect.setScenarioId(mainScenario.getId());
				defect.setScenarioElement(ScenarioElement.TITLE.getScenarioElement());
				defect.setIndicator(DefectIndicatorEnum.BOUNDEDNESS_PETRI_NET_NOT_SAFE.getDefectIndicator());
				defect.setIndicator(defect.getIndicator().replace("<indicator>", "<b>Safe: </b>false"));
				defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
				defect.setFixRecomendation(DefectIndicatorEnum.BOUNDEDNESS_PETRI_NET_NOT_SAFE.getFixRecomendation());
				defects.add(defect);
				
			}
		}

		//Episode 5: Check the absence of deadlocks [7]: If a deadlock happens after performing an alternative's solution, then, it could be Information.
		//indicator: Petri-Net with Path to deadlock
		if(pnAnalysisresult.contains("<b>Deadlock: </b>true")) {
			live = false;
			//deadlock
			String deadlockTransitions = StringUtils.substringBetween(pnAnalysisresult, "<br/><b>Shortest path to deadlock: </b>", "<br/><br/>");
			
			Defect defect = new Defect();
			defect.setQualityProperty(QualityPropertyEnum.LIVENESS.getQualityProperty());
			defect.setScenarioId(mainScenario.getId());
			defect.setScenarioElement(ScenarioElement.TITLE.getScenarioElement());
			defect.setIndicator(DefectIndicatorEnum.LIVENESS_DEADLOCK_INDICATOR.getDefectIndicator());
			defect.setIndicator(defect.getIndicator().replace("<indicator>", deadlockTransitions));
			defect.setDefectCategory(DefectCategoryEnum.INFO.getDefectCategory());
			defect.setFixRecomendation(DefectIndicatorEnum.LIVENESS_DEADLOCK_INDICATOR.getFixRecomendation());
			defects.add(defect);
			
		}

		//Episode 6: Check the absence of never enabled operations; 
		//Indicator: Petri-Net with Never enabled operations
		if(pnAnalysisresult.contains("<b>Never enabled transitions:</b>")) {
			live = false;
			//never enabled
			String neverEnabledTransitions = StringUtils.substringBetween(pnAnalysisresult, "<br/><b>Never enabled transitions:</b>", "<br/><br/>");
			
			Defect defect = new Defect();
			defect.setQualityProperty(QualityPropertyEnum.LIVENESS.getQualityProperty());
			defect.setScenarioId(mainScenario.getId());
			defect.setScenarioElement(ScenarioElement.TITLE.getScenarioElement());
			defect.setIndicator(DefectIndicatorEnum.LIVENESS_NEVER_ENABLED_OPERATIONS_INDICATOR.getDefectIndicator());
			defect.setIndicator(defect.getIndicator().replace("<indicator>", neverEnabledTransitions));
			defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
			defect.setFixRecomendation(DefectIndicatorEnum.LIVENESS_NEVER_ENABLED_OPERATIONS_INDICATOR.getFixRecomendation());
			defects.add(defect);
		}
		
		//Episode 7: Check Reversibility: If the executable model is not reversible, the automatic error recovery is not possible [3].
		//Indicator: There are no a path from an operation to the initial state of the Petri-Net, i.e, Petri-Net is not bounded, not safe and not live
		if(!bounded && !safe && !live) {
			Defect defect = new Defect();
			defect.setQualityProperty(QualityPropertyEnum.REVERSIBILITY.getQualityProperty());
			defect.setScenarioId(mainScenario.getId());
			defect.setScenarioElement(ScenarioElement.TITLE.getScenarioElement());
			defect.setIndicator(DefectIndicatorEnum.REVERSIBILITY_NO_PATH_TO_INITIAL_STATE_INDICATOR.getDefectIndicator());
			//defect.setIndicator(defect.getIndicator().replace("<indicator>", neverEnabledTransitions));
			defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
			defect.setFixRecomendation(DefectIndicatorEnum.REVERSIBILITY_NO_PATH_TO_INITIAL_STATE_INDICATOR.getFixRecomendation());
			defects.add(defect);
		}
		return defects;
	}
	
	
	public static void main(String[] args) {
		
		
		StateSpace stateSpace = new StateSpace();
		/*
		String fileName = "";
		String fileNameResult = "";
		
	
		fileName = "C:/Edgar/Doutorado PUC/Doutorado/Proposta Tese - Projeto/PIPEv4.3.0/SubmitOrderIntegrado.txt";
		String pnAnalysisresult = stateSpace.stateSpaceAnalysis(fileName);
		
		*/
		
		IPetriNetService petrinetService = new PetriNetServiceImpl();
		PetriNet petriNet = petrinetService.transformScenario(createMainScenarioSubmitOrder(), 300, 50);
		StreamSource streamPN = petrinetService.createPNMLStreamSource(petriNet);
		String pnAnalysisresult = stateSpace.stateSpaceAnalysis(streamPN);
		/*
		<br/><b>Bounded:</b> true
		<br/><b>Places to overflow: </b>
		<br/><b>Safe: </b>true
		<br/><b>Deadlock: </b>true
		<br/><b>Shortest path to deadlock: </b>
		<br/><b>Never enabled transitions:</b> 
		*/
		//Bounded?
		if(pnAnalysisresult.contains("<b>Bounded:</b> false")) {
			//not bounded
			System.out.println("<br/><b>Bounded:</b> false");
			System.out.println("<br/><b>Places to overflow: </b>");
			String boundedPlaces = StringUtils.substringBetween(pnAnalysisresult, "<br/><b>Places to overflow: </b>", "<br/>");
			System.out.println(boundedPlaces);
			//indicator
			
		} else {
			if(pnAnalysisresult.contains("<b>Safe: </b>true")) {
				System.out.println("<b>Safe: </b>true");
			} else {
				System.out.println("<b>Safe: </b>false");
			}
		}
		
		//DeadLock?
		if(pnAnalysisresult.contains("<b>Deadlock: </b>true")) {
			//deadlock
			
			System.out.println("<br/><b>Shortest path to deadlock: </b>");
			String deadlockTransitions = StringUtils.substringBetween(pnAnalysisresult, "<br/><b>Shortest path to deadlock: </b>", "<br/>");
			System.out.println(deadlockTransitions);
			//indicator
			
		}
		
		//Never enabled?
		if(pnAnalysisresult.contains("<b>Never enabled transitions:</b>")) {
			//deadlock
			
			System.out.println("<br/><b>Never enabled transitions:</b>");
			String neverEnabledTransitions = StringUtils.substringBetween(pnAnalysisresult, "<br/><b>Never enabled transitions:</b>", "<br/>");
			System.out.println(neverEnabledTransitions);
			//indicator
			
		}
		

		System.out.println(pnAnalysisresult);
		
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
	

}
