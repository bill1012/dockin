package com.eap.framework.activiti.service.impl;

import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.engine.impl.persistence.entity.UserIdentityManager;

/**
 * Created by billJiang on 2017/6/18.
 * e-mail:475572229@qq.com  qq:475572229
 * 自定义用户管理SessionFactory
 */
public class CustomGroupEntityManagerFactory implements SessionFactory {

    private CustomGroupEntityManager customGroupEntityManager;

    public void setCustomGroupEntityManager(CustomGroupEntityManager customGroupEntityManager) {
        this.customGroupEntityManager = customGroupEntityManager;
    }

    @Override
    public Class<?> getSessionType() {
        // 返回原始的UserManager类型
        return UserIdentityManager.class;

    }

    @Override
    public Session openSession() {
        // 返回自定义的UserManager实例
        return customGroupEntityManager;
    }
}
