package edu.fish.blinddate.vo;

import lombok.Data;
@Data
public class CandidateReportVO {
    private Integer id;
    private Integer candidateId;
    private String candidateName;
    private Integer status;
    private String report;
    private Integer times;
    private String updateTime;
}
