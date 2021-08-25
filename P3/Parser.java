
import static java.lang.System.exit;
import java.util.ArrayList;

import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author jenna
 */
public class Parser {

    public List<Node> input;
    public String[] terminals = {"halt", "semicolon", "openBrace", "closeBrace", "openBracket", "closeBracket", "compGreater", "compLess", "assignmentOperator", "comma", "Digit", "String", "variable", "and", "or", "not", "add", "sub", "mult", "if", "then", "else", "while", "for", "input", "output", "halt", "proc"};
    public int count = 0;
    public ListIterator<Node> it;
    public int index = 0;
    public int codeExtra = 0;

    public int tab = 0;

    public List<TreeNode> tree = new ArrayList<TreeNode>();
    public List<TableNode> table = new ArrayList<TableNode>();

    public Parser(List<Node> tokens) {
        input = tokens;
        // System.out.println("Parser.<init>()"+input.size());
        it = input.listIterator();

//        if (start()) {
//            printSyntaxTree();
//        }

    }

    public void printSyntaxTree() {
        tree.get(1).printChildren(0);
    }

    //Start
    public boolean start() {
        // System.out.println("In start");
        TreeNode s = new TreeNode();
        s.id = -1;
        tree.add(s);
        if (it.hasNext()) {
            GNode a = A();
            if (a.status && it.hasNext() != true) {
                tree.add(a.n);
                System.out.println("Parser finished with no errors");
                return true;
            } else {
                System.out.println("Parser finished with errors");
                return false;
            }

        } else {

            System.out.println("Parser finished with errors");
            return false;
        }

    }

    //Prog
    public GNode A() {
        int id = index;
        index++;
        // System.out.println("In A(Prog)");
        GNode b = B();

        GNode o = O();

        // System.out.println("B:" + b);
        // System.out.println("O:" + o);
        if (b.status && o.status) {
            TreeNode t = new TreeNode(id, table, false, "PROG");
            t.addChild(b.n);
            t.addChild(o.n);
            GNode g = new GNode(true);
            g.n = t;
            return g;
        } else {
            return new GNode(false);
        }

    }

    //Code
    public GNode B() {
        int id = index;
        index++;
        // System.out.println("In B(Code)");
        GNode e = E();

        GNode p = P();
        if (e.status && p.status) {
            TreeNode t = new TreeNode(id, table, false, "CODE");
            t.addChild(e.n);
            t.addChild(p.n);
            GNode g = new GNode(true);
            g.n = t;

            return g;
        } else {
            return new GNode(false);
        }
    }

    //Proc_Defs
    public GNode C() {
        int id = index;
        index++;
        //System.out.println("In C(Prog Defs)");
        // boolean d = D();

        if (it.hasNext() != true) {
            return new GNode(false);
        } else {
            GNode d = D();
            if (d.status) {
                GNode q = Q();
                if (q.status) {
                    TreeNode t = new TreeNode(id, table, false, "PROC_DEFS");
                    t.addChild(d.n);
                    t.addChild(q.n);
                    GNode g = new GNode(true);
                    g.n = t;

                    return g;

                } else {
                    //never enters
                    index--;
                    return new GNode(false);
                }
            } else {
                index--;
                return new GNode(false);
            }
        }

    }

    //Proc
    public GNode D() {
        int id = index;
        index++;
        TreeNode proc = null;
        TreeNode variable = null;
        TreeNode prog = null;
        //   System.out.println("In D(Proc)");
        if (it.hasNext() && it.next().tokenName == "proc") {
            proc = new TreeNode(index, table, true, "proc");
            index++;

            if (it.hasNext()) {
                Node tok = it.next();
                if (tok.tokenName == "variable") {
                    variable = new TreeNode(index, table, true, "UserDefinedName");
                    index++;
                    variable.contents = tok.token;
                    if (it.hasNext() && it.next().tokenName == "openBrace") {

                        GNode a = A();

                        if (a.status) {
                            // String tok = it.next().tokenName;
//System.out.println("Parser.D()"+tok);
                            if (it.hasNext() && it.next().tokenName == "closeBrace") {
                                TreeNode n = new TreeNode(id, table, false, "PROC");
                                n.addChild(proc);
                                n.addChild(variable);
                                n.addChild(a.n);
                                GNode g = new GNode(true);
                                g.n = n;
                                return g;
                            } else {
                                System.out.println("Syntax error: expected } Line:" + it.previous().row);
                                terminate();
                            }
                        } else {
                            System.out.println("Syntax error: expected Prog Line:" + it.previous().row);
                            terminate();
                        }
                    } else {
                        System.out.println("Syntax error: expected { Line:" + it.previous().row);
                        terminate();
                    }
                } else {
                    System.out.println("Syntax error: expected variable Line:" + it.previous().row);
                    terminate();
                }

            } else {
                System.out.println("Syntax error: expected variable Line:" + it.previous().row);
                terminate();
            }
        } else {
            //System.out.println("Parser.D()");
            index--;
            it.previous();
            return new GNode(false);
        }
        index--;
        return new GNode(false);
    }

    //Instr
    public GNode E() {
        // System.out.println("In E(Instr)");
        if (it.hasNext()) {
            int id = index;
            index++;
            Node tok = it.next();

            if (tok.tokenName == "halt") {
                TreeNode halt = new TreeNode(index, table, true, "halt");
                index++;
                TreeNode inst = new TreeNode(id, table, false, "INSTR");
                inst.addChild(halt);
                GNode g = new GNode(true);
                g.n = inst;
                return g;
            } else {

                it.previous();
                GNode f = F();
                if (f.status) {

                    TreeNode inst = new TreeNode(id, table, false, "INSTR");
                    inst.addChild(f.n);
                    GNode g = new GNode(true);
                    g.n = inst;
                    return g;

                } else {
                    GNode h = H();
                    if (h.status) {
                        TreeNode inst = new TreeNode(id, table, false, "INSTR");
                        inst.addChild(h.n);
                        GNode g = new GNode(true);
                        g.n = inst;
                        return g;

                    } else {
                        GNode g1 = G();
                        if (g1.status) {
                            TreeNode inst = new TreeNode(id, table, false, "INSTR");
                            inst.addChild(g1.n);
                            GNode g = new GNode(true);
                            g.n = inst;
                            return g;
                        } else {
                            GNode i = I();
                            if (i.status) {
                                TreeNode inst = new TreeNode(id, table, false, "INSTR");
                                inst.addChild(i.n);
                                GNode g = new GNode(true);
                                g.n = inst;
                                return g;

                            } else {
                                GNode j = J();
                                if (j.status) {
                                    TreeNode inst = new TreeNode(id, table, false, "INSTR");
                                    inst.addChild(j.n);
                                    GNode g = new GNode(true);
                                    g.n = inst;
                                    return g;
                                } else {
                                    System.out.println("Syntax error: expecting Instr Line:" + it.previous().row);
                                    terminate();
                                    //Not an instr
                                    return new GNode(false);
                                }
                            }
                        }
                    }

                }
            }
        } else {
            System.out.println("Syntax error: expecting Instr Line:" + it.previous().row);
            terminate();
            return new GNode(false);
        }

    }

    //IO
    public GNode F() {
        //System.out.println("In F(IO)");
        int id = index;
        index++;
        String tok = it.next().tokenName;
        TreeNode t;
        if (tok == "input") {
            t = new TreeNode(index, table, true, tok);
            index++;
        } else if (tok == "output") {
            t = new TreeNode(index, table, true, tok);
            index++;
        } else {
            index--;
            it.previous();
            //neither input nor output symbol
            return new GNode(false);
        }
        if (it.hasNext() && it.next().tokenName == "openBracket") {
            GNode k = K();
            if (k.status) {

                if (it.hasNext() && it.next().tokenName == "closeBracket") {
                    GNode ret = new GNode(true);
                    TreeNode i = new TreeNode(id, table, false, "IO");
                    i.addChild(t);
                    i.addChild(k.n);
                    ret.n = i;
                    return ret;
                } else {
                    System.out.println("Syntax error: expected ) after variable Line:" + it.previous().row);
                    terminate();
                }
            } else {
                System.out.println("Syntax error: expected variable Line:" + it.previous().row);
                terminate();
            }
        } else {
            System.out.println("Syntax error: expected ( Line:" + it.previous().row);
            terminate();
        }

        return new GNode(false);
    }

    //Call
    public GNode G() {
        //  System.out.println("In G(Call)");
        int id = index;
        index++;
        if (it.hasNext()) {
            Node tok = it.next();

            if (tok.tokenName == "variable") {
                TreeNode v = new TreeNode(id, table, false, "UserDefinedName");
                TreeNode var = new TreeNode(index, table, true, tok.token);
                v.contents = tok.token;
                GNode g = new GNode(true);
                //v.addChild(var);
                g.n = v;
                return g;
            } else {
                it.previous();
                index--;
                return new GNode(false);
            }
        } else {
            index--;
            return new GNode(false);

        }

    }

    //Asign
    public GNode H() {
        //   System.out.println("In H(Assign)");
        int id = index;
        index++;

        if (it.hasNext()) {
            Node tok = it.next();
            if (tok.tokenName == "variable") {
                int vID = index;
                index++;
                index++;
                if (it.hasNext()) {
                    if (it.next().tokenName == "assignmentOperator") {
                        int assId = index;
                        index++;
                        GNode r = R();
                        if (r.status) {
                            GNode g = new GNode(true);
                            //  TreeNode t = new TreeNode(id, table, false, "INSTR");
                            TreeNode a = new TreeNode(id, table, false, "ASSIGN");
                            TreeNode var = new TreeNode(vID, table, false, "VAR");
                            TreeNode v = new TreeNode(vID + 1, table, true, "variable");
                            v.contents = tok.token;
                            var.addChild(v);
                            //   t.addChild(a);
                            a.addChild(var);
                            TreeNode eq = new TreeNode(assId, table, true, "assignment");
                            eq.contents = "=";
                            a.addChild(eq);
                            a.addChild(r.n);
                            g.n = a;
                            return g;
                        } else {
                            System.out.println("Syntax Error:Expected string var or num after = Line:" + it.previous().row);
                            terminate();
                        }
                    } else {
                        index--;
                        index--;
                        TreeNode call = new TreeNode(id, table, false, "CALL");
                        // index++;
                        TreeNode v = new TreeNode(index, table, true, "UserDefinedName");
                        v.contents = tok.token;
                        index++;
                        // TreeNode var = new TreeNode(index, table, true, tok.token);
                        //index++;
                        //  var.contents = tok.token;
                        GNode g = new GNode(true);
                        // v.addChild(var);
                        call.addChild(v);
                        g.n = call;
                        it.previous();
                        return g;

                    }
                } else {
                    index--;
                    index--;
                    TreeNode call = new TreeNode(id, table, false, "CALL");
                    //index++;
                    TreeNode v = new TreeNode(index, table, false, "UserDefinedName");
                    index++;
                    TreeNode var = new TreeNode(index, table, true, tok.token);
                    var.contents = tok.token;
                    GNode g = new GNode(true);
                    v.addChild(var);
                    call.addChild(v);
                    g.n = call;
                    return g;

                }

            } else {
                it.previous();
                index--;
                return new GNode(false);
            }

        } else {
            it.previous();
            index--;
            return new GNode(false);
        }
        index--;
        return new GNode(false);
    }

    //CondBranch
    public GNode I() {
        //  System.out.println("In I(CodeBranch)");
        String tok = it.next().tokenName;
        int id = index;
        index++;
        if (it.hasNext() && tok == "if") {
            TreeNode i = new TreeNode(index, table, true, tok);
            index++;
            if (it.hasNext() && it.next().tokenName == "openBracket") {
                GNode n = N();
                if (n.status) {
                    if (it.hasNext() && it.next().tokenName == "closeBracket") {

                        if (it.hasNext() && it.next().tokenName == "then") {
                            TreeNode th = new TreeNode(index, table, true, "then");
                            index++;
                            if (it.hasNext() && it.next().tokenName == "openBrace") {
                                GNode b = B();
                                if (b.status) {
                                    if (it.hasNext() && it.next().tokenName == "closeBrace") {
                                        GNode t = T();

                                        if (t.status) {
                                            GNode g = new GNode(true);
                                            TreeNode ret = new TreeNode(id, table, false, "Cond_Branch");
                                            ret.addChild(i);
                                            ret.addChild(n.n);
                                            ret.addChild(th);
                                            ret.addChild(b.n);
                                            if (t.n != null) {
                                                ListIterator<TreeNode> l = t.n.children.listIterator();
                                                while (l.hasNext()) {
                                                    ret.addChild(l.next());
                                                }
                                            }

                                            //  ret.addChild(t.n);
                                            g.n = ret;
                                            return g;
                                        } else {
                                            System.out.println("Syntax error: check T function Line:" + it.previous().row);
                                            terminate();
                                        }
                                    } else {
                                        System.out.println("Syntax error: expecting }  Line:" + it.previous().row);
                                        terminate();
                                    }
                                } else {
                                    System.out.println("Syntax error: expecting Code after { Line:" + it.previous().row);
                                    terminate();
                                }
                            } else {
                                System.out.println("Syntax error: expecting { after then Line:" + it.previous().row);
                                terminate();
                            }
                        } else {
                            System.out.println("Syntax error: expecting then after ) Line:" + it.previous().row);
                            terminate();
                        }
                    } else {
                        System.out.println("Syntax error: expecting ) after boolean Line:" + it.previous().row);
                        terminate();
                    }
                } else {
                    System.out.println("Syntax error: expecting Boolean after ( Line:" + it.previous().row);
                    terminate();
                }
            } else {
                System.out.println("Syntax error: expecting ( after if Line:" + it.previous().row);
                terminate();
            }
        } else {
            index--;
            it.previous();
            return new GNode(false);
        }
        index--;
        return new GNode(false);
    }

    //CondLoop
    public GNode J() {
        // System.out.println("In J(Condloop)");

        if (it.hasNext()) {
            int id = index;
            index++;
            String tok = it.next().tokenName;

            if (tok == "while") {
                index++;
                if (it.hasNext() && it.next().tokenName == "openBracket") {
                    GNode n = N();
                    if (n.status) {
                        if (it.hasNext() && it.next().tokenName == "closeBracket") {
                            if (it.hasNext() && it.next().tokenName == "openBrace") {
                                GNode b = B();
                                if (b.status) {
                                    if (it.hasNext() && it.next().tokenName == "closeBrace") {

                                        GNode g = new GNode(true);
                                        TreeNode ret = new TreeNode(id, table, false, "Cond_Loop_While");
                                        TreeNode w = new TreeNode(id + 1, table, true, "while");
                                        ret.addChild(w);
                                        ret.addChild(n.n);
                                        ret.addChild(b.n);
                                        g.n = ret;
                                        return g;
                                    } else {
                                        System.out.println("Syntax error: expecting }after Code Line:" + it.previous().row);
                                        terminate();
                                        return new GNode(false);
                                    }
                                } else {
                                    System.out.println("Syntax error: expecting  Code after { Line:" + it.previous().row);
                                    terminate();
                                    return new GNode(false);
                                }
                            } else {
                                System.out.println("Syntax error: expecting { after ) Line:" + it.previous().row);
                                terminate();
                                return new GNode(false);
                            }
                        } else {
                            System.out.println("Syntax error: expecting ) after Bool Line:" + it.previous().row);
                            terminate();
                            return new GNode(false);
                        }
                    } else {
                        System.out.println("Syntax error: expecting Bool after ( Line:" + it.previous().row);
                        terminate();
                        return new GNode(false);
                    }
                } else {
                    System.out.println("Syntax error: expecting ) after while Line:" + it.previous().row);
                    terminate();
                    return new GNode(false);
                }
            } else if (tok == "for") {
                TreeNode forT = new TreeNode(index, table, true, "for");
                index++;
                if (it.hasNext() && it.next().tokenName == "openBracket") {
                    GNode forG = forLoop();
                    if (forG.status) {
                        TreeNode r = new TreeNode(id, table, false, "Cond_Loop_For");
                        r.addChild(forT);
                        TreeNode a = forG.n;
                        List<TreeNode> l = a.children;
                        ListIterator<TreeNode> a1 = l.listIterator();
                        while (a1.hasNext()) {
                            r.addChild(a1.next());
                        }
                        //  forT.addToFront(forG.n);
                        GNode ret = new GNode(true);
                        ret.n = r;
                        return ret;
                    } else {
                        System.out.println("Syntax error: expecting for loop after ( for Line:" + it.previous().row);
                        terminate();
                        return new GNode(false);
                    }
                } else {
                    System.out.println("Syntax error: expecting ) after for Line:" + it.previous().row);
                    terminate();
                    return new GNode(false);
                }

            } else {
                it.previous();
                return new GNode(false);
            }
        } else {
            return new GNode(false);
        }

    }

    //Var
    public GNode K() {
        //    System.out.println("In K(Var)");
        if (it.hasNext()) {
            Node tok = it.next();
            if (tok.tokenName == "variable") {
                GNode g = new GNode(true);
                TreeNode t = new TreeNode(index, table, false, "VAR");
                index++;
                TreeNode t2 = new TreeNode(index, table, true, "variable");
                index++;
                t2.contents = tok.token;
                t.addChild(t2);
                g.n = t;
                return g;
            } else {
                it.previous();
                return new GNode(false);
            }

        } else {
            it.previous();
            return new GNode(false);
        }

    }

    //Numexp
    public GNode L() {
        int id = index;
        index++;
        // System.out.println("In L(NumExp)");
        if (it.hasNext()) {
            Node tok = it.next();

            if (tok.tokenName == "variable") {
                GNode g = new GNode(true);
                TreeNode t = new TreeNode(id, table, false, "NUMEXP");
                TreeNode var = new TreeNode(index, table, false, "VAR");
                index++;
                TreeNode v = new TreeNode(index, table, true, "variable");
                index++;
                v.contents = tok.token;
                var.addChild(v);
                t.addChild(var);
                g.n = t;
                return g;
            } else if (tok.tokenName == "Digit") {
                GNode g = new GNode(true);
                TreeNode t = new TreeNode(id, table, false, "NUMEXP");
                TreeNode var = new TreeNode(index, table, true, "Digit");
                var.contents = tok.token;
                index++;

                t.addChild(var);
                g.n = t;
                return g;

            } else {
                it.previous();
                GNode m = M();
                if (m.status) {
                    GNode g = new GNode(true);
                    TreeNode t = new TreeNode(id, table, false, "NUMEXP");
                    t.addChild(m.n);
                    g.n = t;
                    return g;
                } else {
                    return new GNode(false);
                }
            }
        } else {
            System.out.println("Syntax error: Ran out of tokens Line:" + it.previous().row);
            terminate();
            return new GNode(false);
        }

    }
//Calc

    public GNode M() {
        //   System.out.println("In M(Calc)");
        int id = index;
        index++;
        String tok = it.next().tokenName;
        if (tok == "add" || tok == "sub" || tok == "mult") {
            TreeNode t = new TreeNode(index, table, true, tok);
            index++;
            if (it.hasNext() && it.next().tokenName == "openBracket") {
                GNode l = L();
                if (l.status) {

                    if (it.hasNext() && it.next().tokenName == "comma") {
                        GNode l2 = L();
                        if (l2.status) {

                            if (it.hasNext() && it.next().tokenName == "closeBracket") {
                                GNode g = new GNode(true);
                                TreeNode ret = new TreeNode(id, table, false, "CALC");
                                ret.addChild(t);
                                ret.addChild(l.n);
                                ret.addChild(l2.n);
                                g.n = ret;
                                return g;
                            } else {
                                System.out.println("Syntax error expected ) Line:" + it.previous().row);
                                terminate();
                            }
                        } else {
                            System.out.println("Syntax error expected Numexp Line:" + it.previous().row);
                            terminate();
                        }
                    } else {
                        System.out.println("Syntax error expected , Line:" + it.previous().row);
                        terminate();
                    }
                } else {
                    System.out.println("Syntax error expected NumExp Line:" + it.previous().row);
                    terminate();
                }
            } else {
                System.out.println("Syntax error expected ( Line:" + it.previous().row);
                terminate();
            }

        } else {
            it.previous();
            return new GNode(false);
        }
        return new GNode(true);
    }
    //Cond Bool

    public GNode N() {
        //System.out.println("In N(Bool)");
        if (it.hasNext()) {
            String tok = it.next().tokenName;
            int id = index;
            index++;
            //  System.out.println(tok);
            if (tok.equals("eq")) {
                TreeNode eq = new TreeNode(index, table, true, "eq");
                index++;
                if (it.hasNext() && it.next().tokenName == "openBracket") {
                    GNode u = U();
                    if (u.status) {
                        GNode g = new GNode(true);
                        TreeNode ret = new TreeNode(id, table, false, "BOOL");
                        ret.addChild(eq);
                        List<TreeNode> l = u.n.children;
                        ListIterator<TreeNode> a1 = l.listIterator();
                        while (a1.hasNext()) {
                            ret.addChild(a1.next());
                        }
                        g.n = ret;

                        return g;
                    } else {
                        System.out.println("Syntax Error: error in eq syntax Line:" + it.previous().row);
                        terminate();
                        return new GNode(false);
                    }
                } else {
                    System.out.println("Syntax error: Expecting ( after eq token Line:" + it.previous().row);
                    terminate();
                    return new GNode(false);
                }
            } else if (tok.equals("openBracket")) {
                GNode k = K();
                if (k.status) {
                    if (it.hasNext()) {
                        tok = it.next().tokenName;
                        if (it.hasNext() && (tok == "compGreater" || tok == "compLess")) {
                            TreeNode t = new TreeNode(index, table, true, tok);
                            index++;
                            GNode k2 = K();
                            if (k2.status) {
                                if (it.hasNext() && it.next().tokenName == "closeBracket") {
                                    GNode g = new GNode(true);
                                    TreeNode ret = new TreeNode(id, table, false, "BOOL");
                                    ret.addChild(k.n);
                                    ret.addChild(t);
                                    ret.addChild(k2.n);
                                    g.n = ret;
                                    return g;
                                } else {
                                    System.out.println("Syntax error: Expecting ) after varaible in boolean Line:" + it.previous().row);
                                    terminate();
                                    return new GNode(false);
                                }
                            } else {
                                System.out.println("Syntax error: Expecting variable after " + tok + " in boolean Line:" + it.previous().row);
                                terminate();
                                return new GNode(false);
                            }

                        } else {
                            System.out.println("Syntax error: Expecting " + tok + " after variable in boolean Line:" + it.previous().row);
                            terminate();
                            return new GNode(false);
                        }
                    } else {
                        System.out.println("Syntax error: Expecting " + tok + " after variable in boolean Line:" + it.previous().row);
                        terminate();
                        return new GNode(false);
                    }

                } else {
                    System.out.println("Syntax error: Expecting variable after ( in boolean Line:" + it.previous().row);
                    terminate();
                    return new GNode(false);
                }

            } else if (tok == "not") {
                TreeNode no = new TreeNode(index, table, true, tok);
                GNode n = N();
                if (n.status) {
                    GNode g = new GNode(true);
                    TreeNode ret = new TreeNode(id, table, false, "BOOL");
                    ret.addChild(no);
                    ret.addChild(n.n);
                    g.n = ret;
                    return g;
                } else {
                    System.err.println("Syntax error: Expecting variable bool after not Line:" + it.previous().row);
                    terminate();
                    return new GNode(false);
                }

            } else if (tok == "and" || tok == "or") {
                TreeNode t = new TreeNode(index, table, true, tok);
                index++;
                if (it.hasNext() && it.next().tokenName == "openBracket") {
                    GNode n1 = N();
                    if (n1.status) {
                        if (it.hasNext() && it.next().tokenName == "comma") {
                            GNode n2 = N();

                            if (n2.status) {
                                if (it.hasNext() && it.next().tokenName == "closeBracket") {

                                    GNode g = new GNode(true);
                                    TreeNode ret = new TreeNode(id, table, false, "BOOL");
                                    ret.addChild(t);
                                    ret.addChild(n1.n);
                                    ret.addChild(n2.n);

                                    g.n = ret;
                                    return g;

                                } else {
                                    System.err.println("Syntax error: Expecting ) after boolean Line" + it.previous().row);
                                    terminate();
                                    return new GNode(false);
                                }
                            } else {
                                System.err.println("Syntax error: Expecting boolean after , Line:" + it.previous().row);
                                terminate();
                                return new GNode(false);
                            }
                        } else {
                            System.err.println("Syntax error: Expecting , after boolean Line:" + it.previous().row);
                            terminate();
                            return new GNode(false);
                        }
                    } else {
                        System.err.println("Syntax error: Expecting boolean after ( Line:" + it.previous().row);
                        terminate();
                        return new GNode(false);
                    }
                } else {
                    System.err.println("Syntax error: Expecting ( after " + tok + " in boolean Line:" + it.previous().row);
                    terminate();
                    return new GNode(false);
                }

            } else {
                it.previous();
                return new GNode(false);
            }
        } else {
            return new GNode(false);
        }

    }
//Prog Extra ( ; Proc Def )

    public GNode O() {
        // System.out.println("In O(Prog extra)");

        if (it.hasNext()) {
            String tok = it.next().tokenName;

            it.previous();

            GNode c = C();
            if (c.status) {
                return c;
            } else {
                // System.out.println("Syntax error missing Proc defs");
                //  terminate();
                return new GNode(true);
            }

        } else {
            return new GNode(true);
        }

    }
//code extra (code or eppison

    public GNode P() {
        //  System.out.println("In P(code extra)");
        if (it.hasNext()) {
            String tok = it.next().tokenName;
            //    System.out.println("Parser.P()"+tok);
            if (tok == "semicolon") {
                GNode c = C();

                if (c.status) {

                    return c;
                } else {
                    GNode b = B();
                    if (b.status) {
                        return b;
                    } else {
                        System.out.println("Syntax error missing code after semi colon Line:" + it.previous().row);
                        terminate();
                        return new GNode(false);
                    }
                }

            } else {

                it.previous();
                return new GNode(true);
            }
        } else {
            // it.previous();
            return new GNode(true);
        }

    }

    public GNode Q() {
        //  System.out.println("In Q(Proc Defs Extra)");
        if (it.hasNext() != true) {
            return new GNode(true);
        } else {
            GNode c = C();
            if (c.status) {
                return c;
            } else {
                return new GNode(true);
            }
        }

    }

    public GNode R() {
        // System.out.println("In R(Assign Extra)");
        if (it.hasNext()) {
            //int id = index;
            // index++;
            Node tok = it.next();
            if (tok.tokenName == "String") {
                GNode ret = new GNode(true);
                TreeNode t = new TreeNode(index, table, true, "String");
                t.contents = tok.token;
                index++;
                // t.addChild(e);
                // t.addChild(b.n);
                ret.n = t;
                return ret;

            } else if (tok.tokenName == "variable") {
                GNode ret = new GNode(true);
                TreeNode t = new TreeNode(index, table, false, "VAR");
                index++;
                TreeNode t1 = new TreeNode(index, table, true, "variable");
                t1.contents = tok.token;
                //t.contents=tok.token;
                index++;
                t.addChild(t1);
                // t.addChild(b.n);
                ret.n = t;
                return ret;

            } else {
                // index--;
                it.previous();
                GNode l = L();
                if (l.status) {
                    return l;

                }
            }
        } else {
            return new GNode(false);
        }

        return new GNode(false);
    }

    public GNode T() {
        //  System.out.println("In T(Cond Branch extra)");
        if (it.hasNext()) {
            if (it.next().tokenName == "else") {
                TreeNode e = new TreeNode(index, table, true, "else");
                index++;
                if (it.hasNext() && it.next().tokenName == "openBrace") {
                    GNode b = B();
                    if (b.status) {
                        if (it.hasNext() && it.next().tokenName == "closeBrace") {
                            GNode ret = new GNode(true);
                            TreeNode t = new TreeNode(index, table, false, "ELSE");
                            t.addChild(e);
                            t.addChild(b.n);
                            ret.n = t;
                            return ret;
                        } else {
                            System.out.println("Syntax Error: expecting } Line:" + it.previous().row);
                            terminate();
                        }
                    } else {
                        System.out.println("Syntax Error: Expecting Code Line:" + it.previous().row);
                        terminate();
                    }
                } else {
                    System.out.println("Syntax error: expecting { Line:" + it.previous().row);
                    terminate();
                }
            } else {

                it.previous();
                return new GNode(true);
            }
        }

        return new GNode(true);
    }

    public GNode U() {
        // System.out.println("In U(Bool extra)");
        Node tok = it.next();
        if (tok.tokenName == "variable") {

            int vID =index;
            index++;
                        TreeNode t = new TreeNode(index, table, true, "variable");
            t.contents = tok.token;
            index++;
            if (it.next().tokenName == "comma") {
                GNode k = K();
                if (k.status) {
                    tok = it.next();
                    // System.out.println(tok);
                    if (tok.tokenName == "closeBracket") {
                        TreeNode v = new TreeNode(vID, table, false, "VAR");
                        v.addChild(t);
                        TreeNode r = new TreeNode(index, table, false, "BoolExtra");
                        r.addChild(v);
                        r.addChild(k.n);
                        GNode g = new GNode(true);
                        g.n = r;
                        return g;
                    } else {
                        System.out.println("Syntax error expecting ) Line:" + it.previous().row);
                        terminate();
                        return new GNode(false);
                    }
                } else {
                    index++;
                    GNode n2 = L();
                    if (n2.status) {
                        if (it.next().tokenName == "closeBracket") {
                            TreeNode ex = new TreeNode(vID,table,false,"NUMEXP");
                             TreeNode v = new TreeNode(vID+1, table, false, "VAR");
                             t.id=vID+2;
                             ex.addChild(v);
                        v.addChild(t);
                            GNode ret = new GNode(true);
                            TreeNode retT = new TreeNode(index, table, false, "BoolExtra");
                            retT.addChild(ex);
                            retT.addChild(n2.n);
                            ret.n = retT;
                            return ret;

                        } else {
                            System.out.println("Syntax error expecting ) Line:" + it.previous().row);
                            terminate();
                            return new GNode(false);
                        }

                    } else {
                        System.out.println("Syntax error: expecting variable or numExpr Line:" + it.previous().row);
                        terminate();
                        return new GNode(false);
                    }
                }
            } else {
                System.out.println("Syntax Error: Expected , Line:" + it.previous().row);
                terminate();
                return new GNode(false);
            }

        } else {
            index--;
            it.previous();
            GNode n = N();
            if (n.status) {

                if (it.next().tokenName == "comma") {
                    GNode n2 = N();
                    if (n2.status) {
                        if (it.next().tokenName == "closeBracket") {
                            GNode ret = new GNode(true);
                            TreeNode retT = new TreeNode(index, table, false, "BoolExtra");
                            retT.addChild(n.n);
                            retT.addChild(n2.n);
                            ret.n = retT;
                            return ret;

                        } else {
                            System.out.println("Syntax error expecting ) Line:" + it.previous().row);
                            terminate();
                            return new GNode(false);
                        }
                    } else {
                        System.out.println("Syntax error: expecting bool Line:" + it.previous().row);
                        terminate();
                        return new GNode(false);
                    }
                } else {
                    System.out.println("Syntax Error: Expected , Line:" + it.previous().row);
                    terminate();
                    return new GNode(false);
                }

            } else {
                //  it.previous();
                GNode l = L();
                if (l.status) {

                    if (it.next().tokenName == "comma") {
                        GNode l2 = L();
                        if (l2.status) {
                            if (it.next().tokenName == "closeBracket") {
                                GNode ret = new GNode(true);
                                TreeNode retT = new TreeNode(index, table, false, "BoolExtra");
                                retT.addChild(l.n);
                                retT.addChild(l2.n);
                                ret.n = retT;
                                return ret;
                            } else {
                                System.out.println("Syntax error expecting ) Line:" + it.previous().row);
                                terminate();
                                return new GNode(false);
                            }
                        } else {
                            System.out.println("Syntax error: expecting NumExpr Line:" + it.previous().row);
                            terminate();
                            return new GNode(false);
                        }
                    } else {
                        System.out.println("Syntax Error: Expected ,Line:" + it.previous().row);
                        terminate();
                        return new GNode(false);
                    }

                } else {

                    return new GNode(false);
                }
            }
        }

    }

    public void terminate() {
        System.out.println("Parser failed, terminating parser");
//        while (it.hasNext()) {
//            System.out.println(it.next().token);
//        }
        exit(0);
    }

    public GNode forLoop() {
        //System.out.println("For Loop");
        TreeNode retTree = new TreeNode(-1, table, false, "forExtra");
        GNode k = K();
        if (k.status) {
            retTree.addChild(k.n);
            if (it.hasNext() && it.next().tokenName == "assignmentOperator") {
                TreeNode eq = new TreeNode(index, table, true, "=");
                retTree.addChild(eq);
                index++;
                if (it.hasNext() && it.next().token.equals("0")) {
                    TreeNode zero = new TreeNode(index, table, true, "0");
                    retTree.addChild(zero);
                    index++;
                    if (it.hasNext() && it.next().tokenName == "semicolon") {
                        GNode k2 = K();
                        retTree.addChild(k2.n);
                        if (k2.status) {
                            if (it.hasNext() && it.next().tokenName == "compLess") {
                                TreeNode compLess = new TreeNode(index, table, true, "<");
                                retTree.addChild(compLess);
                                index++;
                                GNode k3 = K();
                                if (k3.status) {
                                    retTree.addChild(k3.n);
                                    if (it.hasNext() && it.next().tokenName == "semicolon") {
                                        GNode k4 = K();
                                        if (k4.status) {
                                            retTree.addChild(k4.n);
                                            if (it.hasNext() && it.next().tokenName == "assignmentOperator") {
                                                TreeNode eq2 = new TreeNode(index, table, true, "=");
                                                retTree.addChild(eq2);
                                                index++;
                                                if (it.hasNext() && it.next().tokenName == "add") {
                                                    TreeNode add = new TreeNode(index, table, true, "add");
                                                    retTree.addChild(add);
                                                    index++;
                                                    if (it.hasNext() && it.next().tokenName == "openBracket") {
                                                        GNode k5 = K();
                                                        if (k5.status) {
                                                            retTree.addChild(k5.n);
                                                            if (it.hasNext() && it.next().tokenName == "comma") {

                                                                if (it.hasNext() && it.next().token.equals("1")) {
                                                                    TreeNode one = new TreeNode(index, table, true, "1");
                                                                    retTree.addChild(one);
                                                                    index++;
                                                                    if (it.hasNext() && it.next().tokenName == "closeBracket") {
                                                                        if (it.hasNext() && it.next().tokenName == "closeBracket") {
                                                                            if (it.hasNext() && it.next().tokenName == "openBrace") {
                                                                                GNode b = B();
                                                                                if (b.status) {
                                                                                    retTree.addChild(b.n);
                                                                                    if (it.hasNext() && it.next().tokenName == "closeBrace") {
                                                                                        GNode ret = new GNode(true);
                                                                                        ret.n = retTree;

                                                                                        return ret;
                                                                                    } else {
                                                                                        System.out.println("Syntax Error: Expected  } Line:" + it.previous().row);
                                                                                        terminate();
                                                                                        return new GNode(false);
                                                                                    }
                                                                                } else {
                                                                                    System.out.println("Syntax Error: Expected  Code Line:" + it.previous().row);
                                                                                    terminate();
                                                                                    return new GNode(false);
                                                                                }
                                                                            } else {
                                                                                System.out.println("Syntax Error: Expected  { Line:" + it.previous().row);
                                                                                terminate();
                                                                                return new GNode(false);
                                                                            }
                                                                        } else {
                                                                            System.out.println("Syntax Error: Expected  ) Line:" + it.previous().row);
                                                                            terminate();
                                                                            return new GNode(false);
                                                                        }
                                                                    } else {
                                                                        System.out.println("Syntax Error: Expected  ) Line:" + it.previous().row);
                                                                        terminate();
                                                                        return new GNode(false);
                                                                    }
                                                                } else {
                                                                    System.out.println("Syntax Error: Expected  1 Line:" + it.previous().row);
                                                                    terminate();
                                                                    return new GNode(false);
                                                                }
                                                            } else {
                                                                System.out.println("Syntax Error: Expected  , Line:" + it.previous().row);
                                                                terminate();
                                                                return new GNode(false);
                                                            }
                                                        } else {
                                                            System.out.println("Syntax Error: Expected  variable Line:" + it.previous().row);
                                                            terminate();
                                                            return new GNode(false);
                                                        }
                                                    } else {
                                                        System.out.println("Syntax Error: Expected  ( Line:" + it.previous().row);
                                                        terminate();
                                                        return new GNode(false);
                                                    }
                                                } else {
                                                    System.out.println("Syntax Error: Expected  add Line:" + it.previous().row);
                                                    terminate();
                                                    return new GNode(false);
                                                }
                                            } else {
                                                System.out.println("Syntax Error: Expected  = Line:" + it.previous().row);
                                                terminate();
                                                return new GNode(false);
                                            }
                                        } else {
                                            System.out.println("Syntax Error: Expected  variable Line:" + it.previous().row);
                                            terminate();
                                            return new GNode(false);
                                        }
                                    } else {
                                        System.out.println("Syntax Error: Expected  ; Line:" + it.previous().row);
                                        terminate();
                                        return new GNode(false);
                                    }
                                } else {
                                    System.out.println("Syntax Error: Expected  variable Line:" + it.previous().row);
                                    terminate();
                                    return new GNode(false);
                                }
                            } else {
                                System.out.println("Syntax Error: Expected < Line:" + it.previous().row);
                                terminate();
                                return new GNode(false);
                            }
                        } else {
                            System.out.println("Syntax Error: Expected variable Line:" + it.previous().row);
                            terminate();
                            return new GNode(false);
                        }
                    } else {
                        System.out.println("Syntax Error: Expected  ; Line:" + it.previous().row);
                        terminate();
                        return new GNode(false);
                    }
                } else {
                    System.out.println("Syntax Error: Expected  0 Line:" + it.previous().row);
                    terminate();
                    return new GNode(false);
                }
            } else {
                System.out.println("Syntax Error: Expected  = Line:" + it.previous().row);
                terminate();
                return new GNode(false);
            }
        } else {
            System.out.println("Syntax Error: Expected  variable Line:" + it.previous().row);
            terminate();
            return new GNode(false);
        }

    }

    public void printTab(int t, String s) {
        for (int i = 0; i < t; i++) {
            System.out.print("\t");
        }
        System.out.print("-" + s);
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
    
    

}
