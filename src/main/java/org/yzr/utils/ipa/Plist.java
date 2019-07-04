package org.yzr.utils.ipa;

import com.dd.plist.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Plist {
    private NSDictionary dictionary;

    public Plist(NSDictionary dictionary) {
        this.dictionary = dictionary;
    }

    public static Plist parseWithFile(File file) {
        try {
            NSDictionary dictionary = (NSDictionary)PropertyListParser.parse(file);
            return new Plist(dictionary);
        }catch (Exception e){}
        return null;
    }

    public static Plist parseWithString(String plist) {
        try {
            NSDictionary dictionary = (NSDictionary)PropertyListParser.parse(plist.getBytes());
            return new Plist(dictionary);
        }catch (Exception e){}
        return null;
    }

    // 通过 keyPath 获取值
    public NSObject valueForKeyPath(String keyPath) {
        String[] values = keyPath.split("\\.");
        try {
            if (values.length > 0) {
                int i = 0;
                NSObject value = null;
                NSDictionary dictionary = this.dictionary;
                while (i < values.length) {
                    value = dictionary.objectForKey(values[i]);
                    if (value instanceof NSDictionary) {
                        dictionary = (NSDictionary)value;
                    }
                    i++;
                }
                return value;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String stringValueForKeyPath(String keyPath) {
        Object object = valueForKeyPath(keyPath);
        if (object != null) {
            return object.toString();
        }
        return null;
    }

    public NSObject valueForPath(String path) {
        try {
            return this.dictionary.objectForKey(path);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String stringValueForPath(String keyPath) {
        Object object = valueForKeyPath(keyPath);
        if (object != null) {
            return object.toString();
        }
        return null;
    }

    public List<String> arrayValueForPath(String path) {
        List<String> devices = new ArrayList<>();
        Object object = valueForKeyPath(path);
        if (object != null) {
            NSArray deviceArray = (NSArray)object;
            if (deviceArray != null && deviceArray.count() > 0) {
                for (int i = 0; i < deviceArray.count(); i++) {
                    devices.add(deviceArray.objectAtIndex(i).toString());
                }
            }
        }
        return devices;
    }

    public boolean boolValueForPath(String keyPath) {
        Object object = valueForKeyPath(keyPath);
        if (object instanceof NSNumber) {
            NSNumber number = (NSNumber)object;
            return number.boolValue();
        }
        return false;
    }
}

