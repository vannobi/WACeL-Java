package pe.edu.unsa.daisi.lis.cel.domain.model.petrinet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author Edgar
 */
public class Node implements Serializable {
    
   
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7340541117534173368L;
	private String id;
    private String name;
    private String label;//to be exported to PIPE_SCENARIO. Ex <id_scenario>_<T | TEX | P | PT>_<step_episode | step.reference_alternative>_<name>
    @JsonIgnore
    private String trace; //trace to scenario
    private NodeTypeEnum type; //Type of Vertex = {NodeType.PLACE=Place, NodeType.PLACE_WITH_TOKEN= Place with Token , NodeType.TRANSITION=Transition}
    private Integer tokens;
    @JsonIgnore
    private Integer capacity;
    @JsonIgnore
    private boolean timed;
    private String groupName; //nodes can be grouped
    @JsonIgnore
    private Integer positionX; //To visualize in X axis
    @JsonIgnore
    private Integer positionY; //To visualize in Y axis. It is also the height of the node in the net: root(start) -> ..... -> node 
    @JsonIgnore
    private Integer orientation; //To visualize: 0 = ||, 90 = ---
    @JsonIgnore
    private List<Node> adjNodes;
    @JsonIgnore
    private boolean dummy;
    @JsonIgnore
    private boolean visited;
    
    

    public Node() {   }
    
    /**
     * New Node
     * @param name
     * @param label
     * @param trace
     * @param type
     * @return
     */
    public Node(String name, String label, String trace, NodeTypeEnum type) {
        this.id = null;
        this.name = name;
        this.label = label;
        this.trace = trace;
        this.type = type;
        this.tokens = 0;
        this.capacity = 0;
        this.positionX = 0;
        this.positionY = 0;
        this.orientation = 90;
        this.dummy = false;
        this.adjNodes = new ArrayList<>();
        this.groupName = "";
    }
    


    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getTrace() {
		return trace;
	}

	public void setTrace(String trace) {
		this.trace = trace;
	}

	public NodeTypeEnum getType() {
		return type;
	}

	public void setType(NodeTypeEnum type) {
		this.type = type;
	}

	public Integer getTokens() {
		return tokens;
	}

	public void setTokens(Integer tokens) {
		this.tokens = tokens;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	public boolean isTimed() {
		return timed;
	}

	public void setTimed(boolean timed) {
		this.timed = timed;
	}

	
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Integer getPositionX() {
		return positionX;
	}

	public void setPositionX(Integer positionX) {
		this.positionX = positionX;
	}

	public Integer getPositionY() {
		return positionY;
	}

	public void setPositionY(Integer positionY) {
		this.positionY = positionY;
	}

	public Integer getOrientation() {
		return orientation;
	}

	public void setOrientation(Integer orientation) {
		this.orientation = orientation;
	}

	public List<Node> getAdjNodes() {
		return adjNodes;
	}

	public void setAdjNodes(List<Node> adjNodes) {
		this.adjNodes = adjNodes;
	}

	public boolean isDummy() {
		return dummy;
	}

	public void setDummy(boolean dummy) {
		this.dummy = dummy;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	@Override
    public String toString() {
        return this.label+" - "+this.name+" - "+this.type;
    }
	
    
 
}
