package com.mtons.mblog.MyMapper;

import com.mtons.mblog.modules.entity.SecurityCode;
import com.mtons.mblog.modules.entity.User;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created by admin on 2019/4/17.
 */
@org.apache.ibatis.annotations.Mapper
@Component
public interface SecurityCodeMapper extends Mapper<SecurityCode>{

    //在使用通用Mapper的基础上 下面的都是mybatis注解的方法
    @Select("select * from mto_security_code")
    List<SecurityCode> getAll();
}
