package AOP.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class Pointcuts {
    @Pointcut("execution(public * abc*(..))")
    public void allAddMethods() {}
}
