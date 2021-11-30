package com.example.create_db_file.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class ControllerAspect {

    @Before("execution(* *..*.*Controller.*(..))")
    public void before(JoinPoint joinPoint){
//        log.info("{}", joinPoint.getSignature());
    }

    @AfterThrowing(pointcut = "execution(* *..*.*Controller.*(..))", throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, Throwable ex){
        log.warn("exception occur point :{}, exception class: {}", joinPoint.getSignature(), ex.getClass());
        Object[] args = joinPoint.getArgs();
        log.warn("method arguments : {}", Arrays.toString(args));
    }
}
