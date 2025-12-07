package org.game.server.Admin;

import org.game.server.Admin.expressions.*;

import java.util.ArrayList;
import java.util.List;

public class CommandParser {

    public static Expression parse(String input) {

        if (input.contains(";")) {
            String[] parts = input.split(";");
            List<Expression> expressions = new ArrayList<Expression>();

            for (String part : parts) {
                Expression e = parseSingle(part.trim());
                expressions.add(e);
            }

            return new SequenceExpression(expressions);
        }

        return parseSingle(input);
    }

    private static Expression parseSingle(String input) {
        if (input == null || input.isEmpty()) {
            return new UnknownExpression(input);
        }

        String[] tokens = input.trim().split(" ");

        if (tokens.length < 2) {
            return new UnknownExpression(input);
        }

        // --- /spawn enemy
        if (tokens[0].equalsIgnoreCase("/spawn") && tokens[1].equalsIgnoreCase("enemy")) {

            // spawn enemy random
            if (tokens.length == 3 && tokens[2].equalsIgnoreCase("random")) {
                return new SpawnRandomEnemyExpression();
            }

            // spawn enemy wave
            if (tokens.length == 3 && tokens[2].equalsIgnoreCase("wave")) {
                return new SpawnWaveEnemyExpression();
            }

            // spawn enemy <size> <type>
            if (tokens.length == 4) {
                String size = tokens[2];
                String type = tokens[3];
                return new SpawnSpecificEnemyExpression(size, type);
            }
        }

        // spawn powerups
        if (tokens[0].equalsIgnoreCase("/spawn") && tokens[1].equalsIgnoreCase("powerups")) {
            return new SpawnPowerupsExpression();
        }

        return new UnknownExpression(input);
    }
}

