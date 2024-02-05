package edu.fish.blinddate.controller;

import edu.fish.blinddate.enums.ResponseEnum;
import edu.fish.blinddate.response.BaseResponse;
import edu.fish.blinddate.service.MainService;
import edu.fish.blinddate.utils.JWTUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @Resource
    MainService mainService;

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public BaseResponse<Object> register(String newAccount, String newPassword, String userName) {
        // 参数校验
        if (newAccount == null || newPassword == null || userName == null) {
            return BaseResponse.set(ResponseEnum.MISSING_PARAMS);
        }

        try {
            boolean isRegister = mainService.registerUser(newAccount, newPassword, userName);
            // 没有注册成功 代表账号存在
            if (!isRegister) {
                return BaseResponse.set(ResponseEnum.ACCOUNT_EXISTS);
            }

            return BaseResponse.success();
        } catch (Exception e) {
            logger.error("system error, input params: {}, {}, {}", newAccount, newPassword, userName);
            return BaseResponse.error();
        }
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public BaseResponse<String> login(String account, String password) {
        // 参数校验
        if (account == null || password == null) {
            return BaseResponse.set(ResponseEnum.MISSING_PARAMS);
        }

        try {
            Integer userId = mainService.userLogin(account, password);
            // 没有拿到userId代表登录失败
            if (userId == null) {
                return BaseResponse.set(ResponseEnum.ACCOUNT_NOT_EXISTS_OR_PASSWORD_ERR);
            }

            // 给予token
            String token = JWTUtil.getJwtToken(userId);
            return BaseResponse.successData(token);
        } catch (Exception e) {
            logger.error("system error, input params: {}, {}", account, password);
            return BaseResponse.error();
        }

    }
}
