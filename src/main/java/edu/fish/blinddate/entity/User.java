package edu.fish.blinddate.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Table(name = "user", indexes = {@Index(name = "account_idx",  columnList="account", unique = true)})
@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "account", columnDefinition = "varchar(32) COMMENT '账号'",nullable = false)
    private String account;

    @Column(name = "username", columnDefinition = "varchar(64) COMMENT '用户名'",nullable = false)
    private String userName;

    @Column(name = "password", columnDefinition = "varchar(255) COMMENT '密码'",nullable = false)
    private String password;

    @CreatedDate
    @Column(name = "create_time", columnDefinition = "DATETIME COMMENT '创建时间'")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @LastModifiedDate
    @Column(name = "update_time", columnDefinition = "DATETIME COMMENT '更新时间'")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
