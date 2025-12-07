package org.game.server.Admin.expressions;

import lombok.extern.slf4j.Slf4j;
import org.game.message.EnemyRemoveMessage;
import org.game.message.Message;
import org.game.server.Admin.InterpreterContext;

import java.util.ArrayList;
import java.util.List;

import static org.game.json.JsonLabelPair.labelPair;

@Slf4j
public class KillAllEnemiesExpression implements Expression{
    @Override
    public void interpret(InterpreterContext ctx) {
        var server = ctx.getServer();

        List<Long> enemyIDs = new ArrayList<>(server.getEnemies().keySet());

        for (var enemyId : enemyIDs)
        {
            server.getEnemies().remove(enemyId);

            EnemyRemoveMessage msg = new EnemyRemoveMessage(enemyId);
            server.sendToAll(server.getJson().toJson(msg, labelPair(Message.JSON_LABEL, "enemyRemove")));
        }

        log.info("All enemies killed! Total removed: {}", enemyIDs.size());
    }
}
