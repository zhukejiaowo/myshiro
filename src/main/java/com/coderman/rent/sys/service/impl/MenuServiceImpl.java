package com.coderman.rent.sys.service.impl;

import com.coderman.rent.sys.bean.DTreeJson;
import com.coderman.rent.sys.bean.Menu;
import com.coderman.rent.sys.bean.RoleMenu;
import com.coderman.rent.sys.contast.MyConstant;
import com.coderman.rent.sys.mapper.MenuMapper;
import com.coderman.rent.sys.mapper.RoleMapper;
import com.coderman.rent.sys.mapper.RoleMenuMapper;
import com.coderman.rent.sys.service.MenuService;
import com.coderman.rent.sys.vo.MenuVo;
import com.coderman.rent.sys.vo.PageVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * Created by zhangyukang on 2019/11/10 11:54
 */
@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Autowired
    private MenuMapper menuMapper;

    @Override
    public List<Menu> loadAllMenu() {
        Example example=new Example(Menu.class);
        example.setOrderByClause("order_num asc");
        example.createCriteria().andEqualTo("available", MyConstant.AVAILABLE)
                                .andEqualTo("type",MyConstant.MENU_TYPE);
        List<Menu> menus = menuMapper.selectByExample(example);
        return menus;
    }

    @Override
    public List<Menu> loadAllMenuByUserId(Long userId) {
        return null;
    }

    @Override
    public List<DTreeJson> loadAllMenuJSON() {
        Example o = new Example(Menu.class);
        o.setOrderByClause("order_num asc");
        o.createCriteria().andEqualTo("available",MyConstant.AVAILABLE);
        List<Menu> menus = menuMapper.selectByExample(o);
        List<DTreeJson> list=new ArrayList<>();
        if(!CollectionUtils.isEmpty(menus)){
            for (Menu menu : menus) {
                Boolean spread=menu.getIsOpen()==MyConstant.MENU_OPEN? true:false;
                list.add(new DTreeJson(menu.getId(),menu.getMenuName(),spread,menu.getParentId(),"0"));
            }
        }
        return list;
    }

    @Override
    public List<DTreeJson> loadAllMenuByRoleId(Long id) {
        List<DTreeJson> list=new ArrayList<>();
        //查询该角色的所有的菜单Id
        Example o = new Example(RoleMenu.class);
        o.createCriteria().andEqualTo("roleId",id);
        List<RoleMenu> roleMenus = roleMenuMapper.selectByExample(o);
        Set<Long> mIdList=new HashSet<>();
        //根据菜单查出菜单
        if(!CollectionUtils.isEmpty(roleMenus)){
            for (RoleMenu roleMenu : roleMenus) {
                mIdList.add(roleMenu.getMenuId());
            }
        }
        //查询所有可用菜单
        Example o1 = new Example(Menu.class);
        o1.createCriteria().andEqualTo("available",MyConstant.AVAILABLE);
        List<Menu> allMenus = menuMapper.selectByExample(o1);
        //封装返回的JSON
        for (Menu menu : allMenus) {
            String checkArr="0";
            if(mIdList.contains(menu.getId())){
                checkArr="1";
            }
            Boolean spread=menu.getIsOpen()==MyConstant.MENU_OPEN? true:false;
            list.add(new DTreeJson(menu.getId(),menu.getMenuName(),spread,menu.getParentId(),checkArr));
        }
        return list;
    }

    @Override
    public PageVo<Menu> findMenuPage(MenuVo menuVo) {
        PageHelper.startPage(menuVo.getPage(),menuVo.getLimit());
        Example o = new Example(Menu.class);
        Example.Criteria criteria = o.createCriteria();
        if(menuVo!=null){
            if(menuVo.getMenuName()!=null&&!"".equals(menuVo.getMenuName())){
                criteria.andLike("menuName","%"+menuVo.getMenuName()+"%");
            }
            if(menuVo.getUrl()!=null&&!"".equals(menuVo.getUrl())){
                criteria.andLike("url","%"+menuVo.getUrl()+"%");
            }
            if(menuVo.getId()!=null&&!"".equals(menuVo.getId())){
                criteria.andEqualTo("id",menuVo.getId());
            }
        }
        List<Menu> menus = menuMapper.selectByExample(o);
        PageInfo<Menu> info=new PageInfo<>(menus);
        return new PageVo<>(info.getTotal(),info.getList());
    }

    @Override
    public void update(MenuVo menuVo) {
        Menu t = new Menu();
        BeanUtils.copyProperties(menuVo,t);
        t.setModifiedTime(new Date());
        menuMapper.updateByPrimaryKeySelective(t);
    }

    @Override
    public void add(MenuVo menuVo) {
        Menu t = new Menu();
        BeanUtils.copyProperties(menuVo,t);
        t.setCreateTime(new Date());
        t.setModifiedTime(new Date());
        if(t.getParentId()==null){
            t.setParentId(0L);
        }
        menuMapper.insertSelective(t);
    }

    @Override
    public void delete(MenuVo menuVo) {
        menuMapper.deleteByPrimaryKey(menuVo.getId());
    }
}
