package edu.fish.blinddate.entity;

import edu.fish.blinddate.entity.convert.OneRecordConverter;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Table(name = "blind_date_record", indexes = {@Index(name = "candidate_id_idx",  columnList="candidate_id", unique = true)})
@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class BlindDateRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "candidate_id", columnDefinition = "int COMMENT '相亲对象'",nullable = false)
    private Integer candidateId;

    @Column(name = "user_id", columnDefinition = "int COMMENT '用户id'",nullable = false)
    private Integer userId;

    @Column(name = "user_record", columnDefinition = "text COMMENT '用户记录'")
    @Convert(converter = OneRecordConverter.class)
    private List<OneRecord> userRecord;

    @Column(name = "candidate_record", columnDefinition = "text COMMENT '相亲对象记录'")
    @Convert(converter = OneRecordConverter.class)
    private List<OneRecord> candidateRecord;

    @CreatedDate
    @Column(name = "create_time", columnDefinition = "DATETIME COMMENT '创建时间'")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @LastModifiedDate
    @Column(name = "update_time", columnDefinition = "DATETIME COMMENT '更新时间'")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
