package com.ppl.stumanage.UserManagement;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SystemUserGenerator {

    private static final String[] NAMES = {"Alice", "Bob", "Charlie", "David", "Eva", "Frank", "Grace", "Henry", "Ivy", "Jack"};
    private static final int MAX_AGE = 60;
    private static final String[] STATUSES = {"Normal", "Locked"};
    private static final String[] EMAIL_DOMAINS = {"example.com", "company.com", "test.org", "mail.net"};

    public static List<SystemUser> generateRandomUserList(int count) {
        List<SystemUser> userList = new ArrayList<>();

        Random random = new Random();

        for (int i = 0; i < count; i++) {
            String name = getRandomName(random);
            int age = random.nextInt(MAX_AGE) + 18; // Random age between 18 and MAX_AGE
            String phoneNumber = generateRandomPhoneNumber(random);
            String status = getRandomStatus(random);
            String email = generateRandomEmail(random);

            SystemUser user = new SystemUser(name, age, phoneNumber, email, status);
            userList.add(user);
        }

        return userList;
    }

    private static String getRandomName(Random random) {
        return NAMES[random.nextInt(NAMES.length)];
    }

    private static String generateRandomPhoneNumber(Random random) {
        StringBuilder phoneNumber = new StringBuilder("+1-");

        for (int i = 0; i < 10; i++) {
            phoneNumber.append(random.nextInt(10));
        }

        return phoneNumber.toString();
    }

    private static String getRandomStatus(Random random) {
        return STATUSES[random.nextInt(STATUSES.length)];
    }

    private static String generateRandomEmail(Random random) {
        String username = getRandomName(random).toLowerCase() + random.nextInt(100);
        String domain = EMAIL_DOMAINS[random.nextInt(EMAIL_DOMAINS.length)];

        return username + "@" + domain;
    }
}
