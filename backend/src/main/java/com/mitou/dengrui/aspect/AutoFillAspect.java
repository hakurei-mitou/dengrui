package com.mitou.dengrui.aspect;

import com.mitou.dengrui.annotation.AutoFill;
import com.mitou.dengrui.constant.AutoFillConstant;
import com.mitou.dengrui.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面，实现公共字段自动填充处理逻辑
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /**
     * 切入点
     */
    @Pointcut("execution(* com..mapper.*.*(..)) && @annotation(com.mitou.dengrui.annotation.AutoFill)")
    public void autoFillPointCut(){}

    /**
     * 前置通知，在通知中进行公共字段的赋值
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("开始进行公共字段自动填充...");

        //获取到当前被拦截的方法上的数据库操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//方法签名对象
        System.out.println("当前切入方法：" + signature.toString());
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);//获得方法上的注解对象
        OperationType operationType = autoFill.value();//获得数据库操作类型

        //获取到当前被拦截的方法的参数--实体对象
        Object[] args = joinPoint.getArgs();
        if(args == null || args.length == 0){
            return;
        }

        // 可能是任何对象类型，用 Object 引用
        Object entity = args[0];

        //准备赋值的数据
        String now = LocalDateTime.now().toString();

        try {
            //根据当前不同的操作类型，为对应的属性通过反射来赋值
            if (operationType == OperationType.INSERT) {
                //为 2 个公共字段赋值
                    Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, String.class);
                    Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, String.class);

                    //通过反射为对象属性赋值
                    setCreateTime.invoke(entity,now);
                    setUpdateTime.invoke(entity,now);
            } else if (operationType == OperationType.UPDATE) {
                //为 1 个公共字段赋值
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, String.class);

                //通过反射为对象属性赋值
                setUpdateTime.invoke(entity,now);
            } else if (operationType == OperationType.HISTORY) {
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, String.class);
                setCreateTime.invoke(entity,now);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}