package edu.fish.blinddate.vo;

import lombok.Data;

@Data
public class ShareMomentVO {
    private Integer userId;
    private String userName;
    private Integer candidateId;
    private String candidateName;
}
