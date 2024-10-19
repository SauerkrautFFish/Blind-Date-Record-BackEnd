package edu.fish.blinddate.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Table(name = "candidate", indexes = {@Index(name = "user_idx",  columnList="user_id", unique = false)})
@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "user_id", columnDefinition = "int COMMENT '用户id'",nullable = false)
    private Integer userId;

    @Column(name = "name", columnDefinition = "varchar(64) COMMENT '相亲对象名字'",nullable = false)
    private String name;

    @Column(name = "status", columnDefinition = "int DEFAULT 0 COMMENT '是否公开 默认0不公开 1公开'",nullable = false)
    private Integer status;

    @CreatedDate
    @Column(name = "create_time", columnDefinition = "DATETIME COMMENT '创建时间'")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @LastModifiedDate
    @Column(name = "update_time", columnDefinition = "DATETIME COMMENT '更新时间'")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
