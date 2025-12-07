package org.game.server.Admin.expressions;

import org.game.server.Admin.InterpreterContext;

import java.util.concurrent.TimeUnit;

public class SpawnRandomEnemyExpression implements Expression {
    @Override
    public void interpret(InterpreterContext ctx) {
        ctx.getGameWorld().startSpawningIndividualEnemies(0, 5, TimeUnit.SECONDS);
        ctx.getGameWorld().ensureEnemyUpdating();
        System.out.println("Spawning random enemies...");
    }
}
