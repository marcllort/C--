package Syntactic;

import Lexer.Scanner;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    @Test
    void parse() {

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

        assertNotNull(parser.getTree());

    }
}