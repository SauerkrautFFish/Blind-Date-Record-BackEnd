package edu.fish.blinddate.service.impl;

import com.alibaba.fastjson2.util.DateUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import edu.fish.blinddate.dto.BlindDateRecordDTO;
import edu.fish.blinddate.entity.BlindDateRecord;
import edu.fish.blinddate.entity.Candidate;
import edu.fish.blinddate.entity.OneRecord;
import edu.fish.blinddate.entity.User;
import edu.fish.blinddate.enums.ResponseEnum;
import edu.fish.blinddate.enums.Role;
import edu.fish.blinddate.exception.BaseException;
import edu.fish.blinddate.gpt.GeminiProModel;
import edu.fish.blinddate.repository.BlindDateRecordRepository;
import edu.fish.blinddate.repository.CandidateRepository;
import edu.fish.blinddate.repository.UserRepository;
import edu.fish.blinddate.service.MainService;
import edu.fish.blinddate.utils.TemplateUtil;
import edu.fish.blinddate.vo.BlindDateRecordVO;
import edu.fish.blinddate.vo.CandidateVO;
import jakarta.annotation.Resource;
import org.apache.hc.core5.http.HttpHost;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
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


        List<CandidateVO> candidateVOList = Lists.newArrayList();
        // 如果查询不到 candidateList = []
        candidateList.forEach(candidate -> {
            CandidateVO candidateVO = new CandidateVO();
            BeanUtils.copyProperties(candidate, candidateVO);
            candidateVO.setCreateTime(DateUtils.format(candidate.getCreateTime()));
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

        BlindDateRecord blindDateRecord = new BlindDateRecord();
        blindDateRecord.setUserId(userId);
        blindDateRecord.setCandidateId(candidate.getId());
        blindDateRecord.setUserRecord(Lists.newArrayList());
        blindDateRecord.setCandidateRecord(Lists.newArrayList());
        blindDateRecordRepository.save(blindDateRecord);

    }

    @Override
    public BlindDateRecordVO getCandidateBlindRecord(final Integer userId, final Integer candidateId) throws BaseException {
        if (candidateId == null) {
            throw new BaseException(ResponseEnum.MISSING_PARAMS);
        }

        // 获取候选人记录
        BlindDateRecord query = new BlindDateRecord();
        query.setUserId(userId);
        query.setCandidateId(candidateId);
        Example<BlindDateRecord> example = Example.of(query);
        BlindDateRecord blindDateRecord = blindDateRecordRepository.findOne(example).orElse(null);
        BlindDateRecordVO blindDateRecordVO = new BlindDateRecordVO();
        if (blindDateRecord == null) {
            return new BlindDateRecordVO();
        }

        // 获取候选人名字
        Candidate candidateQuery = new Candidate();
        candidateQuery.setId(candidateId);
        Example<Candidate> candidateExample = Example.of(candidateQuery);
        Candidate candidate = candidateRepository.findOne(candidateExample).orElse(null);
        if (candidate == null) {
            throw new BaseException(ResponseEnum.CANDIDATE_DONT_EXISTS);
        }

        BeanUtils.copyProperties(blindDateRecord, blindDateRecordVO);

        Set<String> userDateSet = Sets.newHashSet();
        for (OneRecord one : blindDateRecord.getUserRecord()) {
            userDateSet.add(one.getDate());
        }
        Set<String> candidateDateSet = Sets.newHashSet();
        for (OneRecord one : blindDateRecord.getCandidateRecord()) {
            candidateDateSet.add(one.getDate());
        }

        // x轴-日期
        Set<String> dateSet = Sets.newHashSet(userDateSet);
        dateSet.addAll(candidateDateSet);
        List<String> dateList = Lists.newArrayList(dateSet);
        Collections.sort(dateList);

        // y轴-用户和候选人的记录
        List<BigDecimal> userYAxisData = Lists.newArrayList();
        BigDecimal successCnt = BigDecimal.ZERO;
        BigDecimal totalCnt = BigDecimal.ZERO;
        for (String date : dateList) {
            if(userDateSet.contains(date)) {
               for(OneRecord one : blindDateRecord.getUserRecord())  {
                   if (one.getDate().equals(date)) {
                       successCnt = successCnt.add(BigDecimal.valueOf(one.getSuccessCnt()));
                       totalCnt = totalCnt.add(BigDecimal.valueOf(one.getTotalCnt()));
                       break;
                   }
               }
            }

            if(totalCnt.compareTo(BigDecimal.ZERO) == 0) {
                userYAxisData.add(BigDecimal.ZERO);
            } else {
                userYAxisData.add(successCnt.multiply(BigDecimal.valueOf(100)).divide(totalCnt, 2, RoundingMode.HALF_UP));
            }
        }

        List<BigDecimal> candidateYAxisData = Lists.newArrayList();
        successCnt = BigDecimal.ZERO;
        totalCnt = BigDecimal.ZERO;
        for (String date : dateList) {
            if(candidateDateSet.contains(date)) {
                for(OneRecord one : blindDateRecord.getCandidateRecord())  {
                    if (one.getDate().equals(date)) {
                        successCnt = successCnt.add(BigDecimal.valueOf(one.getSuccessCnt()));
                        totalCnt = totalCnt.add(BigDecimal.valueOf(one.getTotalCnt()));
                        break;
                    }
                }
            }

            if(totalCnt.compareTo(BigDecimal.ZERO) == 0) {
                candidateYAxisData.add(BigDecimal.ZERO);
            } else {
                candidateYAxisData.add(successCnt.multiply(BigDecimal.valueOf(100)).divide(totalCnt, 2, RoundingMode.HALF_UP));
            }
        }

        blindDateRecordVO.setCandidateName(candidate.getName());
        blindDateRecordVO.setDateXAxisData(dateList);
        blindDateRecordVO.setUserYAxisData(userYAxisData);
        blindDateRecordVO.setCandidateYAxisData(candidateYAxisData);
        return blindDateRecordVO;
    }

    @Override
    public void setCandidateBlindRecord(Integer userId, BlindDateRecordVO blindDateRecordVO) throws BaseException {
        if (blindDateRecordVO == null || blindDateRecordVO.getId() == null ||
                blindDateRecordVO.getCandidateId() == null ||
                blindDateRecordVO.getCandidateRecord() == null ||
                blindDateRecordVO.getUserRecord() == null) {
            throw new BaseException(ResponseEnum.MISSING_PARAMS);
        }

        // 不能有相同的日期
        Set<String> dateSet = Sets.newHashSet();
        for (OneRecord one : blindDateRecordVO.getCandidateRecord()) {
            if (dateSet.contains(one.getDate())) {
                throw new BaseException(ResponseEnum.RECORD_DATE_DUPLICATION);
            }
            dateSet.add(one.getDate());
        }

        dateSet.clear();
        for (OneRecord one : blindDateRecordVO.getUserRecord()) {
            if (dateSet.contains(one.getDate())) {
                throw new BaseException(ResponseEnum.RECORD_DATE_DUPLICATION);
            }
            dateSet.add(one.getDate());
        }

        BlindDateRecord query = new BlindDateRecord();
        query.setUserId(userId);
        query.setCandidateId(blindDateRecordVO.getCandidateId());
        Example<BlindDateRecord> example = Example.of(query);

        // 判断下是否是该用户的候选人
        BlindDateRecord blindDateRecord = blindDateRecordRepository.findOne(example).orElse(null);
        if(blindDateRecord == null || blindDateRecord.getId().intValue() != blindDateRecordVO.getId()) {
            throw new BaseException(ResponseEnum.CANDIDATE_DONT_EXISTS);
        }

        Collections.sort(blindDateRecordVO.getUserRecord());
        Collections.sort(blindDateRecordVO.getCandidateRecord());

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

        List<BlindDateRecordDTO> dateRecordDTOList = Lists.newArrayList();

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

        List<Pair<String, Integer>> candidateMapScoreList = Lists.newArrayList();
        blindDateRecordDTO.forEach( dto -> {
            List<OneRecord> candidateRecord = dto.getCandidateRecord();
            List<OneRecord> userRecord = dto.getUserRecord();
            int cnt = 0;
            BigDecimal successRate = null;
            if (you) {
                // 如果是你在意谁排行榜 主要看你邀请的次数 + 你赴约的百分比
                cnt = this.calculateTryCnt(userRecord);
                successRate = this.calculateSuccessRate(candidateRecord);
            } else {
                // 如果是谁在意你排行榜 主要看相亲对象邀请你的次数 + 赴约你的百分比
                 cnt = this.calculateTryCnt(candidateRecord);
                 successRate = this.calculateSuccessRate(userRecord);
            }

            int score = this.calculateScore(cnt, successRate);
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

    private int calculateScore(int cnt, BigDecimal successRate) {
        return cnt + successRate.multiply(BigDecimal.valueOf(10000)).intValue();
    }

    @Override
    public String getCandidateAnalysisReport(Integer userId, Integer candidateId) throws BaseException {
        if (candidateId == null) {
            throw new BaseException(ResponseEnum.MISSING_PARAMS);
        }

        BlindDateRecordVO candidateBlindRecord = this.getCandidateBlindRecord(userId, candidateId);

        List<OneRecord> candidateRecordList = candidateBlindRecord.getCandidateRecord();
        List<OneRecord> userRecordList = candidateBlindRecord.getUserRecord();

        // 尝试约会次数
        int candidateCnt = this.calculateTryCnt(candidateRecordList);
        int userCnt = this.calculateTryCnt(userRecordList);
        // 约会成功率
        BigDecimal candidateSuccessRate = this.calculateSuccessRate(candidateRecordList);
        BigDecimal userSuccessRate = this.calculateSuccessRate(userRecordList);

        StringBuilder candidateStr = new StringBuilder();
        candidateRecordList.forEach(oneRecord -> {
            candidateStr.append(TemplateUtil.singleCandidateAnalysisTemplatePart1(candidateStr, oneRecord, Role.CANDIDATE));
        });

        StringBuilder userStr = new StringBuilder();
        userRecordList.forEach(oneRecord -> {
            userStr.append(TemplateUtil.singleCandidateAnalysisTemplatePart1(userStr, oneRecord, Role.USER));
        });

        TemplateUtil.singleCandidateAnalysisTemplatePart2(candidateStr, candidateCnt, candidateSuccessRate, Role.CANDIDATE);
        TemplateUtil.singleCandidateAnalysisTemplatePart2(candidateStr, userCnt, userSuccessRate, Role.USER);

        String promptAndMSg = TemplateUtil.singleCandidateAnalysisTemplatePart3(userStr, candidateStr);

        // send
        GeminiProModel geminiPro = GeminiProModel.builder().init().proxy(new HttpHost("localhost", 7890));
        String responseText = geminiPro.generateTextOnce(promptAndMSg);

        return responseText;
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
