package main;

public class Transaction {
    Double amount;
    String from;
    String word;
    String id;

    @Override
    public String toString() {
        return "{" +
                "amount=" + amount +
                ", from='" + from + '\'' +
                ", word='" + word + '\'' +
                '}';
    }
}
