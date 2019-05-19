/*  a Node holds one node of a parse tree
    with several pointers to children used
    depending on the kind of node
*/

import java.util.*;
import java.awt.*;

public class Node {

    public static int count = 0;  // maintain unique id for each node

    private int id;

    private String kind;  // non-terminal or terminal category for the node
    private String info;  // extra information about the node such as

    // references to children in the parse tree
    private Node first, second, third;

    /** built in functions */
    // bif0 = funcs with no params
    private final static String[] bif0 = {"read", "nl"};
    // bif1 = funcs with one param
    private final static String[] bif1 = {"first", "rest", "null", "num", "list",
            "write", "quote"};
    // bif2 = funcs with two params
    private final static String[] bif2 = {"lt", "le", "eq", "ne", "and", "or",
            "not", "plus", "minus", "times", "div", "ins"};

    // construct a common node with no info specified
    public Node(String k, Node one, Node two) {
        kind = k;
        info = "";
        first = one;
        second = two;
        id = count;
        count++;
        //System.out.println(this);
    }

    // construct a def node with params frame for params
    public Node(String k, String inf, Node one, Node two, Node three){
        kind = k;
        info = inf;
        first = one;
        second = two;
        third = three;
        id = count;
        count++;
        //System.out.println(this);
    }

    // construct a node with specified info
    public Node(String k, String inf, Node one, Node two) {
        kind = k;
        info = inf;
        first = one;
        second = two;
        id = count;
        count++;
        //System.out.println(this);
    }

    // construct a node that is essentially a token
    public Node(Token token) {
        kind = token.getKind();
        info = token.getDetails();
        first = null;
        second = null;
        id = count;
        count++;
        //System.out.println(this);
    }

    public String toString() {
        return "#" + id + "[" + kind + "," + info + "]<" + nice(first) +
                " " + nice(second) + ">";
    }

    public String nice(Node node) {
        if (node == null) {
            return "-";
        } else {
            return "" + node.id;
        }
    }

    // produce array with the non-null children
    // in order
    public String getKind(){ return kind; }

    public String getInfo(){
      return info;
    }

    public Node[] getChildren() {
        int count = 0;
        if (first != null) count++;
        if (second != null) count++;
        if (third != null) count++;
        Node[] children = new Node[count];
        int k = 0;
        if (first != null) {
            children[k] = first;
            k++;
        }
        if (second != null) {
            children[k] = second;
            k++;
        }
        if (third != null) {
            children[k] = third;
        }

        return children;
    }

    //******************************************************
    // graphical display of this node and its subtree
    // in given camera, with specified location (x,y) of this
    // node, and specified distances horizontally and vertically
    // to children
    public void draw(Camera cam, double x, double y, double h, double v) {

        // System.out.println("draw node " + id);

        // set drawing color
        cam.setColor(Color.black);

        String text = kind;
        if (!info.equals("")) text += "(" + info + ")";
        cam.drawHorizCenteredText(text, x, y);

        // positioning of children depends on how many
        // in a nice, uniform manner
        Node[] children = getChildren();
        int number = children.length;
        // System.out.println("has " + number + " children");

        double top = y - 0.75 * v;

        if (number == 0) {
            return;
        } else if (number == 1) {
            children[0].draw(cam, x, y - v, h / 2, v);
            cam.drawLine(x, y, x, top);
        } else if (number == 2) {
            children[0].draw(cam, x - h / 2, y - v, h / 2, v);
            cam.drawLine(x, y, x - h / 2, top);
            children[1].draw(cam, x + h / 2, y - v, h / 2, v);
            cam.drawLine(x, y, x + h / 2, top);
        } else if (number == 3) {
            children[0].draw(cam, x - h, y - v, h / 2, v);
            cam.drawLine(x, y, x - h, top);
            children[1].draw(cam, x, y - v, h / 2, v);
            cam.drawLine(x, y, x, top);
            children[2].draw(cam, x + h, y - v, h / 2, v);
            cam.drawLine(x, y, x + h, top);
        } else {
            System.out.println("no Node kind has more than 3 children???");
            System.exit(1);
        }

    }// draw

    // needs to return Value objects
    public Value evaluate(ArrayList<Node> defsList, ArrayList<String> defNames, StackFrame params) {
        //System.out.println("current kind: " + kind);
        Value arg1, arg2;

        Value ZERO = new Value( 0 );
        Value ONE = new Value( 1 );

        if(kind.equals("name")){
            // retrieves value for name
            return params.retrieve(info);
        }
        else if(kind.equals("expr")){
            //System.out.println("Evaluating expression...");
            if(!info.equals("")){
                return new Value(Double.parseDouble(info));
            }
            else{
                return first.evaluate(defsList, defNames, params);
            }
        }
        else if(kind.equals("list")){
            //System.out.println("info: " + info);
            //System.out.println("Evaluating list...");
            if(!info.equals("")){
                if(member(info, bif0)){
                    switch (info) {
                        case "read":
                            Scanner s = new Scanner(System.in);
                            System.out.println("Enter a num: ");
                            System.out.print("> ");

                            String num = s.nextLine();
                            System.out.println(Double.parseDouble(num));
                        case "nl":
                            System.out.print("\n> ");
                    }
                } else if(member(info, bif1)){
                    arg1 = first.evaluate(defsList, defNames, params);
                    switch (info) {
                        case "not":
                            if(arg1.getNumber() == 0) return ONE;
                            else return ZERO;
                        case "first":
                            if(arg1.isEmpty() || arg1.isNull()) System.out.println("Error: empty list " + arg1.toString());
                            else return arg1.first();
                        case "rest":
                            return arg1.rest();
                        case "null":
                            if(arg1.isNull() || arg1.isEmpty()) return ONE;
                            else return ZERO;
                        case "num":
                            if(arg1.isNumber()) return ONE;
                            else return ZERO;
                        case "list":
                            if(!arg1.isNull()) return ONE;
                            else return ZERO;
                        case "write":
                            System.out.println("write: " + arg1 + " ");
                            return arg1;
                        case "quote":
                            return arg1;
                    }
                } else if(member(info, bif2)){
                    if(first.getKind().equals("items")) {
                        Value items = first.evaluate(defsList, defNames, params);
                            arg1 = items.first();
                            arg2 = items.rest().first();
                    }
                    // For lists and functions
                    else{
                        arg1 = first.evaluate(defsList, defNames, params);
                        arg2 = second.evaluate(defsList, defNames, params);
                    }
                    switch (info) {
                        case "plus":
                            if(!arg2.isNumber()) arg2 = arg2.first();
                            double sum = arg1.getNumber() + arg2.getNumber();
                            return new Value(sum);
                        case "minus":
                            if(!arg2.isNumber()) arg2 = arg2.first();
                            double min = arg1.getNumber() - arg2.getNumber();
                            return new Value(min);
                        case "times":
                            if(!arg2.isNumber()) arg2 = arg2.first();
                            double mul = arg1.getNumber() * arg2.getNumber();
                            return new Value(mul);
                        case "div":
                            if(!arg2.isNumber()) arg2 = arg2.first();
                            double div = arg1.getNumber() / arg2.getNumber();
                            return new Value(div);
                        case "lt":
                            if(!arg2.isNumber()) arg2 = arg2.first();
                            if(arg1.getNumber() < arg2.getNumber()) return ONE;
                            else return ZERO;
                        case "le":
                            if(!arg2.isNumber()) arg2 = arg2.first();
                            if(arg1.getNumber() <= arg2.getNumber()) return ONE;
                            else return ZERO;
                        case "eq":
                            if(!arg2.isNumber()) arg2 = arg2.first();
                            if(arg1.getNumber() == arg2.getNumber()) return ONE;
                            else return ZERO;
                        case "ne":
                            if(!arg2.isNumber()) arg2 = arg2.first();
                            if(arg1.getNumber() != arg2.getNumber()) return ONE;
                            else return ZERO;
                        case "and":
                            if(!arg2.isNumber()) arg2 = arg2.first();
                            if(arg1.getNumber() > 0 && arg2.getNumber() > 0) return ONE;
                            else return ZERO;
                        case "or":
                            if(!arg2.isNumber()) arg2 = arg2.first();
                            if(arg1.getNumber() > 0 || arg2.getNumber() > 0) return ONE;
                            else return ZERO;
                        case "ins":
                            Value newList = arg2;
                            newList = arg2.insert(arg1);
                            System.out.println("after ins: " + newList.toString());
                            System.out.println("arg1: " + arg1.toString());
                            System.out.println("arg2: " + arg2.toString());
                            return newList;
                    }
                }
                // Handles and locates user defined functions
                else if(defNames.contains(info)){
                    int index = defNames.indexOf(info);
                   // System.out.println("Evaluating user defined def...");
                    Node def = defsList.get(index);
                    if(def.first.getKind().equals("params")) {
                        params = passParams(def, defsList, defNames, first, params); // evaluate the params passed
                        System.out.println("Params: " + params.toString());
                        return def.second.evaluate(defsList, defNames, params);
                    } else{
                        return def.first.evaluate(defsList, defNames,params);
                    }
                }
            }
            else{
                // empty list
                if(first == null){
                    return new Value();
                }
                return first.evaluate(defsList, defNames, params);
            }
        }
        else if(kind.equals("if")){
            Value condition = first.evaluate(defsList, defNames, params);
            if(condition.getNumber() == 1) return second.evaluate(defsList, defNames, params);
            else return third.evaluate(defsList, defNames, params);
        }
        else{ // items
            //System.out.println("Evaluating items...");
            Value items = new Value();

            if (second == null){
                //System.out.println("Inserting " + first.toString());
                if(first.getKind().equals("name")){
                    if(params.retrieve(first.info).isEmpty()){

                    }
                    Value holder = first.evaluate(defsList, defNames, params);
                    if(!holder.isNull()) return holder;
                }
                items = items.insert(first.evaluate(defsList, defNames, params));
                return items;
            }
            else{
                items = second.evaluate(defsList, defNames, params);
                items = items.insert(first.evaluate(defsList, defNames, params));
                return items;
            }
        }
        return null;
    }

    // return whether target is a member of array
    private static boolean member (String target, String[]array ){
        for (int k = 0; k < array.length; k++) {
            if (target.equals(array[k])) {
                return true;
            }
        }
        return false;
    }

    // Take params from the user and assign them to the appropriate names
    private StackFrame passParams(Node def, ArrayList<Node> defsList, ArrayList<String> defNames,
                                  Node userParams, StackFrame params){
        StackFrame p = new StackFrame();
        ArrayList<String> paramNames = new ArrayList<>();

        Value fromUser = userParams.evaluate(defsList, defNames, params);
        System.out.println(fromUser.toString());

        Value f = fromUser.first();
        Value r = fromUser.rest();

        // extract the paramNames
        Node param = def.first;
        while(param != null){
            paramNames.add(param.getInfo());
            param = param.first;
        }

        // If only one user param and first is num, just take fromUser and assign it to that param.
        // Chances are that a list was passed, although differently than usual.
        if(paramNames.size() == 1 && f.isNumber() && !r.isEmpty()){
            p.add(paramNames.get(0), fromUser);
        }
        else { // assign user params to param names
            for (int i = 0; i < paramNames.size(); i++) {
                p.add(paramNames.get(i), f);
                if (!r.isEmpty()) {
                    f = r.first();
                    r = r.rest();
                }
            }
        }

//        // get user params as a list
//        Value userParamsList = userParams.evaluate(defsList, defNames, params);
//        if(!userParamsList.isEmpty()) {
//            f = userParamsList.first();
//            r = userParamsList.rest();
//        }
//
//        // get def param names
//        Node param = def.first;
//
//        // if multiple args, but only one param (for lists)
//        if(param.first == null && r != null){
//            paramNames.add(param.getInfo());
//            p.add(param.getInfo(), userParamsList);
//            return p;
//        }
//

//
//        // add all names and values to StackFrame p
//        for(int i = 0; i < paramNames.size(); i++){
//            // for items
//            if(userParams.getKind().equals("list")){
//                p.add(paramNames.get(i), userParamsList);
//            } else {
//                p.add(paramNames.get(i), f);
//                if (!r.isEmpty()) {
//                    f = r.first();
//                    r = r.rest();
//                }
//            }
//        }

        return p;
    }

}// Node
