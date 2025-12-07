package org.game.server.Admin.expressions;

import org.game.server.Admin.InterpreterContext;

public class UnknownExpression implements Expression {
    private final String input;

    public UnknownExpression(String input) {
        this.input = input;
    }

    @Override
    public void interpret(InterpreterContext ctx) {
        System.out.println("Unknown command: " + input);
    }
}
