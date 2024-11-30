package edu.fish.blinddate.controller;

import edu.fish.blinddate.exception.BaseException;
import edu.fish.blinddate.response.BaseResponse;
import edu.fish.blinddate.service.MainService;
import edu.fish.blinddate.utils.JWTUtil;
import edu.fish.blinddate.utils.UserContext;
import edu.fish.blinddate.vo.*;
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
            logger.error("register business error, params[newAccount={}, newPassword={}, userName={}]. err msg: {}", newAccount, newPassword, userName, e.getMessage());
            return BaseResponse.set(e.getCodeAndMsg());
        } catch (Exception e) {
            logger.error("register system error, params[newAccount={}, newPassword={}, userName={}]. error msg: {}", newAccount, newPassword, userName, e.getMessage());
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
            logger.error("login business error, params[account={}, password={}]. err msg: {}", account, password, e.getMessage());
            return BaseResponse.set(e.getCodeAndMsg());
        } catch (Exception e) {
            logger.error("login system error, params[account={}, password={}]. error msg: {}", account, password, e.getMessage());
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
            logger.error("getCandidateList system error, params[userId={}]. error msg: {}", userId, e.getMessage());
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
            logger.error("addCandidate business error, params[userId={}, candidateName={}]. err msg: {}", userId, candidateName, e.getMessage());
            return BaseResponse.set(e.getCodeAndMsg());
        }  catch (Exception e) {
            logger.error("addCandidate system error, params[userId={}, candidateName={}]. error msg: {}", userId, candidateName, e.getMessage());
            return BaseResponse.internalError();
        }
    }

    @RequestMapping(path = "/modifyCandidate", method = RequestMethod.POST)
    public BaseResponse<Object> modifyCandidate(Integer candidateId, String candidateName, Integer status) {
        Integer userId = UserContext.getUserId();
        try {
            mainService.modifyCandidate(userId, candidateId, candidateName, status);
            return BaseResponse.success();
        } catch (BaseException e) {
            logger.error("modifyCandidate business error, params[userId={}, candidateId={}, candidateName={}, status={}]. err msg: {}", userId, candidateId, candidateName, status, e.getMessage());
            return BaseResponse.set(e.getCodeAndMsg());
        }  catch (Exception e) {
            logger.error("modifyCandidate system error, params[userId={}, candidateId={}, candidateName={}, status={}]. error msg: {}", userId, candidateId, candidateName, status, e.getMessage());
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
            logger.error("removeCandidate business error, params[userId={}, candidateId={}]. err msg: {}", userId, candidateId, e.getMessage());
            return BaseResponse.set(e.getCodeAndMsg());
        }  catch (Exception e) {
            logger.error("removeCandidate system error, params[userId={}, candidateId={}]. error msg: {}", userId, candidateId, e.getMessage());
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
            logger.error("getCandidateBlindRecord business error, params[userId={}, candidateId={}]. err msg: {}", userId, candidateId, e.getMessage());
            return BaseResponse.set(e.getCodeAndMsg());
        }  catch (Exception e) {
            logger.error("getCandidateBlindRecord system error, params[userId={}, candidateId={}]. error msg: {}", userId, candidateId, e.getMessage());
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
            logger.error("setCandidateBlindRecord business error, params[userId={}, blindDateRecordVO={}]. err msg: {}", userId, blindDateRecordVO, e.getMessage());
            return BaseResponse.set(e.getCodeAndMsg());
        }  catch (Exception e) {
            logger.error("setCandidateBlindRecord system error, params[userId={}, blindDateRecordVO={}]. error msg: {}", userId, blindDateRecordVO, e.getMessage());
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
            logger.error("getFocusOnRank business error, params[userId={}, rankingListLength={}, youFlag={}]. err msg: {}", userId, rankingListLength, youFlag, e.getMessage());
            return BaseResponse.set(e.getCodeAndMsg());
        }  catch (Exception e) {
            logger.error("getFocusOnRank system error, params[userId={}, rankingListLength={}, youFlag={}]. err msg: {}", userId, rankingListLength, youFlag, e.getMessage());
            return BaseResponse.internalError();
        }
    }

    @RequestMapping(path = "/analyzeCandidate", method = RequestMethod.GET)
    public BaseResponse<Object> analyzeCandidate(Integer candidateId) {
        Integer userId = UserContext.getUserId();
        try {
            mainService.generateAnalysisCandidateReport(userId, candidateId);
            return BaseResponse.success();
        } catch (BaseException e) {
            logger.error("analyzeCandidate business error, params[userId={}, candidateId={}]. err msg: {}", userId, candidateId, e.getMessage());
            return BaseResponse.set(e.getCodeAndMsg());
        }  catch (Exception e) {
            logger.error("analyzeCandidate system error, params[userId={}, candidateId={}]. err msg: {}", userId, candidateId, e.getMessage());
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
            logger.error("getCandidateReport business error, params[userId={}, candidateId={}]. err msg: {}", userId, candidateId, e.getMessage());
            return BaseResponse.set(e.getCodeAndMsg());
        }  catch (Exception e) {
            logger.error("getCandidateReport system error, params[userId={}, candidateId={}]. err msg: {}", userId, candidateId, e.getMessage());
            return BaseResponse.internalError();
        }
    }

    @RequestMapping(path = "/getShareList", method = RequestMethod.GET)
    public BaseResponse<List<ShareMomentVO>> getShareList() {
        Integer userId = UserContext.getUserId();
        try {
            List<ShareMomentVO> ShareMomentVOList = mainService.getShareList();
            return BaseResponse.successData(ShareMomentVOList);
        } catch (Exception e) {
            logger.error("getShareList system error, params[userId={}]. err msg: {}", userId, e.getMessage());
            return BaseResponse.internalError();
        }
    }

    @RequestMapping(path = "/getShareDetail", method = RequestMethod.GET)
    public BaseResponse<ShareMomentDetailVO> getShareDetail(Integer shareUserId, Integer shareCandidateId) {
        // Integer userId = UserContext.getUserId();
        try {
            ShareMomentDetailVO shareMomentDetailVO = mainService.getShareDetail(shareUserId, shareCandidateId);
            return BaseResponse.successData(shareMomentDetailVO);
        } catch (BaseException e) {
            logger.error("getShareDetail business error, params[shareUserId={}, shareCandidateId={}]. err msg: {}", shareUserId, shareCandidateId, e.getMessage());
            return BaseResponse.set(e.getCodeAndMsg());
        } catch (Exception e) {
            logger.error("getShareDetail system error, params[shareUserId={}, shareCandidateId={}]. err msg: {}", shareUserId, shareCandidateId, e.getMessage());
            return BaseResponse.internalError();
        }
    }
}
