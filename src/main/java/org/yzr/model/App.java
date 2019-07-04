package org.yzr.model;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "tb_app", uniqueConstraints = {@UniqueConstraint(columnNames={"platform", "bundleID"})})
@Setter
@Getter
public class App {
    // 主键
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(length = 32)
    private String id;
    // APP ID
    private String bundleID;
    // 应用名称
    private String name;
    // 短链接码
    @Column(unique = true)
    private String shortCode;
    // 平台
    private String platform;
    // 简介
    private String description;
    // 创建时间
    private long createTime;
    // 包列表
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "app")
    private List<Package> packageList;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // 当前包
    @JoinColumn(name = "currentID",referencedColumnName = "id")
    private  Package currentPackage;

}
