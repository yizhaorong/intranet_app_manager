package org.yzr.model;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "tb_web_hook")
@Setter
@Getter
public class WebHook {
    // 钉钉
    public static final String WEB_HOOK_TYPE_DING_DING="DingDing";

    // 主键
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(length = 32)
    private String id;

    private String name;

    private String url;

    private String type;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name="appId")
    private App app;
}
