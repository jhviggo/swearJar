package main;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class SwearJar implements UserManagerObserver, AudioAnalyzerObserver {

    public static void main(String[] args) {
        new SwearJar();
    }

    private UserManager userManager = UserManager.getInstance();
    private SwearManager swearManager = SwearManager.getInstance();
    private AudioAnalyzer audioAnalyzer = new AudioAnalyzer();
    private Thread thread;

    SwearJarGUI gui;

    public SwearJar() {

        audioAnalyzer.add(this);
        this.thread = new Thread(audioAnalyzer);
        try {
            List<User> users = userManager.getUsers();
            System.out.println(users);

            userManager.addObserver(this);

            gui = new SwearJarGUI(users);
            gui.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void userDidLogin(User user) {
        audioAnalyzer.analyze(true);
        if (!thread.isAlive()) thread.start();
    }

    @Override
    public void userDidLogout() {
        audioAnalyzer.analyze(false);
    }

    @Override
    public void didAnalyze(String string) {
        for (SwearModel s : swearManager.analyzeString(string)) {
            handleSwear(s);
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e){
                System.out.println(e);
            }

        }
    }

    private void handleSwear(SwearModel swear) {
        // WE HAVE A SWEAR!

        Transaction transaction = new Transaction();
        transaction.from = userManager.getActiveUser().name;
        transaction.amount = swear.cost;
        transaction.word = swear.word;

        //TODO: fejl, hvis der gives to sweares i en sætning, cleares listen. Mne tilføjer man en igen er alle der.
        gui.violationsModel.insertElementAt(transaction.toString(), 0);
        //gui.violationsModel.add(0, transaction.toString());

        //Send transaction til firebase
        addFirebaseTransaction(transaction);

        // Send penge til mobilepay
        sendMobilepay(transaction.amount);

        // Opdater balance
        userManager.subtractFromBalance(swear.cost);

        // Opdater count
        updateFirebaseSwearCount(swear);

        //Opdater brugerens balance i firebase
        updateFirebaseBalance(userManager.getUser());


        //TODO: Andre kald her, skal ske ligesådan

        //TODO: lav det hele om til en jar fil, så det er nemt at køre?

        System.out.println("bah bah!" + swear);
    }


    private void addFirebaseTransaction(Transaction t) {
        Thread thread = new Thread(() -> {
            try {
                String url = "https://swearjar-4a26e.firebaseio.com/transactions.json";
                URL obj = new URL(url);
                String to_put = "{\"amount\": " + t.amount.toString() +
                        ", \"from\": \"" + t.from + "\"" +
                        ", \"word\": \"" + t.word + "\"}";

                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("POST");
                con.addRequestProperty("Accept", "application/json");
                con.addRequestProperty("Content-Type", "application/json");
                con.setDoOutput(true);
                con.setChunkedStreamingMode(0);

                System.out.println("\nSending 'POST' request to URL : " + url);

                OutputStream out = new BufferedOutputStream(con.getOutputStream());
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(out, "UTF-8"));
                writer.write(to_put);
                writer.flush();
                writer.close();
                out.close();

                con.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    private void updateFirebaseBalance(User user) {
        Thread thread = new Thread(() -> {
            try {
                String url = "https://swearjar-4a26e.firebaseio.com/users/" + user.id + ".json";
                URL obj = new URL(url);
                String to_put = "{\"balance\": " + Double.toString(user.balance) +
                        ", \"name\": \"" + user.name + "\"}";

                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("PUT");
                con.addRequestProperty("Accept", "application/json");
                con.addRequestProperty("Content-Type", "application/json");
                con.setDoOutput(true);
                con.setChunkedStreamingMode(0);

                System.out.println("\nSending 'POST' request to URL : " + url);

                OutputStream out = new BufferedOutputStream(con.getOutputStream());
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(out, "UTF-8"));
                writer.write(to_put);
                writer.flush();
                writer.close();
                out.close();

                con.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    private void sendMobilepay(double amount) {
        Thread thread = new Thread(() -> {
            try {
                String url = "https://api.sandbox.mobilepay.dk/bindings-restapi/api/v1/payments/payout-bankaccount";
                URL obj = new URL(url);
                String to_put =
                        "{ \"merchantId\": \"d314f902-9b51-41f0-90da-948b952efa85\"" +
                                ", \"merchantBinding\": \"000000-9988\"" +
                                ", \"receiverRegNumber\": \"3098\"" +
                                ", \"receiverAccountNumber\": \"3100460793\"" +
                                ", \"amount\": " + Double.toString(amount) + "}";

                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("POST");
                con.addRequestProperty("Content-Type", "application/json");
                con.addRequestProperty("x-ibm-client-id", "1c0cd3ff-1143-476b-b136-efe9b1f5ecf3");
                con.addRequestProperty("x-ibm-client-secret", "L7yW0eV0eK5yX1nK4rO0lI8sX5aN2tL6aQ0sL7gM1xO6sW8kK1");
                con.setDoOutput(true);
                con.setChunkedStreamingMode(0);

                System.out.println("\nSending 'POST' request to URL : " + url);

                OutputStream out = new BufferedOutputStream(con.getOutputStream());
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(out, "UTF-8"));
                writer.write(to_put);
                writer.flush();
                writer.close();
                out.close();

                con.connect();

                InputStream in = new BufferedInputStream(con.getInputStream());
                byte[] contents = new byte[1024];

                int bytesRead = 0;
                String strFileContents = "";
                while ((bytesRead = in.read(contents)) != -1) {
                    strFileContents += new String(contents, 0, bytesRead);

                }

                System.out.println(strFileContents);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    private void updateFirebaseSwearCount(SwearModel swear) {
        Thread thread = new Thread(() -> {
            try {
                SwearModel to_update = new SwearModel();
                for (SwearModel s : swearManager.getSwears()){
                    if (s.id.equals(swear.id)){
                        to_update = s;
                        break;
                    }
                }

                String url = "https://swearjar-4a26e.firebaseio.com/known_swears/" + to_update.id + ".json";
                URL obj = new URL(url);
                String to_put = "{\"cost\": " + to_update.cost.toString() +
                        ", \"count\": " + (to_update.count+1)  +
                        ", \"word\": \"" + to_update.word + "\"}";

                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("PUT");
                con.addRequestProperty("Accept", "application/json");
                con.addRequestProperty("Content-Type", "application/json");
                con.setDoOutput(true);
                con.setChunkedStreamingMode(0);

                System.out.println("\nSending 'POST' request to URL : " + url);

                OutputStream out = new BufferedOutputStream(con.getOutputStream());
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(out, "UTF-8"));
                writer.write(to_put);
                writer.flush();
                writer.close();
                out.close();

                con.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    @Override
    public void balanceDidChange(User user) {
    }
}
