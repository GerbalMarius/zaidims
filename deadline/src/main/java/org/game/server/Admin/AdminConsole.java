package org.game.server.Admin;

import lombok.extern.slf4j.Slf4j;
import org.game.server.Admin.expressions.Expression;
import org.game.server.Server;
import org.game.server.GameWorldFacade;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AdminConsole {

    private final InterpreterContext ctx;

    public AdminConsole(Server server) {
        this.ctx = new InterpreterContext(server);
    }

    public void start() {
        try (Scanner sc = new Scanner(System.in)) {
            log.info("--- Game Admin Console ---");
            log.info("Available commands:");
            log.info("  /spawn enemy random");
            log.info("  /spawn enemy wave");
            log.info("  /spawn enemy <size> <type>");
            log.info("  /spawn powerups");
            log.info("  /killall");
            log.info("  exit");

            while (true) {
                log.info("> ");
                String cmd = sc.nextLine().trim();

                if (cmd.equalsIgnoreCase("exit")) {
                    log.info("Exiting admin console...");
                    return;
                }

                Expression expression = CommandParser.parse(cmd);
                expression.interpret(ctx);

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

