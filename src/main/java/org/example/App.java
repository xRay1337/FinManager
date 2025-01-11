package org.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class App {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String filePath = "data.json";
        File f = new File(filePath);
        List<User> users = (f.exists() && !f.isDirectory()) ? loadUsersFromFile(filePath) : new ArrayList<>();

        try {
            while (true) {
                System.out.println("Введите логин и пароль через пробел. Для выхода exit.");

                String input = in.nextLine().replaceAll("\\s+", " ").trim();
                String[] parts = input.split(" ");
                System.out.println(input);

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
                            System.out.println("Для добавления категории доходов введите название.");
                            System.out.println("Для добавления категории расходов введите название и лимит через пробел.");
                            System.out.println("Для добавления операции введите +/-, сумму и категорию через пробел.");
                            input = in.nextLine().replaceAll("\\s+", " ").trim();
                            parts = input.split(" ");

                            try {
                                if (input.equals("logout")) break;
                                else if (parts.length == 1) user.addCategory(parts[0]);
                                else if (parts.length == 2) {
                                    String amount = parts[1].replaceAll("[KkКк]", "000");
                                    user.addCategory(parts[0], Double.parseDouble(amount));
                                } else if (parts.length == 3) {
                                    String amount = parts[1].replaceAll("[KkКк]", "000");
                                    String message =
                                            user.addAmount(parts[0].charAt(0), Double.parseDouble(amount), parts[2]);
                                    if (!message.isEmpty()) System.out.println(message);
                                }
                            } catch (Exception e) {
                                System.out.println("Ошибка ввода: " + e.getMessage());
                            }
                        }
                    }
                }
            }
        } finally {
            saveUsersToFile(users, filePath);
        }
    }

    private static void PrintUserInfo(User user) {
        double totalRemainder = user.sum('+') - user.sum('-');
        if (totalRemainder < 0) System.out.println("! Расходы превысили доходы");
        System.out.println("┌─────────────────────────────────────────────────┐");
        System.out.println("│ Общий доход: " + user.sum('+'));
        System.out.println("├┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┤");
        System.out.println("│ Доходы по категориям:");
        for (Category c : user.getIncomes()) {
            System.out.println("│ + " + c.getName() + ": " + c.getSum());
        }
        System.out.println("├─────────────────────────────────────────────────┤");
        System.out.println("│ Общие расходы: " + user.sum('-'));
        System.out.println("├┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┤");
        System.out.println("│ Расходы по категориям:");
        for (Category c : user.getOutcomes()) {
            double remainder = c.getLimit() - c.getSum();
            System.out.println("│ - " + c.getName() + ": " + c.getSum() + ". Остаток: " + remainder);
        }
        System.out.println("├─────────────────────────────────────────────────┤");
        System.out.println("│ Общий остаток: " + totalRemainder);
        System.out.println("└─────────────────────────────────────────────────┘");
    }

    private static List<User> loadUsersFromFile(String filePath) {
        List<User> users = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(filePath)) {
            Object obj = jsonParser.parse(reader);
            JSONArray userList = (JSONArray) obj;

            for (Object userObj : userList) {
                JSONObject userJson = (JSONObject) userObj;
                String userLogin = (String) userJson.get("login");
                String userPassword = (String) userJson.get("password");

                JSONArray incomeArray = (JSONArray) userJson.get("incomes");

                List<Category> incomes = new ArrayList<>();

                for (Object incomeObj : incomeArray) {
                    JSONObject incomeJson = (JSONObject) incomeObj;

                    String name = (String) incomeJson.get("name");
                    double limit = (double) incomeJson.get("limit");

                    List<Double> amounts = new ArrayList<>();

                    JSONArray incomeOpsArray = (JSONArray) incomeJson.get("amounts");

                    for (Object o : incomeOpsArray) {
                        amounts.add(((Number) o).doubleValue());
                    }

                    Category category = new Category(name, limit, amounts);
                    incomes.add(category);
                }

                JSONArray outcomeArray = (JSONArray) userJson.get("outcomes");

                List<Category> outcomes = new ArrayList<>();

                for (Object outcomeObj : outcomeArray) {
                    JSONObject outcomeJson = (JSONObject) outcomeObj;

                    String name = (String) outcomeJson.get("name");
                    double limit = (double) outcomeJson.get("limit");

                    List<Double> amounts = new ArrayList<>();

                    JSONArray outcomeOpsArray = (JSONArray) outcomeJson.get("amounts");

                    for (Object o : outcomeOpsArray) {
                        amounts.add(((Number) o).doubleValue());
                    }

                    Category category = new Category(name, limit, amounts);
                    outcomes.add(category);
                }

                User user = new User(userLogin, userPassword, incomes, outcomes);
                users.add(user);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return users;
    }

    private static void saveUsersToFile(Collection<User> users, String filePath) {
        JSONArray userList = new JSONArray();

        for (User user : users) {
            JSONObject userDetails = new JSONObject();
            userDetails.put("login", user.getLogin());
            userDetails.put("password", user.getPassword());

            JSONArray incomesArray = new JSONArray();

            for (Category i : user.getIncomes()) {
                JSONObject incomeDetails = new JSONObject();
                incomeDetails.put("name", i.getName());
                incomeDetails.put("limit", i.getLimit());
                incomeDetails.put("amounts", i.getAmounts());
                incomesArray.add(incomeDetails);
            }

            userDetails.put("incomes", incomesArray);

            JSONArray outcomesArray = new JSONArray();

            for (Category o : user.getOutcomes()) {
                JSONObject incomeDetails = new JSONObject();
                incomeDetails.put("name", o.getName());
                incomeDetails.put("limit", o.getLimit());
                incomeDetails.put("amounts", o.getAmounts());
                outcomesArray.add(incomeDetails);
            }

            userDetails.put("outcomes", outcomesArray);

            userList.add(userDetails);

            try (FileWriter file = new FileWriter(filePath)) {
                file.write(userList.toJSONString());
                file.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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