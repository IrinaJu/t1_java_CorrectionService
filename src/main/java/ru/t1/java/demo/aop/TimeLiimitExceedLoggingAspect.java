package ru.t1.java.demo.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import ru.t1.java.demo.model.TimeLiimitExceedLog;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Aspect
@Component

public class TimeLiimitExceedLoggingAspect {
    @Value("${track.time-limit-exceed}")
    private long timeLimitMilli;



    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public TimeLiimitExceedLoggingAspect(@Lazy KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    @Around("execution(* *(..))")
    public Object checkExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.nanoTime();
        Object result = null;
        Throwable throwable = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable ex) {
            throwable = ex;

            kafkaTemplate.send("t1_demo_error_trace", ex.getMessage());
        } finally {
            long endTime = System.nanoTime();
            long executionTime = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);

            if (executionTime > timeLimitMilli || throwable != null) {
                MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
                Method method = methodSignature.getMethod();
                String methodName = method.getName();
                Object[] methodArgs = joinPoint.getArgs();

                TimeLiimitExceedLog exceedLog = new TimeLiimitExceedLog();
                exceedLog.setMethodSignature(methodName + "(" + Arrays.toString(methodArgs) + ")");
                exceedLog.setExecutionTime(BigDecimal.valueOf(executionTime));
                exceedLog.setLogTime(LocalDateTime.now(ZoneId.systemDefault()));
                exceedLog.setException(throwable != null ? throwable.toString() : null);

                kafkaTemplate.send("t1_demo_metric_trace", exceedLog);
            }

        }


        if (throwable != null) {
            throw throwable;
        }

        return result;
    }
}


