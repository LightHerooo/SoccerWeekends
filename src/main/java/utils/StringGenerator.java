package utils;

import java.util.Random;

public class StringGenerator {
    public StringBuilder generate(int length) {
        String buffer = "_()abcdefghijklmnopqrstuvwxyz0123456789";

        Random random = new Random();
        StringBuilder result = new StringBuilder();
        while (length > 0) {
            result.append(buffer.charAt(random.nextInt(buffer.length())));
            length--;
        }

        return result;
    }
}
