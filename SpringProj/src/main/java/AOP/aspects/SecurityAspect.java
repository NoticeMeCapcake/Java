package AOP.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Order(20)
public class SecurityAspect {
    @Before("AOP.aspects.Pointcuts.allGetMethods()")
    public void beforeGetSecurityAdvice() {
        System.out.println("Before getting security check");
    }
}
