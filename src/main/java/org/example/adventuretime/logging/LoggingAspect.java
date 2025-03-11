package org.example.adventuretime.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* org.example.adventuretime.controller.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("Вход в метод: {} с аргументами: {}",
                joinPoint.getSignature().getName(), joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "execution(* org.example.adventuretime.controller.*.*(..))",
            returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        logger.info("Выход из метода: {} с результатом: {}",
                joinPoint.getSignature().getName(), result);
    }

    @AfterThrowing(pointcut = "execution(* org.example.adventuretime.controller.*.*(..))",
            throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        logger.error("Ошибка в методе: {} с причиной: {}",
                joinPoint.getSignature().getName(), error.getMessage());
    }
}