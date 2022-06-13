package org.joe.cloud.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.persistence.*;

/**
 * @author Tianze Zhu
 * @since 2022-05-06
 */
@Data
@Entity
@Table
public class UserFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    @TableId(type = IdType.AUTO)
    private Long id;
    @Column
    private Long physicalFileId;
    @Column(length = 100)
    private String name;
    @Column(length = 1000)
    private String path;
    @Column
    private Boolean isFolder;
    @Column(length = 21)
    private String updateTime;
    @Column
    private Boolean deleted;
    @Column
    private String extension;
}
