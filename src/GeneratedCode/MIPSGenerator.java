package GeneratedCode;


/*
Operacions: http://alumni.cs.ucr.edu/~vladimir/cs161/mips.html
x := y + 2          // Crec que caldrà modificar el 3ac que tenim generat, ja que ara no sabem si son declaracio i assignacio, son sempre assignacions

al x i y ser int, ocupen 4 bytes, que es guarden de forma descendent rspecte el registre  (pag 46 pdf semantic)
Per tant, X estara a la posicio 0 (al ser la 1a variable) i y a la posicio -4

en MIPS és:

MOVE $fp, $sp               // Inicialitzar $fp (posem on comencen les funcions)
SUB $sp, $sp , 8            // Guardar memoria per variables globals al registre $sp
//Codi Main
LW $8, -4($fp)              // Assignar a $8 el espai de memoria (situat a posicio -4 respecte $fp) la variable y   // Quan les operacions son algoW (lw, sw) vol dir que son de tamany 4 bytes, per ints (mirar link operacions)
LI $9, 2                    // Assignar a $9 el valor 2
ADD $8, $8, $9              // Guardar a $8 la suma de $8 amb $9
SW $8, 0($fp)               // Store (4 bits-int) el valor de $8 a la posicio de memoria de X (posicio 0)

Exemple if:

      i := 0                  ; assignment
L1:   if i >= 10 goto L2      ; conditional jump
      t0 := i*i
L2:

Codi mips:
        MOVE $fp, $sp               // Inicialitzar $fp (posem on comencen les funcions)
        SUB $sp, $sp , 4            // Guardar memoria per variables globals al registre $sp (4 perque nomes guardem un int (i))

        //codi main
        li $t0, 0                   //  assigned i to t0
L1:     bge $t0, 10, L2             // comparem els dos registers, si es bigger, ves a L2, sino continues
        mult $t1, $t0, $t0
L2:

    bne  $r1, $r2, L1    # if ! ( i == j )
    addi $r1, $r1, 1     # i++
L1: addi $r2, $r2, -1    # j--

Caldrà crear una taula de relacions entre operacions de 3ac i MIPS, tipo la


char *str = “Hello World!”;
char *s = str;
while (*s != ‘\0’) {  s = s + 1; }
-------------------------------------------------
    la $t0,str

loop:   lb $a0,0($t0)
        beq $a0,$0,exit

        addi $t0,$t0,1
        j loop
exit:

*/

import Semantic.SymbolTable.Symbol;

import java.util.HashMap;
import java.util.Scanner;

public class MIPSGenerator {

    private String generatedCode;
    private StringBuilder sb = new StringBuilder();
    private HashMap<String, Symbol> symbolHashMap;
    private HashMap<String, Integer> offsetRegisters;
    private boolean acumulate = false;
    private int fp = 0;
    private int i = 0;
    private int loopCounter = 0;

    public MIPSGenerator(HashMap<String, Symbol> symbolHashMap) {
        this.symbolHashMap = symbolHashMap;
        this.offsetRegisters = new HashMap<String, Integer>();
    }

    public String generate(String optimizedCode) {
        generatedCode = initMIPS(optimizedCode);            // Saves memory for variables and stores variables position in offsetRegisters

        Scanner scanner = new Scanner(optimizedCode);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] words = line.split("\\s+");

            for (i = 0; i < words.length; i++) {                                // Handlers for each type of word
                if (isReservedWord(words[i], words) > 0) {                      // Goto or If
                    int operation = isReservedWord(words[i], words);
                    handleReservedWord(words[i], words, false, operation);
                } else if (isOperator(words[i])) {                              // +, -, <, >
                    handleOperator(words[i], words);
                } else if (isVariable(words[i])) {                              // variables
                    handleVariable(words[i], words);
                } else if (isProcess(words[i], words) > 0) {                    // Loop
                    int operation = isProcess(words[i], words);
                    handleProcess(words[i], words, operation);
                } else {

                }
            }
        }
        scanner.close();

        return generatedCode;
    }

    private void handleVariable(String word, String[] words) {
        switch (word) {
            case "int": // Nothing to do, already initialized in initMips()

                break;
        }
    }

    private void handleProcess(String word, String[] words, int operation) {
        if (operation == 1) {
            sb.append(word + ": \n");
            //CODE, cridar handle operation
            generatedCode += sb.toString() + '\n';
            sb.delete(0, sb.length());
        } else if (operation == 2) { // if its a loop function
            word = word.substring(0, word.length() - 1);
            sb.append(word + ": \n");
            //CODE, cridar handle operation http://www.pitt.edu/~kmram/CoE0147/lectures/mips-isa4.pdf
            generatedCode += sb.toString() + '\n';
            sb.delete(0, sb.length());
            i++;
            handleReservedWord(words[i], words, false, 2);
        }

    }

    private void handleReservedWord(String word, String[] words, boolean beq, int loop) {

        switch (word) {
            case "IF":
                i += 2; // change i to < or > position
                handleOperator(words[i], words);
                i += 2; // change i to GOTO position
                handleReservedWord(words[i], words, true, 2);
                i += 2;
                generatedCode += sb.toString() + '\n';
                break;
            case "GOTO":
                if (loop == 1) {                                                        // if its a loop, redirect to j loopX
                    sb.append("j ").append(words[i + 1]).append('\n');
                } else if (beq) {                                                       // if we want to do a equals
                    sb.append("BEQ   $if, $zero, ").append(words[i + 1]).append("\n");
                } else {
                    sb.append("BNE   $if, $zero, ").append(words[i + 1]).append("\n");
                }
                generatedCode += sb.toString() + '\n';
                break;
        }
        sb.delete(0, sb.length());
    }

    private void handleOperator(String word, String[] words) {

        switch (word) {
            case "+":
                // Both variables are in the symbol table
                if (offsetRegisters.containsKey(words[i - 1]) && offsetRegisters.containsKey(words[i + 1])) {
                    int offsetVar1 = offsetRegisters.get(words[i - 1]);
                    int offsetVar2 = offsetRegisters.get(words[i + 1]);

                    sb.append("LW   $" + words[i - 3] + ", " + offsetVar1 + "($fp)\n");
                    sb.append("ADDU   $" + words[i - 3] + ", $" + words[i - 1] + ", $" + words[i + 1] + "\n");

                } else if (offsetRegisters.containsKey(words[i - 1]) && !acumulate) { // One variable and one number
                    int offsetVar1 = offsetRegisters.get(words[i - 1]);
                    try {
                        int valueVar2 = Integer.parseInt(words[i + 1]);
                        sb.append("LW   $" + words[i - 3] + ", " + offsetVar1 + "($fp)\n" + "ADDIU   $" + words[i - 3] + ", $" + words[i - 1] + ", " + valueVar2 + "\n");
                    } catch (NumberFormatException e) {
                        System.out.println("ERROR, non existing var:" + words[i + 1]);
                        return;
                    }

                } else if (offsetRegisters.containsKey(words[i + 1]) && !acumulate) { // One variable and one number
                    int offsetVar2 = offsetRegisters.get(words[i + 1]);
                    try {
                        int valueVar1 = Integer.parseInt(words[i - 1]);
                        sb.append("LW   $" + words[i - 3] + ", " + offsetVar2 + "($fp)\n" + "ADDIU   $" + words[i - 3] + ", $" + words[i + 1] + ", " + valueVar1 + "\n");
                    } catch (NumberFormatException e) {
                        System.out.println("ERROR, non existing var: " + words[i - 1]);
                        return;
                    }

                } else if (!acumulate) { // Both values are integers
                    int valueVar1 = Integer.parseInt(words[i - 1]);
                    int valueVar2 = Integer.parseInt(words[i + 1]);

                    sb.append("ADDIU   $" + words[i - 3] + ", $zero, " + valueVar1 + "\n");
                    sb.append("ADDIU   $t3, $zero, " + valueVar2 + "\n");
                    sb.append("ADDU   $" + words[i - 3] + ", $" + words[i - 3] + ", $t3\n");

                } else { // if its a long operation where we use temporary variables
                    if (offsetRegisters.containsKey(words[i + 1])) {
                        String valueVar2 = symbolHashMap.get(words[i + 1]).getValue();
                        sb.append("ADDIU   $" + words[i - 3] + ", $" + words[i - 3] + ", " + valueVar2 + "\n");
                    } else {
                        sb.append("ADDIU   $" + words[i - 3] + ", $" + words[i - 3] + ", " + words[i + 1] + "\n");
                    }
                }
                acumulate = true;
                generatedCode += sb.toString() + '\n';
                break;
            case "-":
                // Both variables are in the symbol table
                if (offsetRegisters.containsKey(words[i - 1]) && offsetRegisters.containsKey(words[i + 1])) {
                    int offsetVar1 = offsetRegisters.get(words[i - 1]);
                    int offsetVar2 = offsetRegisters.get(words[i + 1]);

                    sb.append("LW   $" + words[i - 3] + ", " + offsetVar1 + "($fp)\n");
                    sb.append("SUBU   $" + words[i - 3] + ", $" + words[i - 1] + ", $" + words[i + 1] + "\n");

                } else if (offsetRegisters.containsKey(words[i - 1]) && !acumulate) {// One variable and one number
                    int offsetVar1 = offsetRegisters.get(words[i - 1]);
                    int valueVar2 = Integer.parseInt(words[i + 1]);

                    sb.append("ADDIU   $t3, $zero, " + valueVar2 + "\n" + "LW   $" + words[i - 3] + ", " + offsetVar1 + "($fp)\n" + "SUBU $" + words[i - 3] + ", $t3, $" + words[i - 1] + "\n");

                } else if (offsetRegisters.containsKey(words[i + 1]) && !acumulate) {// One variable and one number
                    int offsetVar2 = offsetRegisters.get(words[i + 1]);
                    int valueVar1 = Integer.parseInt(words[i - 1]);

                    sb.append("LW $" + words[i - 3] + ", " + offsetVar2 + "\n" + "ADDIU $" + words[i - 3] + ", $" + words[i + 1] + ", -" + valueVar1 + "\n");

                } else if (!acumulate) {
                    int valueVar1 = Integer.parseInt(words[i - 1]);
                    int valueVar2 = Integer.parseInt(words[i + 1]);

                    sb.append("ADDIU   $" + words[i - 3] + ", $zero, " + valueVar1 + "\n");
                    sb.append("ADDIU   $t3, $zero, " + valueVar2 + "\n");
                    sb.append("SUBU   $" + words[i - 3] + ", $t3, $" + words[i - 3] + "\n");

                } else {
                    if (offsetRegisters.containsKey(words[i + 1])) {
                        String valueVar2 = symbolHashMap.get(words[i + 1]).getValue();
                        sb.append("ADDIU   $t3, $zero, " + valueVar2 + "\n");
                        sb.append("SUBU   $" + words[i - 3] + ", $t3, $" + words[i - 3] + "\n");

                    } else {
                        sb.append("ADDIU   $t3, $zero, " + words[i + 1] + "\n");
                        sb.append("SUBU   $" + words[i - 3] + ", $t3, $" + words[i - 3] + "\n");
                    }
                }
                acumulate = true;
                generatedCode += sb.toString() + '\n';
                break;
            case ">":
                if (offsetRegisters.containsKey(words[i - 1]) && offsetRegisters.containsKey(words[i + 1])) { // Both variables are in the symbol table
                    int offsetVar1 = offsetRegisters.get(words[i - 1]);
                    int offsetVar2 = offsetRegisters.get(words[i + 1]);

                    sb.append("LW    $if, " + offsetVar1 + "($fp)\n" + "LW   $t3, " + offsetVar2 + "($fp)\n");
                    sb.append("SLT   $if, $if, $t3\n");
                } else if (offsetRegisters.containsKey(words[i - 1])) {// One variable and one number
                    int offsetVar1 = offsetRegisters.get(words[i - 1]);
                    int valueVar2 = Integer.parseInt(words[i + 1]);

                    sb.append("LW    $if, " + offsetVar1 + "($fp)\n");
                    sb.append("SLT   $if, $if, " + valueVar2 + "\n");
                } else if (offsetRegisters.containsKey(words[i + 1])) {// One variable and one number
                    int offsetVar2 = offsetRegisters.get(words[i + 1]);
                    int valueVar1 = Integer.parseInt(words[i - 1]);

                    sb.append("LW    $if, " + offsetVar2 + "($fp)\n");
                    sb.append("SLT   $if, $if, " + valueVar1 + "\n");
                } else {  // Both variables are numbers
                    int valueVar1 = Integer.parseInt(words[i - 1]);
                    int valueVar2 = Integer.parseInt(words[i + 1]);

                    sb.append("ADDIU   $if, $zero, " + valueVar1 + "\n");
                    sb.append("SLT   $if, $if, " + valueVar2 + "\n");
                }
                generatedCode += sb.toString() + '\n';
                break;
            case "<":
                if (offsetRegisters.containsKey(words[i - 1]) && offsetRegisters.containsKey(words[i + 1])) {
                    int offsetVar1 = offsetRegisters.get(words[i - 1]);
                    int offsetVar2 = offsetRegisters.get(words[i + 1]);

                    sb.append("LW    $if, " + offsetVar1 + "($fp)\n" + "LW   $t3, " + offsetVar2 + "($fp)\n");
                    sb.append("SLT   $if, $t3, $if\n");
                } else if (offsetRegisters.containsKey(words[i - 1])) {
                    int offsetVar1 = offsetRegisters.get(words[i - 1]);
                    int valueVar2 = Integer.parseInt(words[i + 1]);

                    sb.append("LW    $if, " + offsetVar1 + "($fp)\n");
                    sb.append("SLT   $if, " + valueVar2 + ", $if\n");
                } else if (offsetRegisters.containsKey(words[i + 1])) {
                    int offsetVar2 = offsetRegisters.get(words[i + 1]);
                    int valueVar1 = Integer.parseInt(words[i - 1]);

                    sb.append("LW    $if, " + offsetVar2 + "($fp)\n");
                    sb.append("SLT   $if, " + valueVar1 + ", $if\n");
                } else {
                    int valueVar1 = Integer.parseInt(words[i - 1]);
                    int valueVar2 = Integer.parseInt(words[i + 1]);

                    sb.append("ADDIU   $if, $zero, " + valueVar1 + "\n");
                    sb.append("SLT   $if, " + valueVar2 + ", $if\n");
                }
                generatedCode += sb.toString() + '\n';
                break;
            case "=":
                if (symbolHashMap.containsKey(words[i - 1]) && !offsetRegisters.containsKey(words[i + 1])) { // if its a number assigned to a variable
                    sb.append("LW   $" + words[i - 1] + ", " + words[i + 1] + "\n");
                    sb.append("SW   $" + words[i - 1] + ", " + offsetRegisters.get(words[i - 1]) + "($fp)\n");
                    generatedCode += sb.toString() + '\n';
                } else if (symbolHashMap.containsKey(words[i - 1]) && offsetRegisters.containsKey(words[i + 1])) { // if its a variable being stored into a variable
                    sb.append("SW   $" + words[i + 1] + ", " + offsetRegisters.get(words[i - 1]) + "($fp)\n");
                    generatedCode += sb.toString() + '\n';
                }
                acumulate = false;

                break;
        }

        sb.delete(0, sb.length());
    }

    private boolean isOperator(String word) { // una suma/resta/ operador de greater lower...
        if (word.equals("+") || word.equals("-") || word.equals("<") || word.equals(">") || word.equals("=")) {
            return true;
        }

        return false;
    }

    private int isReservedWord(String word, String[] words) {// si es un goto, if...
        if (word.contains("IF")) {
            return 2;
        } else if (word.contains("GOTO") && words[i + 1].contains("LOOP")) {
            return 1;
        } else if (word.contains("GOTO")) {
            return 3;
        }
        return -1;
    }

    private boolean isVariable(String word) {// si es un goto, if...
        if (word.equals("=")) {
            return true;
        }
        return false;
    }

    private int isProcess(String word, String[] words) {            // Detect if its a loop, or a direction pointer of an IF
        if (word.contains("LOOP") && word.contains(":")) {
            return 2;
        } else if (word.contains("L") && words.length == 1) {
            return 1;
        }
        return -1;
    }

    private String initMIPS(String optimizedCode) {
        Scanner scanner = new Scanner(optimizedCode);

        // Iter through optimized code looking for variables
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] words = line.split("\\s+");

            for (int i = 0; i < words.length; i++) {
                if (isVariable(words[i])) {
                    offsetRegisters.put(words[i - 1], fp);          // Save variable in hashmap (variable, postionInMemory)
                    fp -= 4;                                        // Int's size is 4bytes, add it to the memory register size
                }
            }
        }
        scanner.close();                                            // Initialization of memory
        StringBuilder sb = new StringBuilder();
        sb.append("MOVE $fp, $sp");
        sb.append("\n");
        sb.append("SUB $sp, $sp , " + fp);
        sb.append("\n");
        sb.append("\n");


        return sb.toString();
    }

}
