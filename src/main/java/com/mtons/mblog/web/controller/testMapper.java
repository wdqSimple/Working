package com.mtons.mblog.web.controller;

import com.mtons.mblog.MyMapper.SecurityCodeMapper;
import com.mtons.mblog.MyMapper.UserMapper;
import com.mtons.mblog.modules.entity.SecurityCode;
import com.mtons.mblog.modules.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import java.util.Iterator;
import java.util.List;

/**
 * Created by admin on 2019/4/17.
 */
@RestController
public class testMapper {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SecurityCodeMapper securityCodeMapper;

    @RequestMapping("/testMapper")
    public String getAll(){
        List<User> userList = userMapper.getAll();
        Iterator<User> iterator = userList.iterator();
        while (iterator.hasNext()){
            User user = iterator.next();
            System.out.println(user.toString());
        }
        return "ok";
    }

    //条件查询
    @RequestMapping("/testSelectMapper")
    public String selectByCondition(){
        String key = "2440783390@qq.com";
        int type = 3;
        Example example = new Example(SecurityCode.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("key",key);
        criteria.andEqualTo("type",type);
        List<SecurityCode> userList = securityCodeMapper.selectByExample(example);
//        List<SecurityCode> all = securityCodeMapper.selectAll();
        Iterator<SecurityCode> iterator = userList.iterator();
        while (iterator.hasNext()){
            SecurityCode user = iterator.next();
            System.out.println(user.toString());
        }
        return "ok";
    }
}
