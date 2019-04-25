import java.util.*;
import java.io.*;
public class Lexer {

    public static String margin = "";

    // holds any number of tokens that have been put back
    private Stack<Token> stack;

    // the source of physical symbols
    // (use BufferedReader instead of Scanner because it can
    //  read a single physical symbol)
    private BufferedReader input;

    // one lookahead physical symbol
    private int lookahead;

    // construct a Lexer ready to produce tokens from a file
    public Lexer( String fileName ) {
        try {
            BufferedReader input = new BufferedReader( new FileReader( fileName ) );
        }
        catch(Exception e) {
            File fileNameInput = new File("files/repl.txt");
            try {
             if (fileNameInput.createNewFile()) {
              System.out.println("File named " + fileName
                + " created successfully !");
             } else {
              System.out.println("File with name " + fileName
                + " already exixts !");
             }
           } catch (IOException b) {
             b.printStackTrace();
            }
            try{
              FileWriter file2 = new FileWriter("files/repl.txt",true);
              BufferedWriter writer = new BufferedWriter (file2);
              writer.newLine();   //Add new line
              writer.write(fileName);
              writer.close();
              input = new BufferedReader(new FileReader(fileNameInput));
              System.out.println("Problem opening file named [" + fileName + "], instead creating a file based on input");
              String input = fileName;
            } catch (IOException c){
              System.out.println("failed to create and write file");
              c.printStackTrace();
            }

        }
        stack = new Stack<Token>();
        lookahead = 0;  // indicates no lookahead symbol present
    }// constructor

    // produce the next token
    private Token getNext() {
        if( ! stack.empty() ) {
            //  produce the most recently putback token
            Token token = stack.pop();
            return token;
        }
        else {
            // produce a token from the input source

            int state = 0;  // state of FA
            String data = "";  // specific info for the token
            boolean done = false;
            int sym;  // holds current symbol

            do {
                sym = getNextSymbol();

// System.out.println("current symbol: " + sym + " state = " + state );

                if ( state == 0 ) {
                    if ( sym == 9 || sym == 10 || sym == 13 ||
                            sym == 32 ) {// whitespace
                        state = 0;
                    }
                    else if ( letter(sym) ) {// any letter (not just lowercase)
                        data += (char) sym;
                        state = 1;
                    }
                    else if ( digit( sym ) ) {
                        data += (char) sym;
                        state = 4;
                    }
                    else if ( sym == '-'){
                        state = 2;
                    }
                    else if ( sym == ';' ) {
                        data += (char) sym;
                        state = 5;
                    }
                    else if (sym == '(' || sym == ')') {
                        data += (char) sym;
                        state = 8;
                        done = true;
                    }
                    else if ( sym == -1 ) {// end of file
                        state = 7;
                        done = true;
                    }
                    else {
                        error("Error in lexical analysis phase with symbol "
                                + sym + " in state " + state );
                    }
                }
                else if ( state == 1){
                    if( letter(sym)){
                        data += (char) sym;
                        state = 1;
                    }
                    else {
                        putBackSymbol(sym);
                        done =true;
                    }
                }
                else if ( state == 2 ) {
                    if (digit(sym) ) {
                        data += (char) sym;
                        state = 3;
                    }
                    else {// done with variable token
                        error("a minus sign must be followed by a number");
                    }
                }

                else if ( state == 3 ) {
                    if ( digit(sym) ) {
                        data += (char) sym;
                        state = 3;
                    }
                    else if ( sym == '.' ) {
                        data += (char) sym;
                        state = 9;
                    }
                    else {// done with number token
                        putBackSymbol( sym );
                        done = true;
                    }

                }

                else if ( state == 4 ) {
                    if ( digit(sym) ) {
                        data += (char) sym;
                        state = 4;
                    }
                    else if (sym=='.'){
                        data += (char)sym;
                        state = 10;
                    }
                    else {// done with number token
                        putBackSymbol( sym );
                        done = true;
                    }
                }

                else if ( state == 5 ) {
                    if ( sym==13||sym==10 ) {
                        done = true;
                    }
                    else {
                        state = 5;
                    }
                }

                else if ( state == 6 ) {
                    if ( (' '<=sym && sym<='~') && sym != '\"' ) {
                        data += (char) sym;
                        state = 6;
                    }
                    else if ( sym == '\"' ) {
                        state = 7;
                        done = true;
                    }
                }




            }while( !done );

            // generate token depending on stopping state
            Token token;
            if ( state == 1){
                if ( data.equals("ins") || data.equals("first") ||
                        data.equals("rest") || data.equals("null") ||
                        data.equals("num") || data.equals("list") ||
                        data.equals("read") ||data.equals("write") ||
                        data.equals("nl") ||data.equals("quote") ||
                        data.equals("quit") ||data.equals("plus") ||
                        data.equals("minus") ||data.equals("times") ||
                        data.equals("div") ||data.equals("lt") ||
                        data.equals("le") ||data.equals("eq") ||
                        data.equals("ne") ||data.equals("and") ||
                        data.equals("or") ||data.equals("not") ||
                        data.equals("define")
                        ) {
                    return new Token( "KEYWORD", data );
                }
                else{
                    return new Token ("NAME",data);
                }
            }
            if(state == 3){
                return new Token("NEGATIVE NUMBER",data);
            }
            if(state == 4){
                return new Token("NUMBER",data);
            }
            if ( state == 5 ) {
                return new Token("COMMENT",data);
            }
            else if ( state==9 ) {
                return new Token( "NEGATIVE DECIMAL NUMBER", data );
            }
            else if ( state == 10 ) {
                return new Token( "DECIMAL NUMBER", data );
            }
            else if ( state == 8 ) {
                if(data.equals("(")){
                    return new Token ("LPAREN", data);
                } else if(data.equals(")")){
                    return new Token ("RPAREN", data);
                }
            }
            else if ( state == 7 ) {
                return new Token( "eof", data );
            }else {// Lexer error
                error("somehow Lexer FA halted in bad state " + state );
                return null;
            }

        }// else generate token from input
        return null;
    }// getNext

    public Token getNextToken() {
        Token token = getNext();
        System.out.println("                     got token: " + token );
        return token;
    }

    public void putBackToken( Token token )
    {
        System.out.println( margin + "put back token " + token.toString() );
        stack.push( token );
    }

    // next physical symbol is the lookahead symbol if there is one,
    // otherwise is next symbol from file
    private int getNextSymbol() {
        int result = -1;

        if( lookahead == 0 ) {// is no lookahead, use input
            try{  result = input.read();  }
            catch(Exception e){}
        }
        else {// use the lookahead and consume it
            result = lookahead;
            lookahead = 0;
        }
        return result;
    }

    private void putBackSymbol( int sym ) {
        if( lookahead == 0 ) {// sensible to put one back
            lookahead = sym;
        }
        else {
            System.out.println("Oops, already have a lookahead " + lookahead +
                    " when trying to put back symbol " + sym );
            System.exit(1);
        }
    }// putBackSymbol

    private boolean letter( int code ) {
        return 'a'<=code && code<='z' ||
                'A'<=code && code<='Z';
    }

    private boolean digit( int code ) {
        return '0'<=code && code<='9';
    }

    private boolean printable( int code ) {
        return ' '<=code && code<='~';
    }

    private static void error( String message ) {
        System.out.println( message );
        System.exit(1);
    }

    public static void main(String[] args) throws Exception {
        System.out.print("Enter file name: ");
        Scanner keys = new Scanner( System.in );
        String name = keys.nextLine();

        Lexer lex = new Lexer( name );
        Token token;

        do{
            token = lex.getNext();
            System.out.println( token.toString() );
        }while( ! token.getKind().equals( "eof" )  );

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

    }

}