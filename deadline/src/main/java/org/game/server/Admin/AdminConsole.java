package org.game.server.Admin;

import lombok.extern.slf4j.Slf4j;
import org.game.server.Server;
import org.game.server.GameWorldFacade;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AdminConsole {

    private final GameWorldFacade gameWorld;

    public AdminConsole(Server server) {
        this.gameWorld = server.getGameWorld(); // facade
    }

    public void start() {
        try (Scanner sc = new Scanner(System.in)) {
            log.info("--- Game Admin Console ---");
            log.info("Commands: start, waves, powerups, exit");

            while (true) {
                log.info("> ");
                String cmd = sc.nextLine().trim();

                switch (cmd) {
                    case "exit" -> {
                        log.info("Exiting admin console.");
                        return;
                    }
                    case "start" -> {
                        log.info("Starting game world.");
                        gameWorld.startSpawningIndividualEnemies(0, 5, TimeUnit.SECONDS);
                        gameWorld.startUpdatingEnemyPos(0, 50, TimeUnit.MILLISECONDS);
                        log.info("World started!");
                    }
                    case "waves" -> {
                        log.info("Spawning waves!");
                        gameWorld.startSpawningWaves(10, 30, TimeUnit.SECONDS);
                    }
                    case "powerups" -> {
                        log.info("Spawning bonus powerups.");
                        gameWorld.startDispensingPowerUps(10, 15, TimeUnit.SECONDS);
                    }
                    default -> log.info("Unknown command: {}", cmd);
                }
            }
        }
    }

    public static void main() {
        Server server = new Server();
        server.setAdminMode(true);
        new Thread(() -> {
            try {
                server.start();
            } catch (IOException e) {
                log.error("Error starting server", e);
            }
        }).start();

        new AdminConsole(server).start();
    }
}

