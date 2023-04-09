package AOP.aspects;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class AroundLoggingAspect {

    @Around("execution(public String returnBook(..))")
    public Object aroundReturnBookLoggingAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        System.out.println("Logging around START returnBook");
        long begin = System.currentTimeMillis();
        Object targetMethodResult = null;
        try {
            targetMethodResult =  proceedingJoinPoint.proceed();
        }
        catch (Exception e) {
            e.printStackTrace();
            targetMethodResult = "Not A Book";

        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - begin) + " ms");
        System.out.println("Logging around END returnBook");
        return targetMethodResult;
    }
}
