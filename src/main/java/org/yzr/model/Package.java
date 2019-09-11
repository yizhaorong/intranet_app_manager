package org.yzr.model;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name="tb_package")
@Setter
@Getter
public class Package {
    // 主键
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(length = 32)
    private String id;
    // 应用ID
    private String bundleID;
    // 名称
    private String name;
    // 版本
    private String version;
    // 构建版本
    private String buildVersion;
    // 创建时间
    private long createTime;
    // 包大小
    private long size;
    // 最低支持版本
    private String minVersion;
    // 平台(Android 或 iOS)
    private String platform;
    // 扩展消息 (json格式)
    private String extra;
    // 文件名
    private String fileName;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name="appId")
    private App app;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // Provision 文件
    @JoinColumn(name = "provisionId",referencedColumnName = "id")
    private  Provision provision;

}
