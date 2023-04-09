package AOP.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Order(30)
public class ExceptionHandlingAspect {
    @Before("AOP.aspects.Pointcuts.allAddMethods()")
    public void beforeGetExceptionAdvice() {
        System.out.println("Before getting exception Handling");
    }
}
