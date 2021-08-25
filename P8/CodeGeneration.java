
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author jenna
 */
public class CodeGeneration {

    private List<TreeNode> tree;
    private List<TableNode> table;
    private List<VNode> vTable;
    private String endLabel = "FINISH";
    private List<String> code = new ArrayList<>();
    private int labelNum = 0;
    private int ifNum = 0;
    private List<String> labels = new ArrayList<>();

    public CodeGeneration() {
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

    public void generateCode() {
   
        checkProg(tree.get(1), code);
        code.add("END");
        deleteLabels();
    }

    public void printCode() {
        ListIterator it = code.listIterator();
        int lineNum = 0;
        while (it.hasNext()) {
            System.out.println(++lineNum + " " + it.next());
        }
        // System.out.println(++lineNum+" END");

    }

    //done
    public void checkProg(TreeNode t, List<String> code) {
        String eLabel = "L" + labelNum++;
        if (t.children.size() > 1) {
            if (t.children.get(1).textOfNode.equals("CODE")) {
                checkCode(t.children.get(0), code, eLabel);
                checkCode(t.children.get(1), code, eLabel);

            } else {
                checkCode(t.children.get(0), code, eLabel);
                code.add("GOTO " + eLabel);
                checkProcDef(t.children.get(1), code, eLabel);
            }

        } else {
            checkCode(t.children.get(0), code, eLabel);
        }
        code.add(eLabel + ":");

    }

    //done
    public void checkCode(TreeNode t, List<String> code, String endLabel) {

        if (t.children.size() > 1) {
            checkInstr(t.children.get(0), code, endLabel);
            if (t.children.get(1).textOfNode.equals("CODE")) {
                checkCode(t.children.get(1), code, endLabel);
            } else {
                code.add("GOTO " + endLabel);
                checkProcDef(t.children.get(1), code, endLabel);
            }

        } else {

            if (t.children.get(0).textOfNode.equals("INSTR")) {
                checkInstr(t.children.get(0), code, endLabel);

            } else if (t.children.get(0).textOfNode.equals("PROC_DEFS")) {
                code.add("GOTO " + endLabel);
                checkProcDef(t.children.get(0), code, endLabel);
            }

        }

    }

    //done
    public void checkProcDef(TreeNode t, List<String> code, String endLabel) {

        checkProc(t.children.get(0), code, endLabel);
        code.add("RETURN");
        if (t.children.size() > 1) {
            checkProcDef(t.children.get(1), code, endLabel);

        }

    }

    //done
    public void checkProc(TreeNode t, List<String> code, String endLabel) {
        code.add(t.children.get(1).contents + ":");
        checkProg(t.children.get(2), code);
    }

    //done
    public void checkInstr(TreeNode t, List<String> code, String endLabel) {
        if (t.children.get(0).textOfNode.equals("halt")) {
            code.add("STOP");
        } else if (t.children.get(0).textOfNode.equals("CALL")) {
            checkCall(t.children.get(0), code);
        } else if (t.children.get(0).textOfNode.equals("ASSIGN")) {
            checkAssign(t.children.get(0), code);
        } else if (t.children.get(0).textOfNode.equals("IO")) {
            checkIO(t.children.get(0), code);
        } else if (t.children.get(0).textOfNode.equals("Cond_Loop_For")) {
            checkCondLoopFor(t.children.get(0), code, endLabel);
        } else if (t.children.get(0).textOfNode.equals("Cond_Loop_While")) {
            checkCondLoopWhile(t.children.get(0), code, endLabel);

        } else if (t.children.get(0).textOfNode.equals("Cond_Branch")) {
            checkCondBranch(t.children.get(0), code, endLabel);

        }

    }
//done

    public void checkIO(TreeNode t, List<String> code) {
        if (t.children.get(0).textOfNode.equals("input")) {
            if (t.children.get(1).type.equals("s")) {
                //  code.add("INPUT " + "$" + t.children.get(1).children.get(0).contents.toUpperCase());
                code.add("INPUT " + t.children.get(1).children.get(0).contents.toUpperCase() + "$");
            } else {

                code.add("INPUT \" \" ;" + t.children.get(1).children.get(0).contents.toUpperCase());
            }

        } else {
            if (t.children.get(1).type.equals("s")) {
                //   code.add("PRINT " + "$" + t.children.get(1).children.get(0).contents.toUpperCase());
                code.add("PRINT " + t.children.get(1).children.get(0).contents.toUpperCase() + "$");
            } else {

                code.add("PRINT " + t.children.get(1).children.get(0).contents.toUpperCase());
            }
        }

    }

    //done
    public void checkCall(TreeNode t, List<String> code) {
        code.add("GOSUB " + t.children.get(0).contents);
        // findAndCheckProc(t.children.get(0).contents, tree.get(1));
    }

    //done
    public void checkAssign(TreeNode t, List<String> code) {
        String s = "LET " + checkVar(t.children.get(0).children.get(0)) + " = ";

        if (t.children.get(2).textOfNode.equals("VAR")) {
            s = s + checkVar(t.children.get(2).children.get(0));
        } else if (t.children.get(2).textOfNode.equals("NUMEXP")) {
            String exp = checkNumExp(t.children.get(2));
          
            s = s + exp;
        } else if (t.children.get(2).textOfNode.equals("String")) {
            s = s + t.children.get(2).contents;
        }
        code.add(s);
    }

    public void checkCondBranch(TreeNode t, List<String> code, String endLabel) {

        if (t.children.size() > 4) {
            int label1 = labelNum++;
            int label2 = labelNum++;
            int label3 = labelNum++;
            // code.add("IF " + checkBool(t.children.get(1),"L"+label1,"L"+label2) + " THEN L" + label1);
            String b1 = checkBool(t.children.get(1), "L" + label1, "L" + label2);
            // code.add("GOTO L" + label2);
            if (!b1.equals(" ")) {
                code.add("IF " + b1 + " THEN L" + label1);
                code.add("GOTO L" + label2);
            }

            labels.add("L" + label1);
            code.add("L" + label1 + ":");
            checkCode(t.children.get(3), code, endLabel);
            code.add("GOTO L" + label3);
            labels.add("L" + label2);
            code.add("L" + label2 + ":");
            checkCode(t.children.get(5), code, endLabel);
            code.add("L" + label3 + ":");
            labels.add("L" + label3);

        } else if (t.children.get(0).textOfNode.equals("CODE")) {
            checkCode(t.children.get(0), code, endLabel);
        } else {
            int label1 = labelNum++;
            int label2 = labelNum++;
            String b1 = checkBool(t.children.get(1), "L" + label1, "L" + label2);
            if (!b1.equals(" ")) {
                code.add("IF " + b1 + " THEN L" + label1);
                code.add("GOTO L" + label2);
            }

            code.add("L" + label1 + ":");
            checkCode(t.children.get(3), code, endLabel);
            code.add("L" + label2 + ":");
            labels.add("L" + label2);
            labels.add("L" + label1);
        }

    }

    //done
    public void checkIfAnd(TreeNode t, List<String> code, String labelIf, String labelElse) {
        int label1 = labelNum++;
        String b1 = checkBool(t.children.get(1), "L" + label1, labelElse);
        if (!b1.equals(" ")) {
            code.add("IF " + b1 + " THEN L" + label1);
            code.add("GOTO " + labelElse);
        }
        labels.add("L" + label1);
        code.add("L" + label1 + ":");

        String b2 = checkBool(t.children.get(2), labelIf, labelElse);

        if (!b2.equals(" ")) {
            code.add("IF " + b2 + " THEN " + labelIf);
            code.add("GOTO " + labelElse);
        }

    }

    //done
    public void checkIfOr(TreeNode t, List<String> code, String labelIf, String labelElse) {
        int label1 = labelNum++;
        String b1 = checkBool(t.children.get(1), labelIf, "L" + label1);
        if (!b1.equals(" ")) {
            code.add("IF " + b1 + " THEN " + labelIf);
        }

        labels.add("L" + label1);
        code.add("L" + label1 + ":");
        String b2 = checkBool(t.children.get(2), labelIf, labelElse);
        if (!b2.equals(" ")) {
            code.add("IF " + b2 + " THEN " + labelIf);
            code.add("GOTO " + labelElse);
        }

    }

    //done
    public void checkIfNot(TreeNode t, List<String> code, String labelIf, String labelElse) {
        String b1 = checkBool(t.children.get(1), labelElse, labelIf);
        if (!b1.equals(" ")) {
            code.add("IF " + b1 + " THEN " + labelElse);
        }

    }

    //done
    public String checkBool(TreeNode t, String labelIf, String labelElse) {

        if (t.children.get(0).textOfNode.equals("and")) {
            checkIfAnd(t, code, labelIf, labelElse);
            return " ";
        } else if (t.children.get(0).textOfNode.equals("eq")) {

            if (t.children.get(1).textOfNode.equals("NUMEXP")) {
                String a = checkNumExp(t.children.get(1));
                String b = checkNumExp(t.children.get(2));

                return a + " = " + b;
            } else if (t.children.get(1).textOfNode.equals("BOOL")) {
                return checkEqBool(t);
            } else if (t.children.get(1).textOfNode.equals("VAR")) {
                String a = checkVar(t.children.get(1).children.get(0));
                String b = checkVar(t.children.get(2).children.get(0));

                return a + " = " + b;
            }
        } else if (t.children.get(0).textOfNode.equals("not")) {
            checkIfNot(t, code, labelIf, labelElse);
            return " ";
        } else if (t.children.get(0).textOfNode.equals("VAR")) {
            String a = checkVar(t.children.get(0).children.get(0));
            String b = checkVar(t.children.get(2).children.get(0));
            if (t.children.get(1).textOfNode.equals("compLess")) {
                return a + " < " + b;
            } else {
                return a + " > " + b;
            }

        } else if (t.children.get(0).textOfNode.equals("or")) {
            checkIfOr(t, code, labelIf, labelElse);
            return " ";
        }
        return "";
    }

    public String checkEqBool(TreeNode t) {

        String c1Name = "T" + ifNum++;
        String c2Name = "T" + ifNum++;
        String c1IF = "L" + labelNum++;
        String c1Else = "L" + labelNum++;
        String c2IF = "L" + labelNum++;
        String c2Else = "L" + labelNum++;
        String b2Label = "L" + labelNum++;
        String endLabel = "L" + labelNum++;

//        code.add("LET " + c1Name + " = 2");
//        code.add("LET " + c2Name + " = 2");

        String b1 = checkBool(t.children.get(1), c1IF, c1Else);
        if (!b1.equals(" ")) {
            code.add("IF " + b1 + " THEN " + c1IF);
            code.add("GOTO " + c1Else);
        }
        code.add(c1IF + ":");
        code.add("LET " + c1Name + " = 0");
        code.add("GOTO " + b2Label);

        code.add(c1Else + ":");
        code.add("LET " + c1Name + " = 1");
        code.add("GOTO " + b2Label);

        code.add(b2Label + ":");
        String b2 = checkBool(t.children.get(2), c2IF, c2Else);
        if (!b2.equals(" ")) {
            code.add("IF " + b2 + " THEN " + c2IF);
            code.add("GOTO " + c2Else);
        }

        code.add(c2IF + ":");
        code.add("LET " + c2Name + " = 0");
        code.add("GOTO " + endLabel);

        code.add(c2Else + ":");
        code.add("LET " + c2Name + " = 1");

        code.add(endLabel + ":");
        return c1Name + "=" + c2Name;

    }

    public void checkCondLoopFor(TreeNode t, List<String> code, String endLabel) {
        code.add("LET " + t.children.get(1).children.get(0).contents.toUpperCase() + " = 0");
        int label1 = labelNum++;
        int label2 = labelNum++;
        int label3 = labelNum++;
        labels.add("L" + label1);
        labels.add("L" + label2);
        labels.add("L" + label3);
        code.add("L" + label1 + ":");
        code.add("IF " + t.children.get(4).children.get(0).contents.toUpperCase() + " < " + t.children.get(6).children.get(0).contents.toUpperCase() + " THEN L" + label2);
        code.add("GOTO L" + label3);
        code.add("L" + label2 + ":");

        checkCode(t.children.get(12), code, endLabel);
                code.add("LET " + t.children.get(7).children.get(0).contents.toUpperCase() + " = (" + t.children.get(10).children.get(0).contents.toUpperCase() + " +1)");
        code.add("GOTO L" + label1);
        code.add("L" + label3 + ":");
    }

    public void checkCondLoopWhile(TreeNode t, List<String> code, String endLabel) {
        int label1 = labelNum++;
        int label2 = labelNum++;
        int label3 = labelNum++;
        labels.add("L" + label1);
        labels.add("L" + label2);
        labels.add("L" + label3);
        code.add("L" + label1 + ":");
        //code.add("IF " + checkBool(t.children.get(1), "L" + label2, "L" + label3) + " THEN L" + label2);

        String b1 = checkBool(t.children.get(1), "L" + label2, "L" + label3);
        if (!b1.equals(" ")) {
            code.add("IF " + b1 + " THEN L" + label2);
            code.add("GOTO L" + label3);
        }
        //code.add("GOTO L" + label3);
        code.add("L" + label2 + ":");
        checkCode(t.children.get(2), code, endLabel);
        code.add("GOTO L" + label1);
        code.add("L" + label3 + ":");

    }

//done
    public String checkVar(TreeNode t) {
        if (t.type.equals("s")) {
            // return "$" + t.contents.toUpperCase();
            return t.contents.toUpperCase() + "$";
        } else {

            return t.contents.toUpperCase();
        }
    }
//done

    public String checkNumExp(TreeNode t) {
        if (t.children.get(0).textOfNode.equals("VAR")) {
            return checkVar(t.children.get(0).children.get(0));
        } else if (t.children.get(0).textOfNode.equals("Digit")) {
            return t.children.get(0).contents;
        } else if (t.children.get(0).textOfNode.equals("CALC")) {
            return checkCalc(t.children.get(0));

        }
        return "";

    }

    //done
    public String checkCalc(TreeNode t) {
        if (true) {
            String t1Label = "T"+ifNum++;
            String t2Label = "T"+ifNum++;
            code.add("LET " + t1Label +" = " + checkNumExp(t.children.get(1)));
            code.add("LET " + t2Label +" = " + checkNumExp(t.children.get(2)));
              if (t.children.get(0).textOfNode.equals("add")) {
                return  t1Label + "+" + t2Label;
            } else if (t.children.get(0).textOfNode.equals("sub")) {
                return t1Label + "-" + t2Label ;
            } else {
                return  t1Label + "*" + t2Label ;
            }
            
            
        } else if (t.children.get(1).children.get(0).textOfNode.equals("CALC")) {

        } else if (t.children.get(2).children.get(0).textOfNode.equals("CALC")) {

        } else {
            if (t.children.get(0).textOfNode.equals("add")) {
                return "(" + checkNumExp(t.children.get(1)) + "+" + checkNumExp(t.children.get(2)) + ")";
            } else if (t.children.get(0).textOfNode.equals("sub")) {
                return "(" + checkNumExp(t.children.get(1)) + "-" + checkNumExp(t.children.get(2)) + ")";
            } else {
                return "(" + checkNumExp(t.children.get(1)) + "*" + checkNumExp(t.children.get(2)) + ")";
            }
        }
        return "";

    }

    //done
    public void printTree() {
        if (tree.size() > 1) {
            tree.get(1).printChildrenScope(0);
        } else {
            System.out.println("Tree Empty");
        }

    }

    public void deleteLabels() {
        moveLabels();

        for (int i = 0; i < code.size(); i++) {
            String line = (String) code.get(i);
            while (line.contains(":")) {
                int index = line.indexOf(":");
                code.add(i, line.substring(index + 1));
                code.remove(i + 1);
                replaceLabelWithNum(line.substring(0, index), "" + (i + 1));
                line = code.get(i);
            }
        }

    }

    public void replaceLabelWithNum(String label, String num) {

        for (int i = 0; i < code.size(); i++) {
            String line = (String) code.get(i);

            if (line.endsWith(label)) {

                line = line.replace(label, num);
                code.add(i, line);
                code.remove(i + 1);
            }
        }

    }

    public void moveLabels() {
        for (int i = 0; i < code.size(); i++) {
            String line = (String) code.get(i);
            if (line.contains(":")) {
                //replaceLabelWithNum(line.replace(":", ""), "" + (i + 1));
                boolean change = true;
                while (change) {
                    code.remove(i);
                    String next = code.get(i);
                    line = line + next;
                    code.add(i, line);
                    code.remove(i + 1);
                    if (!next.contains(":")) {
                        change = false;
                    }
                }

                //  String newLine = line + code.get(i);
            }
        }
    }

}
