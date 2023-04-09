package AOP.aspects;

import AOP.Book;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;

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
    @Before("AOP.aspects.Pointcuts.allAddMethods()")
    public void beforeGetLoggingAdvice(JoinPoint joinPoint) {
        System.out.println("Before adding logging");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();
        System.out.println("method Signature, " + signature);
        System.out.println("method , " + signature.getMethod());
        System.out.println("args , " + Arrays.toString(args));

        if (signature.getName().equals("addBook")) {
            System.out.println("Adding book");
            for (Object arg : args) {
                if (arg instanceof Book book) {
                    System.out.println("Book title : " + book.getTitle());
                    System.out.println("Book year : " + book.getYearOfPublication());
                }
                else if (arg instanceof String personName) {
                    System.out.println("Person name : " + personName);
                }
            }
        } else if (signature.getName().equals("addMagazine")) {
            System.out.println("Adding magazine");
        }

    }


//    @Before("execution(public * returnBook())")
//    public void beforeReturnBookAdvice() {
//        System.out.println("Before returning");
//    }


}
