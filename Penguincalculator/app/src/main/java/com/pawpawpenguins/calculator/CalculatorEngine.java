package com.pawpawpenguins.calculator;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/** Pure local arithmetic state for the calculator display. */
public final class CalculatorEngine {
    private static final MathContext MATH = new MathContext(18, RoundingMode.HALF_UP);
    private String entry = "0";
    private BigDecimal left = BigDecimal.ZERO;
    private Character pending;
    private boolean replaceEntry;
    private String error;

    public String display() {
        return error != null ? error : entry;
    }

    public void digit(char value) {
        if (value < '0' || value > '9') return;
        error = null;
        if (replaceEntry) {
            entry = String.valueOf(value);
            replaceEntry = false;
        } else if (entry.length() < 30) {
            entry = entry.equals("0") ? String.valueOf(value) : entry + value;
        }
    }

    public void decimalPoint() {
        error = null;
        if (replaceEntry) {
            entry = "0.";
            replaceEntry = false;
        } else if (!entry.contains(".")) {
            entry += ".";
        }
    }

    public void clear() {
        entry = "0";
        left = BigDecimal.ZERO;
        pending = null;
        replaceEntry = false;
        error = null;
    }

    public void backspace() {
        error = null;
        if (replaceEntry) {
            entry = "0";
            replaceEntry = false;
            return;
        }
        if (entry.length() <= 1 || (entry.length() == 2 && entry.startsWith("-"))) {
            entry = "0";
        } else {
            entry = entry.substring(0, entry.length() - 1);
        }
    }

    public void toggleSign() {
        error = null;
        if (replaceEntry) replaceEntry = false;
        if (entry.equals("0") || entry.equals("0.")) return;
        entry = entry.startsWith("-") ? entry.substring(1) : "-" + entry;
    }

    public void percent() {
        if (error != null) return;
        try {
            entry = format(new BigDecimal(entry).divide(BigDecimal.valueOf(100), MATH));
            replaceEntry = true;
        } catch (ArithmeticException ex) {
            setError();
        }
    }

    public void constant(double value) {
        if (error != null || !Double.isFinite(value)) return;
        entry = format(BigDecimal.valueOf(value));
        replaceEntry = pending == null;
    }

    /** Applies a scientific function to the displayed value. Trigonometry uses the selected angle unit. */
    public void scientific(String function, boolean radians) {
        if (error != null) return;
        try {
            double value = Double.parseDouble(entry);
            double result;
            switch (function) {
                case "sin": result = Math.sin(radians ? value : Math.toRadians(value)); break;
                case "cos": result = Math.cos(radians ? value : Math.toRadians(value)); break;
                case "tan":
                    double angle = radians ? value : Math.toRadians(value);
                    if (Math.abs(Math.cos(angle)) < 1e-14) throw new ArithmeticException("undefined tangent");
                    result = Math.tan(angle);
                    break;
                case "sqrt":
                    if (value < 0) throw new ArithmeticException("negative square root");
                    result = Math.sqrt(value);
                    break;
                case "square": result = value * value; break;
                case "reciprocal":
                    if (value == 0) throw new ArithmeticException("division by zero");
                    result = 1d / value;
                    break;
                case "ln":
                    if (value <= 0) throw new ArithmeticException("invalid logarithm");
                    result = Math.log(value);
                    break;
                case "log":
                    if (value <= 0) throw new ArithmeticException("invalid logarithm");
                    result = Math.log10(value);
                    break;
                default: return;
            }
            if (!Double.isFinite(result)) throw new ArithmeticException("non-finite result");
            entry = format(BigDecimal.valueOf(result).round(new MathContext(15, RoundingMode.HALF_UP)));
            replaceEntry = pending == null;
        } catch (NumberFormatException ex) {
            setInvalid();
        } catch (ArithmeticException ex) {
            if ("division by zero".equals(ex.getMessage())) setError();
            else setInvalid();
        }
    }

    public void operator(char op) {
        if ("+-*/".indexOf(op) < 0 || error != null) return;
        try {
            BigDecimal current = new BigDecimal(entry);
            if (pending != null && !replaceEntry) {
                current = calculate(left, current, pending);
                entry = format(current);
            }
            left = current;
            pending = op;
            replaceEntry = true;
        } catch (ArithmeticException | NumberFormatException ex) {
            setError();
        }
    }

    public void equals() {
        if (pending == null || error != null) return;
        try {
            BigDecimal right = replaceEntry ? left : new BigDecimal(entry);
            entry = format(calculate(left, right, pending));
            pending = null;
            replaceEntry = true;
        } catch (ArithmeticException | NumberFormatException ex) {
            setError();
        }
    }

    private BigDecimal calculate(BigDecimal a, BigDecimal b, char op) {
        switch (op) {
            case '+': return a.add(b, MATH);
            case '-': return a.subtract(b, MATH);
            case '*': return a.multiply(b, MATH);
            case '/':
                if (b.compareTo(BigDecimal.ZERO) == 0) throw new ArithmeticException("division by zero");
                return a.divide(b, MATH);
            default: throw new IllegalArgumentException("unsupported operator");
        }
    }

    private String format(BigDecimal value) {
        BigDecimal cleaned = value.stripTrailingZeros();
        return cleaned.compareTo(BigDecimal.ZERO) == 0 ? "0" : cleaned.toPlainString();
    }

    private void setError() {
        entry = "0";
        pending = null;
        replaceEntry = true;
        error = "Cannot divide by zero";
    }

    private void setInvalid() {
        entry = "0";
        pending = null;
        replaceEntry = true;
        error = "Invalid input";
    }
}

