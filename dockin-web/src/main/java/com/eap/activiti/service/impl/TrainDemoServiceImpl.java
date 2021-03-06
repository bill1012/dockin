package com.eap.activiti.service.impl;

import com.eap.activiti.entity.Train;
import com.eap.activiti.service.AssigneeService;
import com.eap.activiti.service.TrainDemoService;
import com.eap.framework.activiti.pojo.Constants;
import com.eap.framework.activiti.service.IdentityPageService;
import com.eap.framework.activiti.service.RuntimePageService;
import com.eap.framework.base.entity.User;
import com.eap.framework.base.pojo.Result;
import com.eap.framework.base.service.impl.BaseServiceImpl;
import com.eap.framework.util.SecurityUtil;
import com.eap.framework.utils.StrUtil;
import org.activiti.engine.identity.Group;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by billJiang on 2017/7/6.
 * e-mail:475572229@qq.com  qq:475572229
 */
@Service("trainDemoService")
public class TrainDemoServiceImpl extends BaseServiceImpl implements TrainDemoService {

    @Resource
    private RuntimePageService runtimePageService;

    @Resource
    private IdentityPageService identityPageService;

    @Resource
    private AssigneeService assigneeService;

    @Override
    public Result startFlow(Train train, String processDefinitionKey) {
        train.setState(1);
        if (StrUtil.isEmpty(train.getId())) {
            this.save(train);
        } else {
            train.setUpdateDateTime(new Date());
            this.update(train);
        }
        //流程实例名称
        User user = SecurityUtil.getUser();
        String name = user.getName() + "申请培训：" + train.getName() + "，课时：" + train.getDuration() + "，时间：" + train
                .getCourseTime();
        //businessKey
        String businessKey = train.getId();
        //配置流程变量
        Map<String, Object> variables = new HashMap<>();
        variables.put(Constants.VAR_APPLYUSER_NAME, user.getName());
        variables.put(Constants.VAR_BUSINESS_KEY, train.getId());

        //---------根据当前人的角色选择不同的流程路径-----------------------
        //提交人角色
        List<Group> groupList = identityPageService.findGroupsByUser(user.getId());
        List<String> groupCodeList = new ArrayList<>();
        for (Group group : groupList) {
            groupCodeList.add(group.getType());
        }
        variables.put("groupCode", StrUtil.join(groupCodeList));
        //单位是否含有角色
        String code = "WORKFLOW-SZR";
        List<String> chiefList = assigneeService.findChiefs(user.getId(), identityPageService.getGroupByCode(code).getId
                ());
        variables.put("chief", chiefList.isEmpty() ? "no" : "yes");
        //启动流程
        return runtimePageService.startProcessInstanceByKey(processDefinitionKey, name, variables,
                user.getId(), businessKey);
    }
}
