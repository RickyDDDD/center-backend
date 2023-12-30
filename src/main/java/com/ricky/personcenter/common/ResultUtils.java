package com.ricky.personcenter.common;

/**
 * 返回工具类
 *
 * @author Ricky
 * @date 2023/12/25
 */
public class ResultUtils {
    /**
     * 成功
     *
     * @param data 数据
     * @return {@link BaseResponse}<{@link T}>
     */
    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<>(0,data,"ok");
    }

    /**
     * 错误
     *
     * @param errorCode 错误代码
     * @return {@link BaseResponse}
     */
    public static BaseResponse error(ErrorCode errorCode){
        return new BaseResponse<>(errorCode);
    }

    /**
     * 错误
     *
     * @param code        法典
     * @param message     消息
     * @param description 描述
     * @return {@link BaseResponse}
     */
    public static BaseResponse error(int code, String message, String description){
        return new BaseResponse(code,null,message,description);
    }

    /**
     * 错误
     *
     * @param errorCode   错误代码
     * @param message     消息
     * @param description 描述
     * @return {@link BaseResponse}
     */
    public static BaseResponse error(ErrorCode errorCode, String message, String description){
        return new BaseResponse(errorCode.getCode(),null,message,description);
    }

    /**
     * 错误
     *
     * @param errorCode   错误代码
     * @param description 描述
     * @return {@link BaseResponse}
     */
    public static BaseResponse error(ErrorCode errorCode,String description){
        return new BaseResponse<>(errorCode.getCode(),errorCode.getMessage(),description);
    }

}
