package GeneratedCode;

import Lexer.Scanner;
import Semantic.SemanticAnalyzer;
import Semantic.SymbolTable.Symbol;
import Syntactic.Parser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class MIPSGeneratorTest {

    @org.junit.jupiter.api.Test
    void generate() {

        String threeaddress = "int num\n" +
                "num = 0\n" +
                "int a\n" +
                "a = 0\n" +
                "int b\n" +
                "b = 1\n" +
                "int result\n" +
                "result = 10\n" +
                "LOOP0: IF 1 < 2 GOTO LOOP1\n" +
                "t0 = a + b\n" +
                "num = t0\n" +
                "a = b\n" +
                "b = num\n" +
                "IF result < num GOTO L2 \n" +
                " GOTO L3 \n" +
                "L2 \n" +
                "t0 = a + b\n" +
                "num = t0\n" +
                "L3 \n" +
                "GOTO  LOOP0 \n" +
                "LOOP1 ";

        String result = "MOVE $fp, $sp\n" +
                "SUB $sp, $sp , -40\n" +
                "\n" +
                "LW   $num, 0\n" +
                "SW   $num, -36($fp)\n" +
                "\n" +
                "LW   $a, 0\n" +
                "SW   $a, -24($fp)\n" +
                "\n" +
                "LW   $b, 1\n" +
                "SW   $b, -28($fp)\n" +
                "\n" +
                "LW   $result, 10\n" +
                "SW   $result, -12($fp)\n" +
                "\n" +
                "LOOP0: \n" +
                "\n" +
                "ADDIU   $if, $zero, 1\n" +
                "SLT   $if, 2, $if\n" +
                "\n" +
                "BEQ   $if, $zero, LOOP1\n" +
                "\n" +
                "\n" +
                "LW   $t0, -24($fp)\n" +
                "ADDU   $t0, $a, $b\n" +
                "\n" +
                "SW   $t0, -36($fp)\n" +
                "\n" +
                "SW   $b, -24($fp)\n" +
                "\n" +
                "SW   $num, -28($fp)\n" +
                "\n" +
                "LW    $if, -12($fp)\n" +
                "LW   $t3, -36($fp)\n" +
                "SLT   $if, $t3, $if\n" +
                "\n" +
                "BEQ   $if, $zero, L2\n" +
                "\n" +
                "\n" +
                "BNE   $if, $zero, L3\n" +
                "\n" +
                "L2: \n" +
                "\n" +
                "LW   $t0, -24($fp)\n" +
                "ADDU   $t0, $a, $b\n" +
                "\n" +
                "SW   $t0, -36($fp)\n" +
                "\n" +
                "L3: \n" +
                "\n" +
                "j LOOP0\n" +
                "\n" +
                "LOOP1: \n" +
                "\n";

        Path path = Paths.get(System.getProperty("user.dir") + "/src/code.cmm");
        String code = null;
        try {
            code = Files.readString(path, StandardCharsets.US_ASCII);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scanner scanner = new Scanner(code);
        scanner.saveTokens();                               // Load all tokens to Token list
        scanner.printTokens();                              // Print Token list

        Parser parser = new Parser(scanner);
        parser.Parse();

        SemanticAnalyzer semantic = new SemanticAnalyzer(parser.getTree());
        HashMap<String, Symbol>  symbolHashMap = semantic.analyze();
        MIPSGenerator mipsGenerator = new MIPSGenerator(symbolHashMap);
        String mipsCode = mipsGenerator.generate(threeaddress);

        assertEquals(mipsCode, result);
    }
}