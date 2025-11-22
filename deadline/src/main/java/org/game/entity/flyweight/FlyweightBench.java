package org.game.entity.flyweight;

import org.game.entity.Enemy;
import org.game.entity.enemy.goblin.SmallGoblin;
import org.game.entity.enemy.skeleton.SmallSkeleton;
import org.game.entity.enemy.zombie.SmallZombie;

import java.util.ArrayList;
import java.util.List;

public class FlyweightBench {

    private static void printMem(String tag) {
        Runtime rt = Runtime.getRuntime();
        rt.gc();
        try { Thread.sleep(50); } catch (InterruptedException ignored) {}
        long used = rt.totalMemory() - rt.freeMemory();
        System.out.printf("%s - used mem: %.2f MB (total: %.2f MB, free: %.2f MB)%n",
                tag, used / (1024.0*1024.0), rt.totalMemory()/(1024.0*1024.0), rt.freeMemory()/(1024.0*1024.0));
    }

    public static void main(String[] args) {
        final int N = 1000;
        System.out.println("=== Flyweight microbenchmark ===");

        printMem("Before load");

        long start = System.currentTimeMillis();
        List<Enemy> list = new ArrayList<>(N * 3);

        for (int i = 0; i < N; i++) {
            list.add(new SmallGoblin(i, 0));
            list.add(new SmallSkeleton(i, 32));
            list.add(new SmallZombie(i, 64));
        }

        long end = System.currentTimeMillis();
        printMem("After create");
        System.out.printf("Created %d enemies in %d ms%n", list.size(), (end - start));

        // cache size
        System.out.println("SpriteCache entries: " + SpriteCache.cachedEntries());

        // clear references
        list = null;
        System.gc();
        try { Thread.sleep(200); } catch (InterruptedException ignored) {}

        printMem("After GC");
    }
}
