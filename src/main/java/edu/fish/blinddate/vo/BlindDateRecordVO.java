package edu.fish.blinddate.vo;

import edu.fish.blinddate.entity.OneRecord;
import lombok.Data;

import java.util.List;

@Data
public class BlindDateRecordVO {
    private Integer id;
    private Integer userId;
    private Integer candidateId;
    private List<OneRecord> userRecord;
    private List<OneRecord> candidateRecord;
}
