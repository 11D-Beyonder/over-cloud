package org.joe.cloud.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.joe.cloud.common.StorageLocationEnum;

import javax.persistence.*;

/**
 * @author Tianze Zhu
 * @since 2022-05-02
 */
@Data
@Table
@Entity
public class PhysicalFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    @Column
    private Long id;
    @Column(nullable = false, length = 500)
    private String url;
    @Column(nullable = false)
    private Long size;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StorageLocationEnum storageLocation;
    @Column(length = 40, nullable = false)
    private String identifier;
    @Column(nullable = false)
    private Long quotationCount;
}
