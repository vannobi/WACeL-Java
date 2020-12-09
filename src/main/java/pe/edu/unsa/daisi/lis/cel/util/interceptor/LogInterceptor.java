package pe.edu.unsa.daisi.lis.cel.util.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An aspect that logs a message before any method of Services is invoked
 */
//@Aspect
public class LogInterceptor {
	static final Logger logger = LoggerFactory.getLogger(LogInterceptor.class);
	/**
	 * Using this Regular expression, the aspect performs a pointcut
	 * on every method call of Service Layer.
	 */
	@Around("execution(* pe.edu.*.service.*.Service*.*(..))")
	public void trackChangeService(ProceedingJoinPoint joinPoint) throws Throwable{
		logger.info("Method about to change " + joinPoint.getSignature().getName());
		
		try{
			joinPoint.proceed();
		} catch (Exception e) {
			logger.info("Failed performing the method " + joinPoint.getSignature().getName() + ".", e);
			throw e;
		}
		logger.info("Method performed succesfully " + joinPoint.getSignature().getName());
	}
	//@Before("execution(void set*(*))")
		public void trackChange() {
			logger.info("property about to change");
		}
}
