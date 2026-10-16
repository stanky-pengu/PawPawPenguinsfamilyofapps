import com.pawpawpenguins.calculator.CalculatorEngine;

public final class EngineSmokeTest {
    private static void check(String expected, String actual, String name) {
        if (!expected.equals(actual)) {
            throw new AssertionError(name + ": expected " + expected + ", got " + actual);
        }
    }

    public static void main(String[] args) {
        CalculatorEngine c = new CalculatorEngine();
        c.digit('0'); c.decimalPoint(); c.digit('1'); c.operator('+');
        c.digit('0'); c.decimalPoint(); c.digit('2'); c.equals();
        check("0.3", c.display(), "decimal addition");

        c.clear();
        c.digit('8'); c.operator('+'); c.digit('2'); c.operator('*');
        c.digit('3'); c.equals();
        check("30", c.display(), "left-to-right chaining");

        c.clear();
        c.digit('5'); c.digit('0'); c.percent();
        check("0.5", c.display(), "percent");
        c.backspace();
        check("0", c.display(), "backspace after result");

        c.clear();
        c.digit('7'); c.operator('/'); c.digit('0'); c.equals();
        check("Cannot divide by zero", c.display(), "division by zero");
        c.clear();
        check("0", c.display(), "clear after error");

        c.digit('4'); c.toggleSign();
        check("-4", c.display(), "sign toggle");
        System.out.println("PASS: 6 engine smoke checks");
    }
}
