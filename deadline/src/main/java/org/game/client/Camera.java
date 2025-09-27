package org.game.client;

import lombok.Getter;
import org.game.server.WorldSettings;

public final class Camera {

    @Getter
    private double x;
    @Getter
    private double y;

    private final double lerpFactor;

    private final int deadzoneHalfW;
    private final int deadzoneHalfH;

    public Camera(double lerpFactor, int deadzoneHalfW, int deadzoneHalfH) {
        this.lerpFactor = lerpFactor;
        this.deadzoneHalfW = deadzoneHalfW;
        this.deadzoneHalfH = deadzoneHalfH;

        this.x = WorldSettings.tileSize * 23;
        this.y = WorldSettings.tileSize * 21;
    }

    public void update(double targetX, double targetY) {
        if (deadzoneHalfW > 0 || deadzoneHalfH > 0) {
            double left = x - deadzoneHalfW;
            double right = x + deadzoneHalfW;
            double top = y - deadzoneHalfH;
            double bottom = y + deadzoneHalfH;


            double nx = x;
            double ny = y;


            if (targetX < left) {
                nx = targetX + deadzoneHalfW;
            }
            if (targetX > right) {
                nx = targetX - deadzoneHalfW;
            }
            if (targetY < top) {
                ny = targetY + deadzoneHalfH;
            }
            if (targetY > bottom) {
                ny = targetY - deadzoneHalfH;
            }


            x += (nx - x) * lerpFactor;
            y += (ny - y) * lerpFactor;
        } else {
            x += (targetX - x) * lerpFactor;
            y += (targetY - y) * lerpFactor;
        }
    }

    public void snapTo(double cx, double cy, int viewportW, int viewportH, int worldWidth, int worldHeight) {
        this.x = cx;
        this.y = cy;
        clamp(viewportW, viewportH, worldWidth, worldHeight);
    }

    public void clamp(int viewportW, int viewportH, int worldWidth, int worldHeight) {
        double halfW = viewportW / 2.0;
        double halfH = viewportH / 2.0;

        if (worldWidth <= viewportW){
            x = worldWidth / 2.0;
        } else {
            x = Math.min(Math.max(x, halfW), worldWidth - halfW);
        }
        if (worldHeight <= viewportH){
            y = worldHeight / 2.0;
        } else {
            y = Math.min(Math.max(y, halfH), worldHeight - halfH);
        }

    }

}
