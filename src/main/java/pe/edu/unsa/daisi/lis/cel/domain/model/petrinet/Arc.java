/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.edu.unsa.daisi.lis.cel.domain.model.petrinet;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author Edgar
 */
public class Arc implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3458029358785965542L;
	private String id;
    @JsonIgnore
	private String value; //label
    @JsonIgnore
    private ArcTypeEnum type; //Type of Arc = Arc = {A}*/
    
    private Node source;
    
    private Node target;
    
    public Arc() {

    }

    /**
     * New Arc
     * @param type
     * @param source
     * @param target
     */
    public Arc(ArcTypeEnum type, Node source, Node target) {
        this.id = null;
        this.value = source.getLabel()+" to "+target.getLabel();
        this.type = type;
        this.source = source;
        this.target = target;
    }
    

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ArcTypeEnum getType() {
        return type;
    }

    public void setType(ArcTypeEnum type) {
        this.type = type;
    }

    public Node getSource() {
        return source;
    }

    public void setSource(Node source) {
        this.source = source;
    }

    public Node getTarget() {
        return target;
    }

    public void setTarget(Node target) {
        this.target = target;
    }
    
    @Override
    public String toString() {
        return this.value+" | ["+this.source.getLabel()+"::"+this.target.getLabel()+"]";
    }
}