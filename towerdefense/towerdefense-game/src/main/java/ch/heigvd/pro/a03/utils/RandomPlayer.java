package ch.heigvd.pro.a03.utils;

import ch.heigvd.pro.a03.users.User;

import java.util.Random;

/**
 * Generates a random player
 */
public class RandomPlayer {
    public static final User USER;

    static {
        Random random = new Random();
        int id = Math.abs(random.nextInt());

        USER = new User(id, "Player" + id, null);
    }
}
