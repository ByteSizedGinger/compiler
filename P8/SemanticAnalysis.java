
import static java.lang.System.exit;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author jenna
 */
public class SemanticAnalysis {

    private List<TreeNode> tree;
    private List<TableNode> table;
    private List<TableNode> deleteList = new ArrayList<TableNode>();
    private List<TreeNode> deleteTreeList = new ArrayList<TreeNode>();
    private List<String> forLoopScopeList = new ArrayList<>();

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
                    printTree();
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

            if (t.delete) {
                TreeNode ptr = tree.get(1);
                ptr.deleteCode(t.nodeID);
            }

            if (t.textOfNode.equals("UserDefinedNameDef") || t.textOfNode.equals("UserDefinedNameCall")) {
                TreeNode ptr = tree.get(1);

                ptr.renameDeF(t.nodeID, t.vName);
            }

        }

    }

    public boolean search(int id) {
        ListIterator it = table.listIterator();
        while (it.hasNext()) {
            TableNode t = (TableNode) it.next();
            if (t.nodeID == id) {
                return true;
            }

        }
        return false;
    }

    public void printTable() {
        ListIterator it = table.listIterator();
        while (it.hasNext()) {
            TableNode t = (TableNode) it.next();
            System.out.println(t.nodeID + ") " + t.textOfNode + " " + t.contents + " " + t.scope + " " + t.vName);

        }
    }

    public void checkSemantics() {
        forLoopListBuild();
        checkProcedure();
        checkVariableNames();
   
        matchDefsAndCalls();

        deleteDeadCode();
        forLoopVarCheck();

        System.out.println("Semantic Analysis finished with no errors ");
    }

    public void printTree() {
        tree.get(1).printChildrenScope(0);
    }

    public void matchDefsAndCalls() {
        int pIndex = 0;
        ListIterator it = table.listIterator();
        while (it.hasNext()) {
            TableNode t = (TableNode) it.next();
            if (t.textOfNode.equals("UserDefinedNameDef")) {
                if (findCalls(t.contents, t.scope, "p" + pIndex)) {
                    t.vName = "p" + pIndex;
                    pIndex++;

                }
            }
        }

        it = table.listIterator();
        while (it.hasNext()) {
            TableNode t = (TableNode) it.next();
            if (t.textOfNode.equals("UserDefinedNameCall") && t.vName == null) {
                System.out.println("Semantic error: Call without matching definition : " + t.contents);
                exit(0);
            }
        }

    }

    public boolean findCalls(String name, String defScope, String newname) {
        boolean found = false;
        ListIterator it = table.listIterator();
        while (it.hasNext()) {
            TableNode t = (TableNode) it.next();
            if (t.textOfNode.equals("UserDefinedNameCall") && t.vName == null && t.contents.equals(name)) {
                if (checkCallScopes(t.scope, defScope)) {
                    t.vName = newname;
                    found = true;
                } else {
                    String newScope = t.scope;

                    while (forLoopScopeList.contains(newScope)) {

                        newScope = newScope.substring(0, newScope.lastIndexOf("."));

                        if (checkCallScopes(newScope, defScope)) {
                            t.vName = newname;
                            found = true;
                            break;
                        }

                    }

                }
            }
        }

        return found;
    }

    public boolean checkCallScopes(String callScope, String defScope) {
        if (callScope.equals(defScope)) {

            return true;
        } else if (callScope.startsWith(defScope) && callScope.lastIndexOf(".") == defScope.length()) {

            return true;
        } else if (defScope.startsWith(callScope) && defScope.lastIndexOf(".") == callScope.length()) {

            return true;
        } else {
            return false;
        }

    }

    public boolean findOnlyRecursive(String defName, String defScope) {

        boolean error = true;
        ListIterator it = table.listIterator();
        while (it.hasNext()) {
            TableNode t = (TableNode) it.next();
            if (defName.equals(t.vName)) {
              
                if (t.scope.equals(defScope)) {

                } else if (defScope.startsWith(t.scope) && defScope.lastIndexOf(".") == t.scope.length()) {
                
                    error = false;
                    return error;
                } else {
                    String newScope = t.scope;

                    while (forLoopScopeList.contains(newScope)) {

                        newScope = newScope.substring(0, newScope.lastIndexOf("."));

                        if (checkRecursiveCallScope(newScope, defScope)) {

                            return false;

                        }

                    }
                }
            }
        }
        return error;
    }

    public boolean checkRecursiveCallScope(String callScope, String defScope) {
        boolean found = false;
        if (callScope.equals(defScope)) {

        } else if (defScope.startsWith(callScope) && defScope.lastIndexOf(".") == callScope.length()) {

            return true;
        }

        return found;

    }

//    public void matchDefsAndCalls() {
//        int nameIndex = 0;
//        ListIterator it = table.listIterator();
//        while (it.hasNext()) {
//            TableNode t = (TableNode) it.next();
//            if (t.textOfNode.equals("UserDefinedNameDef") && t.vName == null) {
//              
//                boolean found = findMatchingProcCalls(t.contents, t.scope, "p" + nameIndex);
//                if (found == true) {
//                      System.out.println(t.contents + " " + t.scope);
//                    t.vName = "p" + nameIndex;
//                    nameIndex++;
//                } else {
//                
//                }
//
//            }
//        }
//        ListIterator it2 = table.listIterator();
//        while (it2.hasNext()) {
//            TableNode t = (TableNode) it2.next();
//            if (t.textOfNode.equals("UserDefinedNameCall") && t.vName == null) {
//                System.out.println("Semantic error: Call without matching definition : " + t.contents);
//                //printTable();
//                exit(0);
//            }
//        }
//
//        //System.out.println("Largest name index "+(nameIndex-1));
//    }
//
//    public boolean findMatchingProcCalls(String name, String scope, String newName) {
//        // System.out.println("name="+name+ " scope="+scope);
//        ListIterator it = table.listIterator();
//        boolean found = false;
//        while (it.hasNext()) {
//            TableNode t = (TableNode) it.next();
//
//            if (t.textOfNode.equals("UserDefinedNameCall") && t.contents.equals(name)) {
//                  System.out.println(" Call:"+t.contents + " Call Scope: "+t.scope + " ID="+t.nodeID);
//                  
//                if (t.scope.equals(scope)) {
//                    t.vName = newName;
//                    found = true;
//                } else if (t.scope.contains(scope) && (((t.scope.length() - 2) == scope.length()) || (scope.length() - 2) == t.scope.length())) {
//                    t.vName = newName;
//                    found = true;
//                } else if (scope.contains(t.scope) && (((t.scope.length() - 2) == scope.length()) || (scope.length() - 2) == t.scope.length())) {
//                    t.vName = newName;
//                    found = true;
//                } else if (forLoopScopeList.contains(t.scope)) {
//                   // System.out.println(t.contents + " Going into testNextScope");
//                    found = testNextScope(t, newName, scope, t.scope);
//                }
//            }
//        }
//        return found;
//    }
//    
//    public String trueScope(String scope){
//        while(forLoopScopeList.contains(scope)){
//          scope =scope.substring(0, scope.lastIndexOf("."));
//        }
//        return scope;
//    }
//
//    public boolean testNextScope(TableNode t, String newName, String scope, String tScope) {
//        //System.out.println("TestNextScope Substring: "+ tScope.substring(0,tScope.lastIndexOf(".")));
//      //  System.out.println("SemanticAnalysis.testNextScope()" + scope.lastIndexOf("."));
//        if (tScope.substring(0, tScope.lastIndexOf(".")).equals("0") && scope.lastIndexOf(".") != 1) {
//            return false;
//                }else {
//            if (scope.startsWith(tScope.substring(0, tScope.lastIndexOf(".")))) {
//                t.vName = newName;
//                return true;
//            } else if (forLoopScopeList.contains(tScope)) {
//                //System.out.println(t.contents +" Going into testNextScope again" );
//                return testNextScope(t, newName, scope, tScope.substring(0, tScope.lastIndexOf(".")));
//            } else {
//              //  System.out.println("TestNextScope False "+t.contents);
//                return false;
//            }
//        }
//
//    }
//
    public void deleteDeadCode() {
        ListIterator it = table.listIterator();

        while (it.hasNext()) {
            TableNode t = (TableNode) it.next();
            if ((t.textOfNode.equals("UserDefinedNameDef") && t.vName == null)) {
                String scope = t.scope;

                deleteBranch(scope);
                if (!deleteList.contains(t)) {
                    t.delete = true;
                    deleteList.add(t);
                }
                if (!deleteList.contains(table.get(t.nodeID - 3))) {
                    (table.get(t.nodeID - 3)).delete = true;
                    deleteList.add(table.get(t.nodeID - 3));
                }

            } else if (t.vName != null && t.textOfNode.equals("UserDefinedNameDef") && findOnlyRecursive(t.vName, t.scope)) {
                String scope = t.scope;

                deleteBranch(scope);
                if (!deleteList.contains(t)) {
                    t.delete = true;
                    deleteList.add(t);
                }
                if (!deleteList.contains(table.get(t.nodeID - 3))) {
                    (table.get(t.nodeID - 3)).delete = true;
                    deleteList.add(table.get(t.nodeID - 3));
                }
            }
        }
        renameTree();

        ListIterator it2 = deleteList.listIterator();
        while (it2.hasNext()) {
            TableNode t = (TableNode) it2.next();
            //System.out.println(t.nodeID);
            table.remove(t);
        }

    }

    public void deleteBranch(String scope) {
        ListIterator it2 = table.listIterator();
        // System.out.println("Scope="+scope);
        //  List<TableNode> delteList = new ArrayList<TableNode>();
        while (it2.hasNext()) {
            TableNode t2 = (TableNode) it2.next();

            if (t2.scope.equals(scope) || t2.scope.startsWith(scope)) {
                //System.out.println("ID="+t2.nodeID+"T="+t2.textOfNode);
                // delteList.add(t2);

                if (!deleteList.contains(t2)) {

                    t2.delete = true;
                    deleteList.add(t2);
                }
            }
        }

    }

    public void forLoopVarCheck() {
        ListIterator it = table.listIterator();
        while (it.hasNext()) {
            TableNode t = (TableNode) it.next();
            if ((t.textOfNode.equals("variableFor"))) {

                int forID = t.nodeID;
                String vName = t.vName;

                ListIterator it2 = table.listIterator();
                boolean v2 = false;
                boolean v3 = false;
                boolean v4 = false;
                while (it2.hasNext()) {
                    TableNode t2 = (TableNode) it2.next();
                    //System.out.println(t2.vName);
                    if (t2.vName != null && t2.vName.equals(vName) && t2.nodeID != forID) {
                        if (t2.nodeID == (forID + 4)) {

                            v2 = true;
                        } else if (t2.nodeID == (forID + 9)) {

                            v3 = true;
                        } else if (t2.nodeID == (forID + 13)) {

                            v4 = true;
                        } else {
                            if (it2.hasNext()) {

                                TableNode t3 = (TableNode) it2.next();
                                if (t3.textOfNode.equals("assignment")) {
                                    System.out.println("Semantic error for loop counter being modified: " + t.vName + " under same scope:" + t.scope);
                                    exit(0);
                                }

                            }

                        }

                    }
                }
                if (v2 && v3 && v4) {

                } else {
                    System.out.println("Semantic error not all loop counting variables the same: " + t.vName + " under same scope:" + t.scope);
                    exit(0);
                }
            }
        }

    }

    public void forLoopListBuild() {
        ListIterator it2 = table.listIterator();
        while (it2.hasNext()) {
            TableNode t = (TableNode) it2.next();
            if (t.textOfNode.equals("variableFor")) {
                forLoopScopeList.add(t.scope);
            }
        }
    }

}
