package edu.fish.blinddate.entity.convert;

import com.alibaba.fastjson2.JSONArray;
import edu.fish.blinddate.entity.OneRecord;
import jakarta.persistence.AttributeConverter;

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
}
