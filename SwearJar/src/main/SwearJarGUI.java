package main;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class SwearJarGUI implements UserManagerObserver {

    private java.util.List<User> users;
    private JFrame frame = new JFrame();
    private UserManager userManager = UserManager.getInstance();

    private JLabel userLabel = new JLabel();
    private JLabel balanceLabel = new JLabel();

    DefaultListModel<String> violationsModel = new DefaultListModel<>();

    public SwearJarGUI(java.util.List<User> userList) {
        this.users = userList;
        this.userManager.addObserver(this);
        setupFrame();
    }

    private void setupFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("SwearJar");
        frame.setSize(300,250);
        frame.setLocationRelativeTo(null);
    }

    void show() {
        Object[] userNames = users.stream().map(u -> u.name).toArray();

        final JPanel loggedOutPanel = new JPanel(new BorderLayout());

        final JPanel dropdownPanel = new JPanel();
        JLabel comboLbl = new JLabel("Choose a user:");
        JComboBox userDropdown = new JComboBox(userNames);
        dropdownPanel.add(comboLbl);
        dropdownPanel.add(userDropdown);


        final JPanel loggedInPanel = new JPanel(new BorderLayout());
        loggedInPanel.setVisible(false);

        final JPanel userInfoPanel = new JPanel();
        userInfoPanel.add(userLabel);
        userInfoPanel.add(balanceLabel);

        final JPanel violationsPanel = new JPanel(new BorderLayout());

        JLabel violationsHeader = new JLabel("Violations:");
        violationsPanel.add(violationsHeader, BorderLayout.NORTH);

        JList<String> violationsList = new JList<>(violationsModel);
        violationsList.setLayoutOrientation(JList.VERTICAL);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(violationsList);
        violationsPanel.add(scrollPane, BorderLayout.CENTER);

        JButton loginButton = new JButton( "Login");
        loginButton.addActionListener(event -> {

            int selectedIndex = userDropdown.getSelectedIndex();
            User activeUser = users.get(selectedIndex);
            UserManager.getInstance().setActiveUser(activeUser);

            loggedInPanel.setVisible(true);
            loggedOutPanel.setVisible(false);
        });

        JButton logoutButton = new JButton( "Logout");
        logoutButton.addActionListener(event -> {
            userManager.logout();
            loggedInPanel.setVisible(false);
            loggedOutPanel.setVisible(true);
        });

        loggedOutPanel.add(dropdownPanel, BorderLayout.NORTH);
        loggedOutPanel.add(loginButton,BorderLayout.SOUTH);

        loggedInPanel.add(userInfoPanel, BorderLayout.NORTH);
        loggedInPanel.add(violationsPanel, BorderLayout.CENTER);
        loggedInPanel.add(logoutButton, BorderLayout.SOUTH);


        frame.add(loggedInPanel, BorderLayout.NORTH);
        frame.add(loggedOutPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    @Override
    public void userDidLogin(User user) {
        if (user == null) return;
        userLabel.setText("User: " + user.name);
        balanceLabel.setText("- Balance: " + user.balance);

        java.util.List<String> transactions = new ArrayList<>();
        for (Transaction t : userManager.getTransactions()) {
            transactions.add(t.toString());
        }
        Collections.reverse(transactions);
        violationsModel.addAll(transactions);
    }

    @Override
    public void userDidLogout() {
        violationsModel.removeAllElements();
    }

    @Override
    public void balanceDidChange(User user) {
        if (user == null) return;
        balanceLabel.setText("- Balance: " + user.balance);
    }
}
