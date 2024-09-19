package edu.fish.blinddate.controller;

import edu.fish.blinddate.exception.BaseException;
import edu.fish.blinddate.response.BaseResponse;
import edu.fish.blinddate.service.MainService;
import edu.fish.blinddate.utils.JWTUtil;
import edu.fish.blinddate.utils.UserContext;
import edu.fish.blinddate.vo.BlindDateRecordVO;
import edu.fish.blinddate.vo.CandidateReportVO;
import edu.fish.blinddate.vo.CandidateVO;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Supplier;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
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
            logger.error("system internalError, register input params: newAccount={}, newPassword={}, userName={}. internalError msg: {}", newAccount, newPassword, userName, e.getMessage());
            return BaseResponse.internalError();
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
            logger.error("system internalError, login input params: account={}, password={}. internalError msg: {}", account, password, e.getMessage());
            return BaseResponse.internalError();
        }

    }

    @RequestMapping(path = "/getCandidateList", method = RequestMethod.GET)
    public BaseResponse<List<CandidateVO>> getCandidateList() {
        Integer userId = UserContext.getUserId();
        try {
            List<CandidateVO> candidateVOList = mainService.getCandidateListByUserId(userId);
            return BaseResponse.successData(candidateVOList);
        } catch (Exception e) {
            logger.error("system internalError, getCandidateList input params: userId={}. internalError msg: {}", userId, e.getMessage());
            return BaseResponse.internalError();
        }
    }

    @RequestMapping(path = "/addCandidate", method = RequestMethod.POST)
    public BaseResponse<Object> addCandidate(String candidateName) {
        Integer userId = UserContext.getUserId();
        try {
            mainService.addCandidate(userId, candidateName);
            return BaseResponse.success();
        } catch (BaseException e) {
            return BaseResponse.set(e.getCodeAndMsg());
        }  catch (Exception e) {
            logger.error("system internalError, addCandidate input params: userId={}, candidateName={}. internalError msg: {}", userId, candidateName, e.getMessage());
            return BaseResponse.internalError();
        }
    }

    @RequestMapping(path = "/modifyCandidate", method = RequestMethod.POST)
    public BaseResponse<Object> modifyCandidate(Integer candidateId, String candidateName) {
        Integer userId = UserContext.getUserId();
        try {
            mainService.modifyCandidate(userId, candidateId, candidateName);
            return BaseResponse.success();
        } catch (BaseException e) {
            return BaseResponse.set(e.getCodeAndMsg());
        }  catch (Exception e) {
            logger.error("system internalError, modifyCandidate input params: userId={}, candidateId={}, candidateName={}. internalError msg: {}", userId, candidateId, candidateName, e.getMessage());
            return BaseResponse.internalError();
        }
    }

    @RequestMapping(path = "/removeCandidate", method = RequestMethod.DELETE)
    public BaseResponse<Object> removeCandidate(Integer candidateId) {
        Integer userId = UserContext.getUserId();
        try {
            mainService.removeCandidate(userId, candidateId);
            return BaseResponse.success();
        } catch (BaseException e) {
            return BaseResponse.set(e.getCodeAndMsg());
        }  catch (Exception e) {
            logger.error("system internalError, removeCandidate input params: userId={}, candidateId={}. internalError msg: {}", userId, candidateId, e.getMessage());
            return BaseResponse.internalError();
        }
    }

    @RequestMapping(path = "/getCandidateBlindRecord", method = RequestMethod.GET)
    public BaseResponse<BlindDateRecordVO> getCandidateBlindRecord(Integer candidateId) {
        Integer userId = UserContext.getUserId();
        try {
            BlindDateRecordVO blindDateRecordVO = mainService.getCandidateBlindRecord(userId, candidateId);
            return BaseResponse.successData(blindDateRecordVO);
        } catch (BaseException e) {
            return BaseResponse.set(e.getCodeAndMsg());
        }  catch (Exception e) {
            logger.error("system internalError, getCandidateBlindRecord input params: userId={}, candidateId={}. internalError msg: {}", userId, candidateId, e.getMessage());
            return BaseResponse.internalError();
        }
    }

    @RequestMapping(path = "/setCandidateBlindRecord", method = RequestMethod.POST)
    public BaseResponse<BlindDateRecordVO> setCandidateBlindRecord(@RequestBody BlindDateRecordVO blindDateRecordVO) {
        Integer userId = UserContext.getUserId();
        try {
            mainService.setCandidateBlindRecord(userId, blindDateRecordVO);
            return BaseResponse.success();
        } catch (BaseException e) {
            return BaseResponse.set(e.getCodeAndMsg());
        }  catch (Exception e) {
            logger.error("system internalError, setCandidateBlindRecord input params: userId={}, blindDateRecordVO={}. internalError msg: {}", userId, blindDateRecordVO, e.getMessage());
            return BaseResponse.internalError();
        }
    }

    @RequestMapping(path = "/getFocusOnRank", method = RequestMethod.GET)
    public BaseResponse<List<String>> getFocusOnRank(@RequestParam(name = "rankingListLength", defaultValue = "5") Integer rankingListLength, boolean youFlag) {
        Integer userId = UserContext.getUserId();
        try {
            List<String> nameList = mainService.getFocusOnRank(userId, youFlag, rankingListLength);
            return BaseResponse.successData(nameList);
        } catch (BaseException e) {
            return BaseResponse.set(e.getCodeAndMsg());
        }  catch (Exception e) {
            logger.error("system internalError, getFocusOnRank input params: userId={}, rankingListLength={}, youFlag={}, internalError msg: {}", userId, rankingListLength, youFlag, e.getMessage());
            return BaseResponse.internalError();
        }
    }

    @RequestMapping(path = "/analyzeCandidate", method = RequestMethod.GET)
    public BaseResponse<Object> generateAnalysisCandidateReport(Integer candidateId) {
        Integer userId = UserContext.getUserId();
        try {
            mainService.generateAnalysisCandidateReport(userId, candidateId);
            return BaseResponse.success();
        } catch (BaseException e) {
            return BaseResponse.set(e.getCodeAndMsg());
        }  catch (Exception e) {
            logger.error("system internalError, analyzeCandidate input params: userId={}, candidateId={}. internalError msg: {}", userId, candidateId, e.getMessage());
            return BaseResponse.internalError();
        }
    }

    @RequestMapping(path = "/getCandidateReport", method = RequestMethod.GET)
    public BaseResponse<CandidateReportVO> getCandidateReport(Integer candidateId) {
        Integer userId = UserContext.getUserId();
        try {
            CandidateReportVO candidateReportVO = mainService.getAnalysisCandidateReport(userId, candidateId);
            return BaseResponse.successData(candidateReportVO);
        } catch (BaseException e) {
            return BaseResponse.set(e.getCodeAndMsg());
        }  catch (Exception e) {
            logger.error("system internalError, getCandidateReport input params: userId={}, candidateId={}. internalError msg: {}", userId, candidateId, e.getMessage());
            return BaseResponse.internalError();
        }
    }
}
