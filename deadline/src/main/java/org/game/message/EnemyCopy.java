package org.game.message;

import org.game.entity.EnemySize;
import org.game.entity.EnemyType;

public record EnemyCopy(long id, EnemyType enemyType, EnemySize enemySize, int initialX, int initialY, int hpPoints){}
