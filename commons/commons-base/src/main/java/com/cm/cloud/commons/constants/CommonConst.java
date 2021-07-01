package com.cm.cloud.commons.constants;

public interface CommonConst {

    /**
     * 数据权限 请求头(api )
     */
    String DATA_AUTHORITY_HEADER_NAME = "x-data-authorities";

    /**
     * 默认 1
     */
    String DEFAULT_DATA_AUTHORITY = "1";
    Integer INT_ZERO = Integer.valueOf(0);

    Long LONG_ZERO = Long.valueOf(0L);

    String EMPTY_STRING = "";

    /**
     * 通用路径 分页查询
     */
    String PATH_PAGE_SEARCH = "_page_search";

    /**
     * 通用路径 列表查询
     */
    String PATH_SEARCH = "_search";
}

