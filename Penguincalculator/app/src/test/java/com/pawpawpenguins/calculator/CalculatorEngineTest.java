package com.pawpawpenguins.calculator;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class CalculatorEngineTest {
    @Test public void addsDecimalValuesWithoutFloatingPointNoise() {
        CalculatorEngine c = new CalculatorEngine();
        c.digit('0'); c.decimalPoint(); c.digit('1'); c.operator('+');
        c.digit('0'); c.decimalPoint(); c.digit('2'); c.equals();
        assertEquals("0.3", c.display());
    }

    @Test public void chainsOperationsLeftToRight() {
        CalculatorEngine c = new CalculatorEngine();
        c.digit('8'); c.operator('+'); c.digit('2'); c.operator('*');
        c.digit('3'); c.equals();
        assertEquals("30", c.display());
    }

    @Test public void supportsPercentAndBackspace() {
        CalculatorEngine c = new CalculatorEngine();
        c.digit('5'); c.digit('0'); c.percent();
        assertEquals("0.5", c.display());
        c.backspace();
        assertEquals("0", c.display());
    }

    @Test public void clearsAndReportsDivisionByZero() {
        CalculatorEngine c = new CalculatorEngine();
        c.digit('7'); c.operator('/'); c.digit('0'); c.equals();
        assertEquals("Cannot divide by zero", c.display());
        c.clear();
        assertEquals("0", c.display());
    }

    @Test public void togglesSign() {
        CalculatorEngine c = new CalculatorEngine();
        c.digit('4'); c.toggleSign();
        assertEquals("-4", c.display());
    }
}
