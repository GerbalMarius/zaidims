package org.game.server.Admin.expressions;

import org.game.server.Admin.InterpreterContext;

import java.util.List;

public class SequenceExpression implements Expression {

    private final List<Expression> expressions;

    public SequenceExpression(List<Expression> expressions) {
            this.expressions = expressions;
    }

    @Override
    public void interpret(InterpreterContext ctx) {
        for (Expression expression : expressions) {
            expression.interpret(ctx);
        }
    }
}
