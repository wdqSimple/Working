/*
+--------------------------------------------------------------------------
|   Mblog [#RELEASE_VERSION#]
|   ========================================
|   Copyright (c) 2014, 2015 mtons. All Rights Reserved
|   http://www.mtons.com
|
+---------------------------------------------------------------------------
*/
package com.mtons.mblog.modules.service.impl;

import com.mtons.mblog.modules.entity.Options;
import com.mtons.mblog.modules.repository.OptionsRepository;
import com.mtons.mblog.modules.service.OptionsService;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author langhsu
 */
@Service
public class OptionsServiceImpl implements OptionsService {
	@Autowired
	private OptionsRepository optionsRepository;
	@Autowired
	private EntityManager entityManager;

	@Override
	@Transactional(readOnly = true)
	public List<Options> findAll() {
		List<Options> list = optionsRepository.findAll();
		List<Options> rets = new ArrayList<>();
		
		for (Options po : list) {
			Options r = new Options();
			BeanUtils.copyProperties(po, r);
			rets.add(r);
		}
		return rets;
	}

	@Override
	@Transactional
	public void update(Map<String, String> options) {
		//获取前台传入条件
		if (options == null) {
			return;
		}

		//循环
		options.forEach((key, value) -> {
			//根据 前台传入数据 key 在数据库查询 返回值封装成一个实体
			Options entity = optionsRepository.findByKey(key);

			String val = StringUtils.trim(value);

			//如果返回对象 不为null 将value值重新付给对象 然后保存到数据库
			if (entity != null) {

				entity.setValue(val);
			} else {
				//如果返回对象 为null key 、 value 付给对象 然后保存到数据库
				entity = new Options();
				entity.setKey(key);
				entity.setValue(val);
			}
			//保存到数据库
			optionsRepository.save(entity);
		});
	}

	@Override
	@Transactional
	public void initSettings(Resource resource) {
		Session session = entityManager.unwrap(Session.class);
		session.doWork(connection -> ScriptUtils.executeSqlScript(connection, resource));
	}

}
