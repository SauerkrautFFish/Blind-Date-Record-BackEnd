package edu.fish.blinddate.service.impl;

import edu.fish.blinddate.dto.BlindDateRecordDTO;
import edu.fish.blinddate.entity.BlindDateRecord;
import edu.fish.blinddate.entity.Candidate;
import edu.fish.blinddate.entity.OneRecord;
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
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        if (newAccount == null || newPassword == null || userName == null) {
            throw new BaseException(ResponseEnum.MISSING_PARAMS);
        }

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
        if (account == null || password == null) {
            throw new BaseException(ResponseEnum.MISSING_PARAMS);
        }

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
    public List<CandidateVO> getCandidateListByUserId(final Integer userId) {
        Candidate query = new Candidate();
        query.setUserId(userId);
        Example<Candidate> example = Example.of(query);
        List<Candidate> candidateList = candidateRepository.findAll(example);

        List<CandidateVO> candidateVOList = new ArrayList<>();
        // 如果查询不到 candidateList = []
        candidateList.forEach(candidate -> {
            CandidateVO candidateVO = new CandidateVO();
            BeanUtils.copyProperties(candidate, candidateVO);
            candidateVOList.add(candidateVO);
        });

        return candidateVOList;
    }

    @Override
    public void addCandidateWithUserId(final Integer userId, String candidateName) throws BaseException {
        if (candidateName == null) {
            throw new BaseException(ResponseEnum.MISSING_PARAMS);
        }

        Candidate candidate = new Candidate();
        candidate.setUserId(userId);
        candidate.setName(candidateName);
        candidateRepository.save(candidate);
    }

    @Override
    public BlindDateRecordVO getCandidateBlindRecord(final Integer userId, final Integer candidateId) throws BaseException {
        if (candidateId == null) {
            throw new BaseException(ResponseEnum.MISSING_PARAMS);
        }

        BlindDateRecord query = new BlindDateRecord();
        query.setUserId(userId);
        query.setCandidateId(candidateId);
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
    public void setCandidateBlindRecord(BlindDateRecordVO blindDateRecordVO) throws BaseException {
        if (blindDateRecordVO == null) {
            throw new BaseException(ResponseEnum.MISSING_PARAMS);
        }

        if (!CollectionUtils.isEmpty(blindDateRecordVO.getUserRecord())) {
            Collections.sort(blindDateRecordVO.getUserRecord());
        }

        if (!CollectionUtils.isEmpty(blindDateRecordVO.getCandidateRecord())) {
            Collections.sort(blindDateRecordVO.getCandidateRecord());
        }

        BlindDateRecord blindDateRecord = new BlindDateRecord();
        BeanUtils.copyProperties(blindDateRecordVO, blindDateRecord);
        blindDateRecordRepository.save(blindDateRecord);
    }

    private List<BlindDateRecordDTO> getAllCandidateBlindRecord(final Integer userId) throws BaseException {
        if (userId == null) {
            throw new BaseException(ResponseEnum.MISSING_PARAMS);
        }

        BlindDateRecord query = new BlindDateRecord();
        query.setUserId(userId);
        Example<BlindDateRecord> example = Example.of(query);
        List<BlindDateRecord> blindDateRecordList = blindDateRecordRepository.findAll(example);

        List<Integer> candidateIdList = blindDateRecordList.stream().map(BlindDateRecord::getCandidateId).toList();
        List<Candidate> candidateList = candidateRepository.findAllById(candidateIdList);

        Map<Integer, Candidate> candidateIdMapCandidate = candidateList.stream().collect(Collectors.toMap(Candidate::getUserId, Function.identity()));

        List<BlindDateRecordDTO> dateRecordDTOList = new ArrayList<>();

        blindDateRecordList.forEach(blindDateRecord -> {
            BlindDateRecordDTO blindDateRecordDTO = new BlindDateRecordDTO();
            BeanUtils.copyProperties(blindDateRecord, blindDateRecordDTO);
            blindDateRecordDTO.setCandidateName(candidateIdMapCandidate.get(blindDateRecord.getCandidateId()).getName());
            dateRecordDTOList.add(blindDateRecordDTO);
        });


        return dateRecordDTOList;
    }

    @Override
    public List<String> getFocusOnRank(final Integer userId, final boolean you, int rankingListLength) throws BaseException {
        List<BlindDateRecordDTO> blindDateRecordDTO = this.getAllCandidateBlindRecord(userId);
        if (CollectionUtils.isEmpty(blindDateRecordDTO)) {
            return Collections.emptyList();
        }

        List<Pair<String, Integer>> candidateMapScoreList = new ArrayList<>();
        blindDateRecordDTO.forEach( dto -> {
            List<OneRecord> candidateRecord = dto.getCandidateRecord();
            List<OneRecord> userRecord = dto.getUserRecord();
            int cnt = 0;
            BigDecimal successRate = null;
            if (you) {
                // 如果是你在意谁排行榜 主要看你邀请的次数 + 你赴约的百分比
                cnt = this.calculateTryCnt(userRecord);
                successRate = this.calculateSuccessRate(candidateRecord);
                int score = cnt + successRate.multiply(BigDecimal.valueOf(10000)).intValue();
            } else {
                // 如果是谁在意你排行榜 主要看她邀请你的次数 + 赴约你的百分比
                 cnt = this.calculateTryCnt(candidateRecord);
                 successRate = this.calculateSuccessRate(userRecord);
            }

            int score = cnt + successRate.multiply(BigDecimal.valueOf(10000)).intValue();
            candidateMapScoreList.add(Pair.of(dto.getCandidateName(), score));
        });

        candidateMapScoreList.sort(new Comparator<Pair<String, Integer>>() {
            @Override
            public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
                return o2.getSecond() - o1.getSecond();
            }
        });

        return candidateMapScoreList.stream().map(Pair::getFirst).toList().subList(0, rankingListLength);
    }

    private BigDecimal calculateSuccessRate(List<OneRecord> record) {
        int successCnt = record.stream().mapToInt(OneRecord::getSuccessCnt).sum();
        int totalCnt = record.stream().mapToInt(OneRecord::getTotalCnt).sum();

        return  totalCnt > 0 ? BigDecimal.valueOf(successCnt)
                .divide(BigDecimal.valueOf(totalCnt), 4, BigDecimal.ROUND_HALF_UP)
                : BigDecimal.ZERO;
    }

    private int calculateTryCnt(List<OneRecord> record) {
        return record.stream().mapToInt(OneRecord::getTotalCnt).sum();
    }
}
