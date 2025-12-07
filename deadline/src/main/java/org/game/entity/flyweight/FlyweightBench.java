package org.game.entity.flyweight;

import org.game.entity.Enemy;
import org.game.entity.enemy.goblin.SmallGoblin;
import org.game.entity.enemy.skeleton.SmallSkeleton;
import org.game.entity.enemy.zombie.SmallZombie;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
public class FlyweightBench {

    @Param({"200", "500", "1000"})
    public int count;

    @Setup(Level.Iteration)
    public void setup() {
        SpriteCache.clear();
        System.gc();
    }

    // Flyweight Benchmark
    @Benchmark
    public void createEnemies(Blackhole bh) {
        List<Enemy> list = new ArrayList<>(count * 3);

        for (int i = 0; i < count; i++) {
            list.add(new SmallGoblin(i, 0));
            list.add(new SmallSkeleton(i, 32));
            list.add(new SmallZombie(i, 64));
        }

        bh.consume(list);
    }

    // MEMORY benchmark
    @Benchmark
    public long memoryUsed_AfterFlyweightCreation() {
        SpriteCache.clear();
        System.gc();

        List<Enemy> list = new ArrayList<>(count * 3);

        for (int i = 0; i < count; i++) {
            list.add(new SmallGoblin(i, 0));
            list.add(new SmallSkeleton(i, 32));
            list.add(new SmallZombie(i, 64));
        }

        System.gc();
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }
}

