
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author jenna
 */
public class Scope {

    public List<TreeNode> tree;
    public List<TableNode> table;
    int scope=0;

    public Scope() {
    }

    public Scope(List<TreeNode> tree, List<TableNode> table) {
        this.tree = tree;
        this.table = table;
    }

    public List<TreeNode> getTree() {
        return tree;
    }

    public void setTree(List<TreeNode> tree) {
        this.tree = tree;
    }

    public List<TableNode> getTable() {
        return table;
    }

    public void setTable(List<TableNode> table) {
        this.table = table;
    }
    
    public void addScope(){
       
    //    System.out.println(tree.get(1).textOfNode);
        tree.get(1).addScope(1,"0");
      
    }
    
    public void printSyntaxTree() {
        tree.get(1).printChildrenScope(0);
    }
    

}
