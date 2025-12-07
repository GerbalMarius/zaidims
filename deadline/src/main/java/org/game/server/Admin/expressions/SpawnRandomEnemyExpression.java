package org.game.server.Admin.expressions;

import lombok.extern.slf4j.Slf4j;
import org.game.server.Admin.InterpreterContext;

import java.util.concurrent.TimeUnit;

@Slf4j
public class SpawnRandomEnemyExpression implements Expression {
    @Override
    public void interpret(InterpreterContext ctx) {
        ctx.getGameWorld().startSpawningIndividualEnemies(0, 5, TimeUnit.SECONDS);
        ctx.getGameWorld().ensureEnemyUpdating();
        log.info("Spawning random enemies...");
    }
}
