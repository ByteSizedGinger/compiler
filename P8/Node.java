

/**
 *
 * @author jenna
 */
public class Node {
    public String tokenName;
    public String token;
    public int col;
    public int row;
    
    public Node(String tN,String t){
        tokenName=tN;
        token=t;
    }

    public Node(String tokenName, String token, int col, int row) {
        this.tokenName = tokenName;
        this.token = token;
        this.col = col;
        this.row = row;
    }
    
    public Node(){}

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }
    
    
    
}

