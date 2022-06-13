package org.joe.cloud.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.persistence.*;

/**
 * @author Tianze Zhu
 * @since 2022-05-10
 */
@Data
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "only_once", columnNames = {"userFileId"})
})
@Entity
public class DeletedFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    @Column
    private Long id;
    @Column
    private Long userFileId;
    @Column
    private String deleteTime;
}
