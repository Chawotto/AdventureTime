package org.example.adventuretime.logging;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
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
        if (logger.isInfoEnabled()) {
            logger.info("Вход в метод контроллера: {} с аргументами: {}",
                    joinPoint.getSignature().toShortString(), joinPoint.getArgs());
        }
    }

    @AfterReturning(pointcut = "execution(* org.example.adventuretime.controller.*.*(..))",
            returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        if (logger.isInfoEnabled()) {
            logger.info("Выход из метода контроллера: {} с результатом: {}",
                    joinPoint.getSignature().toShortString(), result);
        }
    }

    @AfterThrowing(pointcut = "execution(* org.example.adventuretime.controller.*.*(..))",
            throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        if (logger.isErrorEnabled()) {
            logger.error("Ошибка в методе контроллера: {} с причиной: {}",
                    joinPoint.getSignature().toShortString(), error.getMessage());
        }
    }

    @Before("execution(* org.example.adventuretime.config.CacheConfig.get*(Long))")
    public void logCacheGetBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Long id = (Long) joinPoint.getArgs()[0];
        String entityType = getEntityTypeFromMethod(methodName);
        logger.info("Попытка получить {} с ID {} из кэша", entityType, id);
    }

    @AfterReturning(pointcut =
            "execution(* org.example.adventuretime.config.CacheConfig.get*(Long))",
            returning = "result")
    public void logCacheGetAfterReturning(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        Long id = (Long) joinPoint.getArgs()[0];
        String entityType = getEntityTypeFromMethod(methodName);
        if (result != null) {
            logger.info("{} с ID {} успешно получен из кэша", entityType, id);
        } else {
            logger.info("{} с ID {} не найден в кэше", entityType, id);
        }
    }

    @AfterReturning(pointcut =
            "execution(* org.example.adventuretime.config.CacheConfig.getAll*())",
            returning = "result")
    public void logCacheGetAllAfterReturning(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String entityType = getEntityTypeFromMethod(methodName);
        logger.info("Все {} успешно получены из кэша, количество: {}",
                entityType, ((Collection<?>) result).size());
    }

    @Before(
            "execution(* org.example.adventuretime.repository.*.findById(Long))")
    public void logDbFindBefore(JoinPoint joinPoint) {
        Long id = (Long) joinPoint.getArgs()[0];
        String entityType = getEntityTypeFromRepository(joinPoint
                .getTarget().getClass().getSimpleName());
        logger.info("Попытка получить {} с ID {} из базы данных", entityType, id);
    }

    @AfterReturning(pointcut =
            "execution(* org.example.adventuretime.repository.*.findById(Long))",
            returning = "result")
    public void logDbFindAfterReturning(JoinPoint joinPoint, Object result) {
        Long id = (Long) joinPoint.getArgs()[0];
        String entityType = getEntityTypeFromRepository(joinPoint.getTarget()
                .getClass().getSimpleName());
        if (result != null && ((Optional<?>) result).isPresent()) {
            logger.info("{} с ID {} успешно получен из базы данных", entityType, id);
        } else {
            logger.info("{} с ID {} не найден в базе данных", entityType, id);
        }
    }

    @AfterReturning(pointcut =
            "execution(* org.example.adventuretime.repository.*.findAll())",
            returning = "result")
    public void logDbFindAllAfterReturning(JoinPoint joinPoint, Object result) {
        String entityType = getEntityTypeFromRepository(joinPoint
                .getTarget().getClass().getSimpleName());
        logger.info("Все {} успешно получены из базы данных, количество: {}",
                entityType, ((List<?>) result).size());
    }

    @Before(
            "execution(* org.example.adventuretime.config.CacheConfig.put*(Long, *))")
    public void logCachePutBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Long id = (Long) joinPoint.getArgs()[0];
        String entityType = getEntityTypeFromMethod(methodName);
        logger.info("Добавление/обновление {} с ID {} в кэше", entityType, id);
    }

    @AfterReturning(pointcut =
            "execution(* org.example.adventuretime.config.CacheConfig.put*(Long, *))")
    public void logCachePutAfterReturning(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Long id = (Long) joinPoint.getArgs()[0];
        String entityType = getEntityTypeFromMethod(methodName);
        logger.info("{} с ID {} успешно добавлен/обновлен в кэше", entityType, id);
    }

    @Before("execution(* org.example.adventuretime.config.CacheConfig.remove*(Long))")
    public void logCacheRemoveBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Long id = (Long) joinPoint.getArgs()[0];
        String entityType = getEntityTypeFromMethod(methodName);
        logger.info("Удаление {} с ID {} из кэша", entityType, id);
    }

    @AfterReturning(pointcut =
            "execution(* org.example.adventuretime.config.CacheConfig.remove*(Long))")
    public void logCacheRemoveAfterReturning(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Long id = (Long) joinPoint.getArgs()[0];
        String entityType = getEntityTypeFromMethod(methodName);
        logger.info("{} с ID {} успешно удален из кэша", entityType, id);
    }

    private String getEntityTypeFromMethod(String methodName) {
        if (methodName.contains("Tour")) return "Тур";
        if (methodName.contains("Country")) return "Страна";
        if (methodName.contains("Transport")) return "Транспорт";
        return "Неизвестный тип";
    }

    private String getEntityTypeFromRepository(String repositoryName) {
        if (repositoryName.contains("Tour")) return "Тур";
        if (repositoryName.contains("Country")) return "Страна";
        if (repositoryName.contains("Transport")) return "Транспорт";
        return "Неизвестный тип";
    }
}