
import static java.lang.System.exit;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author jenna
 */
public class SemanticAnalysis {

    private List<TreeNode> tree;
    private List<TableNode> table;

    public SemanticAnalysis() {
    }

    public SemanticAnalysis(List<TreeNode> tree) {
        this.tree = tree;
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

    public void buildTable() {
        if (tree != null) {
            TreeNode ptr = tree.get(1);
            table = ptr.getTable();
            if (ptr != null) {
                table.add(new TableNode(ptr.textOfNode, ptr.getId(), ptr.terminal, ptr.scope, ptr.contents));
                List<TreeNode> children = ptr.getChildren();
                if (children != null) {
                    ListIterator it = children.listIterator();
                    while (it.hasNext()) {
                        buildChildren((TreeNode) it.next());

                    }

                }
            }
        }

    }

    public void buildChildren(TreeNode t) {
        if (t != null) {
            table.add(new TableNode(t.textOfNode, t.getId(), t.terminal, t.scope, t.contents));
            List<TreeNode> children = t.getChildren();
            if (children != null) {
                ListIterator it = children.listIterator();
                while (it.hasNext()) {
                    buildChildren((TreeNode) it.next());

                }
            }

        }
    }

    public void checkProcAgainstVar(String name) {
        ListIterator it = table.listIterator();
        while (it.hasNext()) {
            TableNode t = (TableNode) it.next();
            if (t.textOfNode.equals("variable") && t.contents.equals(name)) {
                System.out.println("Semantic error variable and procedure have same name: " + name);
                exit(0);
            }
        }
    }

    public void checkProcScope(String name, String scope) {
        //System.out.println("SemanticAnalysis.checkProcScope()");
        int count = 0;
        ListIterator it = table.listIterator();
        while (it.hasNext()) {
            TableNode t = (TableNode) it.next();

            if (t.textOfNode.equals("UserDefinedNameDef") && t.contents.equals(name) && ((t.scope.contains(scope.substring(0, scope.length() - 2)) && scope.contains(t.scope.substring(0, t.scope.length() - 2))) || t.scope.contains(scope))) {
                //System.out.println(t.contents+","+name+" "+t.scope+","+scope);
                count++;
                if (count == 2) {
                    System.out.println("Semantic error two procedure declarations with same name: " + name + " under same scope:" + scope);
                    exit(0);
                }
            }
        }
    }

    public void checkProcedure() {
        ListIterator it = table.listIterator();
        while (it.hasNext()) {
            TableNode t = (TableNode) it.next();
            if (t.textOfNode.equals("UserDefinedNameDef")) {
                checkProcAgainstVar(t.contents);
                checkProcScope(t.contents, t.scope);
            } else if (t.textOfNode.equals("UserDefinedNameCall")) {
                checkProcAgainstVar(t.contents);
            }
        }
    }

    public void checkVariableNames() {
        int nameIndex = 0;
        ListIterator it = table.listIterator();
        while (it.hasNext()) {
            TableNode t = (TableNode) it.next();
            if (t.textOfNode.equals("variable") && t.vName == null) {
                setVariableNames(t.contents, t.scope, "v" + nameIndex);
                nameIndex++;
            } else if (t.textOfNode.equals("variableFor")) {

                setForVariableNames(t.contents, t.scope, "v" + nameIndex);
                nameIndex++;
            }
        }
        //System.out.println("Largest name index "+(nameIndex-1));
    }

    public void setVariableNames(String s, String scope, String name) {
        ListIterator it = table.listIterator();
        while (it.hasNext()) {
            TableNode t = (TableNode) it.next();
            if (t.textOfNode.equals("variable") && t.contents.equals(s) && t.vName == null && (t.scope.contains(scope) || scope.contains(t.scope))) {
                t.vName = name;
            }
        }
    }

    public void setForVariableNames(String s, String scope, String name) {
        ListIterator it = table.listIterator();
        while (it.hasNext()) {
            TableNode t = (TableNode) it.next();
            if ((t.textOfNode.equals("variableFor") || t.textOfNode.equals("variable")) && t.contents.equals(s) && t.scope.contains(scope)) {
                t.vName = name;
            }
        }
    }

    public void renameTree() {
        ListIterator it = table.listIterator();
        while (it.hasNext()) {
            TableNode t = (TableNode) it.next();
            if (t.textOfNode.equals("variable") || t.textOfNode.equals("variableFor")) {
                TreeNode ptr = tree.get(1);

                ptr.renameVariable(t.nodeID, t.vName);
            }

        }

    }

    public void printTable() {
        ListIterator it = table.listIterator();
        while (it.hasNext()) {
            TableNode t = (TableNode) it.next();
            System.out.println(t.nodeID + ") " + t.textOfNode + " " + t.contents + " " + t.scope + " " + t.vName);

        }
    }

    public void checkSemantics() {
        checkProcedure();
        checkVariableNames();
        renameTree();
        System.out.println("Semantic Analysis finished with no errors ");
    }

    public void printTree() {
        tree.get(1).printChildrenScope(0);
    }

}
