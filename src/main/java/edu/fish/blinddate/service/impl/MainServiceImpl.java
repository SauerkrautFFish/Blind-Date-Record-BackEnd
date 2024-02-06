package edu.fish.blinddate.service.impl;

import edu.fish.blinddate.entity.BlindDateRecord;
import edu.fish.blinddate.entity.Candidate;
import edu.fish.blinddate.entity.User;
import edu.fish.blinddate.enums.ResponseEnum;
import edu.fish.blinddate.exception.BaseException;
import edu.fish.blinddate.repository.BlindDateRecordRepository;
import edu.fish.blinddate.repository.CandidateRepository;
import edu.fish.blinddate.repository.UserRepository;
import edu.fish.blinddate.service.MainService;
import edu.fish.blinddate.vo.BlindDateRecordVO;
import edu.fish.blinddate.vo.CandidateVO;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class MainServiceImpl implements MainService {

    @Resource
    UserRepository userRepository;
    @Resource
    CandidateRepository candidateRepository;
    @Resource
    BlindDateRecordRepository blindDateRecordRepository;

    @Override
    public void registerUser(String newAccount, String newPassword, String userName) throws BaseException {
        User user = new User();
        user.setAccount(newAccount);
        user.setPassword(newPassword);
        user.setUserName(userName);

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new BaseException(ResponseEnum.ACCOUNT_EXISTS);
        }
    }

    @Override
    public Integer userLogin(String account, String password) throws BaseException {
        User query = new User();
        query.setAccount(account);
        query.setPassword(password);
        Example<User> example = Example.of(query);
        User user = userRepository.findOne(example).orElse(null);

        if (user == null) {
            throw new BaseException(ResponseEnum.ACCOUNT_NOT_EXISTS_OR_PASSWORD_ERR);
        }

        return user.getId();
    }

    @Override
    public List<CandidateVO> getCandidateListByUserId(Integer userId) {
        Candidate query = new Candidate();
        query.setUserId(userId);
        Example<Candidate> example = Example.of(query);
        List<Candidate> candidateList = candidateRepository.findAll(example);

        List<CandidateVO> candidateVOList = Collections.emptyList();
        // 如果查询不到 candidateList = []
        candidateList.forEach(candidate -> {
            CandidateVO candidateVO = new CandidateVO();
            BeanUtils.copyProperties(candidate, candidateVO);
            candidateVOList.add(candidateVO);
        });

        return candidateVOList;
    }

    @Override
    public void addCandidateWithUserId(Integer userId, String candidateName) {
        Candidate candidate = new Candidate();
        candidate.setUserId(userId);
        candidate.setName(candidateName);
        candidateRepository.save(candidate);
    }

    @Override
    public BlindDateRecordVO getCandidateBlindRecord(Integer userId, Integer candidateId) {
        BlindDateRecord query = new BlindDateRecord();
        Example<BlindDateRecord> example = Example.of(query);
        BlindDateRecord blindDateRecord = blindDateRecordRepository.findOne(example).orElse(null);
        BlindDateRecordVO blindDateRecordVO = new BlindDateRecordVO();
        if (blindDateRecord == null) {
            return null;
        }
        BeanUtils.copyProperties(blindDateRecord, blindDateRecordVO);

        return blindDateRecordVO;
    }

    @Override
    public void setCandidateBlindRecord(BlindDateRecordVO blindDateRecordVO) {
        BlindDateRecord blindDateRecord = new BlindDateRecord();
        BeanUtils.copyProperties(blindDateRecordVO, blindDateRecord);
        blindDateRecordRepository.save(blindDateRecord);
    }
}
