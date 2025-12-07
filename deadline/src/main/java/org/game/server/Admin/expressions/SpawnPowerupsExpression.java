package org.game.server.Admin.expressions;

import org.game.server.Admin.InterpreterContext;

import java.util.concurrent.TimeUnit;

public class SpawnPowerupsExpression implements Expression {
    @Override
    public void interpret(InterpreterContext ctx) {
        ctx.getGameWorld().startDispensingPowerUps(10, 15, TimeUnit.SECONDS);
        System.out.println("Spawning powerups...");
    }
}
