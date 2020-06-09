package IntermediateCode;

import Lexer.Token;

public class IntermediateTableLine {

    private Operand operand1;
    private Operand operand2;

    private Operand result;

    private Operand operation;

    public IntermediateTableLine(Operand result,Operand operand1, Operand operand2, Operand operation) {
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.operation = operation;
        this.result = result;
    }

    //Constructor to create lines where we declare variables in this case we'll have in operant 1 the name of the variable and in operation the type
    public IntermediateTableLine(Operand operand1,Operand operation) {
        this.operand1 = operand1;
        this.operation = operation;
    }

    //Constructor to create lines where we assign a operand1 to result
    public IntermediateTableLine(Operand result, Operand operand1,Operand operation) {
        this.result = result;
        this.operand1 = operand1;
        this.operation = operation;
    }

    public Operand getOperand1() {
        return operand1;
    }

    public Operand getOperand2() {
        return operand2;
    }

    public Operand getOperation() {
        return operation;
    }

    public Operand getResult() {
        return result;
    }

    public void setOperand1(Operand operand1) {
        this.operand1 = operand1;
    }

    public void setOperand2(Operand operand2) {
        this.operand2 = operand2;
    }

    public void setOperation(Operand operation) {
        this.operation = operation;
    }

    public void setResult(Operand result) {
        this.result = result;
    }
}
