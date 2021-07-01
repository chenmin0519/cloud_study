package com.cm.cloud.commons.pojo.dto;

import com.cm.cloud.commons.pojo.po.BasePO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@ApiModel("节点状态")
@Table(name = "node_status")
@Data
public class NodeStatus extends BasePO {

    @Id
    @OrderBy("desc")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ApiModelProperty(value = "ip地址")
    private String ip;

    @ApiModelProperty(value = "最后成功时间")
    private java.time.LocalDateTime lastSuccess;

    @ApiModelProperty(value = "服务名")
    private String serviceId;

    @ApiModelProperty(value = "")
    private Integer state;

    @ApiModelProperty(value = "服务类型")
    private String environment;

}
