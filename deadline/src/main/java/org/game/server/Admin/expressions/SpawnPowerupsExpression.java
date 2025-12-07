package org.game.server.Admin.expressions;

import lombok.extern.slf4j.Slf4j;
import org.game.server.Admin.InterpreterContext;

import java.util.concurrent.TimeUnit;

@Slf4j
public class SpawnPowerupsExpression implements Expression {
    @Override
    public void interpret(InterpreterContext ctx) {
        ctx.getGameWorld().startDispensingPowerUps(10, 15, TimeUnit.SECONDS);
        log.info("Spawning powerups...");
    }
}
