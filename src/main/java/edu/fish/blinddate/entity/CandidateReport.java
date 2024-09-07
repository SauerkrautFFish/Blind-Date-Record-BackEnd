package edu.fish.blinddate.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Table(name = "candidate_report", indexes = {@Index(name = "candidate_idx",  columnList="candidateId", unique = false)})
@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class CandidateReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "candidate_id", columnDefinition = "int COMMENT '候选人id'",nullable = false)
    private Integer candidateId;

    @Column(name = "status", columnDefinition = "int COMMENT '状态: 1正在生成中 2已生成'",nullable = false)
    private Integer status;

    @Column(name = "report", columnDefinition = "text COMMENT '报告'",nullable = true)
    private String report;

    @Column(name = "times", columnDefinition = "int COMMENT '生成次数'",nullable = false)
    private Integer times;

    @CreatedDate
    @Column(name = "create_time", columnDefinition = "DATETIME COMMENT '创建时间'")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @LastModifiedDate
    @Column(name = "update_time", columnDefinition = "DATETIME COMMENT '更新时间'")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
