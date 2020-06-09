package Syntactic;

import Lexer.Scanner;
import Lexer.Token;
import Utils.Constants;
import Utils.ErrorHandler;
import com.sun.source.tree.Tree;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.exit;
import static java.lang.System.setOut;


/*
            int a = 3;

                root
              /      \
           node      node
        declaration  assign
           /  \        /  \
         int   a      a    3


            a = 8 + 3;

                root
                 |
             node assign
                /   \
               a    node operation
                        /    \
                       8      3

             a = ( b+(3+a))

                root
                 |
              node assign
                /   \
               a    parenthesis
                        |
                     node operation
                       /   \
                     b    parentesis
                                |
                             node operation
                                / \
                               3   a
 */


public class Parser {

    private List<Token> tokens = new ArrayList<>();
    private Token token;
    private int indexToken;
    private TreeNode tree;
    private Scanner scanner;
    private ErrorHandler errorHandler;


    static public enum nodeType {
        Root, node_declaration, leaf, node_assign, assign_expression, node_if, node_condition, parenthesis_operation, operation, condition_operation,
        statements, node_while
    }

    public Parser(Scanner scanner) {
        indexToken = 0;
        tree = new TreeNode(Parser.nodeType.Root);
        tree.setParent(null);
        tree.setChildren(new ArrayList<>());
        this.scanner = scanner;
        errorHandler = new ErrorHandler();
    }

    int open_parent_count = 0;
    int close_parent_count = 0;

    int open_curly_count = 0;
    int close_curly_count = 0;


    public void Parse() {

        token = scanner.getToken();
        //TreeNode node = new TreeNode(Parser.nodeType.Root, tree);

        while (token.getType() != Constants.EOF_TYPE) {

            if (token.getType() == Constants.INTEGER_TYPE || token.getType() == Constants.CHAR_TYPE
                    || token.getType() == Constants.BOOLEAN_TYPE) {

                tree = declarationStatement(tree);


            }
            else if (token.getType() == Constants.STRING_VALUE_TYPE) {

                tree = assignationStatement(tree);

            }
            else if (token.getType() == Constants.IF_TYPE) {

                tree = ifStatement(tree);

            }
            else if (token.getType() == Constants.WHILE_TYPE) {

                tree = whileStatement(tree);
            }
            else {
                errorHandler.printError("UKNNOWN VARIABLE TYPE (LINE " + token.getLine() + ")");
                exit(0);
            }

            if (close_parent_count == open_parent_count) {
                close_parent_count = 0;
                open_parent_count = 0;


            } else {
                errorHandler.printError("MISSING ')' (LINE " + token.getLine() + ")");
                exit(0);
            }
            if (close_curly_count == open_curly_count) {
                close_curly_count = 0;
                open_curly_count = 0;

            } else {
                //TODO: ERROR HANDLER
                errorHandler.printError("ERROR MISSING '}' near line " + token.getLine());
                exit(0);
            }

            token = scanner.getToken();

        }
        System.out.println("");
        printNode(tree, 0);

    }

    private void printNode(TreeNode node, int times) {
        for (TreeNode child : node.getChildren()) {
            String string = "";
            for (int i = 0; i <= times; i++) {
                string += "\t";
            }
            if (child.getNodeType() == Parser.nodeType.leaf || child.getNodeType() == Parser.nodeType.operation || child.getNodeType() == Parser.nodeType.condition_operation) {

                if (child.getNodeType() == Parser.nodeType.operation || child.getNodeType() == Parser.nodeType.condition_operation) {
                    System.out.println(string + child.getNodeType() + " : " + child.getValue().getData());
                    printNode(child, times + 1);
                } else {
                    System.out.println(string + child.getValue().getData());
                }

            } else {
                System.out.println(string + child.getNodeType());
                printNode(child, times + 1);
            }
        }
    }

    private TreeNode declarationStatement(TreeNode parent) {
        TreeNode node = new TreeNode(Parser.nodeType.node_declaration, parent);

        TreeNode child = new TreeNode();
        child = child.makeLeaf(Parser.nodeType.leaf, token, node);
        node.addChild(child);
        tokens.add(token);

        if (expectedNextToken(Constants.STRING_VALUE_TYPE)) {
            tokens.add(token);

            child = child.makeLeaf(Parser.nodeType.leaf, token, node);
            node.addChild(child);

            parent.addChild(node);

            parent = handleDeclaration(parent);
            return parent;

        } else {
            errorHandler.printError("MISSING OR INCOMPATIBLE VARIABLE NAME (LINE " + token.getLine() + ")");
            exit(0);
        }
        return null;
    }

    private TreeNode assignationStatement(TreeNode parent) {

        TreeNode node = new TreeNode(Parser.nodeType.node_assign, parent);

        TreeNode child = new TreeNode();

        child = child.makeLeaf(Parser.nodeType.leaf, token, node);
        node.addChild(child);
        tokens.add(token);

        if (expectedNextToken(Constants.OP_ASSIGN_TYPE)) {

            tokens.add(token);
            node = handleAssignExpression(node);

            parent.addChild(node);
            return parent;
        } else {
            errorHandler.printError("WRONG DECLARATION (LINE " + token.getLine() + ")");
            exit(0);
        }
        return null;
    }

    /*
                    while (a < c)
                        root
                         |
                       node_while
                      /       \
              node_condition  statements
                    |
              < logic_operation
                  /      \
                 a        c


        */

    private TreeNode whileStatement(TreeNode parent) {

        tokens.add(token);
        TreeNode node = new TreeNode(nodeType.node_while, parent);

        if (expectedNextToken(Constants.OPEN_PARENT_TYPE)) {
            open_parent_count++;
            TreeNode child = new TreeNode(nodeType.node_condition, node);

            child = handleCondition(child);
            if (close_parent_count == open_parent_count) {
                close_parent_count = 0;
                open_parent_count = 0;
                node.addChild(child);

                if (expectedNextToken(Constants.OPEN_CURLY_TYPE)) {
                    open_curly_count++;
                    child = new TreeNode(nodeType.statements, node);
                    child = parseStatements(child);
                    node.addChild(child);
                    parent.addChild(node);
                }
                else {
                    errorHandler.printError("MISSSING '{' (LINE " + token.getLine() + ")");
                    exit(0);
                }
            }
            else {
                //TODO: ERROR, abajo en el if pone comparator symbol?¿?¿?¿ porque
            }

        }
        else {
            errorHandler.printError("MISSING '(' (LINE " + token.getLine() + ")");
            exit(0);
        }

        return parent;
    }

    private TreeNode ifStatement(TreeNode parent) {

        /*
                    if ((a+b) < c)
                        root
                         |
                       node_if
                      /       \
              node_condition  statements
                    |
              < logic_operation
                  /      \
            parentesis    c
                |
           + operation
              /   \
             a     b

        */


        tokens.add(token);
        TreeNode node = new TreeNode(Parser.nodeType.node_if, parent);

        if (expectedNextToken(Constants.OPEN_PARENT_TYPE)) {
            open_parent_count++;

            TreeNode child = new TreeNode(Parser.nodeType.node_condition, node);

            child = handleCondition(child);
            if (close_parent_count == open_parent_count) {

                close_parent_count = 0;
                open_parent_count = 0;
                node.addChild(child);

                if (expectedNextToken(Constants.OPEN_CURLY_TYPE)) { // if (...) {
                    open_curly_count++;

                    child = new TreeNode(Parser.nodeType.statements, node);
                    child = parseStatements(child);
                    node.addChild(child);
                    parent.addChild(node);
                } else {
                    errorHandler.printError("MISSSING '{' (LINE " + token.getLine() + ")");
                    exit(0);
                }
            } else {
                errorHandler.printError("MISSING COMPARATOR SYMBOL OR ')' (LINE " + token.getLine() + ")");
                exit(0);
            }

        } else {
            errorHandler.printError("MISSING '(' (LINE " + token.getLine() + ")");
            exit(0);
        }

        return parent;
    }

    private TreeNode parseStatements(TreeNode node) {

        token = scanner.getToken();
        TreeNode child = new TreeNode();


        while (token.getType() != Constants.CLOSE_CURLY_TYPE) {

            if (token.getType() == Constants.INTEGER_TYPE || token.getType() == Constants.CHAR_TYPE
                    || token.getType() == Constants.BOOLEAN_TYPE) {

                //child = new TreeNode(Parser.nodeType.node_declaration, node);
                node = declarationStatement(node);


            } else if (token.getType() == Constants.STRING_VALUE_TYPE) {

                //child = new TreeNode(Parser.nodeType.node_assign, node);
                node = assignationStatement(node);


            } else if (token.getType() == Constants.IF_TYPE) {

                //child = new TreeNode(Parser.nodeType.node_if, node);
                node = ifStatement(node);

            } else if (token.getType() == Constants.WHILE_TYPE) {

                //child = new TreeNode(nodeType.node_while, node);
                node = whileStatement(node);
            }


            if (close_parent_count == open_parent_count) {

                close_parent_count = 0;
                open_parent_count = 0;
                //node.addChild(child);
            } else {
                //TODO: ERROR HANDLER
                errorHandler.printError("ERROR23 (LINE " + token.getLine() + ")");
                exit(0);
            }

            token = scanner.getToken();

        }
        close_curly_count++;

        return node;
    }

    private TreeNode handleCondition(TreeNode node) {

        token = scanner.getToken();
        TreeNode child = new TreeNode(Parser.nodeType.condition_operation, node);
        child.setNodeType(Parser.nodeType.condition_operation);
        TreeNode child2 = new TreeNode();

        if (token.getType() == Constants.STRING_VALUE_TYPE || token.getType() == Constants.NUMBER_TYPE
                || token.getType() == Constants.OPEN_PARENT_TYPE) {
            node.addChild(child);

            if (token.getType() == Constants.OPEN_PARENT_TYPE) {

                open_parent_count++;
                child2 = handleParenthesis();
                child2.setParent(child);
                child.addChild(child2);
            } else {

                child2 = child2.makeLeaf(Parser.nodeType.leaf, token, child);
                child.addChild(child2);
                //token = scanner.getToken();
            }
            token = scanner.getToken();
            child.setValue(token);

            if (token.getType() == Constants.OP_LOWER_TYPE || token.getType() == Constants.OP_GREATER_TYPE
                    || token.getType() == Constants.OP_EQUAL_TYPE || token.getType() == Constants.OP_NO_EQUAL_TYPE) {

                token = scanner.getToken();

                if (token.getType() == Constants.STRING_VALUE_TYPE || token.getType() == Constants.NUMBER_TYPE
                        || token.getType() == Constants.OPEN_PARENT_TYPE) {

                    if (token.getType() == Constants.OPEN_PARENT_TYPE) {

                        open_parent_count++;
                        child2 = handleParenthesis();


                        child.addChild(child2);
                    } else {

                        child2 = child2.makeLeaf(Parser.nodeType.leaf, token, child);
                        child.addChild(child2);
                        //token = scanner.getToken();
                    }

                    if (expectedNextToken(Constants.CLOSE_PARENT_TYPE)) {
                        close_parent_count++;
                        //child.getChildren().remove(1);
                        //node.addChild(child);
                        return node;

                    } else {
                        errorHandler.printError("MISSING ')' (LINE " + token.getLine() + ")");
                        exit(0);
                    }
                } else {
                    errorHandler.printError("MISSING SECOND VALUE TO COMPARE (LINE " + token.getLine() + ")");
                    exit(0);
                }

            } else {
                errorHandler.printError("MISSING OR WRONG COMPARATOR SYMBOL (LINE " + token.getLine() + ")");
                exit(0);
            }

        } else {
            errorHandler.printError("MISSING FIRST VALUE TO COMPARE (LINE " + token.getLine() + ")");
            exit(0);
        }


        return null;
    }

    private TreeNode handleDeclaration(TreeNode parent) {
        Token prev = token;
        token = scanner.getToken();
        tokens.add(token);

        if (token.getType() == Constants.DELIMITER_TYPE) {
            return parent;
        } else if (token.getType() == Constants.OP_ASSIGN_TYPE) {

            TreeNode node = new TreeNode(Parser.nodeType.node_assign, parent);
            TreeNode child = new TreeNode();

            child = child.makeLeaf(Parser.nodeType.leaf, prev, node);
            node.addChild(child);


            node = handleAssignExpression(node);
            parent.addChild(node);

            return parent;
        } else {
            errorHandler.printError("MISSING ';' OR WRONG DECLARATION (LINE " + token.getLine() + ")");
            exit(0);
        }
        return null;
    }

    private TreeNode handleAssignExpression(TreeNode parent) {
        Token prev = token;
        token = scanner.getToken();
        tokens.add(token);

        TreeNode node = new TreeNode();
        TreeNode child;

        if (token.getType() == Constants.STRING_VALUE_TYPE || token.getType() == Constants.NUMBER_TYPE) {

            if (prev.getType() == Constants.OP_ASSIGN_TYPE || prev.getType() == Constants.OP_ADD_TYPE
                    || prev.getType() == Constants.OP_SUBTRACT_TYPE) {

                node = node.makeLeaf(Parser.nodeType.leaf, token, parent);
                parent.addChild(node);

                return handleAssignExpression(parent);

            } else {
                //TODO ERROR HANDLER
                errorHandler.printError("ERROR26 (LINE " + token.getLine() + ")");
                exit(0);
            }
        } else if (token.getType() == Constants.OP_SUBTRACT_TYPE || token.getType() == Constants.OP_ADD_TYPE) {

            if (prev.getType() == Constants.STRING_VALUE_TYPE || prev.getType() == Constants.NUMBER_TYPE
                    || prev.getType() == Constants.CLOSE_PARENT_TYPE) {

                child = parent.getLastChild();
                node = new TreeNode(Parser.nodeType.operation, parent);
                node.setValue(token);
                child.setParent(node);
                node.addChild(child);

                node = handleAssignExpression(node);
                parent.replaceLastChild(node);

                return parent;
            } else {
                errorHandler.printError("NOT EXEPECTED OPERATOR (LINE " + token.getLine() + ")");
                exit(0);
            }
        } else if (token.getType() == Constants.OPEN_PARENT_TYPE) {

            if (prev.getType() == Constants.OP_ASSIGN_TYPE || prev.getType() == Constants.OP_ADD_TYPE
                    || prev.getType() == Constants.OP_SUBTRACT_TYPE) {

                open_parent_count++;

                node = handleParenthesis();
                node.setParent(parent);
                parent.addChild(node);

                return handleAssignExpression(parent);
            } else {
                errorHandler.printError("NOT EXPECTED PARENTHESIS OR MISSING OPERATOR (LINE " + token.getLine() + ")");
                exit(0);
            }

        } else if (token.getType() == Constants.DELIMITER_TYPE) {

            if (prev.getType() == Constants.NUMBER_TYPE || prev.getType() == Constants.STRING_VALUE_TYPE
                    || prev.getType() == Constants.CLOSE_PARENT_TYPE) {

                return parent;
            } else {
                //TODO: ERROR HANDLER expected token before ;
                errorHandler.printError("ERROR29 (LINE " + token.getLine() + ")");
                exit(0);
            }
        }

        return null;
    }

    private TreeNode handleParenthesis() {
        Token prev = token; //(
        token = scanner.getToken();
        tokens.add(token); //a
        TreeNode node = new TreeNode();

        TreeNode parenthesis = new TreeNode(Parser.nodeType.parenthesis_operation);


        if (token.getType() == Constants.NUMBER_TYPE || token.getType() == Constants.STRING_VALUE_TYPE) {

            node = node.makeLeaf(Parser.nodeType.leaf, token, parenthesis);
            parenthesis.addChild(node);

            prev = token;
            token = scanner.getToken();
            tokens.add(token);

            if (token.getType() == Constants.OP_SUBTRACT_TYPE || token.getType() == Constants.OP_ADD_TYPE) {
                node = new TreeNode(Parser.nodeType.operation, parenthesis);
                node.setValue(token);

                TreeNode child = parenthesis.getLastChild();
                node.addChild(child);
                node = handleOperation(node);
                parenthesis.replaceLastChild(node);
                return parenthesis;

            } else if (token.getType() == Constants.CLOSE_PARENT_TYPE) {

                close_parent_count++;
                return parenthesis;
            } else {
                errorHandler.printError("WRONG DECLARATION (LINE " + token.getLine() + ")");
                exit(0);
            }
        } else if (token.getType() == Constants.OPEN_PARENT_TYPE) {

            open_parent_count++;
            node = handleParenthesis();
            parenthesis.addChild(node);
            return parenthesis;
        } else {
            errorHandler.printError("WRONG VALUE TO ASSIGN (LINE " + token.getLine() + ")");
            exit(0);
        }
        return null;
    }

    private TreeNode handleOperation(TreeNode parent) { //Para el parentesis
        Token prev = token;
        token = scanner.getToken();
        tokens.add(token);

        TreeNode node = new TreeNode();

        if (token.getType() == Constants.NUMBER_TYPE || token.getType() == Constants.STRING_VALUE_TYPE) {

            if (prev.getType() == Constants.OP_SUBTRACT_TYPE || prev.getType() == Constants.OP_ADD_TYPE) {

                node = new TreeNode(Parser.nodeType.leaf, token, parent);
                parent.addChild(node);
                return handleOperation(parent);

            } else {
                //TODO: ERROR HANDLER token not expected after previous
                errorHandler.printError("ERROR33 (LINE " + token.getLine() + ")");
                exit(0);
            }
        } else if (token.getType() == Constants.OP_SUBTRACT_TYPE || token.getType() == Constants.OP_ADD_TYPE) {

            if (prev.getType() == Constants.NUMBER_TYPE || prev.getType() == Constants.STRING_VALUE_TYPE
                    || prev.getType() == Constants.CLOSE_PARENT_TYPE) {

                node = new TreeNode(Parser.nodeType.operation, parent);
                node.setValue(token);
                node.addChild(parent.getLastChild());
                node = handleOperation(node);
                parent.replaceLastChild(node);

                return parent;
            }
        } else if (token.getType() == Constants.OPEN_PARENT_TYPE) {

            if (prev.getType() == Constants.OP_SUBTRACT_TYPE || prev.getType() == Constants.OP_ADD_TYPE) {

                open_parent_count++;
                node = handleParenthesis();
                node.setParent(parent);
                parent.addChild(node);

                return handleOperation(parent);
            } else {
                //TODO: ERROR HANDLER
                errorHandler.printError("ERROR34 (LINE " + token.getLine() + ")");
                exit(0);
            }

        } else if (token.getType() == Constants.CLOSE_PARENT_TYPE) {

            if (prev.getType() == Constants.NUMBER_TYPE || prev.getType() == Constants.STRING_VALUE_TYPE
                    || prev.getType() == Constants.CLOSE_PARENT_TYPE) {

                close_parent_count++;
                return parent;
            } else {
                //TODO: ERROR HANDLER not expected )
                errorHandler.printError("ERROR30 (LINE " + token.getLine() + ")");
                exit(0);
            }
        } else {
            //TODO:ERROR HANDLER
            errorHandler.printError("ERROR35 (LINE " + token.getLine() + ")");
            exit(0);
        }
        return null;
    }

    private boolean expectedNextToken(int type) {
        token = scanner.getToken();

        if (token.getType() == type) return true;
        return false;
    }

    public TreeNode getTree() {
        return tree;
    }

}
