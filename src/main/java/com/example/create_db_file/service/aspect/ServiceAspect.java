package com.example.create_db_file.service.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
@Aspect
public class ServiceAspect {

    @Before("execution(* *..*.DbFileCreateService.*(..))")
    public void beforeDbFileCreateService(JoinPoint joinPoint){
//        log.info("{}", joinPoint.getSignature());
    }

    @AfterThrowing(pointcut = "execution(* *..*.DbFileCreateService.*(..))", throwing = "ex")
    public void afterThrowingDbCreateService(JoinPoint joinPoint, Throwable ex){
        log.warn("exception occur point :{}, exception class: {}", joinPoint.getSignature(), ex.getClass());
        Object[] args = joinPoint.getArgs();
        log.warn("method arguments : {}", Arrays.toString(args));
    }

    @Before("execution(* *..*.ExcelInformationReader.*(..))")
    public void before(JoinPoint joinPoint){
//        log.info("{}", joinPoint.getSignature());
    }

    @AfterThrowing(pointcut = "execution(* *..*.ExcelInformationReader.*(..))", throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, Throwable ex){
        log.warn("exception occur point :{}, exception class: {}", joinPoint.getSignature(), ex.getClass());
        Object[] args = joinPoint.getArgs();
        log.warn("method arguments : {}", Arrays.toString(args));
    }
}
