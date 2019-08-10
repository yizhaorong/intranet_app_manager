package org.yzr.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name="tb_provision")
@Setter
@Getter
public class Provision {
    // 主键
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(length = 32)
    private String id;
    private String teamName;
    private String teamID;
    private Date createDate;
    private Date expirationDate;
    private String UUID;
    @Column(length = 80000)
    private String[] devices;
    private int deviceCount;
    private String type;
    private boolean isEnterprise;
}
