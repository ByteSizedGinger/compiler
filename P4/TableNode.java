
/**
 *
 * @author jenna
 */
public class TableNode {

    public String textOfNode;
    public int nodeID;
    public boolean terminal; //true = terminal ; false=non terminal
    public String scope;
    public String contents;
    public String vName;

    public TableNode() {
    }

    public TableNode(String textOfNode, int nodeID, boolean terminal, String scope, String contents) {
        this.textOfNode = textOfNode;
        this.nodeID = nodeID;
        this.terminal = terminal;
        this.scope = scope;
        this.contents = contents;
    }

    public TableNode(String textOfNode, int nodeID) {
        this.textOfNode = textOfNode;
        this.nodeID = nodeID;
    }

    public String getTextOfNode() {
        return textOfNode;
    }

    public void setTextOfNode(String textOfNode) {
        this.textOfNode = textOfNode;
    }

    public int getNodeID() {
        return nodeID;
    }

    public void setNodeID(int nodeID) {
        this.nodeID = nodeID;
    }
    
    public void addScope(String a){
        scope=a;
    } 

}
