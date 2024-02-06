package edu.fish.blinddate.service;

import edu.fish.blinddate.exception.BaseException;
import edu.fish.blinddate.vo.BlindDateRecordVO;
import edu.fish.blinddate.vo.CandidateVO;

import java.util.List;

public interface MainService {
    void registerUser(String newAccount, String newPassword, String userName) throws BaseException;

    Integer userLogin(String account, String password) throws BaseException;

    List<CandidateVO> getCandidateListByUserId(Integer userId);

    void addCandidateWithUserId(Integer userId, String candidateName);

    BlindDateRecordVO getCandidateBlindRecord(Integer userId, Integer candidateId);

    void setCandidateBlindRecord(BlindDateRecordVO blindDateRecordVO);
}
