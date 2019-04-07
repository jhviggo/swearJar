package main;

public interface UserManagerObserver {
    void userDidLogin(User user);
    void userDidLogout();
    void balanceDidChange(User user);
}
