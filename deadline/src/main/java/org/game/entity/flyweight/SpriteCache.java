package org.game.entity.flyweight;

import org.game.entity.ImageSprite;
import org.game.utils.ByteFiles;

import java.awt.image.BufferedImage;
import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class SpriteCache {
    private static final ConcurrentMap<String, ImageSprite[]> CACHE = new ConcurrentHashMap<>();

    private SpriteCache() {}

    /**
     Returns cached ImageSprite[4] for given source+prefix (up,left,down,right).
     Loads from disk only once.
     **/
    public static ImageSprite[] getOrLoad(String source, String prefix) {
        String key = source + "::" + prefix;
        return CACHE.computeIfAbsent(key, _ -> loadSprites(source, prefix));
    }

    private static ImageSprite[] loadSprites(String source, String prefix) {
        // load images only once per (source,prefix)
        BufferedImage up1 = loadImage(source, prefix, "up", 1);
        BufferedImage up2 = loadImage(source, prefix, "up", 2);
        BufferedImage left1 = loadImage(source, prefix, "left", 1);
        BufferedImage left2 = loadImage(source, prefix, "left", 2);
        BufferedImage down1 = loadImage(source, prefix, "down", 1);
        BufferedImage down2 = loadImage(source, prefix, "down", 2);
        BufferedImage right1 = loadImage(source, prefix, "right", 1);
        BufferedImage right2 = loadImage(source, prefix, "right", 2);

        return new ImageSprite[] {
                ImageSprite.upSprite(up1, up2),
                ImageSprite.leftSprite(left1, left2),
                ImageSprite.downSprite(down1, down2),
                ImageSprite.rightSprite(right1, right2)
        };
    }

    private static BufferedImage loadImage(String source, String prefix, String direction, int frame) {
        return ByteFiles.loadImage(MessageFormat.format("assets/{0}/{1}_{2}_{3}.png", source, prefix, direction, frame));
    }

    // For debugging
    public static int cachedEntries() {
        return CACHE.size();
    }

    public static void clear() {
        CACHE.clear();
    }
}
