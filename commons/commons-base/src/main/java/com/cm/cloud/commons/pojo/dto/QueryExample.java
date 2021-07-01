package com.cm.cloud.commons.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

import java.util.List;
import java.util.Map;

/**
 * 自定义查询
 */
@ApiModel(value = "自定义查询（排序 字段 过滤 分页等）", description = "eg 查询id>1 的所有id 并通过id正序 {\"selectProperties:[\"id\"]\",\"\"}")
@Data
@NoArgsConstructor
public class QueryExample<T> {

    @ApiModelProperty(value = "匹配方式 key(匹配字段) value(匹配方式) 支持参数(>,>=,<,<=,like,=)")
    private Map<String, String> match;

    @ApiModelProperty(value = "排序 key(排序字段) value(排序方式) 支持参数(asc,desc)")
    private Map<String, String> order;

    @ApiModelProperty(value = "查询字段列表 与excludeProperties同时为空时 查询所有字段 建议需要什么字段传什么字段")
    private List<String> selectProperties;

    @ApiModelProperty(value = "排除字段列表 与selectProperties同时为空时 查询所有字段 建议需要什么字段传什么字段")
    private List<String> excludeProperties;

    @ApiModelProperty("分页对象 不传不分页")
    private PageInfo pageInfo;

    @ApiModelProperty("筛选条件")
    private T queryPo;

    @Builder
    public QueryExample(@Singular("match") Map<String, String> match,
                        @Singular("order") Map<String, String> order,
                        @Singular("selectProperties") List<String> selectProperties,
                        @Singular("excludeProperties") List<String> excludeProperties,
                        PageInfo pageInfo, T queryPo) {
        this.match = match;
        this.order = order;
        this.selectProperties = selectProperties;
        this.excludeProperties = excludeProperties;
        this.pageInfo = pageInfo;
        this.queryPo = queryPo;
    }

    @ApiModel("分页对象")
    @Data
    public static class PageInfo {

        @ApiModelProperty(value = "每页返回多少行", example = "10")
        private Integer pageSize = 10;

        @ApiModelProperty(value = "第几页", example = "1")
        private Integer pageNum = 1;

        @ApiModelProperty(value = "是否需要统计有多少行（客户端如果知道传false）", example = "true")
        private Boolean count = true;

        @ApiModelProperty(value = "总共多少行(已知total 并且count=fasle)", hidden = true)
        private Long total;
    }


}
