package org.yzr.model;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_app", uniqueConstraints = {@UniqueConstraint(columnNames = {"platform", "bundleID", "userId"})})
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
    // webHook列表
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "app")
    private List<WebHook> webHookList;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // 当前包
    @JoinColumn(name = "currentID", referencedColumnName = "id")
    private Package currentPackage;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User owner;

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

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public List<Package> getPackageList() {
        if (packageList == null) {
            this.packageList = new ArrayList<>();
        }
        return packageList;
    }

    public void setPackageList(List<Package> packageList) {
        this.packageList = packageList;
    }

    public List<WebHook> getWebHookList() {
        return webHookList;
    }

    public void setWebHookList(List<WebHook> webHookList) {
        this.webHookList = webHookList;
    }

    public Package getCurrentPackage() {
        return currentPackage;
    }

    public void setCurrentPackage(Package currentPackage) {
        this.currentPackage = currentPackage;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
