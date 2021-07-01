package com.cm.cloud.commons.pojo.dto;

import com.cm.cloud.commons.constants.CommonConst;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 */
@ApiModel("分页查询对象")
@Data
public class PageQuery<T> implements Serializable {

    @ApiModelProperty(value = "每页返回多少行", example = "10")
    private Integer pageSize = 10;

    @ApiModelProperty(value = "第几页", example = "1")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "是否需要统计有多少行（客户端如果知道传false）", example = "true")
    private Boolean count = true;

    @ApiModelProperty("查询对象")
    private T queryPo;
//
//    @ApiModelProperty(value = "范围查询", example = "{\"id\":\"=\"}")
//    private Map<String, String[]> between;

    @ApiModelProperty(value = "总共多少行(已知total 并且count=fasle)", hidden = true)
    private Long total;

    public boolean needCount(Long value) {
        return Objects.nonNull(value) && CommonConst.LONG_ZERO.compareTo(value) == 1;
    }
}
