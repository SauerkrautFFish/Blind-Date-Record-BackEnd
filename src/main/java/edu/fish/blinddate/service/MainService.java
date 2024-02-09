package edu.fish.blinddate.service;

import edu.fish.blinddate.exception.BaseException;
import edu.fish.blinddate.vo.BlindDateRecordVO;
import edu.fish.blinddate.vo.CandidateVO;

import java.util.List;

public interface MainService {
    void registerUser(String newAccount, String newPassword, String userName) throws BaseException;

    Integer userLogin(String account, String password) throws BaseException;

    List<CandidateVO> getCandidateListByUserId(Integer userId);

    void addCandidateWithUserId(Integer userId, String candidateName) throws BaseException;

    BlindDateRecordVO getCandidateBlindRecord(Integer userId, Integer candidateId) throws BaseException;

    void setCandidateBlindRecord(BlindDateRecordVO blindDateRecordVO) throws BaseException;

    List<String> getFocusOnRank(Integer userId, boolean you, int rankingListLength) throws BaseException;

    String getCandidateAnalysisReport(Integer userId, Integer candidateId) throws BaseException;
}
