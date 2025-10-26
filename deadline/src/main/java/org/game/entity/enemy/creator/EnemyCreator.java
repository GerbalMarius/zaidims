package org.game.entity.enemy.creator;

import org.game.entity.enemy.goblin.Goblin;
import org.game.entity.enemy.goblin.MediumGoblin;
import org.game.entity.enemy.skeleton.MediumSkeleton;
import org.game.entity.enemy.skeleton.Skeleton;
import org.game.entity.enemy.zombie.MediumZombie;
import org.game.entity.enemy.zombie.Zombie;

/**
 * A utility class responsible for creating default instances of various enemy types such as Goblins,
 * Skeletons, and Zombies with predefined attributes.
 */
public final class EnemyCreator {
    private EnemyCreator(){}


    public static Goblin createDefaultGoblin() {
        return Goblin.builder()
                .withEnemy(MediumGoblin::new)
                .withId(0)
                .atPos(20, 20)
                .withAttack(15)
                .withHp(50)
                .withSpeed(4)
                .build();
    }

    public static Skeleton createDefaultSkeleton() {
        return Skeleton.builder()
                .withEnemy(MediumSkeleton::new)
                .withId(0)
                .atPos(20, 20)
                .withAttack(12)
                .withHp(40)
                .withSpeed(3)
                .build();
    }
    public static Zombie createDefaultZombie() {
        return Zombie.builder()
                .withEnemy(MediumZombie::new)
                .withId(0)
                .atPos(20, 20)
                .withAttack(10)
                .withHp(60)
                .withSpeed(2)
                .build();
    }
}
