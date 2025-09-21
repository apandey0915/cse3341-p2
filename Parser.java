import java.io.*;
public class Parser {
    private final CoreScanner scanner;
    public Parser(CoreScanner scanner) {
        this.scanner = scanner;
    }

    public ProgramNode parseProgram() throws ParserException, IOException {
        ProgramNode root = new ProgramNode();
        root.parse(scanner);
        return root;
    }

    // Token helpers

    private Core peek() {
        return scanner.currentToken();
    }

    private void advance() throws IOException {
        scanner.nextToken();
    }

    private void expect(Core expected) throws ParserException, IOException {
        Core actual = peek();
        if (actual != expected) {
            throw new ParserException(
                "expected " + expected.name() + " but found " + actual.name()
                );
        }
        advance();
    }
}