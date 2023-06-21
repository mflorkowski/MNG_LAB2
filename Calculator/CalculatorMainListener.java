import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.*;
import java.lang.*;


public class CalculatorMainListener extends CalculatorBaseListener {

    private Deque<Double> numbers = new ArrayDeque<>();
    private Deque<Double> numbers2 = new ArrayDeque<>();

    public Double getResult() {
        return numbers2.removeLast();
    }



    @Override
    public void exitExpression(CalculatorParser.ExpressionContext ctx) {
        Double result = numbers2.removeLast();
        for (int i = 1; i < ctx.getChildCount(); i = i + 2) {
            if (symbolEquals(ctx.getChild(i), CalculatorParser.PLUS)) {
                result = result + numbers2.removeLast();
            } else {
                result = result - numbers2.removeLast();
            }
        }
        numbers2.push(result);
        System.out.println("Expression: \"" + ctx.getText() + "\" -> " + result);
    }


    @Override
    public void exitAtom(CalculatorParser.AtomContext ctx) {
        double value = Double.parseDouble(ctx.INT().getText());
        if (ctx.MINUS() != null) {
            numbers.push(-1 * value);
        } else {
            numbers.push(value);
        }
        System.out.println("Atom \""+ctx.getText()+ "\"");
    }




    @Override
    public void exitMultiplyExpression(CalculatorParser.MultiplyExpressionContext ctx) {
        if (ctx.getChildCount() == 1) return;
        Double result = numbers.removeLast();
        for (int i = 1; i < ctx.getChildCount(); i = i + 2) {
            if (symbolEquals(ctx.getChild(i), CalculatorParser.MULT)) {
                result = result * numbers.removeLast();
            } else {
                result = result / numbers.removeLast();
            }
        }
        numbers2.push(result);
        System.out.println("MultiplyExpression: \"" + ctx.getText() + "\" -> " + result);
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
        numbers2.push(result);
        System.out.println("ExponentationExpression: \"" + ctx.getText() + "\" -> " + result);
    }

    @Override
    public void exitSqrtExpression(CalculatorParser.SqrtExpressionContext ctx) {
        if (ctx.getChildCount() == 1) return;
        double result = numbers.removeLast();
        if (ctx.SQRT() != null) result = Math.sqrt(result);
        numbers2.push(result);
        System.out.println("SQRTExpression: \"" + ctx.getText() + "\" -> " + result);

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
        CharStream charStreams = CharStreams.fromFileName("./example.txt");
        Double result = calc(charStreams);
        System.out.println("Result = " + result);
    }
}