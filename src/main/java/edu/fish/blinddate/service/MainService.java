package edu.fish.blinddate.service;

import edu.fish.blinddate.exception.BaseException;
import edu.fish.blinddate.vo.*;

import java.util.List;

public interface MainService {
    void registerUser(String newAccount, String newPassword, String userName) throws BaseException;

    Integer userLogin(String account, String password) throws BaseException;

    List<CandidateVO> getCandidateListByUserId(Integer userId);

    void addCandidate(Integer userId, String candidateName) throws BaseException;

    void modifyCandidate(Integer userId, Integer candidateId, String candidateName, Integer status) throws BaseException;

    void removeCandidate(Integer userId, Integer candidateId) throws BaseException;

    BlindDateRecordVO getCandidateBlindRecord(Integer userId, Integer candidateId) throws BaseException;

    void setCandidateBlindRecord(Integer userId, BlindDateRecordVO blindDateRecordVO) throws BaseException;

    List<String> getFocusOnRank(Integer userId, boolean you, int rankingListLength) throws BaseException;

    void generateAnalysisCandidateReport(Integer userId, Integer candidateId) throws BaseException;

    CandidateReportVO getAnalysisCandidateReport(Integer userId, Integer candidateId) throws BaseException;

    List<ShareMomentVO> getShareList();

    ShareMomentDetailVO getShareDetail(Integer shareUserId, Integer shareCandidateId) throws BaseException;
}
