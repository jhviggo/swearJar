package main;

public class User {

    String name;
    double balance;
    String id;

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", balance=" + balance +
                ", id='" + id + '\'' +
                '}';
    }
}
