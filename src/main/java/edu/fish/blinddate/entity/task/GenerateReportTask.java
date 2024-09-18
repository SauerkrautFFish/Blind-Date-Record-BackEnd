package edu.fish.blinddate.entity.task;

import edu.fish.blinddate.entity.CandidateReport;
import edu.fish.blinddate.entity.OneRecord;
import edu.fish.blinddate.entity.convert.OneRecordConverter;
import edu.fish.blinddate.enums.Role;
import edu.fish.blinddate.exception.BaseException;
import edu.fish.blinddate.gpt.HunYuanModel;
import edu.fish.blinddate.repository.CandidateReportRepository;
import edu.fish.blinddate.service.MainService;
import edu.fish.blinddate.utils.TemplateUtil;
import edu.fish.blinddate.vo.BlindDateRecordVO;
import org.springframework.data.domain.Example;

import java.math.BigDecimal;
import java.util.List;

public class GenerateReportTask implements Runnable {
    private int userId;

    private int candidateId;

    private MainService mainService;

    private CandidateReportRepository candidateReportRepository;

    private HunYuanModel hunYuanModel;

    public GenerateReportTask(MainService mainService, CandidateReportRepository candidateReportRepository,
                              int userId, int candidateId, HunYuanModel hunYuanModel) {
        this.mainService = mainService;
        this.candidateReportRepository = candidateReportRepository;
        this.userId = userId;
        this.candidateId = candidateId;
        this.hunYuanModel = hunYuanModel;
    }

    @Override
    public void run() {
        BlindDateRecordVO candidateBlindRecord = null;
        try {
            candidateBlindRecord = mainService.getCandidateBlindRecord(userId, candidateId);
        } catch (BaseException e) {
            throw new RuntimeException(e);
        }

        List<OneRecord> candidateRecordList = candidateBlindRecord.getCandidateRecord();
        List<OneRecord> userRecordList = candidateBlindRecord.getUserRecord();

        // 尝试约会次数
        int candidateCnt = 0, userCnt = 0;
        // 约会成功率
        BigDecimal candidateSuccessRate = BigDecimal.ZERO, userSuccessRate = BigDecimal.ZERO;
        // text
        StringBuilder candidateStr = new StringBuilder(), userStr = new StringBuilder();
        if (candidateRecordList != null && candidateRecordList.size() != 0) {
            candidateCnt = OneRecordConverter.calculateTryCnt(candidateRecordList);
            candidateSuccessRate = OneRecordConverter.calculateSuccessRate(candidateRecordList);
            candidateRecordList.forEach(oneRecord -> {
                candidateStr.append(TemplateUtil.singleCandidateAnalysisTemplatePart1(candidateStr, oneRecord, Role.CANDIDATE));
            });
        }

        if (userRecordList != null && userRecordList.size() != 0) {
            userCnt = OneRecordConverter.calculateTryCnt(userRecordList);
            userSuccessRate = OneRecordConverter.calculateSuccessRate(userRecordList);
            userRecordList.forEach(oneRecord -> {
                userStr.append(TemplateUtil.singleCandidateAnalysisTemplatePart1(userStr, oneRecord, Role.USER));
            });
        }

        TemplateUtil.singleCandidateAnalysisTemplatePart2(candidateStr, candidateCnt, candidateSuccessRate, Role.CANDIDATE);
        TemplateUtil.singleCandidateAnalysisTemplatePart2(userStr, userCnt, userSuccessRate, Role.USER);

        String promptAndMSg = TemplateUtil.singleCandidateAnalysisTemplatePart3(userStr, candidateStr);

        // send
        // GeminiProModel geminiPro = GeminiProModel.builder().init().proxy(new HttpHost("localhost", 7890));
        // String responseText = geminiPro.generateTextOnce(promptAndMSg);

        String responseText = hunYuanModel.generateTextOnce(promptAndMSg);

        // 报告表
        CandidateReport query = new CandidateReport();
        query.setCandidateId(candidateId);
        Example<CandidateReport> example = Example.of(query);
        CandidateReport candidateReport = candidateReportRepository.findOne(example).orElse(null);

        if (candidateReport == null) {
            return;
        }

        candidateReport.setTimes(candidateReport.getTimes() + 1);
        candidateReport.setReport(responseText);
        candidateReport.setStatus(2);
        candidateReportRepository.save(candidateReport);

    }
}
