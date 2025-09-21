import java.io.*;
public class CondNode {
    private enum Kind { CMPR, NOT, AND, OR }
    private Kind kind;

    private CmprNode cmpr;
    private CondNode left, right;

    public void parse(CoreScanner scanner) throws ParserException, IOException {
        Core t = scanner.currentToken();

        // not <cond>
        if (t == Core.NOT) {
            kind = Kind.NOT;
            scanner.nextToken();
            left = new CondNode();
            left.parse(scanner);
            return;
        }

        // Start with a comparison
        cmpr = new CmprNode();
        cmpr.parse(scanner);

        Core look = scanner.currentToken();
        if (look == Core.AND || look == Core.OR) {
            kind = (look == Core.AND) ? Kind.AND : Kind.OR;

            // move the <cmpr> we already parsed into 'left'
            left = new CondNode();
            left.kind = Kind.CMPR;
            left.cmpr = this.cmpr;

            scanner.nextToken(); 
            right = new CondNode();
            right.parse(scanner);
        } else {
            kind = Kind.CMPR;
        }
    }

    public void checkSemantics(SymbolTable symbols) throws SemanticException {
        switch (kind) {
            case CMPR:
                cmpr.checkSemantics(symbols);
                break;
            case NOT:
                left.checkSemantics(symbols);
                break;
            case AND:
            case OR:
                left.checkSemantics(symbols);
                right.checkSemantics(symbols);
                break;
        }
    }

    public void print(int indent) {
        switch (kind) {
            case CMPR:
                cmpr.print(indent);
                break;
            case NOT:
                System.out.print("not ");
                left.print(0);
                break;
            case AND:
                left.print(indent);
                System.out.print(" and ");
                right.print(0);
                break;
            case OR:
                left.print(indent);
                System.out.print(" or ");
                right.print(0);
                break;
        }
    }
}
