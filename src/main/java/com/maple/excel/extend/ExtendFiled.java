package com.maple.excel.extend;

import java.util.Collections;
import java.util.Map;

/**
 * @author gaojinfeng
 * @date 2023/3/20
 * @description
 */
public interface ExtendFiled {
    default Map<String, Object> getExtendFiledMap() {
        return Collections.emptyMap();
    }

    default void setExtendFiledMap(Map<String, Object> extendFiledMap) {
    }
}
