package org.microlending.app.loan.aop.logging;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class LoggingAspect {

	private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);
	
	@Pointcut("execution(public * org.microlending.app.loan.controller.*.*(..))")
	public void loggingPointcut() {}
	
	@AfterThrowing(pointcut="loggingPointcut()", throwing="e")
	public void logAfterThrowing(JoinPoint joinPoint, Throwable e){
		log.error("Exception in "+joinPoint.getSignature().getDeclaringTypeName()+"."+joinPoint.getSignature().getName()+"() with cause "+e.getCause());
	}

	@Around("loggingPointcut() ")
	public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable{
		log.debug("Enter "+joinPoint.getSignature().getDeclaringTypeName()+"."+joinPoint.getSignature().getName()+"() with arguments "+Arrays.toString(joinPoint.getArgs()));

		Object result = joinPoint.proceed();

		log.debug("Exit "+joinPoint.getSignature().getDeclaringTypeName()+"."+joinPoint.getSignature().getName()+"() with result "+result);
		
		return result;
	}

	
}
