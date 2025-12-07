package org.game.server.Admin.expressions;

import lombok.extern.slf4j.Slf4j;
import org.game.server.Admin.InterpreterContext;

@Slf4j
public class SpawnSpecificEnemyExpression implements Expression {
    private final String size;
    private final String type;

    public SpawnSpecificEnemyExpression(String size, String type) {
        this.size = size;
        this.type = type;
    }

    @Override
    public void interpret(InterpreterContext ctx) {
        log.info("Spawning enemy type= {} size= {}", type, size);

        ctx.getGameWorld().spawnSpecificEnemy(size, type);
        ctx.getGameWorld().ensureEnemyUpdating();
    }
}
