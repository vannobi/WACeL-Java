package pe.edu.unsa.daisi.lis.cel.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.StringUtils;

import pe.edu.unsa.daisi.lis.cel.domain.model.petrinet.Arc;
import pe.edu.unsa.daisi.lis.cel.domain.model.petrinet.Node;
import pe.edu.unsa.daisi.lis.cel.domain.model.petrinet.NodeTypeEnum;
import pe.edu.unsa.daisi.lis.cel.domain.model.petrinet.PetriNet;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredScenario;



public interface IPetriNetService {
	
	
	
	
	/**
	 * @param scenario
	 * @param initialPositionX graphics X >= 300
	 * @param initialPositionY graphics Y >= 50
	 * @return
	 */
	PetriNet transformScenario(StructuredScenario scenario, int initialPositionX, int initialPositionY);
	
	/**
	 * For every scenario and its related scenarios, we generate partial Petri-Nets in order to integrate these partial Petri-Nets into a consistent whole Integrated Petri-Net. The Integrated Petri-Net reflects exactly the original properties of the synthesized Petri-Nets 
	 * @param mainScenario
	 * @param sequentiallyRelatedScenariosHashMap
	 * @param nonSeqRelatedScenarioHashMap
	 * @return
	 */
	PetriNet integratePetriNetsFromMainScenario(StructuredScenario mainScenario, HashMap<String, List<StructuredScenario>> sequentiallyRelatedScenariosHashMap, HashMap<String, List<StructuredScenario>> nonSeqRelatedScenarioHashMap);
	
	/**
	 * Return a XML string in PNML format from a PetriNet
	 * @param petriNet
	 * @return
	 */
	String createPNML(PetriNet petriNet);
	
	/**
	 * Return a XML StreamSource in PNML format from a PetriNet
	 * @param petriNet
	 * @return
	 */
	StreamSource createPNMLStreamSource(PetriNet petriNet);
	
	/**
	 * Breadth First Search - with cycles
	 * @param graph
	 * @param visited
	 * @param START
	 * @param END
	 */
	void depthFirstSearchRecursiveAllPaths(PetriNet graph, LinkedList<Node> visited, Node START, Node END);
	
	void printPath(LinkedList<Node> visited);

}