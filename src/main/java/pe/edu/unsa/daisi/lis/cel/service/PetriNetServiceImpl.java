package pe.edu.unsa.daisi.lis.cel.service;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import pe.edu.unsa.daisi.lis.cel.domain.model.analysis.ScenarioElement;
import pe.edu.unsa.daisi.lis.cel.domain.model.petrinet.Arc;
import pe.edu.unsa.daisi.lis.cel.domain.model.petrinet.ArcTypeEnum;
import pe.edu.unsa.daisi.lis.cel.domain.model.petrinet.Node;
import pe.edu.unsa.daisi.lis.cel.domain.model.petrinet.NodeTypeEnum;
import pe.edu.unsa.daisi.lis.cel.domain.model.petrinet.PetriNet;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredEpisode;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredAlternative;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredScenario;
import pe.edu.unsa.daisi.lis.cel.util.scenario.preprocess.ScenarioManipulation;

/**
 *
 * @author Edgar
 *
 */
@Service("petriNetService")
public class PetriNetServiceImpl implements IPetriNetService {
	private static int INCREMENT_Y = 50;
	private static int SMALL_INCREMENT_Y = 25;
	private static int LARGE_INCREMENT_Y = 100;
	private static int INCREMENT_X = 50;
	private static int LARGE_INCREMENT_X = 200;
	private static int ORIENTATION_0 = 0;
	private static String LABEL_COMPONENTS_DELIMITER = "_";
	private static String LABEL_COMPONENTS_OTHER_DELIMITER = "-";
	private List<String> lstPath = new ArrayList<>();
	private List<List<Node>> lstPathNodes = new ArrayList<>();




	public PetriNet transformScenario(StructuredScenario scenario, int initialPositionX, int initialPositionY) {
		Integer countDummyPlaces = 0;
		Integer countTransitions = 0;
		int currentPositionX = initialPositionX - INCREMENT_X;
		int currentPositionY = initialPositionY;

		Node currentNode = null; //pointer to the last Dummy Node of a previous Sub-Petri_net
		Node lastTransitionEpisode = null; //pointer to the last transition generated from an Episode

		PetriNet petriNet = new PetriNet(scenario.getId(), scenario.getTitle());

		//CREATE SUB-PETRI-NET FROM SCENARIO TRIGGERING - INITIAL STATE
		//Create Start Node from Title
		Node startNode = new Node("Start", scenario.getTitle(), ScenarioElement.TITLE.getScenarioElement(), NodeTypeEnum.PLACE_WITH_TOKEN);
		startNode.setTokens(1);
		startNode.setPositionX(initialPositionX - INCREMENT_X);
		startNode.setPositionY(currentPositionY);
		startNode = petriNet.addNode(startNode);
		petriNet.setStartPlace(startNode);
		petriNet.setMinPositionX(initialPositionX - INCREMENT_X);
		petriNet.setMinPositionY(currentPositionY);

		//Dummy Transition to enable firing - T0
		Node dummyTranstion = new Node("Scenario Triggering",
				scenario.getId()+LABEL_COMPONENTS_DELIMITER+NodeTypeEnum.TRANSITION.getAcronym()+LABEL_COMPONENTS_DELIMITER+scenario.getTitle()+LABEL_COMPONENTS_DELIMITER+"Scenario Triggering",
				ScenarioElement.TITLE.getScenarioElement(), NodeTypeEnum.TRANSITION);

		dummyTranstion.setTimed(true);
		dummyTranstion.setDummy(true);
		dummyTranstion.setPositionX(initialPositionX);
		currentPositionY = currentPositionY + INCREMENT_Y;
		dummyTranstion.setPositionY(currentPositionY);
		dummyTranstion = petriNet.addNode(dummyTranstion);
		petriNet.setMaxPositionX(dummyTranstion.getPositionX());
		countTransitions++;

		//Link Start Node to T0
		Arc arc = new Arc(ArcTypeEnum.ARC, startNode, dummyTranstion);
		arc=petriNet.addArc( arc);
		
		lastTransitionEpisode = dummyTranstion;

		//Create Input Places from Pre-conditions
		currentPositionX = initialPositionX + INCREMENT_X;
		currentPositionY = startNode.getPositionY();
		for(String preCondition : scenario.getContext().getPreConditions()) {
			Node node = new Node(preCondition, preCondition, ScenarioElement.CONTEXT_PRE_CONDITION.getScenarioElement(), NodeTypeEnum.PLACE_WITH_TOKEN);
			node.setTokens(1);
			currentPositionX = currentPositionX + INCREMENT_X;
			node.setPositionX(currentPositionX);
			currentPositionY = currentPositionY + SMALL_INCREMENT_Y;
			node.setPositionY(currentPositionY);

			//IF there exist a place with same name THEN Fuse Places
			Node oldNode = petriNet.findPlaceByNameWithTracePrePostCondition(node.getName());
			if(oldNode == null) {
				node = petriNet.addNode(node);
				if(petriNet.getMaxPositionX() < node.getPositionX())
					petriNet.setMaxPositionX(node.getPositionX());

				arc = new Arc(ArcTypeEnum.ARC, node, dummyTranstion);
				arc = petriNet.addArc(arc);
			}

		}
		if(!scenario.getContext().getPreConditions().isEmpty()) {
			currentPositionY = currentPositionY + SMALL_INCREMENT_Y;
			dummyTranstion.setPositionY(currentPositionY);
		}

		//Dummy Output Place - P0
		Node dummyPlace = new Node(NodeTypeEnum.PLACE.getAcronym()+countDummyPlaces, NodeTypeEnum.PLACE.getAcronym()+countDummyPlaces, ScenarioElement.TITLE.getScenarioElement(), NodeTypeEnum.PLACE);
		dummyPlace.setDummy(true);
		currentPositionX = initialPositionX;
		dummyPlace.setPositionX(currentPositionX);
		currentPositionY = currentPositionY + INCREMENT_Y;
		dummyPlace.setPositionY(currentPositionY);
		dummyPlace = petriNet.addNode(dummyPlace);
		countDummyPlaces++;

		//Link Dummy Transition - T0 to Dummy Output Place - P0
		arc = new Arc(ArcTypeEnum.ARC, dummyTranstion, dummyPlace);
		arc = petriNet.addArc(arc);

		currentNode = dummyPlace;

		//CREATE INPUT PLACES, OUTPUT PLACES AND TRANSITIONS FROM EPISODES
		Node forkTransition = null;
		Node joinTransition = null;
		int indexCurrentEpisode = 1;
		for(StructuredEpisode episode : scenario.getEpisodes()) {
			//IF episode sentence starts with "#" THEN
			if(!episode.getSentence().isEmpty() && episode.getSentence().startsWith("#")) {
				//Create Input Place and transition for denoting a FORK
				forkTransition = new Node("FORK_"+Integer.toString(indexCurrentEpisode),
						scenario.getId() +LABEL_COMPONENTS_DELIMITER+ NodeTypeEnum.TRANSITION.getAcronym() + LABEL_COMPONENTS_DELIMITER +Integer.toString(indexCurrentEpisode) + LABEL_COMPONENTS_DELIMITER +"FORK",
						ScenarioElement.EPISODES.getScenarioElement(), NodeTypeEnum.TRANSITION);

				currentPositionX = initialPositionX;
				forkTransition.setPositionX(currentPositionX);
				currentPositionY = currentPositionY + INCREMENT_Y;
				forkTransition.setPositionY(currentPositionY);
				forkTransition = petriNet.addNode(forkTransition);

				//Link Places and transitions into a Sub-Petri-Net
				arc = new Arc(ArcTypeEnum.ARC, currentNode , forkTransition);
				arc = petriNet.addArc(arc);

				currentNode = forkTransition;
				lastTransitionEpisode = forkTransition;

				//Create transition for denoting a JOIN
				joinTransition = new Node("JOIN_"+Integer.toString(indexCurrentEpisode),
						scenario.getId() +LABEL_COMPONENTS_DELIMITER+ NodeTypeEnum.TRANSITION.getAcronym() + LABEL_COMPONENTS_DELIMITER +Integer.toString(indexCurrentEpisode) + LABEL_COMPONENTS_DELIMITER +"JOIN",
						ScenarioElement.EPISODE_SENTENCE.getScenarioElement(), NodeTypeEnum.TRANSITION);

				currentPositionX = initialPositionX;
				joinTransition.setPositionX(currentPositionX);
				currentPositionY = currentPositionY + INCREMENT_Y;
				joinTransition.setPositionY(currentPositionY);
				joinTransition = petriNet.addNode(joinTransition);

				//Update JOIN when find the right "JOIN"


			}

			//Transform episode according to its type
			//Create a transition from episode sentence
			String episodeName = episode.getSentence() != null? episode.getSentence().replace("#", "").trim() : "NULL"; //first and Last?
			String episodeId = episode.getId() != null? episode.getId() : countTransitions.toString();
			String traceTransition = forkTransition != null ? ScenarioElement.EPISODE_SENTENCE_NON_SEQUENTIAL.getScenarioElement().replace("<id>", episodeId): ScenarioElement.EPISODE_SENTENCE.getScenarioElement().replace("<id>", episodeId); //where i am from?
			String episodeLabel = episodeName.length() > 50 ? episodeName.substring(0, 50) : episodeName;
			Node transitionEpisodeSentence = new Node(episodeName,
					scenario.getId() +LABEL_COMPONENTS_DELIMITER+ NodeTypeEnum.TRANSITION.getAcronym() + LABEL_COMPONENTS_DELIMITER + episodeId + LABEL_COMPONENTS_DELIMITER + episodeLabel,
					traceTransition, NodeTypeEnum.TRANSITION);


			//create Input Dummy Place and link to structure control generated from episode type
			currentPositionX = currentNode.getPositionX();
			Node inputDummyPlace = null;
			if(forkTransition != null) { // AND JOIN?
				inputDummyPlace = new Node(NodeTypeEnum.PLACE.getAcronym()+countDummyPlaces, NodeTypeEnum.PLACE.getAcronym()+countDummyPlaces, ScenarioElement.EPISODE_ID.getScenarioElement().replace("<id>", episodeId), NodeTypeEnum.PLACE);
				inputDummyPlace.setDummy(true);
				inputDummyPlace.setPositionX(initialPositionX + forkTransition.getAdjNodes().size()*LARGE_INCREMENT_X);
				currentPositionY = forkTransition.getPositionY();
				inputDummyPlace.setPositionY(currentPositionY + INCREMENT_Y);

				inputDummyPlace = petriNet.addNode(inputDummyPlace);
				countDummyPlaces++;

				//arc = new Arc(ArcTypeEnum.ARC, inputDummyPlace, transitionEpisodeSentence);
				//arc = petriNet.addArc(arc);

				//link to previous dummy place
				arc = new Arc(ArcTypeEnum.ARC, currentNode, inputDummyPlace);
				arc = petriNet.addArc(arc);

			} else {
				inputDummyPlace = currentNode; //previous output dummy place
				//arc = new Arc(ArcTypeEnum.ARC, inputDummyPlace, transitionEpisodeSentence);
				//arc = petriNet.addArc(arc);

			}


			Node outputDummyPlace = null;
			if(episode.isSimple() || episode.isConditional() || episode.isOptional()) {//transitionEpisodeSentence.getLabel()+"_ELSE",
				//Create transition T
				transitionEpisodeSentence = petriNet.addNode(transitionEpisodeSentence);
				countTransitions++;
				currentPositionX = inputDummyPlace.getPositionX();
				transitionEpisodeSentence.setPositionX(currentPositionX);
				currentPositionY = inputDummyPlace.getPositionY() + INCREMENT_Y;
				transitionEpisodeSentence.setPositionY(currentPositionY);
				if(petriNet.getMaxPositionX() < transitionEpisodeSentence.getPositionX())
					petriNet.setMaxPositionX(transitionEpisodeSentence.getPositionX());

				arc = new Arc(ArcTypeEnum.ARC, inputDummyPlace, transitionEpisodeSentence);
				arc = petriNet.addArc(arc);


				//Create Input Places from Constraints and Link to transition
				currentPositionX = transitionEpisodeSentence.getPositionX();
				currentPositionY = transitionEpisodeSentence.getPositionY();
				for(String constraint : episode.getConstraints()) {
					Node node = new Node(constraint, constraint, ScenarioElement.EPISODE_CONSTRAINT.getScenarioElement().replace("<id>", episodeId), NodeTypeEnum.PLACE_WITH_TOKEN);
					node.setTokens(1);
					currentPositionX = currentPositionX + INCREMENT_X;
					node.setPositionX(currentPositionX);
					node.setPositionY(currentPositionY - INCREMENT_Y);
					node = petriNet.addNode( node);

					if(petriNet.getMaxPositionX() < node.getPositionX())
						petriNet.setMaxPositionX(node.getPositionX());

					arc = new Arc(ArcTypeEnum.ARC, node, transitionEpisodeSentence);
					arc = petriNet.addArc(arc);
					arc = new Arc(ArcTypeEnum.ARC, transitionEpisodeSentence, node);
					arc = petriNet.addArc(arc);

				}

				//Create Input Places from Pre-conditions and Link to transition
				for(String preCondition : episode.getPreConditions()) {
					Node node = new Node(preCondition, preCondition, ScenarioElement.EPISODE_PRE_CONDITION.getScenarioElement().replace("<id>", episodeId), NodeTypeEnum.PLACE_WITH_TOKEN);
					node.setTokens(1);
					currentPositionX = currentPositionX + INCREMENT_X;
					node.setPositionX(currentPositionX);
					node.setPositionY(currentPositionY - INCREMENT_Y);
					//IF there exist a place with same name THEN Fuse Places
					Node oldNode = petriNet.findPlaceByNameWithTracePrePostCondition(node.getName());
					if(oldNode == null) {
						node = petriNet.addNode( node);

						if(petriNet.getMaxPositionX() < node.getPositionX())
							petriNet.setMaxPositionX(node.getPositionX());

						arc = new Arc(ArcTypeEnum.ARC, node, transitionEpisodeSentence);
						arc = petriNet.addArc( arc);

					} else {
						oldNode.setTokens(oldNode.getTokens() + 1);
						arc = new Arc(ArcTypeEnum.ARC, oldNode, transitionEpisodeSentence);
						arc = petriNet.addArc( arc);
					}
				}

				//Create Output Dummy Place and link to transition
				outputDummyPlace = new Node(NodeTypeEnum.PLACE.getAcronym()+countDummyPlaces, NodeTypeEnum.PLACE.getAcronym()+countDummyPlaces, ScenarioElement.EPISODE_ID.getScenarioElement().replace("<id>", episodeId), NodeTypeEnum.PLACE);
				outputDummyPlace.setDummy(true);
				currentPositionX = transitionEpisodeSentence.getPositionX();
				outputDummyPlace.setPositionX(currentPositionX);
				outputDummyPlace.setPositionY(currentPositionY + INCREMENT_Y);
				outputDummyPlace = petriNet.addNode(outputDummyPlace);
				countDummyPlaces++;

				arc = new Arc(ArcTypeEnum.ARC, transitionEpisodeSentence, outputDummyPlace);
				arc = petriNet.addArc(arc);

				//Create Output Places from Post-conditions and Link to transition
				currentPositionX = transitionEpisodeSentence.getPositionX();
				for(String postCondition : episode.getPostConditions()) {
					Node node = new Node(postCondition, postCondition, ScenarioElement.EPISODE_POST_CONDITION.getScenarioElement().replace("<id>", episodeId), NodeTypeEnum.PLACE);
					currentPositionX = currentPositionX + INCREMENT_X;
					node.setPositionX(currentPositionX);
					node.setPositionY(currentPositionY + INCREMENT_Y);
					//IF there exist a place with same name THEN Fuse Places
					Node oldNode = petriNet.findPlaceByNameWithTracePrePostCondition(node.getName());
					if(oldNode == null) {
						node = petriNet.addNode(node);

						if(petriNet.getMaxPositionX() < node.getPositionX())
							petriNet.setMaxPositionX(node.getPositionX());

						arc = new Arc(ArcTypeEnum.ARC, transitionEpisodeSentence, node);
						arc = petriNet.addArc( arc);


					} else {
						arc = new Arc(ArcTypeEnum.ARC, transitionEpisodeSentence, oldNode);
						arc = petriNet.addArc( arc);
					}
				}

				//Create Input Places and transitions from Conditional/Optional episode
				Node dummyTransition = null;
				currentPositionX = transitionEpisodeSentence.getPositionX();
				if(episode.isConditional() || episode.isOptional()) {//transitionEpisodeSentence.getLabel()+"_ELSE",
					dummyTransition = new Node("ELSE_"+transitionEpisodeSentence.getLabel(),
							scenario.getId() +LABEL_COMPONENTS_DELIMITER+ NodeTypeEnum.TRANSITION.getAcronym() + LABEL_COMPONENTS_DELIMITER + episodeId + LABEL_COMPONENTS_DELIMITER + "ELSE",
							ScenarioElement.EPISODE_SENTENCE.getScenarioElement().replace("<id>", episodeId), NodeTypeEnum.TRANSITION);

					//dummyTransition.setTimed(true);
					dummyTransition.setDummy(true);
					dummyTransition.setPositionX(currentPositionX - INCREMENT_X);
					dummyTransition.setPositionY(currentPositionY + SMALL_INCREMENT_Y);
					dummyTransition = petriNet.addNode(dummyTransition);
					//countTransitions++;

					arc = new Arc(ArcTypeEnum.ARC, inputDummyPlace, dummyTransition);
					arc = petriNet.addArc(arc);

					arc = new Arc(ArcTypeEnum.ARC, dummyTransition, outputDummyPlace);
					arc = petriNet.addArc(arc);

					//Conditions
					currentPositionX = transitionEpisodeSentence.getPositionX();
					for(String condition : episode.getConditions()) {
						if(condition != null && !condition.isEmpty()) {
							Node node = new Node(condition, condition, ScenarioElement.EPISODE_CONDITION.getScenarioElement().replace("<id>", episodeId), NodeTypeEnum.PLACE_WITH_TOKEN);
							node.setTokens(1);
							currentPositionX = currentPositionX - INCREMENT_X;
							node.setPositionX(currentPositionX);
							node.setPositionY(currentPositionY - INCREMENT_Y);
							node = petriNet.addNode( node);

							if(petriNet.getMinPositionX() > node.getPositionX())
								petriNet.setMinPositionX(node.getPositionX());


							arc = new Arc(ArcTypeEnum.ARC, node, transitionEpisodeSentence);
							arc = petriNet.addArc( arc);
							//arc = new Arc(ArcType.ARC, transitionEpisodeSentence, node);
							//petriNet = addArc( arc);
						}
					}

				}
				currentPositionY = outputDummyPlace.getPositionY();
				currentPositionY = outputDummyPlace.getPositionY();
				
				lastTransitionEpisode = transitionEpisodeSentence;

			}

			if(episode.isIterativeDoWhile() || episode.isIterativeWhileDo() || episode.isIterativeForEachDo()) {//transitionEpisodeSentence.getLabel()+"_WHILE",
				//BEGIN transition
				Node beginStructTransition = new Node("BEGIN_"+transitionEpisodeSentence.getLabel(),
						scenario.getId() +LABEL_COMPONENTS_DELIMITER+ NodeTypeEnum.TRANSITION.getAcronym() + LABEL_COMPONENTS_DELIMITER + episodeId + LABEL_COMPONENTS_DELIMITER + "BEGIN",
						ScenarioElement.EPISODE_SENTENCE.getScenarioElement().replace("<id>", episodeId), NodeTypeEnum.TRANSITION);

				//beginStructTransition.setTimed(true);
				beginStructTransition.setDummy(true);
				currentPositionX = inputDummyPlace.getPositionX();
				beginStructTransition.setPositionX(currentPositionX);
				currentPositionY = inputDummyPlace.getPositionY() + INCREMENT_Y;
				beginStructTransition.setPositionY(currentPositionY);
				
				beginStructTransition = petriNet.addNode(beginStructTransition);
				if(petriNet.getMaxPositionX() < beginStructTransition.getPositionX())
					petriNet.setMaxPositionX(beginStructTransition.getPositionX());
				//countTransitions++;



				//Create Input Places from Pre-conditions and Link to BEGIN transition
				currentPositionX = beginStructTransition.getPositionX();
				currentPositionY = beginStructTransition.getPositionY();
				for(String preCondition : episode.getPreConditions()) {
					Node node = new Node(preCondition, preCondition, ScenarioElement.EPISODE_PRE_CONDITION.getScenarioElement().replace("<id>", episodeId), NodeTypeEnum.PLACE_WITH_TOKEN);
					node.setTokens(1);
					currentPositionX = currentPositionX + INCREMENT_X;
					node.setPositionX(currentPositionX);
					node.setPositionY(currentPositionY - INCREMENT_Y);
					//IF there exist a place with same name THEN Fuse Places
					Node oldNode = petriNet.findPlaceByNameWithTracePrePostCondition(node.getName());
					if(oldNode == null) {
						node = petriNet.addNode( node);

						if(petriNet.getMaxPositionX() < node.getPositionX())
							petriNet.setMaxPositionX(node.getPositionX());

						arc = new Arc(ArcTypeEnum.ARC, node, beginStructTransition);
						arc = petriNet.addArc( arc);

					} else {
						oldNode.setTokens(oldNode.getTokens() + 1);
						arc = new Arc(ArcTypeEnum.ARC, oldNode, beginStructTransition);
						arc = petriNet.addArc( arc);
					}
				}

				//IDP to BEGIN
				arc = new Arc(ArcTypeEnum.ARC, inputDummyPlace, beginStructTransition);
				arc = petriNet.addArc(arc);

				//Create Output Place of BEGIN transition
				Node outDummyPlaceLoopBegin = new Node(NodeTypeEnum.PLACE.getAcronym()+countDummyPlaces, NodeTypeEnum.PLACE.getAcronym()+countDummyPlaces, ScenarioElement.EPISODE_ID.getScenarioElement().replace("<id>", episodeId), NodeTypeEnum.PLACE);
				outDummyPlaceLoopBegin.setDummy(true);
				outDummyPlaceLoopBegin.setPositionX(currentPositionX);
				outDummyPlaceLoopBegin.setPositionY(currentPositionY + INCREMENT_Y);

				outDummyPlaceLoopBegin = petriNet.addNode(outDummyPlaceLoopBegin);
				countDummyPlaces++;
				
				//BEGIN to ODPL
				arc = new Arc(ArcTypeEnum.ARC, beginStructTransition, outDummyPlaceLoopBegin);
				arc = petriNet.addArc(arc);

				//Create transition T
				transitionEpisodeSentence = petriNet.addNode(transitionEpisodeSentence);
				countTransitions++;
				currentPositionX = outDummyPlaceLoopBegin.getPositionX() + INCREMENT_X;
				transitionEpisodeSentence.setPositionX(currentPositionX);
				currentPositionY = outDummyPlaceLoopBegin.getPositionY() + INCREMENT_Y;
				transitionEpisodeSentence.setPositionY(currentPositionY);
				if(petriNet.getMaxPositionX() < transitionEpisodeSentence.getPositionX())
					petriNet.setMaxPositionX(transitionEpisodeSentence.getPositionX());


				//Create Input Places from Constraints and Link to transition
				currentPositionX = transitionEpisodeSentence.getPositionX();
				currentPositionY = transitionEpisodeSentence.getPositionY();
				for(String constraint : episode.getConstraints()) {
					Node node = new Node(constraint, constraint, ScenarioElement.EPISODE_CONSTRAINT.getScenarioElement().replace("<id>", episodeId), NodeTypeEnum.PLACE_WITH_TOKEN);
					node.setTokens(1);
					currentPositionX = currentPositionX + INCREMENT_X;
					node.setPositionX(currentPositionX);
					node.setPositionY(currentPositionY - INCREMENT_Y);
					node = petriNet.addNode( node);

					if(petriNet.getMaxPositionX() < node.getPositionX())
						petriNet.setMaxPositionX(node.getPositionX());

					arc = new Arc(ArcTypeEnum.ARC, node, transitionEpisodeSentence);
					arc = petriNet.addArc(arc);
					arc = new Arc(ArcTypeEnum.ARC, transitionEpisodeSentence, node);
					arc = petriNet.addArc(arc);

				}

				//Create Xput Place
				Node xDummyPlaceLoop = new Node(NodeTypeEnum.PLACE.getAcronym()+countDummyPlaces, NodeTypeEnum.PLACE.getAcronym()+countDummyPlaces, ScenarioElement.EPISODE_ID.getScenarioElement().replace("<id>", episodeId), NodeTypeEnum.PLACE);
				xDummyPlaceLoop.setDummy(true);
				xDummyPlaceLoop.setPositionX(currentPositionX - INCREMENT_X);
				xDummyPlaceLoop.setPositionY(currentPositionY + INCREMENT_Y);

				xDummyPlaceLoop = petriNet.addNode(xDummyPlaceLoop);
				countDummyPlaces++;

				//END transition
				Node endStructTransition = new Node("END_"+transitionEpisodeSentence.getLabel(),
						scenario.getId() +LABEL_COMPONENTS_DELIMITER+ NodeTypeEnum.TRANSITION.getAcronym() + LABEL_COMPONENTS_DELIMITER + episodeId + LABEL_COMPONENTS_DELIMITER + "END",
						ScenarioElement.EPISODE_SENTENCE.getScenarioElement().replace("<id>", episodeId), NodeTypeEnum.TRANSITION);

				//endStructTransition.setTimed(true);
				endStructTransition.setDummy(true);
				currentPositionX = xDummyPlaceLoop.getPositionX();
				endStructTransition.setPositionX(currentPositionX + INCREMENT_X);
				currentPositionY = xDummyPlaceLoop.getPositionY() + INCREMENT_Y;
				endStructTransition.setPositionY(currentPositionY);
				
				endStructTransition = petriNet.addNode(endStructTransition);
				if(petriNet.getMaxPositionX() < endStructTransition.getPositionX())
					petriNet.setMaxPositionX(endStructTransition.getPositionX());
				//countTransitions++;


				//Create Output Dummy Place and link to END transition
				currentPositionY = endStructTransition.getPositionY();
				outputDummyPlace = new Node(NodeTypeEnum.PLACE.getAcronym()+countDummyPlaces, NodeTypeEnum.PLACE.getAcronym()+countDummyPlaces, ScenarioElement.EPISODE_ID.getScenarioElement().replace("<id>", episodeId), NodeTypeEnum.PLACE);
				outputDummyPlace.setDummy(true);
				currentPositionX = endStructTransition.getPositionX();
				outputDummyPlace.setPositionX(currentPositionX);
				outputDummyPlace.setPositionY(currentPositionY + INCREMENT_Y);
				outputDummyPlace = petriNet.addNode(outputDummyPlace);
				countDummyPlaces++;

				arc = new Arc(ArcTypeEnum.ARC, endStructTransition, outputDummyPlace);
				arc = petriNet.addArc(arc);

				//Create Output Places from Post-conditions and Link to END transition
				currentPositionX = endStructTransition.getPositionX();
				for(String postCondition : episode.getPostConditions()) {
					Node node = new Node(postCondition, postCondition, ScenarioElement.EPISODE_POST_CONDITION.getScenarioElement().replace("<id>", episodeId), NodeTypeEnum.PLACE);
					currentPositionX = currentPositionX + INCREMENT_X;
					node.setPositionX(currentPositionX);
					node.setPositionY(currentPositionY + INCREMENT_Y);
					//IF there exist a place with same name THEN Fuse Places
					Node oldNode = petriNet.findPlaceByNameWithTracePrePostCondition(node.getName());
					if(oldNode == null) {
						node = petriNet.addNode(node);

						if(petriNet.getMaxPositionX() < node.getPositionX())
							petriNet.setMaxPositionX(node.getPositionX());

						arc = new Arc(ArcTypeEnum.ARC, endStructTransition, node);
						arc = petriNet.addArc( arc);


					} else {
						arc = new Arc(ArcTypeEnum.ARC, endStructTransition, oldNode);
						arc = petriNet.addArc( arc);
					}
				}


				Node dummyTransition = null;
				currentPositionX = transitionEpisodeSentence.getPositionX();
				//Create Input Places and transitions from DO-WHILE episode
				if(episode.isIterativeDoWhile()) {//transitionEpisodeSentence.getLabel()+"_WHILE",
					dummyTransition = new Node("WHILE_"+transitionEpisodeSentence.getLabel(),
							scenario.getId() +LABEL_COMPONENTS_DELIMITER+ NodeTypeEnum.TRANSITION.getAcronym() + LABEL_COMPONENTS_DELIMITER + episodeId + LABEL_COMPONENTS_DELIMITER + "WHILE",
							ScenarioElement.EPISODE_SENTENCE.getScenarioElement().replace("<id>", episodeId), NodeTypeEnum.TRANSITION);

					//dummyTransition.setTimed(true);
					dummyTransition.setDummy(true);
					dummyTransition.setPositionX(currentPositionX - 2*INCREMENT_X);
					dummyTransition.setPositionY(transitionEpisodeSentence.getPositionY() + SMALL_INCREMENT_Y);
					dummyTransition = petriNet.addNode(dummyTransition);
					//countTransitions++;

					arc = new Arc(ArcTypeEnum.ARC, dummyTransition, outDummyPlaceLoopBegin);
					arc = petriNet.addArc(arc);

					arc = new Arc(ArcTypeEnum.ARC, outDummyPlaceLoopBegin, transitionEpisodeSentence);
					arc = petriNet.addArc(arc);

					arc = new Arc(ArcTypeEnum.ARC, transitionEpisodeSentence, xDummyPlaceLoop);
					arc = petriNet.addArc(arc);

					arc = new Arc(ArcTypeEnum.ARC, xDummyPlaceLoop, dummyTransition);
					arc = petriNet.addArc(arc);

					arc = new Arc(ArcTypeEnum.ARC, xDummyPlaceLoop, endStructTransition);
					arc = petriNet.addArc(arc);


					//Conditions
					currentPositionX = dummyTransition.getPositionX();
					for(String condition : episode.getConditions()) {
						if(condition != null && !condition.isEmpty()) {
							Node node = new Node(condition, condition, ScenarioElement.EPISODE_CONDITION.getScenarioElement().replace("<id>", episodeId), NodeTypeEnum.PLACE_WITH_TOKEN);
							node.setTokens(1);
							currentPositionX = currentPositionX - INCREMENT_X;
							node.setPositionX(currentPositionX);
							node.setPositionY(dummyTransition.getPositionY() - INCREMENT_Y);
							node = petriNet.addNode( node);

							if(petriNet.getMinPositionX() > node.getPositionX())
								petriNet.setMinPositionX(node.getPositionX());

							arc = new Arc(ArcTypeEnum.ARC, node, dummyTransition);
							arc = petriNet.addArc( arc);
							//arc = new Arc(ArcType.ARC, dummyTransition, node);
							//petriNet = addArc( arc);
						}
					}

				}

				//Create Input Places and transitions from DO-WHILE episode
				if(episode.isIterativeWhileDo()) {//transitionEpisodeSentence.getLabel()+"_WHILE",
					dummyTransition = new Node("WHILE_"+transitionEpisodeSentence.getLabel(),
							scenario.getId() +LABEL_COMPONENTS_DELIMITER+ NodeTypeEnum.TRANSITION.getAcronym() + LABEL_COMPONENTS_DELIMITER + episodeId + LABEL_COMPONENTS_DELIMITER + "WHILE",
							ScenarioElement.EPISODE_SENTENCE.getScenarioElement().replace("<id>", episodeId), NodeTypeEnum.TRANSITION);

					//dummyTransition.setTimed(true);
					dummyTransition.setDummy(true);
					dummyTransition.setPositionX(currentPositionX - 2*INCREMENT_X);
					dummyTransition.setPositionY(transitionEpisodeSentence.getPositionY() + SMALL_INCREMENT_Y);
					dummyTransition = petriNet.addNode(dummyTransition);
					//countTransitions++;

					arc = new Arc(ArcTypeEnum.ARC, outDummyPlaceLoopBegin, dummyTransition);
					arc = petriNet.addArc(arc);

					arc = new Arc(ArcTypeEnum.ARC, dummyTransition, xDummyPlaceLoop);
					arc = petriNet.addArc(arc);

					arc = new Arc(ArcTypeEnum.ARC, xDummyPlaceLoop, transitionEpisodeSentence);
					arc = petriNet.addArc(arc);

					arc = new Arc(ArcTypeEnum.ARC, transitionEpisodeSentence, outDummyPlaceLoopBegin);
					arc = petriNet.addArc(arc);

					arc = new Arc(ArcTypeEnum.ARC, outDummyPlaceLoopBegin, endStructTransition);
					arc = petriNet.addArc(arc);

					//Conditions
					currentPositionX = dummyTransition.getPositionX();
					for(String condition : episode.getConditions()) {
						if(condition != null && !condition.isEmpty()) {
							Node node = new Node(condition, condition, ScenarioElement.EPISODE_CONDITION.getScenarioElement().replace("<id>", episodeId), NodeTypeEnum.PLACE_WITH_TOKEN);
							node.setTokens(1);
							currentPositionX = currentPositionX - INCREMENT_X;
							node.setPositionX(currentPositionX);
							node.setPositionY(dummyTransition.getPositionY() - INCREMENT_Y);
							node = petriNet.addNode( node);

							if(petriNet.getMinPositionX() > node.getPositionX())
								petriNet.setMinPositionX(node.getPositionX());

							arc = new Arc(ArcTypeEnum.ARC, node, dummyTransition);
							arc = petriNet.addArc( arc);
							//arc = new Arc(ArcType.ARC, dummyTransition, node);
							//petriNet = addArc( arc);
						}
					}

				}

				//Create Input Places and transitions from FOR-EACH episode
				if(episode.isIterativeForEachDo()) {//transitionEpisodeSentence.getLabel()+"_WHILE",
					dummyTransition = new Node("NEXT_"+transitionEpisodeSentence.getLabel(),
							scenario.getId() +LABEL_COMPONENTS_DELIMITER+ NodeTypeEnum.TRANSITION.getAcronym() + LABEL_COMPONENTS_DELIMITER + episodeId + LABEL_COMPONENTS_DELIMITER + "NEXT",
							ScenarioElement.EPISODE_SENTENCE.getScenarioElement().replace("<id>", episodeId), NodeTypeEnum.TRANSITION);

					//dummyTransition.setTimed(true);
					dummyTransition.setDummy(true);
					dummyTransition.setPositionX(currentPositionX - 2*INCREMENT_X);
					dummyTransition.setPositionY(transitionEpisodeSentence.getPositionY() + SMALL_INCREMENT_Y);
					dummyTransition = petriNet.addNode(dummyTransition);
					//countTransitions++;

					arc = new Arc(ArcTypeEnum.ARC, outDummyPlaceLoopBegin, dummyTransition);
					arc = petriNet.addArc(arc);

					arc = new Arc(ArcTypeEnum.ARC, dummyTransition, xDummyPlaceLoop);
					arc = petriNet.addArc(arc);

					arc = new Arc(ArcTypeEnum.ARC, xDummyPlaceLoop, transitionEpisodeSentence);
					arc = petriNet.addArc(arc);

					arc = new Arc(ArcTypeEnum.ARC, transitionEpisodeSentence, outDummyPlaceLoopBegin);
					arc = petriNet.addArc(arc);

					arc = new Arc(ArcTypeEnum.ARC, outDummyPlaceLoopBegin, endStructTransition);
					arc = petriNet.addArc(arc);

					//Conditions
					currentPositionX = dummyTransition.getPositionX();
					for(String condition : episode.getConditions()) {
						if(condition != null && !condition.isEmpty()) {
							Node node = new Node(condition, condition, ScenarioElement.EPISODE_CONDITION.getScenarioElement().replace("<id>", episodeId), NodeTypeEnum.PLACE_WITH_TOKEN);
							node.setTokens(1);
							currentPositionX = currentPositionX - INCREMENT_X;
							node.setPositionX(currentPositionX);
							node.setPositionY(dummyTransition.getPositionY() - INCREMENT_Y);
							node = petriNet.addNode( node);

							if(petriNet.getMinPositionX() > node.getPositionX())
								petriNet.setMinPositionX(node.getPositionX());

							arc = new Arc(ArcTypeEnum.ARC, node, dummyTransition);
							arc = petriNet.addArc( arc);
							//arc = new Arc(ArcType.ARC, dummyTransition, node);
							//petriNet = addArc( arc);
						}
					}

				}

				currentPositionX = outputDummyPlace.getPositionX();
				currentPositionY = outputDummyPlace.getPositionY();
				lastTransitionEpisode = endStructTransition;
			}

			//Link episode sub-petri-net to JOIN
			if(forkTransition != null) {
				arc = new Arc(ArcTypeEnum.ARC, outputDummyPlace , joinTransition);
				arc = petriNet.addArc( arc);

			} else {
				//Update Y position
				currentPositionY = currentPositionY + INCREMENT_Y;
				currentNode = outputDummyPlace;
			}

			//IF episode sentence ends with "#" THEN
			if(!episode.getSentence().isEmpty() && episode.getSentence().endsWith("#") && forkTransition != null) {
				//Update transition "JOIN" when find the end of a FORK-JOIN structure
				joinTransition = petriNet.findNodeByName( joinTransition.getName());
				joinTransition.setName("JOIN_"+Integer.toString(indexCurrentEpisode));
				joinTransition.setLabel(scenario.getId() +LABEL_COMPONENTS_DELIMITER+ NodeTypeEnum.TRANSITION.getAcronym() + LABEL_COMPONENTS_DELIMITER +Integer.toString(indexCurrentEpisode) + LABEL_COMPONENTS_DELIMITER +"JOIN");

				currentPositionY = currentPositionY + 2*INCREMENT_Y;
				joinTransition.setPositionY(currentPositionY);
				joinTransition = petriNet.updateNodeById( joinTransition);
				//petriNet.updateNode(joinTransition, "JOIN_"+Integer.toString(indexCurrentEpisode), "JOIN_"+Integer.toString(indexCurrentEpisode));

				//Create Output Place for denoting a JOIN
				Node joinOutputDummyPlace = new Node(NodeTypeEnum.PLACE.getAcronym()+countDummyPlaces, NodeTypeEnum.PLACE.getAcronym()+countDummyPlaces, ScenarioElement.EPISODE_SENTENCE.getScenarioElement().replace("<id>", episodeId), NodeTypeEnum.PLACE);
				joinOutputDummyPlace.setDummy(true);
				currentPositionX = joinTransition.getPositionX();
				joinOutputDummyPlace.setPositionX(currentPositionX);
				currentPositionY = currentPositionY + INCREMENT_Y;
				joinOutputDummyPlace.setPositionY(currentPositionY);
				joinOutputDummyPlace = petriNet.addNode( joinOutputDummyPlace);
				countDummyPlaces++;

				//Link Places and transitions into a Sub-Petri-Net
				arc = new Arc(ArcTypeEnum.ARC, joinTransition, joinOutputDummyPlace);
				arc = petriNet.addArc( arc);

				forkTransition = null;
				joinTransition = null;

				currentNode = joinOutputDummyPlace;

			}

			indexCurrentEpisode++;

		}

		//CREATE SUB-PETRI-NET FROM SCENARIO COMPLETION - FINAL STATE

		//Dummy Transition to enable COMPLETION - Tn
		Node dummyFinalTransition = new Node("Scenario Completion",
				scenario.getId() +LABEL_COMPONENTS_DELIMITER+ NodeTypeEnum.TRANSITION.getAcronym()+ LABEL_COMPONENTS_DELIMITER + countTransitions + LABEL_COMPONENTS_DELIMITER + "Scenario Completion",
				ScenarioElement.CONTEXT_POST_CONDITION.getScenarioElement(), NodeTypeEnum.TRANSITION);
		//dummyFinalTransition.setTimed(true);
		dummyFinalTransition.setDummy(true);
		currentPositionX = initialPositionX;
		dummyFinalTransition.setPositionX(currentPositionX);
		currentPositionY = currentPositionY + INCREMENT_Y;
		dummyFinalTransition.setPositionY(currentPositionY);
		dummyFinalTransition = petriNet.addNode( dummyFinalTransition);
		petriNet.setFinalTransition(dummyFinalTransition);
		countTransitions++;
		petriNet.setMaxPositionY(currentPositionY);

		//create Input Dummy Place and link to transition
		//Link previous output dummy place to dummy final transition Tn
		arc = new Arc(ArcTypeEnum.ARC, currentNode, dummyFinalTransition);
		arc = petriNet.addArc( arc);

		//Find last transition generated form the last Episode;
		//Create Output Places from Post-conditions and link to last transition generated from the last episode
		currentPositionY = currentPositionY + INCREMENT_Y;
		for(String postCondition : scenario.getContext().getPostConditions()) {
			Node node = new Node(postCondition, postCondition, ScenarioElement.CONTEXT_POST_CONDITION.getScenarioElement(), NodeTypeEnum.PLACE);
			currentPositionX = currentPositionX + INCREMENT_X;
			node.setPositionX(currentPositionX);
			node.setPositionY(lastTransitionEpisode.getPositionY());
			//IF there exist a place with same name THEN Fuse Places
			Node oldNode = petriNet.findPlaceByNameWithTracePrePostCondition( node.getName());
			if(oldNode == null) {
				node = petriNet.addNode( node);
				if(petriNet.getMaxPositionX() < node.getPositionX())
					petriNet.setMaxPositionX(node.getPositionX());

				arc = new Arc(ArcTypeEnum.ARC, lastTransitionEpisode, node);
				arc = petriNet.addArc( arc);
			} else {
				arc = new Arc(ArcTypeEnum.ARC, lastTransitionEpisode, oldNode);
				arc = petriNet.addArc( arc);
			}
		}


		//Release pre-conditions of the scenario
		for(String preCondition : scenario.getContext().getPreConditions()) {
			Node node = petriNet.findNodeByName( preCondition, NodeTypeEnum.PLACE_WITH_TOKEN); //FIX: Verify that is a precondition of Context
			if (node != null) {
				arc = new Arc(ArcTypeEnum.ARC, dummyFinalTransition, node);
				arc = petriNet.addArc( arc);
			}
		}

		//create Output Dummy Place and link to transition
		//Fuse last output dummy place to the first input dummy place of the petri-net
		arc = new Arc(ArcTypeEnum.ARC, dummyFinalTransition, startNode);
		arc = petriNet.addArc( arc);

		currentPositionX = initialPositionX;
		//CREATE INPUT PLACES, OUTPUT PLACES AND TRANSITIONS FROM ALTERNATIVES: ALTERNATES/EXCEPTIONS
		for(StructuredAlternative alternative : scenario.getAlternative()) {
			//Find the transition node (episode sentence) that branches this alternative  
			String alternativeId = alternative.getId() != null ? alternative.getId() : countTransitions.toString();
			String branchingEpisodeId = null;
			String branchingEpisodeSentence = null;
			if(alternative.getBranchingEpisode() != null) {
				branchingEpisodeId = alternative.getBranchingEpisode().getId() != null ? alternative.getBranchingEpisode().getId() : null;
				branchingEpisodeSentence = alternative.getBranchingEpisode().getSentence() != null ? alternative.getBranchingEpisode().getSentence().trim() : null;
			}
			branchingEpisodeSentence = branchingEpisodeSentence != null && branchingEpisodeSentence.length() > 50 ? branchingEpisodeSentence.substring(0, 50) : branchingEpisodeSentence;
			String branchingEpisodeLabel = branchingEpisodeId != null && branchingEpisodeSentence != null? scenario.getId() +LABEL_COMPONENTS_DELIMITER+ NodeTypeEnum.TRANSITION.getAcronym() +LABEL_COMPONENTS_DELIMITER + branchingEpisodeId +LABEL_COMPONENTS_DELIMITER+ branchingEpisodeSentence : null;
			Node branchingEpisodeTransition = null;
			Node branchingEpisodeOutDummyPlace = null;
			if(branchingEpisodeLabel != null) {
				branchingEpisodeTransition = petriNet.findNodeByLabel( branchingEpisodeLabel, NodeTypeEnum.TRANSITION);
				if(branchingEpisodeTransition != null) {
					//Find output dummy place of transition node (episode sentence) that branches this alternative
					List<Node> adjacentNodes = branchingEpisodeTransition.getAdjNodes();
					for(Node adjacent : adjacentNodes) {
						if(adjacent.isDummy() && adjacent.getType().equals(NodeTypeEnum.PLACE)) {
							branchingEpisodeOutDummyPlace = adjacent;
							break;
						}
					}
				}
			}

			//Find the transition node (episode sentence), which this alternative returns  

			String goToEpisodeId = null;
			String goToEpisodeSentence = null;
			if(alternative.getGoToEpisode() != null) {
				goToEpisodeId = alternative.getGoToEpisode().getId() != null ? alternative.getGoToEpisode().getId() : null;
				goToEpisodeSentence = alternative.getGoToEpisode().getSentence() != null ? alternative.getGoToEpisode().getSentence().trim() : null;
			}
			goToEpisodeSentence = goToEpisodeSentence != null && goToEpisodeSentence.length() > 50 ? goToEpisodeSentence.substring(0, 50) : goToEpisodeSentence;
			String goToEpisodeLabel = goToEpisodeId != null && goToEpisodeSentence != null? scenario.getId() +LABEL_COMPONENTS_DELIMITER+ NodeTypeEnum.TRANSITION.getAcronym() +LABEL_COMPONENTS_DELIMITER + goToEpisodeId +LABEL_COMPONENTS_DELIMITER+ goToEpisodeSentence : null;
			Node goToEpisodeTransition = null;
			Node goToEpisodeInputDummyPlace = null;
			if(goToEpisodeLabel != null) {
				goToEpisodeTransition = petriNet.findNodeByLabel(goToEpisodeLabel, NodeTypeEnum.TRANSITION);
				if(goToEpisodeTransition != null) {
					//Find input dummy place of transition node (episode sentence), which this alternative returns
					List<Node> incidentNodes = petriNet.getIncidentNodes(goToEpisodeTransition);
					for(Node incident : incidentNodes) {
						if(incident.isDummy() && incident.getType().equals(NodeTypeEnum.PLACE)) {
							goToEpisodeInputDummyPlace = incident;
							break;
						}
					}
				}
			}



			//CREATE INPUT PLACES, OUTPUT PLACES AND TRANSITIONS FROM ALTERNATIVES: SOLUTION []
			if (alternative.getSolution() != null && !alternative.getSolution().isEmpty()) {
				Node transitionFirstSolution = null;//First Transition
				Node transitionLastSolution = null;//Last Transition
				Node transitionPrevSolution = null;//Previous to Current Transition
				//Initial Positions
				if(branchingEpisodeTransition != null) {
					currentPositionX = initialPositionX + INCREMENT_X + branchingEpisodeTransition.getAdjNodes().size()*INCREMENT_X;
					currentPositionY = branchingEpisodeTransition.getPositionY() + branchingEpisodeOutDummyPlace.getAdjNodes().size()*INCREMENT_Y
							+ (branchingEpisodeOutDummyPlace.getAdjNodes().size() - 1)*INCREMENT_Y;
				} else {
					currentPositionX = initialPositionX + 2*INCREMENT_X;
					currentPositionY = currentPositionY + LARGE_INCREMENT_Y;
				}
				int indexSolution = 1;
				for(String solution : alternative.getSolution()) {
					String alternativeSolution = solution != null ? solution.trim() : "NULL";
					String alternativeSolutionLabel = alternativeSolution.length() > 50 ? alternativeSolution.substring(0, 50) : alternativeSolution;

					Node transitionSolution = new Node(alternativeSolution ,
							scenario.getId() +LABEL_COMPONENTS_DELIMITER+ NodeTypeEnum.TRANSITION.getAcronym() + LABEL_COMPONENTS_DELIMITER + alternativeId + LABEL_COMPONENTS_OTHER_DELIMITER + indexSolution + LABEL_COMPONENTS_DELIMITER + alternativeSolutionLabel,
							ScenarioElement.ALTERNATIVE_SOLUTION.getScenarioElement().replace("<id>", alternativeId), NodeTypeEnum.TRANSITION);
					//POSITIONS
					transitionSolution.setPositionX(currentPositionX);
					transitionSolution.setPositionY(currentPositionY);
					transitionSolution.setOrientation(ORIENTATION_0);
					//Update Global Positions
					if(branchingEpisodeTransition != null) {
						if(petriNet.getMaxPositionX() < transitionSolution.getPositionX())
							petriNet.setMaxPositionX(transitionSolution.getPositionX());
						//update Y position of nodes after current transition-first-solution
						if(indexSolution == 1) {
							if(branchingEpisodeOutDummyPlace.getAdjNodes().size() >= 2) {
								petriNet = updateNodesPositionY(petriNet, transitionSolution.getPositionY() - INCREMENT_Y, 2*INCREMENT_Y);
								//Update Max Y Position?
							}
						}
					} else {
						petriNet.setMaxPositionY(currentPositionY);
					}
					transitionSolution = petriNet.addNode( transitionSolution);
					countTransitions++;
					//New Positions
					currentPositionX = currentPositionX + 2*INCREMENT_X;

					//Create a transition-solution-first from alternative first-solution
					if(indexSolution == 1) {
						//Update pointers
						transitionFirstSolution = transitionSolution;
						transitionPrevSolution = transitionSolution;
						if(alternative.getSolution().size() == 1)
							transitionLastSolution = transitionSolution;
					} else {//Create a transition-solution from alternative next-solution
						//Link current transition to previous transition (previous -> place -> current)
						//Create Input dummy place
						Node inputDummyPlace = new Node(NodeTypeEnum.PLACE.getAcronym()+countDummyPlaces, NodeTypeEnum.PLACE.getAcronym()+countDummyPlaces, ScenarioElement.ALTERNATIVE_ID.getScenarioElement().replace("<id>", alternativeId), NodeTypeEnum.PLACE);
						inputDummyPlace.setPositionX(transitionSolution.getPositionX() - INCREMENT_X);
						inputDummyPlace.setPositionY(transitionSolution.getPositionY());
						inputDummyPlace.setDummy(true);
						inputDummyPlace = petriNet.addNode( inputDummyPlace);
						countDummyPlaces++;

						arc = new Arc(ArcTypeEnum.ARC, transitionPrevSolution, inputDummyPlace);
						arc = petriNet.addArc( arc);

						arc = new Arc(ArcTypeEnum.ARC, inputDummyPlace, transitionSolution);
						arc = petriNet.addArc( arc);

						//Update pointers
						transitionLastSolution = transitionSolution;
						transitionPrevSolution = transitionSolution;
					}

					indexSolution++;
				}

				//Create Input Places from Causes and Link to transition-solution-first
				currentPositionX = transitionFirstSolution.getPositionX();
				if(alternative.getCauses() != null && !alternative.getCauses().isEmpty()) {
					for(String cause : alternative.getCauses()) {
						if(cause != null && !cause.isEmpty()) {
							Node node = new Node(cause, cause, ScenarioElement.ALTERNATIVE_CAUSE.getScenarioElement().replace("<id>", alternativeId), NodeTypeEnum.PLACE_WITH_TOKEN);
							node.setPositionX(currentPositionX);
							node.setPositionY(transitionFirstSolution.getPositionY() - INCREMENT_Y);
							node.setTokens(1);
							node = petriNet.addNode( node);
							if(petriNet.getMaxPositionX() < node.getPositionX())
								petriNet.setMaxPositionX(node.getPositionX());
							currentPositionX = node.getPositionX() + INCREMENT_X;
	
							arc = new Arc(ArcTypeEnum.ARC, node, transitionFirstSolution);
							arc = petriNet.addArc( arc);
							/*
								arc = new Arc(ArcType.ARC, transitionSolution, node);
								petriNet = addArc( arc);
							 */
						}
					}
				} else { //input dummy place of T
					//Create Input dummy place
					Node inputDummyPlace = new Node(NodeTypeEnum.PLACE.getAcronym()+countDummyPlaces, NodeTypeEnum.PLACE.getAcronym()+countDummyPlaces, ScenarioElement.ALTERNATIVE_ID.getScenarioElement().replace("<id>", alternativeId), NodeTypeEnum.PLACE);
					inputDummyPlace.setPositionX(transitionFirstSolution.getPositionX());
					inputDummyPlace.setPositionY(transitionFirstSolution.getPositionY() - INCREMENT_Y);
					inputDummyPlace.setDummy(true);
					inputDummyPlace = petriNet.addNode( inputDummyPlace);
					countDummyPlaces++;

					arc = new Arc(ArcTypeEnum.ARC, inputDummyPlace, transitionFirstSolution);
					arc = petriNet.addArc( arc);
				}
				//Create Output Places from Post-conditions and Link to transition-solution-last
				currentPositionX = transitionLastSolution.getPositionX();
				for(String postCondition : alternative.getPostConditions()) {
					Node node = new Node(postCondition, postCondition, ScenarioElement.ALTERNATIVE_POST_CONDITION.getScenarioElement().replace("<id>", alternativeId), NodeTypeEnum.PLACE);
					currentPositionX = currentPositionX + INCREMENT_X;
					node.setPositionX(currentPositionX);
					node.setPositionY(transitionLastSolution.getPositionY());
					//IF there exist a place with same name THEN Fuse Places
					Node oldNode = petriNet.findPlaceByNameWithTracePrePostCondition( node.getName());
					if(oldNode == null) {
						node = petriNet.addNode( node);
						if(petriNet.getMaxPositionX() < node.getPositionX())
							petriNet.setMaxPositionX(node.getPositionX());

						arc = new Arc(ArcTypeEnum.ARC, transitionLastSolution, node);
						arc = petriNet.addArc( arc);
					} else {
						arc = new Arc(ArcTypeEnum.ARC, transitionLastSolution, oldNode);
						arc = petriNet.addArc( arc);
					}
				}

				//create Input Dummy Place and link to transition-solution-first
				if(branchingEpisodeTransition != null) {

					//Link output dummy place of branching episode to input dummy place of alternative
					arc = new Arc(ArcTypeEnum.ARC, branchingEpisodeOutDummyPlace, transitionFirstSolution);
					arc = petriNet.addArc( arc);

				} else {
					//Create Input dummy place
					Node inputDummyPlace = new Node(NodeTypeEnum.PLACE.getAcronym()+countDummyPlaces, NodeTypeEnum.PLACE.getAcronym()+countDummyPlaces, ScenarioElement.ALTERNATIVE_ID.getScenarioElement().replace("<id>", alternativeId), NodeTypeEnum.PLACE);
					inputDummyPlace.setPositionX(transitionFirstSolution.getPositionX() - INCREMENT_X);
					inputDummyPlace.setPositionY(transitionFirstSolution.getPositionY());
					inputDummyPlace.setDummy(true);
					inputDummyPlace = petriNet.addNode( inputDummyPlace);
					countDummyPlaces++;

					arc = new Arc(ArcTypeEnum.ARC, inputDummyPlace, transitionFirstSolution);
					arc = petriNet.addArc( arc);
				}

				//create Output Dummy Place and link to transition-solution-last
				currentPositionX = transitionLastSolution.getPositionX();
				if(alternative.getPostConditions().isEmpty() && goToEpisodeTransition == null) {
					Node outputDummyPlace = new Node(NodeTypeEnum.PLACE.getAcronym()+countDummyPlaces, NodeTypeEnum.PLACE.getAcronym()+countDummyPlaces, ScenarioElement.ALTERNATIVE_ID.getScenarioElement().replace("<id>", alternativeId), NodeTypeEnum.PLACE);
					outputDummyPlace.setDummy(true);
					outputDummyPlace.setPositionX(currentPositionX + INCREMENT_X);
					outputDummyPlace.setPositionY(transitionLastSolution.getPositionY());
					outputDummyPlace = petriNet.addNode( outputDummyPlace);
					if(petriNet.getMaxPositionX() < outputDummyPlace.getPositionX())
						petriNet.setMaxPositionX(outputDummyPlace.getPositionX());
					countDummyPlaces++;

					arc = new Arc(ArcTypeEnum.ARC, transitionLastSolution, outputDummyPlace);
					arc = petriNet.addArc( arc);
				}

				//Link transition-solution-last to GO_TO Input Dummy Place of episode
				if(goToEpisodeTransition != null) {

					//Link transition-solution-last to input dummy place of GO_TO episode
					arc = new Arc(ArcTypeEnum.ARC, transitionLastSolution, goToEpisodeInputDummyPlace);
					arc = petriNet.addArc( arc);

				}
				
				//Link transition-solution-last to LAST Input Dummy Place of FINAL transition
				if(alternative.isScenarioFinish()) {

					//Link transition-solution-last to input dummy place of FINAL transition
					arc = new Arc(ArcTypeEnum.ARC, transitionLastSolution, currentNode);
					arc = petriNet.addArc( arc);

				}

			}
		}

		return petriNet;
	}


	public PetriNet integratePetriNetsFromMainScenario(StructuredScenario mainScenario, HashMap<String, List<StructuredScenario>> sequentiallyRelatedScenariosHashMap, HashMap<String, List<StructuredScenario>> nonSeqRelatedScenarioHashMap) {

		//Derive a Petri-Net IPN from the main scenario S;
		PetriNet mainPetriNet = transformScenario(mainScenario, 300, INCREMENT_Y);
		/*
		//Identify sequentially related scenarios from the main scenario S;
		List<Scenario> scenarios = scenarioService.findScenariosByProjectId(mainScenario.getProjectId());
		List<StructuredScenario> structuredScenarios = new ArrayList<StructuredScenario>();
		if(scenarios != null && !scenarios.isEmpty()) {
		for(Scenario relatedScenario : scenarios)
		structuredScenarios.add(scenarioService.convertToStructuredScenario(relatedScenario));
		}
		HashMap<String, List<StructuredScenario>> sequentiallyRelatedScenariosHashMap = scenarioService.findSequentiallyRelatedScenarios(mainScenario, structuredScenarios);
		//Identify non-explicit (*and explicit) non-sequentially related scenarios from the Main Scenario S;
		HashMap<String, List<StructuredScenario>> nonSeqRelatedScenarioHashMap =  scenarioService.findNonSequentiallyRelatedScenarios(mainScenario, structuredScenarios, sequentiallyRelatedScenariosHashMap); //Non-Sequentially related scenarios by type of relationship (EXPLICIT and NON_EXPLICIT)
				 */
				/*
		3. For each sequentially related scenario:
		4. Derive a Petri-Net PN from related scenario;
		5. IF PN represents a Sub-scenario inside a non-sequential constructs (#<episodes series>#) in Main Scenario THEN
		6. Remove input arcs of the first input dummy place (Start) of PN;
		7.  Remove tokens of the first input place (Start) of PN;
		8.  Substitute the corresponding "Transition" of the IPN (Definition 10);
		 */
		if(nonSeqRelatedScenarioHashMap != null) {
			List<StructuredScenario> explicitNonSeqScenarios = nonSeqRelatedScenarioHashMap.get("EXPLICIT");
			if(explicitNonSeqScenarios != null) {
				//Sort scenario by the number of episodes - DESC
				List<StructuredScenario> sortedExplicitNonSeqScenarios = ScenarioManipulation.sortScenariosByEpisodes(explicitNonSeqScenarios, 0, explicitNonSeqScenarios.size() -1 );
				int indexScenario = 0;
				for(StructuredScenario scenario : sortedExplicitNonSeqScenarios) {
					//Transition (episode) between a non-sequential group (parallel or concurrent)
					Node transition = mainPetriNet.findtransitionByNameWithTraceNonSequential(scenario.getTitle().toUpperCase().trim());
					if(transition != null) {
						PetriNet petriNet = transformScenario(scenario, transition.getPositionX(), transition.getPositionY() - INCREMENT_Y);
						Arc arc = petriNet.removeArcBetweenNodes(petriNet.getFinalTransition(), petriNet.getStartPlace());
						petriNet.getStartPlace().setTokens(0);
						petriNet.getStartPlace().setType(NodeTypeEnum.PLACE);;

						//update positions of main petri-net
						if(indexScenario == 0) { //the first petriNet - scenario has the maximum position "Y"
							int numberOfNonSequentialtransitions = 1;
							ListIterator<Node> nodeIterator = mainPetriNet.getNodes().listIterator();
							while(nodeIterator.hasNext()){
								Node node = nodeIterator.next();
								//update Y position of nodes after the FORK-JOIN structure
								if(!node.getTrace().contains("Non-sequential") && node.getPositionY() > petriNet.getMinPositionY() ) {
									node.setPositionY(petriNet.getMaxPositionY() - petriNet.getMinPositionY() + node.getPositionY() );
									mainPetriNet.setMaxPositionY(node.getPositionY());
								}
								//update X position of nodes (corresponding to the other concurrent transitions-episodes) between the FORK-JOIN struture
								if(node.getType().equals(NodeTypeEnum.TRANSITION) && node.getTrace().contains("Non-sequential")
										&& !node.equals(transition)
										&& (node.getPositionY() > petriNet.getMinPositionY() && node.getPositionY() < petriNet.getMaxPositionY())) { //Concurrent/Parallel Transition to current Transition
									node.setPositionX(numberOfNonSequentialtransitions*petriNet.getMaxPositionX() + INCREMENT_X + node.getPositionX());
									if(mainPetriNet.getMaxPositionX() < node.getPositionX())
										mainPetriNet.setMaxPositionX(node.getPositionX());
									numberOfNonSequentialtransitions++;
								}
								//update X position of input places (dummy, condition, pre-condition, constraint) of concurrent transition
								if(node.getAdjNodes().contains(transition)) {
									node.setPositionX(petriNet.getMaxPositionX() + LARGE_INCREMENT_X + node.getPositionX());
									if(mainPetriNet.getMaxPositionX() < node.getPositionX())
										mainPetriNet.setMaxPositionX(node.getPositionX());
								}
								//update X position of output places (dummy, post-condition, alternative) of concurrent transition
								//TBD

								nodeIterator.set(node); 
							}
						}

						//substitute petriNet in mainPetriNet
						mainPetriNet = substituteTransition(mainPetriNet, transition, petriNet, scenario.getId());


					}
					indexScenario++;
				}
			}
			/*
14. For each non-explicit non-sequentially related scenario:
15. Derive a Petri-Net PN from related scenario;
16. Fuse the common places between the PN  and IPN(Definition 13);
			 */
			List<StructuredScenario> nonExplicitNonSeqScenarios = nonSeqRelatedScenarioHashMap.get("NON_EXPLICIT");
			if(nonExplicitNonSeqScenarios != null) {
				for(StructuredScenario scenario : nonExplicitNonSeqScenarios) {
					PetriNet petriNet = transformScenario(scenario, mainPetriNet.getMaxPositionX() + LARGE_INCREMENT_X, INCREMENT_Y);
					//Update id and groupName of Nodes in replacing Petri-Net
					ListIterator<Node> nodeIterator = petriNet.getNodes().listIterator();
					while(nodeIterator.hasNext()){
						Node node = nodeIterator.next();
						node.setId(Long.toString(scenario.getId().longValue())+"."+node.getId());
						node.setGroupName(petriNet.getName().toUpperCase());
						nodeIterator.set(node); 
					}
					mainPetriNet = fusePetriNets(mainPetriNet, petriNet);
					//Update max/min X and Y
					if(mainPetriNet.getMaxPositionX() < petriNet.getMaxPositionX())
						mainPetriNet.setMaxPositionX(petriNet.getMaxPositionX());
					if(mainPetriNet.getMaxPositionY() < petriNet.getMaxPositionY())
						mainPetriNet.setMaxPositionY(petriNet.getMaxPositionY());
				}
			}
		}

		return mainPetriNet;
	}

	/**
	 * A petriNet is fused to a mainPetriNet by adding the new nodes and arcs and fusing the common places (generated from Pre-Condition or Post-Condition) among them (Definition 9);
	 * @param mainPetriNet
	 * @param petriNet
	 * @return
	 */
	private PetriNet fusePetriNets(PetriNet mainPetriNet, PetriNet petriNet) {
		//add new nodes to mainPetriNet
		//Get places to be fused (from Pre-condition or Post-condition)
		HashMap<Node, Node> fuseNodes = new HashMap<Node, Node>(); //Fuse newNode with oldNode (mainPetriNet)
		for(Node node : petriNet.getNodes()) {
			//IF there exist a place with same name in mainPetriNet THEN Fuse Places ELSE Add place
			Node oldNode = mainPetriNet.findPlaceByNameWithTracePrePostCondition(node.getName());//(mainPetriNet)
			if(oldNode == null) {
				node = mainPetriNet.addNode(node); 
			} else {
				//Fuse
				oldNode.setGroupName("");
				fuseNodes.put(node, oldNode);
			}
		}

		//add new arcs to mainPetriNet
		for(Arc arc : petriNet.getArcs()) {
			if (!fuseNodes.isEmpty()) {
				//FUSE NODES
				if(fuseNodes.containsKey(arc.getSource())) {
					Node oldNode = fuseNodes.get(arc.getSource());
					//Link oldNode (mainPetriNet) to adjacent nodes
					Arc newArc = new Arc(ArcTypeEnum.ARC, oldNode, arc.getTarget());
					newArc = mainPetriNet.addArc(newArc); 
				} else if (fuseNodes.containsKey(arc.getTarget())) {
					Node oldNode = fuseNodes.get(arc.getTarget());
					//Link incident nodes to oldNode (mainPetriNet)
					Arc newArc = new Arc(ArcTypeEnum.ARC, arc.getSource(), oldNode);
					newArc = mainPetriNet.addArc(newArc);
				} else {
					Arc newArc = new Arc(ArcTypeEnum.ARC, arc.getSource(), arc.getTarget());
					newArc = mainPetriNet.addArc(newArc);
				}

			} else {
				Arc newArc = new Arc(ArcTypeEnum.ARC, arc.getSource(), arc.getTarget());
				newArc = mainPetriNet.addArc(newArc);
			}
		}

		return mainPetriNet;
	}

	/**
	 * Any transition can be replaced by a Petri-Net by
	 * fusing the input dummy place of the transition with the first input place (Start) of the Petri-Net and
	 * linking the last dummy transition (Final) of the Petri-Net to the output dummy place of the transition.
	 * @param mainPetriNet
	 * @param transition to replace in mainPetriNet
	 * @param petriNet that replace the transition
	 * @param scenarioId  that replace the transition
	 * @return
	 */
	private PetriNet substituteTransition(PetriNet mainPetriNet, Node transition, PetriNet petriNet, Long scenarioId ) {
		//Get input dummy place of the transition
		List<Node> incidentNodes = mainPetriNet.getIncidentNodes(transition);
		Node inputDummyPlace = null;
		for(Node incident : incidentNodes) {
			if(incident.isDummy() && incident.getType().equals(NodeTypeEnum.PLACE)) {
				inputDummyPlace = incident;
				break;
			}
		}
		//Get output dummy place of the transition
		List<Node> adjacentNodes = transition.getAdjNodes();
		Node outputDummyPlace = null;
		for(Node adjacent : adjacentNodes) {
			if(adjacent.isDummy() && adjacent.getType().equals(NodeTypeEnum.PLACE)) {
				outputDummyPlace = adjacent;
				break;
			}
		}

		if( inputDummyPlace != null && outputDummyPlace != null) { 

			//Update id, label and groupName of Nodes in replacing Petri-Net
			ListIterator<Node> nodeIterator = petriNet.getNodes().listIterator();
			while(nodeIterator.hasNext()){
				Node node = nodeIterator.next();
				node.setId(Long.toString(scenarioId.longValue())+"."+node.getId());
				node.setGroupName(petriNet.getName().toUpperCase());
				nodeIterator.set(node);

			}

			//Update id and value of Arcs in replacing Petri-Net
			ListIterator<Arc> arcIterator = petriNet.getArcs().listIterator();

			/*
        fuse the input dummy place of the transition with the first input place (Start) of the Petri-Net
        Link the last dummy transition (Final) of the Petri-Net to the output dummy place of the transition.
			 */
			Node startPlace = petriNet.getStartPlace();

			Node finalTransition = petriNet.getFinalTransition();

			//LINK SUB-SCENARIO TO PREVIOUS-PARENT TRANSITION (FORK) AND NEXT-CHILDREN PLACE (OUTPUT DUMMY PLACE) OF CURRENT TRANSITION
			Node parentTranstion = null;
			incidentNodes = mainPetriNet.getIncidentNodes(inputDummyPlace);
			for(Node incident : incidentNodes) {
				if(incident.getType().equals(NodeTypeEnum.TRANSITION)) {
					parentTranstion = incident;
					break;
				}
			}

			if(parentTranstion != null && outputDummyPlace != null) {
				//remove input dummy place of transition (main)
				mainPetriNet.removeNode(inputDummyPlace);

				//Remove transition
				mainPetriNet.removeNode(transition);

				//add new nodes and new arcs to mainPetriNet
				mainPetriNet = fusePetriNets(mainPetriNet, petriNet);

				//Link start place of petriNet to PREVIOUS-PARENT TRANSITION (FORK)
				Arc mainArc = new Arc(ArcTypeEnum.ARC, parentTranstion, startPlace);
				mainArc = mainPetriNet.addArc(mainArc);

				//Link final transition of petriNet to NEXT-CHILDREN PLACE (OUTPUT DUMMY PLACE)
				mainArc = new Arc(ArcTypeEnum.ARC, finalTransition, outputDummyPlace);
				mainArc = mainPetriNet.addArc(mainArc);
			}
		}


		return mainPetriNet;
	}

	public void depthFirstSearchRecursiveAllPaths(PetriNet graph, LinkedList<Node> visitedNodes, Node START, Node END) {

		if( visitedNodes == null) {
			visitedNodes = new LinkedList<>();
			visitedNodes.add(START);
		}
		LinkedList<Node> nodes = getLinkedList(visitedNodes.getLast().getAdjNodes());
		// examine adjacent nodes
		for (Node node : nodes) {
			if (visitedNodes.contains(node)) {
				if(node.equals(END)){
					visitedNodes.add(node);
					printPath(visitedNodes);
					visitedNodes.removeLast();
					break;
				}
				else{
					LinkedList<Node> lstCycle = new LinkedList<>();
					LinkedList<Node> lstTemp = new LinkedList<>();
					LinkedList<Node> lstCopy = new LinkedList<>();
					lstTemp.addAll(visitedNodes);
					lstCopy.addAll(visitedNodes);
					lstTemp.add(node);
					int lastIndex = visitedNodes.lastIndexOf(node);
					while(lstCopy.size()>(lastIndex+1)){
						lstCopy.removeLast();
					}
					int i = lastIndex;
					while(i < lstTemp.size()){
						lstCycle.add(lstTemp.get(i));
						i++;
					}
					boolean iguales = containList(lstCopy, lstCycle);
					if(!iguales){
						visitedNodes.addLast(node);
						depthFirstSearchRecursiveAllPaths(graph, visitedNodes, START, END);
						visitedNodes.removeLast();
					}
				}
				continue;
			}

			if (node.equals(END)) {
				visitedNodes.add(node);
				printPath(visitedNodes);
				visitedNodes.removeLast();
				break;
			}
			/*
        if (visitedNodes.contains(node)) {
                if(!node.equals(START))
                    continue;
            }
            if (node.equals(END)) {
                visitedNodes.add(node);
                printPath(visitedNodes);
                visitedNodes.removeLast();
                break;
            }
			 */
		}
		for (Node node : nodes) {
			if (visitedNodes.contains(node) || node.equals(END)) {
				continue;
			}
			visitedNodes.addLast(node);
			depthFirstSearchRecursiveAllPaths(graph, visitedNodes, START, END);
			visitedNodes.removeLast();
		}

	}


	private boolean containList(LinkedList<Node> lstTemp1,LinkedList<Node> lstTemp2){
		Node first = lstTemp2.getFirst();
		boolean val = false;
		for(int i = 0;i<lstTemp1.size();i++){
			if(lstTemp1.get(i).getId().equals(first.getId())){
				if(i+lstTemp2.size()<=lstTemp1.size()){
					int x = i+(lstTemp2.size());
					LinkedList<Node> lstAux = new LinkedList<>();
					lstAux.addAll(lstTemp1.subList(i, x));
					val = equalList(lstAux, lstTemp2);
					if(val)
						break;
				}
			}
		}
		return val;
	}

	private boolean equalList(LinkedList<Node> lstTemp1,LinkedList<Node> lstTemp2){
		boolean equal = true;
		for(int i = 0;i<lstTemp1.size();i++){
			if(!lstTemp1.get(i).getId().equals(lstTemp2.get(i).getId())){
				equal = false;
				break;
			}
		}
		return equal;
	}
	private LinkedList<Node> getLinkedList(List<Node> adjNodes) {
		LinkedList<Node> lstLinkedNode = new LinkedList<>();
		for(Node node : adjNodes)
			lstLinkedNode.add(node);
		return lstLinkedNode;
	}

	public void printPath(LinkedList<Node> visited) {
		for (Node node : visited) {
			System.out.print(node.getLabel());
			System.out.print(" ");
		}
		System.out.println();
	}


	@Override
	public StreamSource createPNMLStreamSource(PetriNet petriNet) {
		String strPetriNet = createPNML(petriNet);
		return new StreamSource(new StringReader(strPetriNet));
	}

	public String createPNML(PetriNet petriNet) {
		String title = petriNet.getName().trim().replace(" ","");
		String filePath = title+".pnml";
		try {
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
			Document document = documentBuilder.newDocument();
			// root element
			Element root = document.createElement("pnml");
			document.appendChild(root);
			// employee element
			Element net = document.createElement("net");
			root.appendChild(net);

			// set an attribute to staff element
			Attr attr1 = document.createAttribute("id");
			attr1.setValue("Net-One");
			net.setAttributeNode(attr1);
			Attr attr2 = document.createAttribute("type");
			attr2.setValue("P/T net");
			net.setAttributeNode(attr2);

			Element token = document.createElement("token");
			net.appendChild(token);
			Attr tokenAtr1 = document.createAttribute("id");
			tokenAtr1.setValue("Default");
			token.setAttributeNode(tokenAtr1);
			Attr tokenAtr2 = document.createAttribute("enabled");
			tokenAtr2.setValue("true");
			token.setAttributeNode(tokenAtr2);
			Attr tokenAtr3 = document.createAttribute("red");
			tokenAtr3.setValue("0");
			token.setAttributeNode(tokenAtr3);
			Attr tokenAtr4 = document.createAttribute("green");
			tokenAtr4.setValue("0");
			token.setAttributeNode(tokenAtr4);
			Attr tokenAtr5 = document.createAttribute("blue");
			tokenAtr5.setValue("0");
			token.setAttributeNode(tokenAtr5);
			net.appendChild(token);

			for(Node node: petriNet.getNodes()) {
				if(node.getType().equals(NodeTypeEnum.PLACE_WITH_TOKEN) || node.getType().equals(NodeTypeEnum.PLACE)) {//PLACE
					createXmlElementFromNodePlace(net, document, node);
					//guardar nodo
				}
				else {//TRANSITIONS
					createXmlElementFromNodeTransition(net, document, node);
				}
			}
			createXmlElemenstFromArcs(net, document, petriNet);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource domSource = new DOMSource(document);
			//StreamResult streamResult = new StreamResult(new File(filePath));
			StringWriter writer = new StringWriter();
			StreamResult streamResult = new StreamResult(writer);
			transformer.transform(domSource, streamResult);

			return writer.toString();


		} catch (ParserConfigurationException | TransformerException pce) {
		}
		return "";
	}

	private void createXmlElementFromNodePlace (Element net,Document document, Node node ) {
		Element place = document.createElement("place");
		Attr placeAtr1 = document.createAttribute("id");
		placeAtr1.setValue(node.getId());
		place.setAttributeNode(placeAtr1);
		node.setVisited(true);
		net.appendChild(place);

		Element name = document.createElement("name");
		Element valueName = document.createElement("value");
		Element graphicsName = document.createElement("graphics");
		valueName.appendChild(document.createTextNode(node.getLabel()));
		name.appendChild(valueName);
		name.appendChild(graphicsName);
		place.appendChild(name);

		Element initialMarking = document.createElement("initialMarking");
		Element valueIniMar = document.createElement("value");
		Element graphicsIniMar = document.createElement("graphics");
		valueIniMar.appendChild(document.createTextNode(node.getTokens().toString()));
		initialMarking.appendChild(valueIniMar);
		initialMarking.appendChild(graphicsIniMar);
		place.appendChild(initialMarking);

		Element graphics = document.createElement("graphics");
		Element graphicsPosition = document.createElement("position");
		Attr graphicsAtr1 = document.createAttribute("x");
		Attr graphicsAtr2 = document.createAttribute("y");
		graphicsAtr1.setValue(node.getPositionX().toString());
		graphicsAtr2.setValue(node.getPositionY().toString());
		graphicsPosition.setAttributeNode(graphicsAtr1);
		graphicsPosition.setAttributeNode(graphicsAtr2);
		graphics.appendChild(graphicsPosition);
		place.appendChild(graphics);
	}

	private void createXmlElementFromNodeTransition(Element net,Document document, Node node) {
		Element transition = document.createElement("transition");
		Attr transitionAtr1 = document.createAttribute("id");
		transitionAtr1.setValue(node.getId());
		transition.setAttributeNode(transitionAtr1);
		node.setVisited(true);
		net.appendChild(transition);

		Element graphics = document.createElement("graphics");
		Element graphicsPosition = document.createElement("position");
		Attr graphicsAtr1 = document.createAttribute("x");
		graphicsAtr1.setValue(node.getPositionX().toString());
		Attr graphicsAtr2 = document.createAttribute("y");
		graphicsAtr2.setValue(node.getPositionY().toString());
		graphicsPosition.setAttributeNode(graphicsAtr1);
		graphicsPosition.setAttributeNode(graphicsAtr2);
		graphics.appendChild(graphicsPosition);
		transition.appendChild(graphics);

		Element name = document.createElement("name");
		Element valueName = document.createElement("value");
		Element graphicsName = document.createElement("graphics");
		valueName.appendChild(document.createTextNode(node.getLabel()));
		name.appendChild(valueName);
		name.appendChild(graphicsName);
		transition.appendChild(name);

		Element orientation = document.createElement("orientation");
		Element valueOrientation = document.createElement("value");
		valueOrientation.appendChild(document.createTextNode(node.getOrientation().toString()));
		orientation.appendChild(valueOrientation);
		transition.appendChild(orientation);

		Element rate = document.createElement("rate");
		Element valueRate = document.createElement("value");
		valueRate.appendChild(document.createTextNode("11"));
		rate.appendChild(valueRate);
		transition.appendChild(rate);

		Element timed = document.createElement("timed");
		Element valueTimed = document.createElement("value");
		valueTimed.appendChild(document.createTextNode(node.isTimed()+""));
		timed.appendChild(valueTimed);
		transition.appendChild(timed);
	}

	private void createXmlElemenstFromArcs(Element net,Document document,PetriNet petriNet ) {
		for(Arc arc : petriNet.getArcs()) {
			Element arcElement = document.createElement("arc");
			Attr arcAtr1 = document.createAttribute("id");
			Attr arcAtr2 = document.createAttribute("source");
			Attr arcAtr3 = document.createAttribute("target");
			arcAtr1.setValue(arc.getValue());
			arcAtr2.setValue(arc.getSource().getId());
			arcAtr3.setValue(arc.getTarget().getId());
			arcElement.setAttributeNode(arcAtr1);
			arcElement.setAttributeNode(arcAtr2);
			arcElement.setAttributeNode(arcAtr3);
			net.appendChild(arcElement);

			Element graphics = document.createElement("graphics");
			arcElement.appendChild(graphics);

			Element inscription = document.createElement("inscription");
			Element valueInscription = document.createElement("value");
			Element graphicsInscription = document.createElement("graphics");
			valueInscription.appendChild(document.createTextNode("1"));
			inscription.appendChild(valueInscription);
			inscription.appendChild(graphicsInscription);
			arcElement.appendChild(inscription);

			Element arcpath1 = document.createElement("arcpath");
			Attr arcpath1Atr1 = document.createAttribute("id");
			Attr arcpath1Atr2 = document.createAttribute("x");
			Attr arcpath1Atr3 = document.createAttribute("y");
			Attr arcpath1Atr4 = document.createAttribute("curvePoint");
			arcpath1Atr1.setValue("000");
			arcpath1Atr2.setValue("001");
			arcpath1Atr3.setValue("002");
			arcpath1Atr4.setValue("false");
			arcpath1.setAttributeNode(arcpath1Atr1);
			arcpath1.setAttributeNode(arcpath1Atr2);
			arcpath1.setAttributeNode(arcpath1Atr3);
			arcpath1.setAttributeNode(arcpath1Atr4);
			arcElement.appendChild(arcpath1);

			Element arcpath2 = document.createElement("arcpath");
			Attr arcpath2Atr1 = document.createAttribute("id");
			Attr arcpath2Atr2 = document.createAttribute("x");
			Attr arcpath2Atr3 = document.createAttribute("y");
			Attr arcpath2Atr4 = document.createAttribute("curvePoint");
			arcpath2Atr1.setValue("001");
			arcpath2Atr2.setValue("101");
			arcpath2Atr3.setValue("102");
			arcpath2Atr4.setValue("false");
			arcpath2.setAttributeNode(arcpath2Atr1);
			arcpath2.setAttributeNode(arcpath2Atr2);
			arcpath2.setAttributeNode(arcpath2Atr3);
			arcpath2.setAttributeNode(arcpath2Atr4);
			arcElement.appendChild(arcpath2);
		}
	}


	/**
	 * Update nodes from this position "Y":
	 * y = y + increment
	 * @param petriNet
	 * @param fromPositionY
	 * @param increment
	 * @return
	 */
	private PetriNet updateNodesPositionY(PetriNet petriNet, int fromPositionY, int increment) {
		//update positions of main petri-net
		if(fromPositionY > 0) { //Update nodes from this position "Y"
			ListIterator<Node> nodeIterator = petriNet.getNodes().listIterator();
			while(nodeIterator.hasNext()){
				Node node = nodeIterator.next();
				//update Y position of nodes after fromPositionY
				if(node.getPositionY() >= fromPositionY) {
					node.setPositionY(node.getPositionY() + increment);
					nodeIterator.set(node); 
				}
			}
		}
		return petriNet;
	}

	/**
	 * Update nodes from this position "X":
	 * x = x + increment
	 * @param petriNet
	 * @param fromPositionX
	 * @param increment
	 * @return
	 */
	private PetriNet updateNodesPositionX(PetriNet petriNet, int fromPositionX, int increment) {
		//update positions of main petri-net
		if(fromPositionX > 0) { //Update nodes from this position "X"
			ListIterator<Node> nodeIterator = petriNet.getNodes().listIterator();
			while(nodeIterator.hasNext()){
				Node node = nodeIterator.next();
				//update X position of nodes after fromPositionX
				if(node.getPositionX() >= fromPositionX) {
					node.setPositionX(node.getPositionX() + increment);
					nodeIterator.set(node);
				}
			}
		}
		return petriNet;
	}
	
}
