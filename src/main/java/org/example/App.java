package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        List<User> users = new ArrayList<>();

        while (true) {
            System.out.println("Введите логин и пароль через пробел. Для выхода exit.");

            String input = in.nextLine();
            String[] parts = input.split(" ");

            if (input.equals("exit")) break;

            if (parts.length == 2) {
                String login = parts[0];
                String password = parts[1];
                User user = GetUser(users, login, password);

                if (user == null) {
                    System.out.println("Неверный пароль.");
                } else {
                    while (true) {
                        PrintUserInfo(user);
                        System.out.println("Для смены пользователя введите logout.");
                        System.out.println("Для добавления категории доходов введите название через пробел.");
                        System.out.println("Для добавления категории расходов введите название и лимит через пробел.");
                        System.out.println("Для добавления операции введите +/-, сумму и категорию через пробел.");
                        input = in.nextLine();
                        parts = input.split(" ");

                        try {
                            if (input.equals("logout")) break;
                            else if (parts.length == 1) user.addCategory(parts[0]);
                            else if (parts.length == 2) user.addCategory(parts[0], Double.parseDouble(parts[1]));
//                            {
//                                Double limit;
//                                try {
//                                    limit = Double.parseDouble(parts[1]);
//                                    user.addCategory(parts[0], limit);
//                                }
//                                catch (NumberFormatException e) {
//                                    user.renameCategory(parts[0], parts[1]);
//                                }
//                            }
                            else if (parts.length == 3)
                                user.addAmount(parts[0].charAt(0), Double.parseDouble(parts[1]), parts[2]);
                        } catch (Exception e) {
                            System.out.println("Ошибка ввода: " + e.getMessage());
                        }
                    }
                }
            }
        }

    }

    private static void PrintUserInfo(User user) {
        System.out.println("┌───────────────────────────────────────────────┐");
        System.out.println("│ Общий доход: ");
        System.out.println("├┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┤");
        System.out.println("│ Доходы по категориям:");
        for (Category c : user.getIncomes()) {
            System.out.println("│ + " + c.getName() + ": " + c.getSum());
        }
        System.out.println("├───────────────────────────────────────────────┤");
        System.out.println("│ Общие расходы: ");
        System.out.println("├┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┤");
        System.out.println("│ Расходы по категориям:");
        for (Category c : user.getOutcomes()) {
            double remainder = c.getLimit() - c.getSum();
            System.out.println("│ - " + c.getName() + ": " + c.getSum() + ". Оставшийся бюджет: " + remainder);
        }
        System.out.println("└───────────────────────────────────────────────┘");
    }

    private static User GetUser(List<User> users, String login, String password) {
        User user = UserIdentification(users, login);

        if (user == null) {
            user = UserRegistration(users, login, password);
        } else if (!UserAuthentication(user, password)) {
            user = null;
        }

        return user;
    }

    private static boolean UserAuthentication(User user, String password) {
        return user.getPassword().equals(password);
    }

    private static User UserRegistration(List<User> users, String login, String password) {
        User user = new User(login, password);
        users.add(user);
        return user;
    }

    private static User UserIdentification(List<User> users, String login) {
        User user = null;

        for (User currentUser : users) {
            if (currentUser.getLogin().equals(login)) {
                user = currentUser;
                break;
            }
        }

        return user;
    }
}