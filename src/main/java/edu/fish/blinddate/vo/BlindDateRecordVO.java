package edu.fish.blinddate.vo;

import edu.fish.blinddate.entity.OneRecord;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BlindDateRecordVO {
    private Integer id;
    private Integer candidateId;
    private String candidateName;
    private List<OneRecord> userRecord;
    private List<OneRecord> candidateRecord;
    private List<String> dateXAxisData;
    private List<BigDecimal> userYAxisData;
    private List<BigDecimal> candidateYAxisData;
}
