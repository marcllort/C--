package IntermediateCode;

import Lexer.Keywords;
import Lexer.Token;
import Syntactic.Parser;
import Syntactic.TreeNode;
import Utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class IntermediateCodeGenerator {
    private int labelNumber;
    private int temporalVar;
    public List<IntermediateTableLine> table;

    public IntermediateCodeGenerator() {
        temporalVar = 0;
        table = new ArrayList<>();
    }

    public String generateCode(TreeNode tree) {
        String globalCode = "";
        for (TreeNode node : tree.getChildren()) {
            temporalVar = 0;
            switch (node.getNodeType()) {
                case node_if:
                    globalCode += ifCode(node);
                    break;
                case node_assign:
                    globalCode += operationsCode(node);
                    break;
                case node_declaration:
                    globalCode += assignationCode(node);
                    break;
                case node_while:
                    globalCode += whileCode(node);
            }
        }
        return globalCode;
    }

    public String operationsCode(TreeNode operationRoot) {
        String code = "";
        boolean leafs = true;
        for (TreeNode children : operationRoot.getChildren()) {
            if (!children.isLeaf()) {
                leafs = false;
                if (children.getNodeType() == Parser.nodeType.parenthesis_operation) {
                    code = operationsCode(children);


                } else {
                    code = operationsCode(children);
                }
            }
        }
        if (leafs && operationRoot.getNodeType() == Parser.nodeType.operation) {
            //if the operation has to be stored in a temporal var and the operands are leafs
            Operand result = new Operand(Constants.IDENTIFIER_TYPE, "t" + temporalVar);
            Operand operand1 = new Operand(operationRoot.getChildren().get(0).getValue().getType(), operationRoot.getChildren().get(0).getValue().getData());
            Operand operand2 = new Operand(operationRoot.getChildren().get(1).getValue().getType(), operationRoot.getChildren().get(1).getValue().getData());
            Operand operation = new Operand(operationRoot.getValue().getType(), operationRoot.getValue().getData());

            table.add(new IntermediateTableLine(result, operand1, operand2, operation));

            return result.getName() + " = " + operand1.getName() + " " + operation.getName() + " " + operand2.getName() + '\n';

        } else if (operationRoot.getNodeType() == Parser.nodeType.operation) {
            if (operationRoot.getChildren().get(0).isLeaf()) {
                //if the operation has to be stored in a temporal var and the operands are a leaf and a temporal var

                Operand result = new Operand(Constants.IDENTIFIER_TYPE, "t" + (++temporalVar));
                Operand operand1 = new Operand(operationRoot.getChildren().get(0).getValue().getType(), operationRoot.getChildren().get(0).getValue().getData());
                Operand operand2 = new Operand(Constants.IDENTIFIER_TYPE, "t" + (temporalVar - 1));
                Operand operation = new Operand(operationRoot.getValue().getType(), operationRoot.getValue().getData());

                table.add(new IntermediateTableLine(result, operand1, operand2, operation));

                return code + result.getName() + " = " + operand1.getName() + " " + operation.getName() + " " + operand2.getName() + '\n';

            } else {
                //if the operation has to be stored in a temporal var and the operands are a temporal var and a leaf

                Operand result = new Operand(Constants.IDENTIFIER_TYPE, "t" + (++temporalVar));
                Operand operand1 = new Operand(Constants.IDENTIFIER_TYPE, "t" + (temporalVar - 1));
                Operand operand2 = new Operand(operationRoot.getChildren().get(1).getValue().getType(), operationRoot.getChildren().get(1).getValue().getData());
                Operand operation = new Operand(operationRoot.getValue().getType(), operationRoot.getValue().getData());
                table.add(new IntermediateTableLine(result, operand1, operand2, operation));

                return code + result.getName() + " = " + operand1.getName() + " " + operation.getName() + " " + operand2.getName() + '\n';

            }
        } else if (operationRoot.getNodeType() == Parser.nodeType.node_assign && !leafs && operationRoot.getChildren().size() > 1) {
            //if is an assignation of a temporal var to a variable

            Operand result = new Operand(operationRoot.getChildren().get(0).getValue().getType(), operationRoot.getChildren().get(0).getValue().getData());
            Operand operand1 = new Operand(Constants.IDENTIFIER_TYPE, "t" + (temporalVar));
            Operand operation = new Operand(Constants.OP_ASSIGN_TYPE, Constants.OP_ASSIGN_PATTERN);
            table.add(new IntermediateTableLine(result, operand1, operation));

            return code + result.getName() + " " + operation.getName() + " " + operand1.getName() + '\n';
        } else if (operationRoot.getNodeType() == Parser.nodeType.node_assign && leafs) {
            //if i an assignation of a variable to another variable
            Operand result = new Operand(operationRoot.getChildren().get(0).getValue().getType(), operationRoot.getChildren().get(0).getValue().getData());
            Operand operand1 = new Operand(operationRoot.getChildren().get(1).getValue().getType(), operationRoot.getChildren().get(1).getValue().getData());
            Operand operation = new Operand(Constants.OP_ASSIGN_TYPE, Constants.OP_ASSIGN_PATTERN);
            table.add(new IntermediateTableLine(result, operand1, operation));

            return code + result.getName() + " = " + operand1.getName() + '\n';
        } else if (operationRoot.getNodeType() == Parser.nodeType.parenthesis_operation) {
            return code;
        } else {
            return code;
        }

    }

    public String ifCode(TreeNode rootNodeIf) {
        int localLabel = labelNumber;
        labelNumber+=2;
        String code = "";
        TreeNode conditionOperator = rootNodeIf.getChildren().get(0)/*node_condition*/.getChildren().get(0)/*condition_operation*/;
        if (conditionOperator.getChildren().size() == 2) {   //Normal condition operation
            String tvar1, tvar2;
            if (!conditionOperator.getChildren().get(0).isLeaf()) {
                code = operationsCode(conditionOperator.getChildren().get(0));
                tvar1 = "t" + (temporalVar++);
            } else {
                tvar1 = conditionOperator.getChildren().get(0).getValue().getData();
            }
            if (!conditionOperator.getChildren().get(1).isLeaf()) {
                code = operationsCode(conditionOperator.getChildren().get(1));
                tvar2 = "t" + (temporalVar++);
            } else {
                tvar2 = conditionOperator.getChildren().get(1).getValue().getData();
            }
            code += "IF " + tvar1 + " " + conditionOperator.getValue().getData() + " " + tvar2 + " GOTO L" + localLabel + " \n";
        } else {
            code += "IF " + conditionOperator.getChildren().get(0).getValue().getData() + " GOTO L" + localLabel + " \n";         //one node operation conditions
        }
        code += " GOTO L" + ++localLabel + " \n";
        code += "L" + --localLabel + " \n";

        code += generateCode(rootNodeIf.getChildren().get(1)/*statements*/);




        //code += generateCode(rootNodeIf.getChildren().get(1)/*SHOULD BE ELSE STATEMENTS*/);
        code += "L" + ++localLabel + " \n";


        return code;
    }

    public String whileCode(TreeNode rootNodeIf) {
        String code = "";
        int localLabel = labelNumber;
        labelNumber+=2;
        TreeNode conditionOperator = rootNodeIf.getChildren().get(0)/*node_condition*/.getChildren().get(0)/*condition_operation*/;
        if (conditionOperator.getChildren().size() == 2) {   //Normal condition operation
            String tvar1, tvar2;
            if (!conditionOperator.getChildren().get(0).isLeaf()) {
                code = operationsCode(conditionOperator.getChildren().get(0));
                tvar1 = "t" + (temporalVar++);
            } else {
                tvar1 = conditionOperator.getChildren().get(0).getValue().getData();
            }
            if (!conditionOperator.getChildren().get(1).isLeaf()) {
                code = operationsCode(conditionOperator.getChildren().get(1));
                tvar2 = "t" + (temporalVar++);
            } else {
                tvar2 = conditionOperator.getChildren().get(1).getValue().getData();
            }
            code += "LOOP" + localLabel + ": IF " + tvar1 + " " + conditionOperator.getValue().getData() + " " + tvar2 + " GOTO LOOP" + ++localLabel + "\n";
        } else {
            code += "LOOP" + localLabel + ": IF " + conditionOperator.getChildren().get(0).getValue().getData() + " GOTO LOOP" + ++localLabel + "\n";       //one node operation conditions
        }
        code += generateCode(rootNodeIf.getChildren().get(1)/*statements*/);

        code += "GOTO  LOOP" + --localLabel + " \n";
        code += "LOOP" + ++localLabel + " \n";

        return code;
    }

    public String assignationCode(TreeNode nodeAssign) {
        //En cas de ser una declaracio, a la taula creem una linia amb l'operand 1 i a l'operació el tipus de variable que declarem.
        table.add(new IntermediateTableLine(new Operand(nodeAssign.getChildren().get(1).getValue().getType(), nodeAssign.getChildren().get(1).getValue().getData()), new Operand(nodeAssign.getChildren().get(0).getValue().getType(), nodeAssign.getChildren().get(0).getValue().getData())));

        return nodeAssign.getChildren().get(0).getValue().getData() + " " + nodeAssign.getChildren().get(1).getValue().getData() + '\n';
    }


}
