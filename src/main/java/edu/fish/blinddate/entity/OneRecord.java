package edu.fish.blinddate.entity;

import lombok.Data;

@Data
public class OneRecord {
    private String date;
    private boolean success;
    private String explanation;
}
