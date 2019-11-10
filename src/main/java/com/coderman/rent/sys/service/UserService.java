package com.coderman.rent.sys.service;

import com.coderman.rent.sys.bean.User;
import com.coderman.rent.sys.vo.PageVo;
import com.coderman.rent.sys.vo.UserVo;

/**
 * Created by zhangyukang on 2019/11/10 10:44
 */
public interface UserService {

    /**
     * 根据用户名查询用户
     * @param userName
     * @return
     */
    User findUserByName(String userName);


    /**
     * 查询用户
     * @param userVo
     * @return
     */
    PageVo<User> findPage(UserVo userVo);

    /**
     * 用户更新
     * @param userVo
     */
    void update(UserVo userVo);

    /**
     * 添加用户信息
     * @param userVo
     */
    void add(UserVo userVo);

    /**
     * 删除用户
     * @param userVo
     */
    void delete(UserVo userVo);
}