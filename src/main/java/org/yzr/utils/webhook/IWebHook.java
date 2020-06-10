package org.yzr.utils.webhook;

import org.yzr.model.App;
import org.yzr.storage.StorageUtil;

public interface IWebHook {
    void sendMessage(App app, String baseURL, StorageUtil storageUtil);
}
