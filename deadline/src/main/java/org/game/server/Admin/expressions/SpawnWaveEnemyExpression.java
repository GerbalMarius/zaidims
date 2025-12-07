package org.game.server.Admin.expressions;

import lombok.extern.slf4j.Slf4j;
import org.game.server.Admin.InterpreterContext;

import java.util.concurrent.TimeUnit;

@Slf4j
public class SpawnWaveEnemyExpression implements Expression {
    @Override
    public void interpret(InterpreterContext ctx) {
        ctx.getGameWorld().startSpawningWaves(10, 30, TimeUnit.SECONDS);
        ctx.getGameWorld().ensureEnemyUpdating();
        log.info("Spawning enemy waves...");
    }
}
