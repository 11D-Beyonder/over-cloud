package org.joe.cloud.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Tianze Zhu
 * @since 2022-05-03
 */
@Data
@ApiModel("统一返回类型")
public class RestResponse<T> {
    @ApiModelProperty("成功标记")
    private Boolean success;
    @ApiModelProperty("描述")
    private String message;
    @ApiModelProperty("对象数据")
    private Object data;

    public static RestResponse success(String message, Object data) {
        RestResponse response = new RestResponse();
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static RestResponse success(String message) {
        RestResponse response = new RestResponse();
        response.setSuccess(true);
        response.setMessage(message);
        return response;
    }

    public static RestResponse success() {
        RestResponse response = new RestResponse();
        response.setSuccess(true);
        return response;
    }

    public static RestResponse failure(String message) {
        RestResponse response = new RestResponse();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }

    public static RestResponse failure() {
        RestResponse response = new RestResponse();
        response.setSuccess(false);
        return response;
    }
}
