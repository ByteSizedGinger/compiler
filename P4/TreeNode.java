
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author jenna
 */
public class TreeNode {

    public List<TreeNode> children = new ArrayList<TreeNode>();
    public int numChildren;
    public int id;
    public List<TableNode> table = new ArrayList<TableNode>();
    List<TreeNode> newList = new ArrayList<TreeNode>();
    public boolean terminal;
    public String textOfNode;
    public String contents;
    public String scope;

    public TreeNode() {
        numChildren = 0;
    }

    public TreeNode(int id, List<TableNode> table, boolean terminal, String text) {
        this.id = id;
        this.table = table;
        this.terminal = terminal;
        numChildren = 0;
        textOfNode = text;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(LinkedList<TreeNode> children) {
        this.children = children;
    }

    public int getNumChildren() {
        return numChildren;
    }

    public void setNumChildren(int numChildren) {
        this.numChildren = numChildren;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<TableNode> getTable() {
        return table;
    }

    public void setTable(LinkedList<TableNode> table) {
        this.table = table;
    }

    public void increaseChildren() {
        numChildren++;
    }

    public void addChild(TreeNode t) {
        if (t != null) {
            children.add(t);
            increaseChildren();
        }

    }

    public void addToFront(TreeNode t) {

        newList.add(t);
        ListIterator<TreeNode> it = children.listIterator();
        while (it.hasNext()) {
            newList.add(it.next());
        }
        children = newList;
        numChildren++;

    }

    public void printChildren(int tab) {
        System.out.print(id + ") " + textOfNode);
        if (textOfNode == "variable" || textOfNode == "Digit" || textOfNode == "String" || textOfNode == "UserDefinedNameDef" || textOfNode == "UserDefinedNameCall" || textOfNode == "variableFor") {
            System.out.print("_" + contents);
        }
        System.out.println("");
        ListIterator<TreeNode> l = children.listIterator();

        while (l.hasNext()) {
            for (int i = 0; i < tab; i++) {
                System.out.print("--");
            }
            System.out.print("*-* ");
            l.next().printChildren(tab + 1);

        }
    }

//    public int addScope(int scope,String s){
//        if(textOfNode.equals("PROG") || textOfNode =="PROC" || textOfNode=="Cond_Loop_For"){
//            if(s==""){
//                s=scope+"";
//            }else{
//                s=s+"."+scope;
//            }
//            this.scope=s;
//            scope++;
//        }else{
//            //System.out.println("TreeNode.addScope()");
//            this.scope=s;
//        }
//        ListIterator<TreeNode> childIt = children.listIterator();
//        while(childIt.hasNext()){
//          //  System.out.println(s);
//            scope=childIt.next().addScope(scope, s);
//        }
//        return scope;
//    }
    public int addScope(int scope, String s) {
        if (textOfNode == "PROC" || textOfNode == "Cond_Loop_For") {
            if (s == "") {
                s = scope + "";
            } else {
                s = s + "." + scope;
            }
            this.scope = s;
            scope++;
        } else {
            //System.out.println("TreeNode.addScope()");
            this.scope = s;
        }
        ListIterator<TreeNode> childIt = children.listIterator();
        while (childIt.hasNext()) {
            //  System.out.println(s);
            scope = childIt.next().addScope(scope, s);
        }
        return scope;
    }

    public void printChildrenScope(int tab) {
        if (textOfNode == "variableFor") {
            System.out.print(id + ") " + "variable");
        } else {
            System.out.print(id + ") " + textOfNode);
        }

        if (textOfNode == "variable" || textOfNode == "Digit" || textOfNode == "String" || textOfNode == "UserDefinedNameDef" || textOfNode == "UserDefinedNameCall" || textOfNode == "variableFor") {
            System.out.print("_" + contents);
        }
        System.out.print(" ,Scope=" + scope);
        System.out.println("");
        ListIterator<TreeNode> l = children.listIterator();

        while (l.hasNext()) {
            for (int i = 0; i < tab; i++) {
                System.out.print("--");
            }
            System.out.print("*-* ");
            l.next().printChildrenScope(tab + 1);

        }
    }

    public void renameVariable(int i, String contents) {
        if (i == id) {

            this.contents = contents;
            return;
        } else {
            ListIterator<TreeNode> l = children.listIterator();

            while (l.hasNext()) {

                l.next().renameVariable(i, contents);

            }
        }

    }

}
