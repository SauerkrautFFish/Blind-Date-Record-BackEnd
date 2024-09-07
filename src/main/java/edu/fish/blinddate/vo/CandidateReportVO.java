package edu.fish.blinddate.vo;

import lombok.Data;

import java.util.Date;
@Data
public class CandidateReportVO {
    private Integer id;
    private Integer candidateId;
    private Integer status;
    private String report;
    private Integer times;
    private Date updateTime;
}
