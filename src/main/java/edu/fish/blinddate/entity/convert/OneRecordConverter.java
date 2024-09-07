package edu.fish.blinddate.entity.convert;

import com.alibaba.fastjson2.JSONArray;
import edu.fish.blinddate.entity.OneRecord;
import jakarta.persistence.AttributeConverter;

import java.math.BigDecimal;
import java.util.List;

public class OneRecordConverter implements AttributeConverter<List<OneRecord>, String> {
    @Override
    public String convertToDatabaseColumn(List<OneRecord> oneRecords) {
        return JSONArray.toJSONString(oneRecords);
    }

    @Override
    public List<OneRecord> convertToEntityAttribute(String s) {
        if (s == null) return null;

        return JSONArray.parseArray(s, OneRecord.class);
    }

    public static BigDecimal calculateSuccessRate(List<OneRecord> record) {
        int successCnt = record.stream().mapToInt(OneRecord::getSuccessCnt).sum();
        int totalCnt = record.stream().mapToInt(OneRecord::getTotalCnt).sum();

        return  totalCnt > 0 ? BigDecimal.valueOf(successCnt)
                .divide(BigDecimal.valueOf(totalCnt), 4, BigDecimal.ROUND_HALF_UP)
                : BigDecimal.ZERO;
    }

    public static int calculateTryCnt(List<OneRecord> record) {
        return record.stream().mapToInt(OneRecord::getTotalCnt).sum();
    }
}
