package com.coderman.rent.sys.exception;

import com.coderman.rent.sys.enums.ResultEnum;
import lombok.Data;

/**
 * Created by zhangyukang on 2019/11/10 10:21
 */
@Data
public class SysException extends RuntimeException {
    private Integer code;
    public SysException() {

    }
    public SysException(ResultEnum resultEnum) {
        super(resultEnum.getMsg());
        this.code=resultEnum.getCode();
    }
}
