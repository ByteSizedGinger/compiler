
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
      Parser parser = new Parser(lex.getTokens());
     
       
    }
    
}
