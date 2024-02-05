package edu.fish.blinddate.service;

public interface MainService {
    boolean registerUser(String newAccount, String newPassword, String userName);

    Integer userLogin(String account, String password);
}
