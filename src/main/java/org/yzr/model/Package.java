package org.yzr.model;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "tb_package")
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
    // 源文件
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="source_file_id",referencedColumnName="id")
    private Storage sourceFile;
    // 图标
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="icon_file_id",referencedColumnName="id")
    private Storage iconFile;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "appId")
    private App app;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // Provision 文件
    @JoinColumn(name = "provisionId", referencedColumnName = "id")
    private Provision provision;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBundleID() {
        return bundleID;
    }

    public void setBundleID(String bundleID) {
        this.bundleID = bundleID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getBuildVersion() {
        return buildVersion;
    }

    public void setBuildVersion(String buildVersion) {
        this.buildVersion = buildVersion;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getMinVersion() {
        return minVersion;
    }

    public void setMinVersion(String minVersion) {
        this.minVersion = minVersion;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Storage getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(Storage sourceFile) {
        this.sourceFile = sourceFile;
    }

    public Storage getIconFile() {
        return iconFile;
    }

    public void setIconFile(Storage iconFile) {
        this.iconFile = iconFile;
    }

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public Provision getProvision() {
        return provision;
    }

    public void setProvision(Provision provision) {
        this.provision = provision;
    }
}
