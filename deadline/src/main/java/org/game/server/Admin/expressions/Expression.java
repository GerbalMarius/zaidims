package org.game.server.Admin.expressions;

import org.game.server.Admin.InterpreterContext;

public interface Expression {
    void interpret(InterpreterContext ctx);
}
