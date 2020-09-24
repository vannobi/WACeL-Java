package pe.edu.unsa.daisi.lis.cel.domain.model.petrinet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * @author Edgar
 *
 */
public class PetriNet implements Serializable {

	private static final long serialVersionUID = 4648570254148736090L;

	private Long id;
	private String name;
	@JsonIgnore
	private Integer lastNodeId; 
	@JsonIgnore
	private Integer lastArcId;
	private List<Node> nodes; 
	private List<Arc> arcs;
	@JsonIgnore
	private Node startPlace;		//First place - root of the graph
	@JsonIgnore
	private Node finalTransition; //Last transition of the success path
	//Graphics: node (x,y) position
	@JsonIgnore
	private int minPositionX;
	@JsonIgnore
	private int maxPositionX;
	@JsonIgnore
	private int minPositionY;
	@JsonIgnore
	private int maxPositionY;
	
	private String pnml; //String-XML with PNML format
		
	public PetriNet() {
		
	}

	public PetriNet(Long id, String name) {
		this.id = id;
		this.name = name;
		this.lastNodeId = 0;
		this.lastArcId = 0;
		this.nodes  = new ArrayList<>();
		this.arcs  = new ArrayList<>();
		this.minPositionX = 0;
		this.minPositionY = 0;
		this.maxPositionX = 0;
		this.maxPositionY = 0;
		this.pnml = "";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getLastNodeId() {
		return lastNodeId;
	}

	public void setLastNodeId(Integer lastNodeId) {
		this.lastNodeId = lastNodeId;
	}

	public Integer getLastArcId() {
		return lastArcId;
	}

	public void setLastArcId(Integer lastArcId) {
		this.lastArcId = lastArcId;
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	public List<Arc> getArcs() {
		return arcs;
	}

	public void setArcs(List<Arc> arcs) {
		this.arcs = arcs;
	}

	public Node getStartPlace() {
		return startPlace;
	}

	public void setStartPlace(Node startPlace) {
		this.startPlace = startPlace;
	}

	public Node getFinalTransition() {
		return finalTransition;
	}

	public void setFinalTransition(Node finalTransition) {
		this.finalTransition = finalTransition;
	}

	public int getMinPositionX() {
		return minPositionX;
	}

	public void setMinPositionX(int minPositionX) {
		this.minPositionX = minPositionX;
	}

	public int getMaxPositionX() {
		return maxPositionX;
	}

	public void setMaxPositionX(int maxPositionX) {
		this.maxPositionX = maxPositionX;
	}

	public int getMinPositionY() {
		return minPositionY;
	}

	public void setMinPositionY(int minPositionY) {
		this.minPositionY = minPositionY;
	}

	public int getMaxPositionY() {
		return maxPositionY;
	}

	public void setMaxPositionY(int maxPositionY) {
		this.maxPositionY = maxPositionY;
	}

	public String getPnml() {
		return pnml;
	}

	public void setPnml(String pnml) {
		this.pnml = pnml;
	}

	
	/**
	@Titulo: Add New Node
	@Objetivo: Add a node to the petriNet.
	@Contexto: node does not exist; create a new one: node = {id= id, name = name,label = label, type = type, adjNodes = {}}
				, Type of Vertex = {NodeTypeEnum.PLACE=Place, NodeTypeEnum.PLACE_WITH_TOKEN= Marked Place (with token) , NodeTypeEnum.TRANSITION=Transition}
		Localizacion: util.
		Pre-condicion:
	@Atores: sistema.
	@Recursos: node.
	 */
    public Node addNode(Node node) {
		//@episodio1: Find the node in the petriNet nodes.
		Node oldNode = findNode(node);
		//@excepcion 1.1: IF node is included in the petriNet nodes THEN Return
		//@episodio2: Add the node to the petriNet nodes
		if (oldNode == null) {
			//@episodio3: IF the node has not an id THEN Set the id: 0,1,...,n
			if (node.getId() == null || (StringUtils.isNumeric(node.getId()) &&  Integer.parseInt(node.getId()) <= 0)) {
				this.setLastNodeId(this.getLastNodeId() + 1); 
				node.setId(Integer.toString(this.getLastNodeId()));
			}
			this.getNodes().add(node);
		}
		//@episodio4: Return a new node.
		return node;
	}
	
	/**
	@Titulo: Remove Node
	@Objetivo: Remove the node and delete all adjacent and incident arcs of the removed node.
	@Contexto: node exist; create a new one: node = {id= id, name = name,label = label, type = type, adjNodes = {}}
				, Type of Vertex = {NodeTypeEnum.PLACE=Place, NodeTypeEnum.PLACE_WITH_TOKEN= Marked Place (with token) , NodeTypeEnum.TRANSITION=Transition}
		Localizacion: util.
		Pre-condicion:
	@Atores: sistema.
	@Recursos: node.
	 **/
	public Node removeNode( Node node) {
		//@episodio1: Find the node to be deleted in the PetriNet
		node = findNode(node);
		if(node != null ) {
			//@episodio2: Remove all adjacent arcs of node in PetriNet
			List<Arc> lstAdjs = adjacentArcs(node);
			for(Arc a1 : lstAdjs)
				removeArc(a1);

			//@episodio3: Remove all incident arcs of node in PetriNet
			List<Arc> lstIncd = incidentArcs(node);
			for(Arc a2 : lstIncd)
				removeArc(a2);

			//@episodio5: Remove node from nodes of PetriNet
			this.getNodes().remove(node);

			//@episodio6: Return The removed node.
			return node;
		}
		else 
			return null;
	}
	

	/**
	@Titulo:  Find Node
	@Objetivo: looks up the node with the given node name in the petriNet.
	@Contexto:
		Localizacion: util.
		Pre-condicion:
	@Atores: sistema.
	@Recursos: node.
	 **/
	public Node findNode(Node node) {
		Node result = null;
		//@episodio1: Look up the node with the given name (id) in the petriNet.
		for (Node nodeV: this.getNodes()) {
			if (nodeV.getId().equals(node.getId())) {
				result = nodeV;
				break;
			}
		}
		//@excepcion 1.1: if node was not found in the petriNet THEN Return nil.
		//@episodio2: Return the node with the given name.
		return result;
	}
	
	/**
	@Titulo:  Find Node by Name
	@Objetivo: looks up the node with the given node name in the petriNet.
	@Contexto:
		Localizacion: util.
		Pre-condicion:
	@Atores: sistema.
	@Recursos: name.
	 **/
	public Node findNodeByName(String name) {
		Node result = null;
		//@episodio1: Look up the node with the given name  in the petriNet.
		for (Node nodeV: this.getNodes()) {
			if (nodeV.getName().equals(name)) {
				result = nodeV;
				break;
			}
		}
		//@excepcion 1.1: if node was not found in the petriNet THEN Return nil.
		//@episodio2: Return the node with the given name.
		return result;
	}
	/**
	@Titulo:  Find Node by Name and Type
	@Objetivo: looks up the node with the given node name in the petriNet.
	@Contexto:
		Localizacion: util.
		Pre-condicion:
	@Atores: sistema.
	@Recursos: name, type.
	 **/
	public Node findNodeByName(String name, NodeTypeEnum type) {
		Node result = null;
		//@episodio1: Look up the node with the given name  in the petriNet.
		for (Node nodeV: this.getNodes()) {
			if (nodeV.getName().equals(name) && nodeV.getType().equals(type)) {
				result = nodeV;
				break;
			}
		}
		//@excepcion 1.1: if node was not found in the petriNet THEN Return nil.
		//@episodio2: Return the node with the given name.
		return result;
	}
	/**
	@Titulo:  Find Place Node (Place and Place with Token) by Name
	@Objetivo: looks up the node with the given node name in the petriNet.
	@Contexto:
		Localizacion: util.
		Pre-condicion:
	@Atores: sistema.
	@Recursos: name.
	 **/
	public Node findPlaceByName(String name) {
		Node result = null;
		//@episodio1: Look up the node with the given name  in the petriNet.
		for (Node nodeV: this.getNodes()) {
			if (nodeV.getName().equals(name) && (nodeV.getType().equals(NodeTypeEnum.PLACE) || nodeV.getType().equals(NodeTypeEnum.PLACE_WITH_TOKEN))) {
				result = nodeV;
				break;
			}
		}
		//@excepcion 1.1: if node was not found in the petriNet THEN Return nil.
		//@episodio2: Return the node with the given name.
		return result;
	}
	
	/**
	@Titulo:  Find Place Node (Place and Place with Token) by Name and Trace of "Pre-condition" or "Post-condition"
	@Objetivo: looks up the node with the given node name in the petriNet.
	@Contexto:
		Localizacion: util.
		Pre-condicion:
	@Atores: sistema.
	@Recursos: name.
	 **/
	public Node findPlaceByNameWithTracePrePostCondition( String name) {
		Node result = null;
		//@episodio1: Look up the node with the given name  in the petriNet.
		for (Node nodeV: this.getNodes()) {
			if (nodeV.getName().equals(name) 
					&& (nodeV.getType().equals(NodeTypeEnum.PLACE) || nodeV.getType().equals(NodeTypeEnum.PLACE_WITH_TOKEN))
					&& (nodeV.getTrace() != null && (nodeV.getTrace().contains("Pre-condition") || nodeV.getTrace().contains("Post-condition"))) ) {
				result = nodeV;
				break;
			}
		}
		//@excepcion 1.1: if node was not found in the petriNet THEN Return nil.
		//@episodio2: Return the node with the given name.
		return result;
	}
	
	/**
	@Titulo:  Find Transition Node by Name and Trace of "Non-sequential"
	@Objetivo: looks up the node with the given node name in the petriNet.
	@Contexto:
		Localizacion: util.
		Pre-condicion:
	@Atores: sistema.
	@Recursos: name.
	 **/
	public Node findTransitionByNameWithTraceNonSequential(String name) {
		Node result = null;
		//@episodio1: Look up the node with the given name  in the petriNet.
		//TBD: equals or contains?
		for (Node nodeV: this.getNodes()) {
			//if (nodeV.getName().contains(name)
			if (nodeV.getName().toUpperCase().contains(name.toUpperCase()) 
					&& nodeV.getType().equals(NodeTypeEnum.TRANSITION)
					&& (nodeV.getTrace() != null && nodeV.getTrace().contains("Non-sequential")) ) {
				result = nodeV;
				break;
			}
		}
		//@excepcion 1.1: if node was not found in the petriNet THEN Return nil.
		//@episodio2: Return the node with the given name.
		return result;
	}

	/**
	@Titulo:  Find Node by Label
	@Objetivo: looks up the node with the given node label in the petriNet.
	@Contexto:
		Localizacion: util.
		Pre-condicion:
	@Atores: sistema.
	@Recursos: label.
	 **/
	private Node findNodeByLabel(String label) {
		Node result = null;
		//@episodio1: Look up the node with the given label in the petriNet.
		for (Node nodeV: this.getNodes()) {
			if (nodeV.getLabel().equals(label)) {
				result = nodeV;
				break;
			}
		}
		//@excepcion 1.1: if node was not found in the petriNet THEN Return nil.
		//@episodio2: Return the node with the given label.
		return result;
	}
	
	/**
	@Titulo:  Find Node by Label and Type
	@Objetivo: looks up the node with the given node label in the petriNet.
	@Contexto:
		Localizacion: util.
		Pre-condicion:
	@Atores: sistema.
	@Recursos: label, type.
	 **/
	public Node findNodeByLabel(String label, NodeTypeEnum type) {
		Node result = null;
		//@episodio1: Look up the node with the given label in the petriNet.
		for (Node nodeV: this.getNodes()) {
			if (nodeV.getLabel().equals(label) && nodeV.getType().equals(type)) {
				result = nodeV;
				break;
			}
		}
		//@excepcion 1.1: if node was not found in the petriNet THEN Return nil.
		//@episodio2: Return the node with the given label.
		return result;
	}
	
	public Node findNodeById( String id) {
		Node result = null;
		//@episodio1: Look up the node with the given label in the petriNet.
		for (Node n: this.getNodes()) {
			if (n.getId().equals(id)) {
				result = n;
				break;
			}
		}
		//@excepcion 1.1: if node was not found in the petriNet THEN Return nil.
		//@episodio2: Return the node with the given label.
		return result;
	}
	
	/**
	@Titulo: Update Node by Id
	@Objetivo: Look up the node with the given node id and update it in the petriNet.
	@Contexto:
		Localizacion: util.
		Pre-condicion:
	@Atores: sistema.
	@Recursos: node.
	 **/
	public Node updateNodeById(Node node) {
		Node result = null;
		//@episodio1: Look up the node with the given name (id) in the petriNet.
		for (Node nodeV: this.getNodes()) {
			//@episodio2: Update the node with the new_node.
			if (nodeV.getId().equals(node.getId())) {
				nodeV = node;
				result = nodeV;
				break;

			}
		}
		//@excepcion 1.1: If node was not found in the petriNet then return nil.
		//@episodio3: Return the node with the given name.
		return result;

	}
	
	/**
	@Titulo: Adjacent Nodes 
	@Objetivo: Return all adjacent nodes
	@Contexto:
		Localizacion: util.
		Pre-condicion:
	@Atores: sistema.
	@Recursos:
	 **/
	private List<Node> adjacentNodes(Node node) {
		List<Node> lstNode = new ArrayList<>();
		for(Arc a : this.getArcs()) {
			if(a.getSource().getId().equals(node.getId())) 
				lstNode.add(a.getTarget());
		}
		return lstNode;
	}
	
	/**
	@Titulo: Incident Nodes 
	@Objetivo: Return all incident nodes
	@Contexto:
		Localizacion: util.
		Pre-condicion:
	@Atores: sistema.
	@Recursos: 
	 **/
	public List<Node> getIncidentNodes(Node node) {
		List<Node> incident = new ArrayList<>(); 
		//@episodio1: Look up the node with the given name (id) in the petriNet.
		node = findNode( node);
		if (node != null){
			//@episodio2: If node has incident nodes then save the incident nodes.
			for (Arc arcV : this.getArcs()) {
				if (node.getId().equals(arcV.getTarget().getId())){
					incident.add(arcV.getSource());
				}
			}
		}
		//@episodio3: Return the incident arcs.
		return incident;
	}
	
	/**
	@Titulo: Add Arc
	@Objetivo: Add an arc to the petriNet.
	@Contexto: arc does not exist; create a new one: arc = {index= index, value = value, type = type, source = source, target = target}
				, Type of Arc = Arc = {A}
		Localizacion: util.
		Pre-condicion:
	@Atores: sistema.
	@Recursos: arc
	 **/
	public Arc addArc(Arc arc) {
		//@episodio1: Find the arc in the petriNet arcs
		Arc oldArc = findArc(arc);
		//@excepcion 1.1: IF arc is included in the petriNet arcs THEN Return
		//if not oldArc and not self:isConnected(arc.source, arc.target) then
		if(oldArc == null) {
			//@episodio2: If arc has not an id THEN Set the id: 0,1,...,n
			if (arc.getId() == null || Integer.parseInt(arc.getId()) <= 0) {
				//arc.id = self:getNumberItems(self.arcs) + 1
				this.setLastArcId(this.getLastArcId() + 1);
				arc.setId(Integer.toString(this.getLastArcId()));

			}
			Node from = findNode(arc.getSource());
			Node to = findNode(arc.getTarget());
			from.getAdjNodes().add(to);//addAdjacentNode(to); //TBD: Verify, is it updating?
			this.getArcs().add(arc);
		}
		//@episodio3: return the new arc
		return arc;
	}

	/**
	@Titulo: Remove Arc
	@Objetivo: Remove the arc from arcs of the PetriNet.
	@Contexto:
		Localizacion: util.
		Pre-condicion:
	@Atores: sistema.
	@Recursos:  arc.
	 **/
	public Arc removeArc(Arc arc) {
		//@episodio1: Find the arc to be removed in the petriNet
		arc = findArc(arc);
		//@excepcion 1.1: if arc was not found in the petriNet THEN return nil.
		if (arc != null) {
			//@episodio2: Remove adjacent link between source and target nodes
			Node from = findNode(arc.getSource());
			Node to = findNode(arc.getTarget());
			from.getAdjNodes().remove(to);
			this.getArcs().remove(arc);

		}
		//@episodio5: Return The removed arc.
		return arc;
	}
	/**
	@Titulo: Remove Arc Between Nodes
	@Objetivo: Removes an arc between two nodes and also removes it from these nodes.
	@Contexto:
		Localizacion: util.
		Pre-condicion:
	@Atores: sistema.
	@Recursos:  start node, end node
	 **/
	public Arc removeArcBetweenNodes(Node from, Node to) {
		//@episodio1: Find the arc in the petriNet between start node and final node
		Arc arc = isConnected(from, to);
		//@excepcion 1.1: if arc was not found in the petriNet THEN return nil.
		//@episodio2: Delete the arc
		if (arc != null) {
			arc = removeArc(arc);
		}
		//@episodio3: Return the arc
		return arc;
	}
	/**
	@Titulo: Remove Arcs by Value
	@Objetivo: Removes arcs when #(petriNet:incidentNodes(target)) > #(petriNet:adjacentNodes(target)).
	@Contexto:
		Localizacion: util.
		Pre-condicion:
	@Atores: sistema.
	@Recursos:  arcs, value
	 **/
	private Arc removeArcsByValue(String value) {
		Arc arc = null;
		for (Arc arcV  : this.getArcs()) {
			if (arcV.getValue().equals(value)) { // arcs with the same id
				Node source = arcV.getSource();
				Node target = arcV.getTarget();
				if (getIncidentNodes(target).size() > target.getAdjNodes().size()) {
					arc = removeArcBetweenNodes(source, target);
					//arc = self:removeArc(arc)
				}	
			}
		}
		return arc;
	}

	/**
        @Titulo: Find Arc
	@Objetivo: Check whether the arc already exists in the petriNet and returns it if possible.
	@Contexto:
		Localizacion: util.
		Pre-condicion:
	@Atores: sistema.
	@Recursos: arc.
	 **/
	private Arc findArc(Arc arc) {
		Arc result = null;
		//@episodio1: Look up the arc with the given id in the petriNet.
		for (Arc arcV : this.getArcs()) {
			if (arcV.getId().equals(arc.getId())){ // arcs with the same id
				result = arcV;
				break;
			}
		}
		//@excepcion 1.1: if arc was not found in the petriNet THEN return nil.
		//@episodio2: Return The arc.
		return result;
	}

	/**
	@Titulo: Is Connected
	@Objetivo: Verify that a Node x is connected to Node y
	@Contexto:
		Localizacion: util.
		Pre-condicion:
	@Atores: sistema.
	@Recursos: start node , final node
	 **/
	private Arc isConnected(Node from, Node to) {
		Arc arc = null;
		//@episodio1: If start node is adjacent to final node THEN save the arc between the nodes.

		if (from.getAdjNodes().contains(to)) {
			for (Arc vArc : this.getArcs()) {
				if(vArc.getSource().getId().equals(from.getId()) && vArc.getTarget().getId().equals(to.getId())) {
					arc = vArc;
					break;
				}
			}
		}
		//@excepcion 1.1: if arc was not found in the petriNet THEN return nil.
		//@episodio2: Return The arc link between start node and final node
		return arc;
	}

	/**
	@Titulo: Adjacent Arcs
	@Objetivo: Return all adjacent Arcs to node
	@Contexto:
		Localizacion: util.
		Pre-condicion:
	@Atores: sistema.
	@Recursos: node to look for
	 **/
	private List<Arc> adjacentArcs(Node node){
		List<Arc> adjacent = new ArrayList<>(); 
		//@episodio1: Look up the node with the given name (id) in the petriNet.
		node = findNode( node);
		if (node != null) {
			//@episodio2: If node has adjacent nodes then save the adjacent nodes.
			if (node.getAdjNodes() != null && node.getAdjNodes().size() > 0) {
				for (Arc arcV : this.getArcs()) {
					if(arcV.getSource().getId().equals(node.getId()))
						adjacent.add(arcV);
				}
				//return adjacent;
			}
		}
		//@episodio3: Return the adjacent arcs.
		return adjacent;
	}

	/**
	@Titulo: Incident Arcs
	@Objetivo: Return all incident Arcs to node
	@Contexto:
		Localizacion: util.
		Pre-condicion:
	@Atores: sistema.
	@Recursos: node to look for
	 **/
	private List<Arc> incidentArcs(Node node) {
		List<Arc> incident = new ArrayList<Arc>(); 
		//@episodio1: Look up the node with the given name (id) in the petriNet.
		node = findNode(node);
		if (node != null) {
			//@episodio2: If node has incident nodes then save the incident nodes.
			for (Arc arcV : this.getArcs())  {
				if (node.getId().equals(arcV.getTarget().getId())) {
					incident.add(arcV);
				}
			}
		}
		//@episodio3: Return the incident arcs.
		return incident;
	}




	
	public static void main(String[] args) {
		System.out.println("New Graph");	
		PetriNet graph = new PetriNet(1L, "test");
		//graph.createPetriNet(1L, "test");

		Node nodeA = new Node();
		nodeA = new Node("A", "A", "A", NodeTypeEnum.PLACE);
		graph.addNode(nodeA);

		Node nodeB = new Node();
		nodeB = new Node("B", "B", "B", NodeTypeEnum.PLACE);
		graph.addNode(nodeB);

		Node nodeC = new Node();
		nodeC = new Node("C", "C", "C", NodeTypeEnum.PLACE);
		graph.addNode(nodeC);

		Node nodeD = new Node();
		nodeD = new Node("D", "D", "D", NodeTypeEnum.PLACE);
		graph.addNode(nodeD);

		Node nodeE = new Node();
		nodeE = new Node("E", "E", "E", NodeTypeEnum.PLACE);
		graph.addNode(nodeE);

		Node nodeF = new Node();
		nodeF = new Node("F", "F", "F", NodeTypeEnum.PLACE);
		graph.addNode(nodeF);


		Arc arcAB = new Arc();
		arcAB= new Arc(ArcTypeEnum.ARC, nodeA, nodeB);
		graph.addArc(arcAB);

		Arc arcAC = new Arc();
		arcAC= new Arc(ArcTypeEnum.ARC, nodeA, nodeC);
		graph.addArc(arcAC);

		Arc arcBA = new Arc();
		arcBA= new Arc(ArcTypeEnum.ARC, nodeB, nodeA);
		graph.addArc(arcBA);

		Arc arcBD = new Arc();
		arcBD= new Arc(ArcTypeEnum.ARC, nodeB, nodeD);
		graph.addArc(arcBD);

		Arc arcBE = new Arc();
		arcBE= new Arc(ArcTypeEnum.ARC, nodeB, nodeE);
		graph.addArc(arcBE);

		Arc arcBF = new Arc();
		arcBF= new Arc(ArcTypeEnum.ARC, nodeB, nodeF);
		graph.addArc(arcBF);

		Arc arcCA = new Arc();
		arcCA= new Arc(ArcTypeEnum.ARC, nodeC, nodeA);
		graph.addArc(arcCA);

		Arc arcCE = new Arc();
		arcCE= new Arc(ArcTypeEnum.ARC, nodeC, nodeE);
		graph.addArc(arcCE);

		Arc arcCF = new Arc();
		arcCF= new Arc(ArcTypeEnum.ARC, nodeC, nodeF);
		graph.addArc(arcCF);

		Arc arcDB = new Arc();
		arcDB= new Arc(ArcTypeEnum.ARC, nodeD, nodeB);
		graph.addArc(arcDB);

		Arc arcEC = new Arc();
		arcEC= new Arc(ArcTypeEnum.ARC, nodeE, nodeC);
		graph.addArc(arcEC);

		Arc arcEF = new Arc();
		arcEF= new Arc(ArcTypeEnum.ARC, nodeE, nodeF);
		graph.addArc(arcEF);

		Arc arcFB = new Arc();
		arcFB= new Arc(ArcTypeEnum.ARC, nodeF, nodeB);
		graph.addArc(arcFB);

		Arc arcFC = new Arc();
		arcFC= new Arc(ArcTypeEnum.ARC, nodeF, nodeC);
		graph.addArc(arcFC);

		Arc arcFE = new Arc();
		arcFE= new Arc(ArcTypeEnum.ARC, nodeF, nodeE);
		graph.addArc(arcFE);

		System.out.println(graph.toString());
	}
	
}


