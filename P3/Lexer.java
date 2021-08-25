
import java.io.*;
import static java.lang.System.exit;
import java.util.ArrayList;
import java.util.Iterator;

import java.util.List;
import java.util.Scanner;

/**
 *
 * @author jenna
 */
public class Lexer {

    public static int line = 0;
    public static int col = 0;
    public static List tokList = new ArrayList<Node>();
    public static String[] keywords = {"and", "or", "not", "add", "sub", "mult", "if", "then", "else", "while", "for", "input", "output", "halt", "proc","eq"};
    public static String currentSymbols = "";
    int currentPos = 0;
    // public static Scanner lineScanner;
    public static Scanner scanner;
    public static boolean backTrack;
    public static char currentCharacter;

   Lexer(String fileName){
        scanFile(fileName);
       // printList();
    }
   
   public List<Node> getTokens(){
       return tokList;
   }

    public static void scanFile(String fName) {
        try {
            //scanner to read file
            scanner = new Scanner(new File(fName));
            backTrack = false;
            while (scanner.hasNextLine()) {
                line++;
                col = 0;
                String currentLine = scanner.nextLine();
                // System.out.println("line " + line + ":" + currentLine);
                Scanner lineScanner = new Scanner(currentLine);
                lineScanner.useDelimiter("");
                currentCharacter = ' ';
                while (lineScanner.hasNext() || backTrack == true) {

                    //get current symbol
                    if (backTrack == true) {
                        backTrack = false;
                    } else {
                        String current = lineScanner.next();
                        col++;
                        //convert to char
                        currentCharacter = current.charAt(0);
                    }

                    if (Character.compare(currentCharacter, ';') == 0) {
                        createAndAdd("semicolon", ";",col,line);
                    } else if (Character.compare(currentCharacter, '{') == 0) {
                        createAndAdd("openBrace", "{",col,line);
                    } else if (Character.compare(currentCharacter, '}') == 0) {
                        createAndAdd("closeBrace", "}",col,line);
                    } else if (Character.compare(currentCharacter, '(') == 0) {
                        createAndAdd("openBracket", "(",col,line);
                    } else if (Character.compare(currentCharacter, ')') == 0) {
                        createAndAdd("closeBracket", ")",col,line);
                    } else if (Character.compare(currentCharacter, '>') == 0) {
                        createAndAdd("compGreater", ">",col,line);
                    } else if (Character.compare(currentCharacter, '<') == 0) {
                        createAndAdd("compLess", "<",col,line);
                    } else if (Character.compare(currentCharacter, '=') == 0) {
                        createAndAdd("assignmentOperator", "=",col,line);
                    } else if (Character.compare(currentCharacter, ',') == 0) {
                        createAndAdd("comma", ",",col,line);
                    } else if (Character.isDigit(currentCharacter) || Character.compare(currentCharacter, '-') == 0) {

                        if (Character.compare(currentCharacter, '0') == 0) {

                            String dig = "0";
                            if (lineScanner.hasNext()) {
                                currentCharacter = lineScanner.next().charAt(0);
                                //System.out.println(currentCharacter);
                                col++;
                                if (Character.isDigit(currentCharacter)) {
                                    System.out.println("Lexical Error:" + dig + currentCharacter + "= illegal integer precedding 0 at Line:" + line + " Col:" + col);
                                    exit(0);
                                    //backTrack = true;
                                } else if (Character.isLetter(currentCharacter)) {
                                    //System.out.println("Lexical Error:" + dig + currentCharacter + "= illegal variable names can not start with 0 at Line:" + line + " Col:" + col);
                                    backTrack = true;
                                    createAndAdd("Digit", "0",col,line);
                                }else{
                                    backTrack = true;
                                    createAndAdd("Digit", "0",col,line);
                                }
                            } else {
                                createAndAdd("Digit", "0",col,line);
                            }
                        } else if (Character.compare(currentCharacter, '-') == 0) {
                            String dig = "-";
                            if (lineScanner.hasNext()) {
                                currentCharacter = lineScanner.next().charAt(0);
                                col++;
                                if (Character.compare(currentCharacter, '0') == 0) {
                                    System.out.println("Lexical Error:" + dig + currentCharacter + "= illegal integer cant have -0 at Line:" + line + " Col:" + col);
                                    exit(0);
                                    //backTrack = true;
                                } else if (Character.isLetter(currentCharacter)) {
                                    // System.out.println("Lexical Error:" + dig + currentCharacter + "= illegal variable names can not start with 0 at Line:" + line + " Col:" + col);
                                    //backTrack=true;
                                } else if(Character.isDigit(currentCharacter)) {
                                    dig = dig + Character.toString(currentCharacter);
                                    boolean illegal = false;
                                    while (lineScanner.hasNext()) {
                                        currentCharacter = lineScanner.next().charAt(0);
                                        col++;
                                        if (Character.isDigit(currentCharacter)) {
                                            dig = dig + Character.toString(currentCharacter);
                                        } else if (Character.isLetter(currentCharacter)) {
                                            // System.out.println("Lexical Error:" + dig + currentCharacter + "= illegal variable names can not start with digits at Line:" + line + " Col:" + col);
                                            //illegal = true;
                                            backTrack = true;
                                            break;
                                        } else {
                                            // illegal=true;
                                            backTrack = true;
                                            break;
                                        }

                                    }
                                    if (illegal != true) {
                                        createAndAdd("Digit", dig,col,line);
                                    }
                                }else{
                                     System.out.println("Lexical Error:" + dig + currentCharacter + "= illegal integer cant have - without digits following at Line:" + line + " Col:" + col);
                                     exit(0);
                                }
                            } else {
                                    System.out.println("Lexical Error:" + dig + currentCharacter + "= illegal integer cant have - without digits following at Line:" + line + " Col:" + col);
                                    exit(0);
                                //createAndAdd("token_Digit", "0");
                            }
                        } else {
                            String dig = "";

                            dig = dig + Character.toString(currentCharacter);
                            boolean illegal = false;
                            while (lineScanner.hasNext()) {
                                currentCharacter = lineScanner.next().charAt(0);
                                col++;
                                if (Character.isDigit(currentCharacter)) {
                                    dig = dig + Character.toString(currentCharacter);
                                } else if (Character.isLetter(currentCharacter)) {
                                    // System.out.println("Lexical Error:" + dig + currentCharacter + "= illegal variable names can not start with digits at Line:" + line + " Col:" + col);
                                    //illegal = true;
                                    backTrack = true;
                                    break;
                                } else {
                                    // illegal=true;
                                    backTrack = true;
                                    break;
                                }

                            }
                            if (illegal != true) {
                                createAndAdd("Digit", dig,col,line);
                            }

                        }

                    } else if (Character.compare(currentCharacter, '"') == 0) {
                        boolean close = false;
                        int length = 0;
                        String str = "";

                        str = str + Character.toString(currentCharacter);
                        //length++;
                        while (lineScanner.hasNext()) {

                            currentCharacter = lineScanner.next().charAt(0);
                            col++;
                            if (Character.compare(currentCharacter, '"') != 0 && (Character.isLowerCase(currentCharacter) || Character.isDigit(currentCharacter) || Character.compare(currentCharacter, ' ') == 0 || Character.isWhitespace(currentCharacter)) && length < 8) {
                                str = str + Character.toString(currentCharacter);
                                length++;
                            } else {
                                close = true;
                                if (Character.compare(currentCharacter, '"') == 0) {
                                    str = str + '"';
                                    createAndAdd("String", str,col,line);

                                } else if (length == 8) {
                                    System.out.println("Lexical Error:" + str + currentCharacter + " = illegal string length greater than 8 at Line:" + line + " Col:" + col);
                                    exit(0);
                                    break;

                                } else {
                                    if (Character.isUpperCase(currentCharacter)) {
                                        System.out.println("Lexical error:" + currentCharacter + " uppercase letters not valid at Line:" + line + " Col:" + col);
                                        exit(0);
                                    } else {
                                        System.out.println("Lexical error:" + currentCharacter + " invalid character at Line:" + line + " Col:" + col);
                                        exit(0);
                                    }
                                    // backTrack = true;
                                }

                                break;
                            }

                        }
                        if (close == false) {
                            System.out.println("Lexical error:" + str + " missing closing quotation marks Line:" + line + " Col:" + col);
                            exit(0);
                        }

                    } else if (Character.compare(currentCharacter, ' ') == 0 || Character.isWhitespace(currentCharacter)) {

                    } else if (Character.compare(currentCharacter, '#') == 0) {
                        break;
                    } else {

                        //variable or keyword or illegal
                        if (Character.isLowerCase(currentCharacter)) {

                            checkLiteralOrOperator(lineScanner);
                        } else {
                            if (Character.isUpperCase(currentCharacter)) {
                                System.out.println("Lexical error:" + currentCharacter + " uppercase letters not valid at Line:" + line + " Col:" + col);
                                exit(0);
                            } else {
                                System.out.println("Lexical error:" + currentCharacter + " invalid character at Line:" + line + " Col:" + col);
                                exit(0);
                            }

                        }

                    }
                }

            }
        } catch (FileNotFoundException ex) {
            System.out.println("File not found");
            exit(0);
        }
    }

    //function to check if input is string literal or operator in operator list
    public static void checkLiteralOrOperator(Scanner lineScanner) {

        String str = "";
        str = str + Character.toString(currentCharacter);

//while scanner has next continue to loop
        while (lineScanner.hasNext()) {
            //get character
            currentCharacter = lineScanner.next().charAt(0);
            col++;
//check if charcter digit 0-9 or a-z only lowercase
            if (Character.isLowerCase(currentCharacter) || Character.isDigit(currentCharacter)) {
//add to accepting state
                str = str + Character.toString(currentCharacter);

            } else if (Character.isWhitespace(currentCharacter)) {
                //if white space add to list and reset DFA
                createNode(str);
                return;
            } else {
//other character reset dfa at current position and add accepting to list
                backTrack = true;
                createNode(str);
                return;
            }

        }
        //add accepting to list
        createNode(str);

    }

//function to print linked list
    public static void printList() {
        //iterator to loop through list
        Iterator it = tokList.iterator();
        int count = 1;

        while (it.hasNext()) {
            Node n = (Node) it.next();
            System.out.println(count + ":" + n.token + " (" + n.tokenName + ")");
            count++;
        }
    }

//create node and determine if was a keyword or is literal
    public static void createNode(String str) {
//loop through literal list and check if matches
        for (int i = 0; i < keywords.length; i++) {
            if (str.equals(keywords[i])) {
                //matches keyword add to list
                createAndAdd(keywords[i], str,col,line);
                return;
            }
        }
//Must be varibale name add to list
        createAndAdd("variable", str,col,line);

    }

    public static void createAndAdd(String tN, String t,int col,int row) {
        Node n = new Node(tN, t,col,row);
        tokList.add(n);
    }

}
