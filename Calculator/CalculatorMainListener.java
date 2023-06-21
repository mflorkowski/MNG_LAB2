import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.*;
import java.lang.*;


public class CalculatorMainListener extends CalculatorBaseListener {

    private Deque<Double> numbers = new ArrayDeque<>();


    public Double getResult() {
        return numbers.peek();
    }



    @Override
    public void exitExpression(CalculatorParser.ExpressionContext ctx) {
        Double result = numbers.pop();
        for (int i = 1; i < ctx.getChildCount(); i = i + 2) {
            if (symbolEquals(ctx.getChild(i), CalculatorParser.PLUS)) {
                result = result + numbers.pop();
            } else {
                result = result - numbers.pop();
            }
        }
        numbers.add(result);
        System.out.println("Expression: \"" + ctx.getText() + "\" -> " + result);
        super.exitExpression(ctx);
    }


    @Override
    public void exitAtom(CalculatorParser.AtomContext ctx) {
        if (ctx.MINUS() != null) {
            numbers.add(-1 * Double.parseDouble(ctx.INT().getText()));
        }
        System.out.println("Atom \""+ctx.getText()+ "\"");
        super.exitAtom(ctx);
    }




    @Override
    public void exitMultiplyExpression(CalculatorParser.MultiplyExpressionContext ctx) {
        Double result = numbers.removeLast();
        for (int i = 1; i < ctx.getChildCount(); i = i + 2) {
            if (symbolEquals(ctx.getChild(i), CalculatorParser.MULT)) {
                result = result * numbers.removeLast();
            } else {
                result =  numbers.removeLast() / result;
            }
        }
        numbers.add(result);
        System.out.println("MultiplyExpression: \"" + ctx.getText() + "\" -> " + result);
        super.exitMultiplyExpression(ctx);
    }

    private boolean symbolEquals(ParseTree child, int symbol) {
        return ((TerminalNode) child).getSymbol().getType() == symbol;
    }

    @Override
    public void exitExponentationExpression(CalculatorParser.ExponentationExpressionContext ctx) {
        if (ctx.getChildCount() == 1) return;
        double power = numbers.removeLast();
        double base = numbers.removeLast();
        double result = Math.pow(base, power);
        numbers.add(result);
        System.out.println("ExponentationExpression: \"" + ctx.getText() + "\" -> " + result);
        super.exitExponentationExpression(ctx);
    }

    @Override
    public void exitSqrtExpression(CalculatorParser.SqrtExpressionContext ctx) {
        if (ctx.getChildCount() == 1) return;
        double result = numbers.removeLast();
        if (ctx.SQRT() != null) result = Math.sqrt(result);
        numbers.add(result);
        System.out.println("SQRTExpression: \"" + ctx.getText() + "\" -> " + result);
        super.exitSqrtExpression(ctx);
    }

    @Override
    public void visitTerminal(TerminalNode node) {

        if (node.getSymbol().getType() == CalculatorParser.INT) {
            numbers.addLast(Double.parseDouble(node.getText()));
        } else if (node.getSymbol().getType() == CalculatorParser.MINUS && node.getParent() instanceof CalculatorParser.AtomContext) {
            return;
        }
    }


    public static Double calc(CharStream charStream) {
        CalculatorLexer lexer = new CalculatorLexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        System.out.println(tokens.getText());

        CalculatorParser parser = new CalculatorParser(tokens);
        ParseTree tree = parser.expression();

        ParseTreeWalker walker = new ParseTreeWalker();
        CalculatorMainListener mainListener = new CalculatorMainListener();
        walker.walk(mainListener, tree);
        return mainListener.getResult();
    }

    public static Double calc(String expression) {
        return calc(CharStreams.fromString(expression));
    }

    public static void main(String[] args) throws Exception {
        Double result = calc("11 + 7 * 2 - 15 / 2 + 1"); //18.5
        Double result1 = calc("1 + 2 ^ 3 ^ 2 / 3 + 1"); //172.66666
        Double result2 = calc("sqrt121 +sqrt4 * sqrt100"); // 31
        System.out.println("Result = " + result);
        System.out.println("Result1 = " + result1);
        System.out.println("Result2 = " + result2);
    }
}