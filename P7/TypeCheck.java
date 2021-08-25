
import static java.lang.System.exit;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author jenna
 */
public class TypeCheck {

    private List<TreeNode> tree;
    private List<TableNode> table;
    private List<VNode> vTable;
    private boolean update;
    private boolean error = false;
    private boolean delete;

    public TypeCheck() {
        vTable = new ArrayList<>();

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
    
    

    public void buildVTable() {
        ListIterator it = table.listIterator();
        while (it.hasNext()) {
            TableNode t = (TableNode) it.next();
            if (t.textOfNode.equals("variable") || t.textOfNode.equals("variableFor")) {

                if (!searchVTable(t.vName)) {
                    VNode v = new VNode("u", t.vName, t.scope);
                    vTable.add(v);
                }
            }
        }

    }

    public boolean searchVTable(String name) {
        ListIterator it = vTable.listIterator();
        while (it.hasNext()) {
            VNode v = (VNode) it.next();
            if (v.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void checkTypes() {
        buildVTable();
        //printTree();
        update = true;
        removeOldCode(tree.get(1));
     //  tree.get(1).printDeleteScope(0);
        while (update) {

            update = false;
            checkProg(tree.get(1));
          //  System.out.println("TypeCheck.checkTypes()");
           // printTree();
        }
        checkErrors(tree.get(1));
        if (error == true) {
 
            exit(0);
        }
        deleteDeadCode(tree.get(1));
        removeInstr(tree.get(1));
        
        if(tree.get(1).type.equals("d")){
            tree.remove(1);
        }

    }

    public void checkErrors(TreeNode t) {

        if (t.type != null && t.delete != true) {
            if (t.type.equals("e") || t.type.equals("u")) {
                System.out.println("Type Error: tree node " + t.id + "->" + t.textOfNode + " has type " + t.type + " which is invaild type");
                error = true;
            }
        }

        ListIterator it = t.children.listIterator();
        while (it.hasNext()) {
            checkErrors((TreeNode) it.next());
        }

    }

    public String checkProg(TreeNode t) {

        if (t.children.size() > 1) {
            //2

            if (t.children.get(1).textOfNode.equals("CODE")) {
                String c1 = checkCode(t.children.get(0));
                String c2 = checkCode(t.children.get(1));
                if (c1.equals("c") && c2.equals("c")) {
                    t.type = "c";
                }
            } else {
                String c1 = checkCode(t.children.get(0));
                String c2 = checkProcDef(t.children.get(1));

                if (c1.equals("c") && c2.equals("c")) {
                    t.type = "c";
                }
            }

        } else {
            //1
            String c1 = checkCode(t.children.get(0));
            if (c1.equals("c")) {
                t.type = "c";
            }
        }

        return t.type;
    }

    public String checkCode(TreeNode t) {

        if (t.children.size() > 1) {

            //7
            String c1 = checkInstr(t.children.get(0));

            if (t.children.get(1).textOfNode.equals("CODE")) {
                String c2 = checkCode(t.children.get(1));
                if (c1.equals("c") && c2.equals("c")) {
                    t.type = "c";
                }
            } else {
                String c2 = checkProcDef(t.children.get(1));
                if (c1.equals("c") && c2.equals("c")) {
                    t.type = "c";
                }
            }

        } else {

            if (t.children.get(0).textOfNode.equals("INSTR")) {
                String c1 = checkInstr(t.children.get(0));
                if (c1.equals("c")) {
                    t.type = "c";
                }
            } else if (t.children.get(0).textOfNode.equals("PROC_DEFS")) {
                String c1 = checkProcDef(t.children.get(0));
                if (c1.equals("c")) {
                    t.type = "c";
                }
            }

            //6
        }

        return t.type;

    }

    public String checkProcDef(TreeNode t) {
        String c1 = checkProc(t.children.get(0));
        if (t.children.size() > 1) {
            String c2 = checkProcDef(t.children.get(1));
            //4
            if (c1.equals("c") && c2.equals("c")) {
                t.type = "c";
            }

        } else {
            //3
            if (c1.equals("c")) {
                t.type = "c";
            }
        }

        return t.type;
    }

    public String checkProc(TreeNode t) {
        //5
        t.children.get(0).type = null;
        t.children.get(1).type = null;
        if (checkProg(t.children.get(2)).equals("c")) {
            t.type = "c";
        }
        return t.type;
    }

    public String checkInstr(TreeNode t) {
        if (t.children.get(0).textOfNode.equals("halt")) {
            t.children.get(0).type = null;
            t.type = "c";
        } else if (t.children.get(0).textOfNode.equals("CALL")) {
            if (checkCall(t.children.get(0)).equals("c")) {
                t.type = "c";
            }

        } else if (t.children.get(0).textOfNode.equals("ASSIGN")) {
            if (checkAssign(t.children.get(0)).equals("c")) {
                t.type = "c";
            }

        } else if (t.children.get(0).textOfNode.equals("IO")) {
            if (checkIO(t.children.get(0)).equals("c")) {
                t.type = "c";
            }
        } else if (t.children.get(0).textOfNode.equals("Cond_Loop_For")) {
            if (checkCondLoopFor(t.children.get(0)).equals("c")) {
                t.type = "c";
            }
        } else if (t.children.get(0).textOfNode.equals("Cond_Loop_While")) {
            if (checkCondLoopWhile(t.children.get(0)).equals("c")) {
                t.type = "c";
            }

        } else if (t.children.get(0).textOfNode.equals("Cond_Branch")) {
            if (checkCondBranch(t.children.get(0)).equals("c")) {
                t.type = "c";
            }
        }
        return t.type;
    }

    public String checkIO(TreeNode t) {
        
        if (t.children.get(0).textOfNode.equals("input")) {
          
            // System.out.println("" + t.children.get(1).textOfNode);
            if (checkST(t.children.get(1).children.get(0).contents).equals("s")) {
                
                t.type = "e";
                t.children.get(1).type = "s";
                t.children.get(1).children.get(0).type = "s";
                //  t.type = "c";
            } else {
                updateST(t.children.get(1).children.get(0).contents, "n");
                t.children.get(1).type = "n";
                t.children.get(1).children.get(0).type = "n";
                t.type = "c";
            }
        } else {
            
            if (checkST(t.children.get(1).children.get(0).contents).equals("n") || checkST(t.children.get(1).children.get(0).contents).equals("s")) {
                t.type = "c";
                String type = checkST(t.children.get(1).children.get(0).contents);
                      t.children.get(1).type = type;
                t.children.get(1).children.get(0).type = type;
            } else {
              
                t.type = "c";
                t.children.get(1).type = "o";
                t.children.get(1).children.get(0).type = "o";
                updateST(t.children.get(1).children.get(0).contents, "o");
            }
        }
        t.children.get(0).type = null;
        return t.type;
    }

    public String checkCall(TreeNode t) {

        //16
        if (t.children.get(0).textOfNode.equals("UserDefinedNameCall")) {
            t.type = "c";
        }
        t.children.get(0).type = null;
        return t.type;
    }

    public String checkAssign(TreeNode t) {
        if (t.children.get(2).textOfNode.equals("VAR")) {
            if (checkST(t.children.get(0).children.get(0).contents).equals("n") && checkST(t.children.get(2).children.get(0).contents).equals("s")) {
                t.type = "e";
            } else if (checkST(t.children.get(0).children.get(0).contents).equals("n") && !checkST(t.children.get(2).children.get(0).contents).equals("s")) {
                updateST(t.children.get(2).children.get(0).contents, "n");
                updateST(t.children.get(0).children.get(0).contents, "n");
                t.children.get(2).children.get(0).type = "n";
                t.children.get(0).children.get(0).type = "n";
                t.children.get(2).type = "n";
                t.children.get(0).type = "n";
                t.type = "c";
            } else if (checkST(t.children.get(0).children.get(0).contents).equals("s") && !checkST(t.children.get(2).children.get(0).contents).equals("n")) {
                updateST(t.children.get(2).children.get(0).contents, "s");
                updateST(t.children.get(0).children.get(0).contents, "s");
                t.children.get(2).children.get(0).type = "s";
                t.children.get(0).children.get(0).type = "s";
                t.children.get(2).type = "s";
                t.children.get(0).type = "s";
                t.type = "c";
            } else if (checkST(t.children.get(2).children.get(0).contents).equals("n") && !checkST(t.children.get(0).children.get(0).contents).equals("s")) {
                t.type = "c";
                updateST(t.children.get(2).children.get(0).contents, "n");
                updateST(t.children.get(0).children.get(0).contents, "n");
                t.children.get(2).children.get(0).type = "n";
                t.children.get(0).children.get(0).type = "n";
                t.children.get(2).type = "n";
                t.children.get(0).type = "n";
            } else if (checkST(t.children.get(2).children.get(0).contents).equals("s") && !checkST(t.children.get(0).children.get(0).contents).equals("n")) {
                t.type = "c";
                updateST(t.children.get(2).children.get(0).contents, "s");
                updateST(t.children.get(0).children.get(0).contents, "s");
                t.children.get(2).children.get(0).type = "s";
                t.children.get(0).children.get(0).type = "s";
                t.children.get(2).type = "s";
                t.children.get(0).type = "s";
            } else {
                t.type = "c";
                updateST(t.children.get(2).children.get(0).contents, "o");
                updateST(t.children.get(0).children.get(0).contents, "o");
                t.children.get(2).children.get(0).type = "o";
                t.children.get(0).children.get(0).type = "o";
                t.children.get(2).type = "o";
                t.children.get(0).type = "o";

            }
        } else if (t.children.get(2).textOfNode.equals("NUMEXP")) {
            if (checkST(t.children.get(0).children.get(0).contents).equals("s")) {
                t.type = "e";
                t.children.get(0).children.get(0).type = "s";
                t.children.get(0).type = "s";

            } else if (checkNumExp(t.children.get(2)).equals("n")) {
                t.children.get(0).children.get(0).type = "n";
                t.children.get(0).type = "n";
                updateST(t.children.get(0).children.get(0).contents, "n");
                t.type = "c";
            }

        } else if (t.children.get(2).textOfNode.equals("String")) {
            t.children.get(2).type = null;
            if (checkST(t.children.get(0).children.get(0).contents).equals("n")) {
                t.type = "e";
            } else {
                updateST(t.children.get(0).children.get(0).contents, "s");
                t.children.get(0).children.get(0).type = "s";
                t.children.get(0).type = "s";
                t.type = "c";
            }

        }
        t.children.get(1).type = null;
        return t.type;
    }

    public String checkCondBranch(TreeNode t) {
        t.children.get(0).type = null;
        t.children.get(2).type = null;

        if (t.children.size() > 4) {
            String c1 = checkBool(t.children.get(1));
            t.children.get(4).type = null;
            if (c1.equals("b") || c1.equals("f")) {
                String c2 = checkCode(t.children.get(3));
                String c3 = checkCode(t.children.get(5));
                if (c2.equals("c") && c3.equals("c")) {
                    t.type = "c";
                }
            }
        } else {
            String c1 = checkBool(t.children.get(1));
            if (c1.equals("b") || c1.equals("f")) {

                if (checkCode(t.children.get(3)).equals("c")) {
                    t.type = "c";
                }
            }

        }
        return t.type;
    }

    public String checkCondLoopFor(TreeNode t) {
        if (checkST(t.children.get(1).children.get(0).contents).equals("s")) {
            t.type = "e";
        } else if (checkST(t.children.get(4).children.get(0).contents).equals("s")) {
            t.type = "e";
        } else if (checkST(t.children.get(6).children.get(0).contents).equals("s")) {
            t.type = "e";
        } else if (checkST(t.children.get(7).children.get(0).contents).equals("s")) {
            t.type = "e";
        } else if (checkST(t.children.get(10).children.get(0).contents).equals("s")) {
            t.type = "e";
        } else {
            if (checkCode(t.children.get(12)).equals("c")) {
                t.type = "c";
                updateST(t.children.get(1).children.get(0).contents, "n");
                updateST(t.children.get(4).children.get(0).contents, "n");
                updateST(t.children.get(6).children.get(0).contents, "n");
                updateST(t.children.get(7).children.get(0).contents, "n");
                updateST(t.children.get(10).children.get(0).contents, "n");
                t.children.get(1).children.get(0).type = "n";
                t.children.get(1).type = "n";
                t.children.get(4).children.get(0).type = "n";
                t.children.get(4).type = "n";
                t.children.get(6).children.get(0).type = "n";
                t.children.get(6).type = "n";
                t.children.get(7).children.get(0).type = "n";
                t.children.get(7).type = "n";
                t.children.get(10).children.get(0).type = "n";
                t.children.get(10).type = "n";

            }
        }

        t.children.get(0).type = null;
        t.children.get(2).type = null;
        t.children.get(3).type = null;
        t.children.get(5).type = null;
        t.children.get(8).type = null;
        t.children.get(9).type = null;
        t.children.get(11).type = null;

        return t.type;
    }

    public String checkCondLoopWhile(TreeNode t) {
        t.children.get(0).type = null;

        String c1 = checkBool(t.children.get(1));
        if (c1.equals("b") || c1.equals("f")) {
            if (checkCode(t.children.get(2)).equals("c")) {
                t.type = "c";
            }
        } else {

        }
        return t.type;
    }

    public String checkVar(TreeNode t) {
        if (checkST(t.children.get(0).contents).equals("u")) {
            updateST(t.children.get(0).contents, "o");
            t.children.get(0).type = "o";
            t.type = "o";
        }
        return t.type;
    }

    public String checkNumExp(TreeNode t) {
        if (t.children.get(0).textOfNode.equals("VAR")) {
            if (checkST(t.children.get(0).children.get(0).contents).equals("s")) {
                t.type = "e";
            } else {
                updateST(t.children.get(0).children.get(0).contents, "n");
                t.children.get(0).type = "n";
                t.children.get(0).children.get(0).type = "n";
                t.type="n";
            }
        } else if (t.children.get(0).textOfNode.equals("Digit")) {
            t.type = "n";
            t.children.get(0).type = "n";
        } else if (t.children.get(0).textOfNode.equals("CALC")) {
            if (checkCalc(t.children.get(0)).equals("n")) {
                t.type = "n";
            }
        }
        return t.type;
    }

    public String checkCalc(TreeNode t) {
        t.children.get(0).type = null;
        String c1 = checkNumExp(t.children.get(1));
        String c2 = checkNumExp(t.children.get(2));
        if (c1.equals("n") && c2.equals("n")) {
            t.type = "n";
        }
        return t.type;
    }

    //fix
    public String checkBool(TreeNode t) {

        if (t.children.get(0).textOfNode.equals("and")) {
            String c1 = checkBool(t.children.get(1));
            String c2 = checkBool(t.children.get(2));
            if (c1.equals("f") || c2.equals("f")) {
                t.type = "f";

            } else {
                t.type = "b";
            }

            t.children.get(0).type = null;

        } else if (t.children.get(0).textOfNode.equals("eq")) {

            t.children.get(0).type = null;
            if (t.children.get(1).textOfNode.equals("NUMEXP")) {
                String c1 = checkNumExp(t.children.get(1));
                String c2 = checkNumExp(t.children.get(2));
                if (c1.equals("n") && c2.equals("n")) {
                    t.type = "b";
                }
            } else if (t.children.get(1).textOfNode.equals("BOOL")) {
                String c1 = checkBool(t.children.get(1));
                String c2 = checkBool(t.children.get(2));
                if ((c1.equals("f") || c1.equals("b")) && (c2.equals("f") || c2.equals("b"))) {
                    t.type = "b";
                }

            } else if (t.children.get(1).textOfNode.equals("VAR")) {
                if (checkST(t.children.get(1).children.get(0).contents).equals("n")) {
                    if (checkST(t.children.get(2).children.get(0).contents).equals("s")) {
                        t.type = "f";
                        t.children.get(2).children.get(0).type = "s";
                        t.children.get(2).type = "s";
                    } else {
                        updateST(t.children.get(2).children.get(0).contents, "n");
                        t.children.get(2).children.get(0).type = "n";
                        t.children.get(2).type = "n";
                        t.type = "b";

                    }
                    t.children.get(1).children.get(0).type = "n";
                    t.children.get(1).type = "n";
                }else if (checkST(t.children.get(2).children.get(0).contents).equals("n")) {
                    if (checkST(t.children.get(1).children.get(0).contents).equals("s")) {
                        t.type = "f";
                        t.children.get(1).children.get(0).type = "s";
                        t.children.get(1).type = "s";
                    } else {
                        updateST(t.children.get(1).children.get(0).contents, "n");
                        t.children.get(1).children.get(0).type = "n";
                        t.children.get(1).type = "n";
                        t.type = "b";

                    }
                    t.children.get(2).children.get(0).type = "n";
                    t.children.get(2).type = "n";
                } else if (checkST(t.children.get(1).children.get(0).contents).equals("s")) {

                    if (checkST(t.children.get(2).children.get(0).contents).equals("n")) {
                        t.type = "f";
                        t.children.get(2).children.get(0).type = "n";
                        t.children.get(2).type = "n";
                    } else {
                        updateST(t.children.get(2).children.get(0).contents, "s");
                        t.children.get(2).children.get(0).type = "s";
                        t.children.get(2).type = "s";
                        t.type = "b";

                    }
                    t.children.get(1).children.get(0).type = "s";
                    t.children.get(1).type = "s";

                }  else if (checkST(t.children.get(2).children.get(0).contents).equals("s")) {

                    if (checkST(t.children.get(1).children.get(0).contents).equals("n")) {
                        t.type = "f";
                        t.children.get(1).children.get(0).type = "n";
                        t.children.get(1).type = "n";
                    } else {
                        updateST(t.children.get(1).children.get(0).contents, "s");
                        t.children.get(1).children.get(0).type = "s";
                        t.children.get(1).type = "s";
                        t.type = "b";

                    }
                    t.children.get(2).children.get(0).type = "s";
                    t.children.get(2).type = "s";

                } else {
                    
                    updateST(t.children.get(2).children.get(0).contents, "o");
                    t.children.get(2).children.get(0).type = "o";
                    t.children.get(2).type = "o";
                    updateST(t.children.get(1).children.get(0).contents, "o");
                    t.children.get(1).children.get(0).type = "o";
                    t.children.get(1).type = "o";
                    t.type = "b";

                }
            }

        } else if (t.children.get(0).textOfNode.equals("not")) {
            String c1 = checkBool(t.children.get(1));
            if (c1.equals("b") || c1.equals("f")) {
                t.type = "b";

            }
            t.children.get(0).type = null;

        } else if (t.children.get(0).textOfNode.equals("VAR")) {
            if (checkST(t.children.get(0).children.get(0).contents).equals("s")) {
                t.type = "e";
                if (checkST(t.children.get(2).children.get(0).contents).equals("s")) {
                    t.children.get(2).type = "s";
                    t.children.get(2).children.get(0).type = "s";
                } else {
                    t.children.get(2).type = "n";
                    t.children.get(2).children.get(0).type = "n";
                }
                t.children.get(0).children.get(0).type = "s";

                t.children.get(0).type = "s";

            } else if (checkST(t.children.get(2).children.get(0).contents).equals("s")) {
                t.type = "e";
                t.children.get(0).children.get(0).type = "n";
                t.children.get(2).children.get(0).type = "s";
                t.children.get(0).type = "n";
                t.children.get(2).type = "s";
            } else {
                updateST(t.children.get(2).children.get(0).contents, "n");
                updateST(t.children.get(0).children.get(0).contents, "n");
                t.children.get(0).children.get(0).type = "n";
                t.children.get(2).children.get(0).type = "n";
                t.children.get(0).type = "n";
                t.children.get(2).type = "n";
                t.type = "b";
            }

            t.children.get(1).type = null;

        } else if (t.children.get(0).textOfNode.equals("or")) {
            String c1 = checkBool(t.children.get(1));
            String c2 = checkBool(t.children.get(2));
            if (c1.equals("u") || c2.equals("u")) {
                t.type = "u";

            } else {
                t.type = "b";
            }
            t.children.get(0).type = null;
        }

        return t.type;
    }

    public String checkST(String name) {
        ListIterator it = vTable.listIterator();
        while (it.hasNext()) {
            VNode v = (VNode) it.next();
            if (v.getName().equals(name)) {
                return v.getType();
            }
        }
        return "e";
    }

    public void updateST(String name, String type) {
        ListIterator it = vTable.listIterator();
        while (it.hasNext()) {
            VNode v = (VNode) it.next();
            if (v.getName().equals(name)) {
                if (v.getType().equals(type)) {

                } else {
                    //System.out.println(name + " "+ type );
                    update = true;
                    v.setType(type);
                }

            }
        }
    }

    public void printTree() {
        if(tree.size()>1){
                  tree.get(1).printChildrenScope(0);
        }else{
            System.out.println("Tree Empty");
        }
  

    }

    public void deleteDeadCode(TreeNode t) {
        if (t.textOfNode.equals("Cond_Branch")) {
            if (t.children.size() > 4) {

                if (t.children.get(1).type.equals("f")) {
                   
                    t.children.remove(4);
                    t.children.remove(3);
                    t.children.remove(2);
                    t.children.remove(1);
                    t.children.remove(0);

                } else {
                    if (t.children.get(1).children.get(0).textOfNode.equals("not")) {
                        if (t.children.get(1).children.get(1).type.equals("f")) {
                           
                            t.children.remove(5);
                            t.children.remove(4);
                            t.children.remove(2);
                            t.children.remove(1);
                            t.children.remove(0);
                        }

                    }
                }

            } else {
              if (t.children.get(1).type.equals("f")) {

                    t.type = "d";
                    t.delete = true;
                    t.children.clear();
                }else   if (t.children.get(1).children.get(0).textOfNode.equals("not") && t.children.get(1).children.get(1).type.equals("f")) {
                    t.children.remove(2);
                    t.children.remove(1);
                    t.children.remove(0);

                }

            }

        } else if (t.textOfNode.equals("Cond_Loop_While")) {
            if (t.children.get(1).children.get(0).textOfNode.equals("not") && t.children.get(1).children.get(1).type.equals("f")) {
                System.out.println("Warning! infinite Loop!");
            }

            if (t.children.get(1).type.equals("f")) {

                t.type = "d";
                t.delete = true;
                //t.children=null;
            }

        } else if (t.textOfNode.equals("Cond_Loop_For")) {
            if (t.children.get(4).children.get(0).contents.equals(t.children.get(6).children.get(0).contents)) {
           
                t.type = "d";
                t.delete = true;
            }

        }

        for (int i = 0; i < t.children.size(); i++) {
            deleteDeadCode(t.children.get(i));

        }


    }

    public void removeInstr(TreeNode t) {
        if (t.terminal != true) {
            if (!t.children.isEmpty()) {

                for (int i = t.children.size(); i > 0; i--) {
                    if (t.children.get(i - 1).type != null) {
                        if (t.children.get(i - 1).type.equals("d")) {

                            t.children.remove(i - 1);
                        } else {
                            removeInstr(t.children.get(i - 1));
                            if (t.children.get(i - 1).type.equals("d")) {
                                t.children.remove(i - 1);
                            }

                        }
                        if (t.children.isEmpty()) {
                            t.type = "d";
                        }
                    }

                }

            } else {

                t.type = "d";
            }
        } else {

        }

    }
    
    public void removeOldCode(TreeNode t){
        if(t.textOfNode.equals("PROC_DEFS") && t.delete==true){
            if(t.children.size()>1){
                removeOldCode(t.children.get(1));
                if(t.children.get(1).delete==false){
                    t.children.remove(0);
                    t.children.addAll(t.children.get(0).children);
                     t.id=t.children.get(0).id;
                    t.children.remove(0);
                
                    t.delete=false;
                }
            }else{
                   t.children.remove(0);
            }
         
        }else{
            for(int i =0;i<t.children.size();i++){
                removeOldCode(t.children.get(i));
                if(t.children.get(i).textOfNode.equals("PROC_DEFS") && t.children.get(i).delete==true){
                 
                    t.children.remove(i);
                }
            }
        }
        
    }

}
