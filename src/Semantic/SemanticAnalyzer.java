package Semantic;

import Semantic.SymbolTable.Symbol;
import Semantic.SymbolTable.SymbolTable;
import Syntactic.Parser;
import Syntactic.TreeNode;
import Utils.Constants;
import Utils.ErrorHandler;

import java.util.ArrayList;
import java.util.HashMap;

public class SemanticAnalyzer {

    private TreeNode tree;
    private ErrorHandler errorHandler;

    public SemanticAnalyzer(TreeNode tree) {
        errorHandler = new ErrorHandler();
        this.tree = tree;
    }

    public void setTree(TreeNode tree) {
        this.tree = tree;
    }

    public HashMap<String, Symbol> analyze() {
        System.out.println("\nAnalyzing Semantics\n");
        //TreeNode.printTree(tree);
        exploreTree(tree);
        SymbolTable.printSymbolTable();

        return SymbolTable.getScope(Constants.SCOPE_GLOBAL);
    }

    public void exploreTree(TreeNode tree) {
        if (tree.getNodeType().equals(Parser.nodeType.Root)) {
            exploreSubTree(tree);
        } else if (tree.isLeaf()) {
            TreeNode.printOrigin(TreeNode.getOrigin(tree));
        } else {
            if (tree.getNodeType().equals(Parser.nodeType.node_declaration)) verifyDeclarationNode(tree);
            if (tree.getNodeType().equals(Parser.nodeType.node_assign)) verifyAssignNode(tree);
            if (tree.getNodeType().equals(Parser.nodeType.node_if)) verifyIfNode(tree);
            if (tree.getNodeType().equals(Parser.nodeType.node_while)) verifyWhileLoop(tree);
            exploreSubTree(tree);
        }
    }

    private void exploreSubTree(TreeNode tree) {
        for (TreeNode node : tree.getChildren()) exploreTree(node);
    }

    private boolean verifyDeclarationNode(TreeNode node) {
        if (node.getChildren() != null && node.getChildren().size() == 2) {
            //System.out.println("DECLARATION CORRECT");
            Symbol symbol = new Symbol();
            symbol.setType(node.getChildren().get(0).getValue().getType());
            symbol.setName(node.getChildren().get(1).getValue().getData());
            switch (symbol.getType()) {
                case Constants.INTEGER_TYPE:
                    symbol.setValue(String.valueOf(Constants.INTEGER_DEFAULT_VALUE));
                    break;
                default:
                    break;
            }
            if (!SymbolTable.insertSymbol(symbol, Constants.SCOPE_GLOBAL)) {
                errorHandler.printError("VARIABLE [" + symbol.getName() + "] ALREADY EXISTS");
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    private boolean verifyAssignNode(TreeNode node) {
        String var = node.getChildren().get(0).getValue().getData();
        Symbol s = SymbolTable.getSymbol(var, Constants.SCOPE_GLOBAL);
        if (s == null) {
            errorHandler.printError("VARIABLE [" + var + "] IS NOT DECLARED");
            return false;
        }

        TreeNode rightLeaf = searchRightLeaf(node);

        if (rightLeaf.getParent() == node) {
            //System.out.println("DIRECT ASSIGN");
            verifyType(rightLeaf.getValue().getData(), s.getType());
        } else {
            while (rightLeaf.getParent() != node) {
                if (rightLeaf.getParent().getNodeType().equals(Parser.nodeType.operation)) {
                    verifyOperationNode(rightLeaf.getParent(), s.getType());
                }
                rightLeaf = rightLeaf.getParent();
            }
        }

        return true;
    }

    private TreeNode searchRightLeaf(TreeNode node) {
        while (!node.getChildren().get(node.getChildren().size() - 1).isLeaf()) {
            node = node.getChildren().get(node.getChildren().size() - 1);
        }
        return node.getChildren().get(node.getChildren().size() - 1);
    }

    private boolean verifyOperationNode(TreeNode node, int type) {
        for (TreeNode child : node.getChildren()) {
            /*Como venimos de abajo, puede ser que el hijo derecho del nodo operación actual sea otro nodo operación o
            un nodo parentesis, de manera que sólo verificamos el type de los nodos leaf.*/
            if (child.isLeaf()) {
                verifyType(child.getValue().getData(), type);
            }
        }
        return true;
    }

    private boolean verifyIfNode(TreeNode node) {
        for (TreeNode n : node.getChildren()) {
            //System.out.println(n.getNodeType().name());
            if (n.getNodeType().equals(Parser.nodeType.node_condition)) {
                verifyConditionNode(n);
            }

            if (n.getNodeType().equals(Parser.nodeType.statements)) {
                verifyStatements(n);
            }
        }

        return true;
    }

    private void getAllLeaves(TreeNode node, ArrayList<TreeNode> nodes) {
        for (TreeNode n : node.getChildren()) {
            if (n.isLeaf()) {
                nodes.add(n);
            } else {
                getAllLeaves(n, nodes);
            }
        }
    }

    /**
     * La función recibe un array de TreeNode y verifica si todos los nodos son del mismo tipo, el tipo del primer
     * nodo es el que determina de que tipo deben ser los demás nodos.
     *
     * @param nodes
     * @return
     */
    private boolean sameType(ArrayList<TreeNode> nodes) {
        int prevType = -1;
        int currentType = -1;
        boolean error = false;

        for (TreeNode n : nodes) {
            Symbol s = SymbolTable.getSymbol(n.getValue().getData(), Constants.SCOPE_GLOBAL);
            if (error = (s == null)) {
                /* Si no encontramos la variable en la tabla de simbolos, comprovamos que no sea un typo de valor predefinido */
                if (n.getValue().getData().matches(Constants.NUMBER_PATTERN)) {
                    error = false;
                    currentType = Constants.INTEGER_TYPE;
                }

                if (n.getValue().getData().matches(Constants.BOOLEAN_PATTERN)) {
                    error = false;
                    currentType = Constants.BOOLEAN_TYPE;
                }

                /* Si la variable no coincide con algun typo de valor predeterminado mostramos error */
                if (error) {
                    errorHandler.printError("ERROR: VARIABLE [" + n.getValue().getData() + "] IS NOT DECLARED OR IS OF INCOMPATIBLE TYPE");
                }

            } else {
                currentType = s.getType();
            }

            if (!error) {
                if (prevType == -1) {
                    prevType = currentType;
                } else {
                    if (currentType != prevType) {
                        error = true;
                    }
                }
            }
        }
        return !error;
    }

    private boolean verifyConditionNode(TreeNode node) {
        ArrayList<TreeNode> nodes = new ArrayList<>();
        getAllLeaves(node, nodes);
        if (!sameType(nodes)) {
            errorHandler.printError("ERROR: CONDITION NODE VARIABLES SHOULD BE THE SAME TYPE");
        }
        return true;
    }

    private boolean verifyStatements(TreeNode node) {
        //TODO
        System.out.println("Verifying statements");
        // Como de momento no tenenemos scopes, al entrar en la verificacion de los statements, no hacemos nada aqui.
        // La comprobación se realizara en el main loop "exploreTree" pudiendo ser declaracion o asignacion.
        return true;
    }

    private boolean verifyWhileLoop(TreeNode node) {
        verifyIfNode(node);
        return true;
    }

    private boolean verifyType(String value, int type) {
        switch (type) {
            case Constants.INTEGER_TYPE:
                if (value.matches(Constants.NUMBER_PATTERN)) {
                    //System.out.println("VALUE IS CONSTANT NUMBER: " + value);
                    return true;
                } else {
                    //System.out.println("VALUE IS VARIABLE: " + value);
                    Symbol s = SymbolTable.getSymbol(value, Constants.SCOPE_GLOBAL);
                    if (s == null) {
                        errorHandler.printError("VARIABLE [" + value + "] IS NOT DECLARED OR IS OF INCOMPATIBLE TYPE");
                        return false;
                    } else {
                        /*Cuando tengamos más tipos de variables también se verificara si son del mismo tipo.*/
                        if (s.getType() != type) {
                            errorHandler.printError("VARIABLE [" + value + "] IS OF INCOMPATIBLE TYPE");
                            return false;
                        }
                    }
                }
            default:
                return false;
        }
    }

}
