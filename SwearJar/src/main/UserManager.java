package main;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserManager {

    private static UserManager singleton = new UserManager();

    private UserManager() { }

    public static UserManager getInstance( ) {
        return singleton;
    }

    private List<UserManagerObserver> observers = new ArrayList<>();

    void addObserver(UserManagerObserver observer) {
        observers.add(observer);
    }

    private User user;
    private List<Transaction> transactions = new ArrayList<>();

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public User getActiveUser() {
        return user;
    }

    public void setActiveUser(User activeUser) {
        if (this.user != null) return;
        this.user = activeUser;
        this.transactions = getTransactionsFrom(user);

        for (UserManagerObserver observer : observers) observer.userDidLogin(activeUser);
    }

    public void logout() {
        this.user = null;
        this.transactions = new ArrayList<>();
        for (UserManagerObserver observer : observers) observer.userDidLogout();
    }

    List<User> getUsers() throws IOException {
        try (InputStream is = new URL("https://swearjar-4a26e.firebaseio.com/users.json").openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            Gson gson = new Gson();

            Map<String, User> users = gson.fromJson(rd, new TypeToken<Map<String, User>>(){}.getType());
            for (Map.Entry<String, User> entry : users.entrySet()) {
                entry.getValue().id = entry.getKey();
            }

            return new ArrayList<>(users.values());
        }
    }

    void subtractFromBalance(double delta) {
        user.balance -= delta;
        for (UserManagerObserver observer : observers) observer.balanceDidChange(user);
    }

    User getUser(){
        return user;
    }

    private List<Transaction> getTransactionsFrom(User user) {
        try (InputStream is = new URL("https://swearjar-4a26e.firebaseio.com/transactions.json").openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            Gson gson = new Gson();

            Map<String, Transaction> transactions = gson.fromJson(rd, new TypeToken<Map<String, Transaction>>(){}.getType());
            for (Map.Entry<String, Transaction> entry : transactions.entrySet()) {
                entry.getValue().id = entry.getKey();
            }

            return transactions.values().stream().filter(t -> t.from.equals(user.name)).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
