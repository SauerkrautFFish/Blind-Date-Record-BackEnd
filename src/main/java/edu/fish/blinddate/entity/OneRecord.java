package edu.fish.blinddate.entity;

import lombok.Data;

@Data
public class OneRecord implements Comparable<OneRecord> {
    private String date;
    private Integer totalCnt;
    private Integer successCnt;
    private String explanation;

    @Override
    public int compareTo(OneRecord o) {
        return this.getDate().compareTo(o.getDate());
    }
}
