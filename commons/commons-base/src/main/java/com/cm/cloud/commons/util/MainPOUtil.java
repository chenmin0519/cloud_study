package com.cm.cloud.commons.util;


import com.cm.cloud.commons.pojo.po.MainPO;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Create By chenmin on 2017/12/21 17:36
 */
public class MainPOUtil {

    public static void initAdd(MainPO po, Long userId) {

        if (Objects.isNull(po.getCreateTime()))
            po.setCreateTime(LocalDateTime.now());
        if (Objects.isNull(po.getCreator()))
            po.setCreator(userId);
        if (Objects.isNull(po.getEditTime()))
            po.setEditTime(LocalDateTime.now());
        if (Objects.isNull(po.getEditor()))
            po.setEditor(userId);
    }

    public static void initUpdate(MainPO po, Long userId) {
        if (Objects.isNull(po.getEditTime()))
            po.setEditTime(LocalDateTime.now());
        if (Objects.isNull(po.getEditor()))
            po.setEditor(userId);
    }
}
