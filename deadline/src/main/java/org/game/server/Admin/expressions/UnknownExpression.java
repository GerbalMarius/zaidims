package org.game.server.Admin.expressions;

import lombok.extern.slf4j.Slf4j;
import org.game.server.Admin.InterpreterContext;

@Slf4j
public class UnknownExpression implements Expression {
    private final String input;

    public UnknownExpression(String input) {
        this.input = input;
    }

    @Override
    public void interpret(InterpreterContext ctx) {
        log.info("Unknown command: {}",input);
    }
}
