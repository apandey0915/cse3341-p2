import java.io.*;
public class FactorNode {
    private enum Kind { ID, INDEXED_ID, CONST, PAREN, NEG, POS }
    private Kind kind;

    private String id;        // for ID / INDEXED_ID
    private String strKey;    // for INDEXED_ID
    private Integer constant; // for CONST
    private ExprNode expr;    // for PAREN
    private FactorNode unary; // for NEG/POS

    public void parse(CoreScanner scanner) throws ParserException, IOException {
        Core t = scanner.currentToken();

        // unary minus / plus
        if (t == Core.SUBTRACT) {
            kind = Kind.NEG;
            scanner.nextToken();
            unary = new FactorNode();
            unary.parse(scanner);
            return;
        } else if (t == Core.ADD) {
            kind = Kind.POS;
            scanner.nextToken();
            unary = new FactorNode();
            unary.parse(scanner);
            return;
        }

        if (t == Core.ID) {
            id = scanner.getId();
            scanner.nextToken();

            if (scanner.currentToken() == Core.LSQUARE) {
                kind = Kind.INDEXED_ID;
                scanner.nextToken();

                if (scanner.currentToken() != Core.STRING) {
                    throw new ParserException("expected string inside []");
                }
                strKey = scanner.getString();
                scanner.nextToken();

                if (scanner.currentToken() != Core.RSQUARE) {
                    throw new ParserException("expected ']'");
                }
                scanner.nextToken();
            } else {
                kind = Kind.ID;
            }
            return;
        }

        if (t == Core.CONST) {
            kind = Kind.CONST;
            constant = scanner.getConst();
            scanner.nextToken();
            return;
        }

        if (t == Core.LPAREN) {
            kind = Kind.PAREN;
            scanner.nextToken();
            expr = new ExprNode();
            expr.parse(scanner);
            if (scanner.currentToken() != Core.RPAREN) {
                throw new ParserException("expected ')'");
            }
            scanner.nextToken();
            return;
        }

        throw new ParserException("expected factor");
    }

    public void checkSemantics(SymbolTable symbols) throws SemanticException {
        switch (kind) {
            case ID: {
                Type t = symbols.lookup(id);
                if (t == null) throw new SemanticException("undeclared id " + id);
                break;
            }
            case INDEXED_ID: {
                Type t = symbols.lookup(id);
                if (t == null) throw new SemanticException("undeclared id " + id);
                if (t != Type.OBJECT) throw new SemanticException(id + " must be object");
                break;
            }
            case PAREN:
                expr.checkSemantics(symbols);
                break;
            case NEG:
            case POS:
                unary.checkSemantics(symbols);
                break;
            case CONST:
                break;
        }
    }

    public void print(int indent) {
        switch (kind) {
            case ID:
                System.out.print(id);
                break;
            case INDEXED_ID:
                System.out.print(id + "[\"" + strKey + "\"]");
                break;
            case CONST:
                System.out.print(constant);
                break;
            case PAREN:
                System.out.print("(");
                expr.print(0);
                System.out.print(")");
                break;
            case NEG:
                System.out.print("-");
                boolean paren = (unary.kind == Kind.ID || unary.kind == Kind.CONST || unary.kind == Kind.INDEXED_ID);
                unary.print(0);
                break;
            case POS:
                System.out.print("+");
                unary.print(0);
                break;
        }
    }
}
