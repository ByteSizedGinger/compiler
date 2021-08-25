/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jenna
 */
public class VNode {
    private String type;
    private String subType;
    private String name;
    private String scope;
    String value = "-";

    public VNode() {
    }

    public VNode(String type, String name, String scope) {
        this.type = type;
        this.name = name;
        this.scope = scope;
        subType="";
    }
    

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
    
    
    
}
