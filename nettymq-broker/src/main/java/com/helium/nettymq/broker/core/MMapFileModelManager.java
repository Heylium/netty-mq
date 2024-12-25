package com.helium.nettymq.broker.core;

import java.util.HashMap;
import java.util.Map;

public class MMapFileModelManager {

    private Map<String, MMapFileModel> mMapFileModelMap = new HashMap<>();

    public void put(String topic, MMapFileModel mapFileModel) {
        mMapFileModelMap.put(topic, mapFileModel);
    }

    public MMapFileModel get(String topic) {
        return mMapFileModelMap.get(topic);
    }
}
