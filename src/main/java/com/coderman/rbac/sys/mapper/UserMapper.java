package com.coderman.rbac.sys.mapper;

import com.coderman.rbac.sys.bean.User;
import com.coderman.rbac.sys.vo.UserVo;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created by zhangyukang on 2019/11/9 20:42
 */

public interface UserMapper extends Mapper<User> {
}
