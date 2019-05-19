import java.util.*;
import java.io.*;
public class Parser {

    private static Lexer lex;
    private ArrayList<Node> defs = new ArrayList<>();
    private ArrayList<String> defNames = new ArrayList<>();
    public Parser( Lexer lexer) {
            lex = lexer;
    }

    public Node parseProgram(){
          Token token = lex.getNextToken();
          if(token.isKind("eof")){
              //System.out.println("hitting an eof");
            return new Node("Null","null",null,null);
          }
          errorCheck(token, "LPAREN", "(");
          token = lex.getNextToken();
          if(token.getDetails().equals("define") && token.isKind("KEYWORD")){
              //System.out.println("Parsing define...");
              lex.putBackToken(token);
              Node first = parseDefs();
              return new Node("program", first, null);
          }
          // Pre-defined or user-defined def
          else if(token.isKind("KEYWORD") || token.isKind("NAME")){
              //System.out.println("Parsing list...");
              lex.putBackToken(token);
              return parseList();
          }
          // need to handle where a list goes
          error("if type isnt name or defs this isn't a valid input/file");
          return new Node(token);
    }

    public Node parseDefs() {
        //System.out.println("-----> parsing <defs>:");
        Node first = parseDef();

        Token token = lex.getNextToken();
        if ( token.isKind("eof") ){
            return new Node("defs", first, null);
        }
        else {
            errorCheck(token, "LPAREN");
            Node second = parseDefs();
            return new Node("defs", first, second);
        }
    }

    public Node parseDef(){
        Node def;
        //System.out.println("-----> parsing <def>:");

        Token token = lex.getNextToken();
        errorCheck(token, "KEYWORD", "define");

        token = lex.getNextToken();
        errorCheck(token, "LPAREN", "(");
        System.out.println("before: " + token.toString());
        Token name = lex.getNextToken();
        System.out.println("after: " + name.toString());

        errorCheck(name, "NAME");
        token = lex.getNextToken();

        // if no params
        if ( token.isKind("RPAREN") ) {
            Node first = parseExpr();
            token = lex.getNextToken();
            errorCheck(token, "RPAREN", ")");
            def = new Node("def",name.getDetails(),first, null);
        }
        // if params found
        else {
            lex.putBackToken( token );
            Node first = parseParams();
            token = lex.getNextToken();
            errorCheck(token, "RPAREN", ")");
            Node second = parseExpr();
            token = lex.getNextToken();
            errorCheck(token, "RPAREN", ")");
            def = new Node("def",name.getDetails(),first, second);
        }

        defs.add(def);
        defNames.add(def.getInfo());
        return def;
    }

    public Node parseParams() {
        //System.out.println("-----> parsing <params>:");

        Token name = lex.getNextToken();
        errorCheck(name, "NAME");

        Token token = lex.getNextToken();

        if ( token.isKind("RPAREN") ) {
            lex.putBackToken( token );
            return new Node("params", name.getDetails(), null, null);
        }
        else{
            lex.putBackToken( token );
            Node first = parseParams();
            return new Node("params", name.getDetails(), first, null);
        }
    }

    public Node parseExpr() {
       // System.out.println("-----> parsing <expr>:");

        Token token = lex.getNextToken();

        // is a list
        if ( token.isKind("LPAREN") ) {
            Node first = parseList();
            return new Node("expr", first, null);
        }
        // is a named variable
        else if ( token.isKind("NAME")){
            return new Node("name", token.getDetails(), null, null);
        }
        // is a num
        else{
            return new Node("expr", token.getDetails(), null, null);
        }
    }

    public Node parseList(){
       // System.out.println("-----> parsing <list>:");

        Token token = lex.getNextToken();
        // empty list
        if ( token.isKind("RPAREN") ) {
            return new Node("list", null, null);
        }
        // function call
        else if ( token.isKind("NAME") || token.isKind("KEYWORD")){
            String funcType = token.getDetails();
            token = lex.getNextToken();

            if(funcType.equals("if")){
                lex.putBackToken(token);

                Node first = parseExpr();
                Node second = parseExpr();
                Node third = parseExpr();
                token = lex.getNextToken();
                errorCheck(token, "RPAREN");
                return new Node("if", funcType, first, second, third);
            }
            if(token.isKind("RPAREN")){
                return new Node("list", funcType, null, null);
            }
            else{
                lex.putBackToken(token);
                Node first = parseItems();

                token = lex.getNextToken();
                errorCheck(token, "RPAREN");
                return new Node("list", funcType, first, null);

            }
        }
        // just a list
        else {
            lex.putBackToken( token );
            Node first = parseItems();
            token = lex.getNextToken();
            errorCheck(token, "RPAREN", ")");
            return new Node("list", first, null);
        }
    }

    public Node parseItems(){
        //System.out.println("-----> parsing <items>:");

        Node first = parseExpr();
        Token token = lex.getNextToken();

        if(!token.isKind("RPAREN")){
            lex.putBackToken(token);
            Node second = parseItems();

            return new Node("items", first, second);
        }

        lex.putBackToken(token);
        return new Node("items", first, null);
    }

    private void error(String message){
      System.out.println(message);
      System.exit(1);
    }

    // check whether token is correct kind
    private void errorCheck( Token token, String kind ) {
        if( ! token.isKind( kind ) ) {
            System.out.println("Error:  expected " + token +
                    " to be of kind " + kind );
              try {
               File fileToDelete = new File("files/repl.txt");

               if (fileToDelete.delete()) {
                System.out.println("File deleted successfully !");
               } else {
                System.out.println("File delete operation failed !");
               }

              } catch (Exception e) {
               e.printStackTrace();
              }
            System.exit(1);
        }
    }

    // check whether token is correct kind and details
    private void errorCheck( Token token, String kind, String details ) {
        if( ! token.isKind( kind ) ||
                ! token.getDetails().equals( details ) ) {
            System.out.println("Error:  expected " + token +
                    " to be kind= " + kind +
                    " and details= " + details );
            System.exit(1);
        }
    }

    public ArrayList<Node> getDefs(){ return defs; }

    public ArrayList<String> getDefNames() { return defNames; }
}
