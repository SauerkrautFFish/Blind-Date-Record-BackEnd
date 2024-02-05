package edu.fish.blinddate.service.impl;

import edu.fish.blinddate.service.MainService;
import org.springframework.stereotype.Service;

@Service
public class MainServiceImpl implements MainService {

    @Override
    public boolean registerUser(String newAccount, String newPassword, String userName) {
        return false;
    }

    @Override
    public Integer userLogin(String account, String password) {
        return null;
    }
}
