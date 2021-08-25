
/**
 *
 * @author jenna
 */
public class TableNode {

    public String textOfNode;
    public int nodeID;
    public boolean terminal; //true = terminal ; false=non terminal
    public String scope;

    public TableNode() {
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
