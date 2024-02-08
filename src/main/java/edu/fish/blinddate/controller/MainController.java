package edu.fish.blinddate.controller;

import edu.fish.blinddate.exception.BaseException;
import edu.fish.blinddate.response.BaseResponse;
import edu.fish.blinddate.service.MainService;
import edu.fish.blinddate.utils.JWTUtil;
import edu.fish.blinddate.vo.BlindDateRecordVO;
import edu.fish.blinddate.vo.CandidateVO;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.function.Supplier;

@RestController
@RequestMapping("/api")
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    private static final Supplier<Long> consumeTimer = System::currentTimeMillis;

    @Resource
    MainService mainService;

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public BaseResponse<Object> register(String newAccount, String newPassword, String userName) {
        try {
            mainService.registerUser(newAccount, newPassword, userName);

            return BaseResponse.success();
        } catch (BaseException e) {
            return BaseResponse.set(e.getCodeAndMsg());
        } catch (Exception e) {
            logger.error("system error, input params: newAccount={}, newPassword={}, userName={}. error msg: {}", newAccount, newPassword, userName, e);
            return BaseResponse.error();
        }
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public BaseResponse<String> login(String account, String password) {
        try {
            Integer userId = mainService.userLogin(account, password);
            String token = JWTUtil.getJwtToken(userId);
            return BaseResponse.successData(token);
        } catch (BaseException e) {
            return BaseResponse.set(e.getCodeAndMsg());
        } catch (Exception e) {
            logger.error("system error, input params: account={}, password={}. error msg: {}", account, password, e);
            return BaseResponse.error();
        }

    }

    @RequestMapping(path = "/getCandidateList", method = RequestMethod.GET)
    public BaseResponse<List<CandidateVO>> getCandidateList(Integer userId) {
        try {
            List<CandidateVO> candidateVOList = mainService.getCandidateListByUserId(userId);
            return BaseResponse.successData(candidateVOList);
        } catch (Exception e) {
            logger.error("system error, input params: userId={}. error msg: {}", userId, e);
            return BaseResponse.error();
        }
    }

    @RequestMapping(path = "/addCandidate", method = RequestMethod.POST)
    public BaseResponse<Object> addCandidate(Integer userId, String candidateName) {
        try {
            mainService.addCandidateWithUserId(userId, candidateName);
            return BaseResponse.success();
        } catch (BaseException e) {
            return BaseResponse.set(e.getCodeAndMsg());
        }  catch (Exception e) {
            logger.error("system error, input params: userId={}, candidateName={}. error msg: {}", userId, candidateName, e);
            return BaseResponse.error();
        }
    }

    @RequestMapping(path = "/getCandidateBlindRecord", method = RequestMethod.GET)
    public BaseResponse<BlindDateRecordVO> getCandidateBlindRecord(Integer userId, Integer candidateId) {
        try {
            BlindDateRecordVO blindDateRecordVO = mainService.getCandidateBlindRecord(userId, candidateId);
            return BaseResponse.successData(blindDateRecordVO);
        } catch (BaseException e) {
            return BaseResponse.set(e.getCodeAndMsg());
        }  catch (Exception e) {
            logger.error("system error, input params: userId={}, candidateId={}. error msg: {}", userId, candidateId, e);
            return BaseResponse.error();
        }
    }

    @RequestMapping(path = "/setCandidateBlindRecord", method = RequestMethod.POST)
    public BaseResponse<BlindDateRecordVO> setCandidateBlindRecord(Integer userId, BlindDateRecordVO blindDateRecordVO) {
        try {
            mainService.setCandidateBlindRecord(blindDateRecordVO);
            return BaseResponse.success();
        } catch (BaseException e) {
            return BaseResponse.set(e.getCodeAndMsg());
        }  catch (Exception e) {
            logger.error("system error, input params: userId={}, blindDateRecordVO={}. error msg: {}", userId, blindDateRecordVO, e);
            return BaseResponse.error();
        }
    }

    @RequestMapping(path = "/getFocusOnYouRank", method = RequestMethod.GET)
    public BaseResponse<List<String>> getFocusOnYouRank(Integer userId, @RequestParam(name = "rankingListLength", defaultValue = "5") Integer rankingListLength) {
        try {
            List<String> nameList = mainService.getFocusOnRank(userId,false, rankingListLength);
            return BaseResponse.successData(nameList);
        } catch (BaseException e) {
            return BaseResponse.set(e.getCodeAndMsg());
        }  catch (Exception e) {
            logger.error("system error, input params: userId={}, rankingListLength={}. error msg: {}", userId, rankingListLength, e);
            return BaseResponse.error();
        }
    }

    @RequestMapping(path = "/getYouFocusOnRank", method = RequestMethod.GET)
    public BaseResponse<List<String>> getYouFocusOnRank(Integer userId, @RequestParam(name = "rankingListLength", defaultValue = "5") Integer rankingListLength) {
        try {
            List<String> nameList = mainService.getFocusOnRank(userId, true, rankingListLength);
            return BaseResponse.successData(nameList);
        } catch (BaseException e) {
            return BaseResponse.set(e.getCodeAndMsg());
        }  catch (Exception e) {
            logger.error("system error, input params: userId={}, rankingListLength={}. error msg: {}", userId, rankingListLength, e);
            return BaseResponse.error();
        }
    }
}
