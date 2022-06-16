package org.joe.cloud.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.persistence.*;

/**
 * @author Tianze Zhu
 * @since 2022-05-02
 */
@Data
@Entity
@Table
public class ShareFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    @Column
    private Long id;
    @Column(length = 20)
    private Integer userFileId;
    @Column
    private String urlkey;
}
