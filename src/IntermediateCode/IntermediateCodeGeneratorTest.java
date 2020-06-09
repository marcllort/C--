package IntermediateCode;

import Lexer.Scanner;
import Semantic.SemanticAnalyzer;
import Semantic.SymbolTable.Symbol;
import Syntactic.Parser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class IntermediateCodeGeneratorTest {

    @Test
    void generateCode() {
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
        HashMap<String, Symbol> symbolHashMap = semantic.analyze();


        // 3AC
        IntermediateCodeGenerator intermediate = new IntermediateCodeGenerator();
        String generatedCode = intermediate.generateCode(parser.getTree());

        String result = "int num\n" +
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
                "LOOP1 \n";
        assertEquals(result, generatedCode);

    }
}