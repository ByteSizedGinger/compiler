
import java.util.LinkedList;
import java.util.List;
import javax.swing.JOptionPane;



/**
 *
 * @author jenna
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //String textName=JOptionPane.showInputDialog(null,"File Name");
      Lexer lex = new Lexer(args[0]);
        List<Node> tokens=lex.getTokens();
       // lex.printList();
        System.out.println("Lexer finished with no errors");
      Parser parser = new Parser(lex.getTokens());
      parser.start();
  
      Scope s =new Scope();
      s.setTree(parser.getTree());
      s.setTable(parser.getTable());
      s.addScope();
       System.out.println("Scoping finished");
      //s.printSyntaxTree();
      SemanticAnalysis a = new SemanticAnalysis(s.getTree());
      a.buildTable();
      a.checkSemantics();
      a.printTree();
    //  a.printTable();
     
       
    }
    
}
