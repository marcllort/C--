import GeneratedCode.MIPSGenerator;
import IntermediateCode.IntermediateCodeGenerator;
import Lexer.Scanner;
import Optimizer.CodeOptimizer;
import Semantic.SemanticAnalyzer;
import Semantic.SymbolTable.Symbol;
import Syntactic.Parser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;


public class Main {

    public static void main(String[] args) {
        try {
            Path path = Paths.get(System.getProperty("user.dir") + "/src/code.cmm");
            String code = Files.readString(path, StandardCharsets.US_ASCII);

            Scanner scanner = new Scanner(code);
            scanner.saveTokens();                               // Load all tokens to Token list
            scanner.printTokens();                              // Print Token list

            Parser parser = new Parser(scanner);
            parser.Parse();

            SemanticAnalyzer semantic = new SemanticAnalyzer(parser.getTree());
            HashMap<String, Symbol>  symbolHashMap = semantic.analyze();

            // 3AC
            IntermediateCodeGenerator intermediate = new IntermediateCodeGenerator();
            String generatedCode = intermediate.generateCode(parser.getTree());
            System.out.println(generatedCode);

            // Code Optimization
            CodeOptimizer codeOptimizer = new CodeOptimizer(); // Reb codi intermedi i taula de simbols
            String optimizedCode = codeOptimizer.optimize(generatedCode);

            // MIPS Generator
            MIPSGenerator mipsGenerator = new MIPSGenerator(symbolHashMap);
            String mipsCode = mipsGenerator.generate(optimizedCode);
            System.out.println(mipsCode);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}