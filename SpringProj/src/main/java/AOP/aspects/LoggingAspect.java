package AOP.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Order(10)
public class LoggingAspect {

//    @Pointcut("execution(* AOP.UniverLibrary.*(..))")
//    private void allMethodsFromUniverLibrary() {}
//
//    @Pointcut("execution(public void AOP.UniverLibrary.returnMagazine())")
//    private void returnMagazineMethodFromUniverLibrary() {}
//
//    @Pointcut("allMethodsFromUniverLibrary() && !returnMagazineMethodFromUniverLibrary()")
//    private void allMethodsExceptReturnMagazineMethodFromUniverLibrary() {}
//
//    @Before("allMethodsExceptReturnMagazineMethodFromUniverLibrary()")
//    public void beforeAllMethodsExceptReturnMagazineMethodFromUniverLibraryAdvice() {
//        System.out.println("Before all methods except return magazine method from univer library");
//    }


//    @Pointcut("execution(* AOP.UniverLibrary.get*(..))")
//    private void allGetMethodsFromUniverLibrary() {}
//
//    @Pointcut("execution(public * return*(..))")
//    private void allReturnMethodsFromUniverLibrary() {}
//
//    @Pointcut("allGetMethodsFromUniverLibrary() || allReturnMethodsFromUniverLibrary()")
//    private void allGetAndReturnMethodsFromUniverLibrary() {}
//
//    @Before("allGetMethodsFromUniverLibrary()")
//    public void beforeGetLoggingAdvice() {
//        System.out.println("Before getting: log #1");
//    }
//
//    @Before("allReturnMethodsFromUniverLibrary()")
//    public void beforeReturnLoggingAdvice() {
//        System.out.println("Before returning: log #2");
//    }
//
//    @Before("allGetAndReturnMethodsFromUniverLibrary()")
//    public void beforeGetAndReturnLoggingAdvice() {
//        System.out.println("Before getting and returning: log #3");
//    }



//    @Before("execution(public void AOP.UniverLibrary.getBook(AOP.Book))")
//    public void beforeGetBookAdvice() {
//        System.out.println("Before get book");
//    }
    @Before("AOP.aspects.Pointcuts.allGetMethods()")
    public void beforeGetLoggingAdvice() {
        System.out.println("Before getting logging");
    }


    @Before("execution(public * returnBook())")
    public void beforeReturnBookAdvice() {
        System.out.println("Before returning");
    }


}
