package org.yzr.utils.ipa;

import com.dd.plist.NSDate;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Date;
import java.util.List;

@Getter
public class Provision {
    private String teamName;
    private String teamID;
    private Date createDate;
    private Date expirationDate;
    private String UUID;
    private List<String> devices;
    private int deviceCount;
    private String type;

    public Provision(String appPath) {
        String profile = appPath + File.separator + "embedded.mobileprovision";
        try {
            boolean started = false;
            boolean ended = false;
            BufferedReader reader = new BufferedReader(new FileReader(profile));
            StringBuffer plist = new StringBuffer();
            String str = null;
            while ((str = reader.readLine()) != null) {
                if (str.contains("</plist>")) {
                    ended = true;
                    plist.append("</plist>").append("\n");
                } else if (started && !ended) {
                    plist.append(str).append("\n");
                } else  if (str.contains("<?xml")) {
                    started = true;
                    plist.append(str.substring(str.indexOf("<?xml"))).append("\n");
                }
            }
            reader.close();
            Plist provisionFile = Plist.parseWithString(plist.toString());
            this.devices = provisionFile.arrayValueForPath("ProvisionedDevices");
            this.deviceCount = this.devices.size();
            this.teamName = provisionFile.stringValueForPath("TeamName");
            this.teamID = provisionFile.arrayValueForPath("TeamIdentifier").get(0);
            this.createDate = ((NSDate)provisionFile.valueForKeyPath("CreationDate")).getDate();
            this.expirationDate = ((NSDate)provisionFile.valueForKeyPath("ExpirationDate")).getDate();
            this.UUID = provisionFile.stringValueForPath("UUID");
            this.type = this.deviceCount > 0 ? "Ad-hoc" : "Release";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
