package org.game.server.Admin.expressions;

import org.game.server.Admin.InterpreterContext;

public class SpawnSpecificEnemyExpression implements Expression {
    private final String size;
    private final String type;

    public SpawnSpecificEnemyExpression(String size, String type) {
        this.size = size;
        this.type = type;
    }

    @Override
    public void interpret(InterpreterContext ctx) {
        System.out.println("Spawning enemy type=" + type + ", size=" + size);

        ctx.getGameWorld().spawnSpecificEnemy(size, type);
        ctx.getGameWorld().ensureEnemyUpdating();
    }
}
