package edu.fish.blinddate.dto;

import edu.fish.blinddate.entity.OneRecord;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class BlindDateRecordDTO {
    private Integer id;

    private Integer candidateId;

    private String candidateName;

    private Integer userId;

    private List<OneRecord> userRecord;

    private List<OneRecord> candidateRecord;

    private Date createTime;

    private Date updateTime;
}
