package ru.kirill.portalService.services;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class GeneratePasswordService {
    private static final String CHAR_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 6;

    public static String generatePassword() {
        Random random = new Random();

        List<Character> password = new ArrayList<>();

        password.add(CHAR_SET.charAt(random.nextInt(10)));

        password.add(CHAR_SET.charAt(10 + random.nextInt(26)));

        password.add(CHAR_SET.charAt(36 + random.nextInt(26)));

        while (password.size() < PASSWORD_LENGTH) {
            password.add(CHAR_SET.charAt(random.nextInt(CHAR_SET.length())));
        }

        Collections.shuffle(password);

        return StringUtils.join(password.toArray());
    }
}