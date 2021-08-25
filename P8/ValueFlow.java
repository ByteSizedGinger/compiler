
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author jenna
 */
public class ValueFlow {

    private List<TreeNode> tree;
    private List<TableNode> table;
    private List<VNode> vTable;
    private ArrayList<String> changeList = new ArrayList<>();
    private List<String> doubleList = new ArrayList<>();

    public ValueFlow() {
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

    public List<VNode> getvTable() {
        return vTable;
    }

    public void setvTable(List<VNode> vTable) {
        this.vTable = vTable;
    }

    public void checkValueFlow() {
        checkProg(tree.get(1));

    }
//done

    public void checkProg(TreeNode t) {
        if (t.children.size() > 1) {
            if (t.children.get(1).textOfNode.equals("CODE")) {
                checkCode(t.children.get(0));
                checkCode(t.children.get(1));

            } else {
                checkCode(t.children.get(0));
              //  checkProcDef(t.children.get(1));
            }

        } else {
            checkCode(t.children.get(0));
        }

    }
//done

    public void checkCode(TreeNode t) {

        if (t.children.size() > 1) {
            checkInstr(t.children.get(0));
            if (t.children.get(1).textOfNode.equals("CODE")) {
                checkCode(t.children.get(1));
            } else {
               // checkProcDef(t.children.get(1));
            }

        } else {

            if (t.children.get(0).textOfNode.equals("INSTR")) {
                checkInstr(t.children.get(0));

            } else if (t.children.get(0).textOfNode.equals("PROC_DEFS")) {
               // checkProcDef(t.children.get(0));
            }

        }

    }

    public void checkProcDef(TreeNode t) {
        checkProc(t.children.get(0));
        if (t.children.size() > 1) {
          //  checkProcDef(t.children.get(1));

        }

    }
//done

    public void checkProc(TreeNode t) {
        checkProg(t.children.get(2));
    }
//done

    public void checkInstr(TreeNode t) {
        if (t.children.get(0).textOfNode.equals("halt")) {

        } else if (t.children.get(0).textOfNode.equals("CALL")) {
            checkCall(t.children.get(0));
        } else if (t.children.get(0).textOfNode.equals("ASSIGN")) {
            checkAssign(t.children.get(0));
        } else if (t.children.get(0).textOfNode.equals("IO")) {
            checkIO(t.children.get(0));
        } else if (t.children.get(0).textOfNode.equals("Cond_Loop_For")) {
            checkCondLoopFor(t.children.get(0));
        } else if (t.children.get(0).textOfNode.equals("Cond_Loop_While")) {
            checkCondLoopWhile(t.children.get(0));

        } else if (t.children.get(0).textOfNode.equals("Cond_Branch")) {
            checkCondBranch(t.children.get(0));

        }

    }
//done

    public void checkIO(TreeNode t) {
        if (t.children.get(0).textOfNode.equals("input")) {
            updateST(t.children.get(1).children.get(0).contents);
            t.children.get(1).value = "+";
            t.children.get(1).children.get(0).value = "+";
        } else {
            if (checkST(t.children.get(1).children.get(0).contents).equals("+")) {
                t.children.get(1).value = "+";
                t.children.get(1).children.get(0).value = "+";
            } else {
                System.out.println("Value Flow error output statement with variable " + t.children.get(1).children.get(0).contents + " which has no value ");
                System.exit(0);
            }
        }

    }
//done

    public void checkCall(TreeNode t) {
        findAndCheckProc(t.children.get(0).contents, tree.get(1));
    }
    //done   

    public void findAndCheckProc(String pName, TreeNode t) {
        if (t.textOfNode.equals("PROC_DEFS") && t.children.get(0).children.get(1).contents.equals(pName) && !t.children.get(0).value.equals("*")) {
            t.children.get(0).value="*";
            checkProc(t.children.get(0));
        } else {
            ListIterator it = t.children.listIterator();
            while (it.hasNext()) {
                findAndCheckProc(pName, (TreeNode) it.next());
            }
        }
    }
//done

    public void checkAssign(TreeNode t) {
        if (t.children.get(2).textOfNode.equals("VAR")) {
            if (checkST(t.children.get(2).children.get(0).contents).equals("+")) {
                updateST(t.children.get(0).children.get(0).contents);
                t.children.get(0).children.get(0).value = "+";
                t.children.get(0).value = "+";
            } else {
                System.out.println("Value Flow error variable " + t.children.get(0).children.get(0).contents + " being assigned to variable " + t.children.get(2).children.get(0).contents
                        + " which has no value");
                System.exit(0);
            }
        } else if (t.children.get(2).textOfNode.equals("NUMEXP")) {
            checkNumExp(t.children.get(2));
            if (t.children.get(2).value.equals("+")) {
                updateST(t.children.get(0).children.get(0).contents);
                t.children.get(0).children.get(0).value = "+";
                t.children.get(0).value = "+";
            } else {
                System.out.println("Value Flow error variable " + t.children.get(0).children.get(0).contents + " being assigned to Numexpr with no value");
                System.exit(0);
            }

        } else if (t.children.get(2).textOfNode.equals("String")) {
            updateST(t.children.get(0).children.get(0).contents);
            t.children.get(0).children.get(0).value = "+";
            t.children.get(0).value = "+";
        }

    }

    public void checkCondBranch(TreeNode t) {

        if (t.children.size() > 4) {
            checkBool(t.children.get(1));
            getVarsLoops(t.children.get(3));
            ArrayList<String> temp = (ArrayList<String>) changeList.clone();
             ArrayList<String> temp3 = (ArrayList<String>) changeList.clone();
             changeList.clear();
            checkCode(t.children.get(3));
            clearVarsLoops(temp3);
            getVarsLoops(t.children.get(5));
            List<String> temp2 = (List<String>) changeList.clone();
            changeList.clear();
            checkCode(t.children.get(5));
           
            List<String> combined = combineLists(temp, temp2);
             clearVarsLoops(temp2);
            
            addDoubleVariables(combined);
            temp.clear();
            temp2.clear();
        } else if (t.children.get(0).textOfNode.equals("CODE")) {
            checkCode(t.children.get(0));
        } else {
            checkBool(t.children.get(1));
            changeList.clear();
            getVarsLoops(t.children.get(3));
            List<String> temp = (List<String>) changeList.clone();
          
            checkCode(t.children.get(3));
            
            clearVarsLoops(temp);
         

        }

    }
    
     public void getVars(TreeNode t) {
        if (t.textOfNode.equals("VAR")) {
            if (!changeList.contains(t.children.get(0).contents) && checkST(t.children.get(0).contents).equals("-")) {

                changeList.add(t.children.get(0).contents);
            }
        }
        ListIterator it = t.children.listIterator();
        while (it.hasNext()) {
            getVars((TreeNode) it.next());
        }
    }

    public void clearVars(List<String> l) {
        ListIterator it = l.listIterator();
        while (it.hasNext()) {
            updateNegativeST((String) it.next());
        }
        changeList.clear();
    }
    
      public void getVarsLoops(TreeNode t) {
        if (t.textOfNode.equals("VAR")) {
            if (!changeList.contains(t.children.get(0).contents) && checkST(t.children.get(0).contents).equals("-")) {

                changeList.add(t.children.get(0).contents);
            }
        }else if(t.textOfNode.equals("CALL")){
            if(!changeList.contains(t.children.get(0).contents)){
                 changeList.add(t.children.get(0).contents);
            }
           
            getVarsCall(t.children.get(0).contents,tree.get(1));
        }
        ListIterator it = t.children.listIterator();
        while (it.hasNext()) {
            getVarsLoops((TreeNode) it.next());
        }
    }
      
    public void getVarsCall(String name,TreeNode t){
        if (t.textOfNode.equals("PROC_DEFS") && t.children.get(0).children.get(1).contents.equals(name) && !t.children.get(0).value.equals("/") && !t.children.get(0).value.equals("*")) {
            t.children.get(0).value="/";
            getVarsProc(t.children.get(0));
        } else {
            ListIterator it = t.children.listIterator();
            while (it.hasNext()) {
               getVarsCall(name, (TreeNode) it.next());
            }
        }
    }
    
    public void getVarsProc(TreeNode t){
        
           if (t.textOfNode.equals("VAR")) {
            if (!changeList.contains(t.children.get(0).contents) && checkST(t.children.get(0).contents).equals("-")) {

                changeList.add(t.children.get(0).contents);
            }
        }else if(t.textOfNode.equals("CALL")){
            getVarsCall(t.children.get(0).contents,tree.get(1));
        }
        ListIterator it = t.children.listIterator();
        while (it.hasNext()) {
            getVarsProc((TreeNode) it.next());
        }
    }
    
        public void clearVarsLoops(List<String> l) {
        ListIterator it = l.listIterator();
       
        while (it.hasNext()) {
            String s = (String) it.next();
         
            if(s.startsWith("p")){
             
                resetDef(s,tree.get(1));
            }else{
                updateNegativeST((String) s);
            }
            
        }
        l.clear();
    }
        
        public void resetDef(String name,TreeNode t){
           
            if(t.textOfNode.equals("PROC_DEFS") && t.children.get(0).children.get(1).contents.equals(name) ){
            
                t.children.get(0).value=("-");
            }else{
                ListIterator it = t.children.listIterator();
                while(it.hasNext()){
                    resetDef(name, (TreeNode) it.next());
                }
            }
        }
    
    public List<String> combineLists(List<String> l1,List<String> l2){
        List<String> newList = new ArrayList<>();
        ListIterator it = l1.listIterator();
        while(it.hasNext()){
            String s = (String) it.next();
           if(l2.contains(s)){
               newList.add(s);
           }
        }
        return newList;
    }
    
    public void addDoubleVariables(List<String> s){
         
        ListIterator it = s.listIterator();
        while(it.hasNext()){
            String s2 = (String) it.next();
            updateST(s2);
        }
    }

    public void checkCondLoopFor(TreeNode t) {
        
        updateST(t.children.get(1).children.get(0).contents);
        updateST(t.children.get(7).children.get(0).contents);
        if(checkST(t.children.get(4).children.get(0).contents).equals("+") && 
                checkST(t.children.get(6).children.get(0).contents).equals("+") && checkST(t.children.get(10).children.get(0).contents).equals("+")){
              //System.out.println("Value Flow error for loop variable with unassigned value");
              //System.exit(0);
        }
        
        
        changeList.clear();
        getVarsLoops(t.children.get(12));
           clearAmps(tree.get(1));
        ArrayList<String> temp = (ArrayList<String>) changeList.clone();
        checkCode(t.children.get(12));
        clearVarsLoops(temp);
        changeList.clear();

    }

    public void checkCondLoopWhile(TreeNode t) {
        checkBool(t.children.get(1));
        changeList.clear();
        getVarsLoops(t.children.get(2));
        clearAmps(tree.get(1));
        ArrayList<String> temp = (ArrayList<String>) changeList.clone();
       
        checkCode(t.children.get(2));
        clearVarsLoops(temp);
        
    }
    public void clearAmps(TreeNode t){
        if(t.value.equals("/")){
            t.value="-";
        }
        ListIterator it = t.children.listIterator();
        while(it.hasNext()){
            clearAmps((TreeNode) it.next());
        }
    }
//done

    public void checkVar(TreeNode t) {

    }
//done

    public void checkNumExp(TreeNode t) {
        if (t.children.get(0).textOfNode.equals("VAR")) {
            if (checkST(t.children.get(0).children.get(0).contents).equals("+")) {
                t.value = "+";

                t.children.get(0).children.get(0).value = "+";
                t.children.get(0).value = "+";
            } else {
                System.out.println("Value Flow error numexp variable " + t.children.get(0).children.get(0).contents
                        + " has no value");
                System.exit(0);
            }
        } else if (t.children.get(0).textOfNode.equals("Digit")) {
            t.value = "+";
        } else if (t.children.get(0).textOfNode.equals("CALC")) {
            checkCalc(t.children.get(0));
            t.value = "+";
        }

    }

    //done
    public void checkCalc(TreeNode t) {
        checkNumExp(t.children.get(1));
        checkNumExp(t.children.get(2));

    }

    public void checkBool(TreeNode t) {
      
        if (t.children.get(0).textOfNode.equals("and")) {
            checkBool(t.children.get(1));
            checkBool(t.children.get(2));

        } else if (t.children.get(0).textOfNode.equals("eq")) {
       
            if (t.children.get(1).textOfNode.equals("NUMEXP")) {
                checkNumExp(t.children.get(1));
                checkNumExp(t.children.get(2));

            } else if (t.children.get(1).textOfNode.equals("BOOL")) {
                checkBool(t.children.get(1));
                checkBool(t.children.get(2));

            } else if (t.children.get(1).textOfNode.equals("VAR")) {
                   if (checkST(t.children.get(1).children.get(0).contents).equals("+")) {
                if(checkST(t.children.get(2).children.get(0).contents).equals("+")){
                    t.children.get(1).value="+";
                    t.children.get(2).value="+";
                    t.children.get(1).children.get(0).value="+";
                        t.children.get(2).children.get(0).value="+";
                }else{
                      System.out.println("Value Flow error bool with variable " + t.children.get(2).children.get(0).contents
                        + " has no value");
                System.exit(0);
                }
            } else {
                System.out.println("Value Flow error bool with variable " + t.children.get(1).children.get(0).contents
                        + " has no value");
                System.exit(0);
            }
            }
        } else if (t.children.get(0).textOfNode.equals("not")) {
            checkBool(t.children.get(1));

        } else if (t.children.get(0).textOfNode.equals("VAR")) {
            if (checkST(t.children.get(0).children.get(0).contents).equals("+")) {
                if(checkST(t.children.get(2).children.get(0).contents).equals("+")){
                    t.children.get(0).value="+";
                    t.children.get(2).value="+";
                    t.children.get(0).children.get(0).value="+";
                        t.children.get(2).children.get(0).value="+";
                }else{
                      System.out.println("Value Flow error bool with variable " + t.children.get(2).children.get(0).contents
                        + " has no value");
                System.exit(0);
                }
            } else {
                System.out.println("Value Flow error bool with variable " + t.children.get(0).children.get(0).contents
                        + " has no value");
                System.exit(0);
            }

        } else if (t.children.get(0).textOfNode.equals("or")) {
            checkBool(t.children.get(1));
            checkBool(t.children.get(2));

        }
    }

    public String checkST(String name) {
        ListIterator it = vTable.listIterator();
        while (it.hasNext()) {
            VNode v = (VNode) it.next();
            if (v.getName().equals(name)) {
                return v.value;
            }
        }
        return " ";
    }

    public void updateST(String name) {
        ListIterator it = vTable.listIterator();
        while (it.hasNext()) {
            VNode v = (VNode) it.next();
            if (v.getName().equals(name)) {
                v.value = "+";
            }
        }

    }

    public void updateNegativeST(String name) {
        ListIterator it = vTable.listIterator();
        while (it.hasNext()) {
            VNode v = (VNode) it.next();
            if (v.getName().equals(name)) {
                v.value = "-";
            }
        }

    }

    public void printTree() {
        if (tree.size() > 1) {
            tree.get(1).printChildrenScope(0);
        } else {
            System.out.println("Tree Empty");
        }

    }

   
}
