package org.yzr.vo;

import lombok.Data;

@Data
public class WebHookViewModel {

    private String appId;

    private String name;

    private String url;

    private String type;

}
