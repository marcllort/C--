package Semantic;

import Lexer.Scanner;
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

class SemanticAnalyzerTest {

    @Test
    void analyze() {

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

        String result="{result=Symbol{type=1, name='result', value='0', scope=-1}, a=Symbol{type=1, name='a', value='0', scope=-1}, b=Symbol{type=1, name='b', value='0', scope=-1}, num=Symbol{type=1, name='num', value='0', scope=-1}}";
        assertEquals(result, symbolHashMap.toString());

    }
}