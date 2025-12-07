package org.game.server.Admin.expressions;

import org.game.server.Admin.InterpreterContext;

import java.util.concurrent.TimeUnit;

public class SpawnWaveEnemyExpression implements Expression {
    @Override
    public void interpret(InterpreterContext ctx) {
        ctx.getGameWorld().startSpawningWaves(10, 30, TimeUnit.SECONDS);
        ctx.getGameWorld().ensureEnemyUpdating();
        System.out.println("Spawning enemy waves...");
    }
}
