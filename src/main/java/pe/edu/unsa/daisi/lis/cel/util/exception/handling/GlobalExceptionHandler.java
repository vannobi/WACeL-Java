package pe.edu.unsa.daisi.lis.cel.util.exception.handling;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import pe.edu.unsa.daisi.lis.cel.util.exception.ViewException;

/**
 * Exception Handling is a cross-cutting concern, it should be done for all the pointcuts in our application. 
 * We use Spring AOP and that's why Spring provides @ControllerAdvice annotation that we can use with any class to define our global exception handler.
 * <br/>
 * The handler methods in Global Controller Advice is same as Controller based exception handler methods and used when controller class is not able to handle the exception.
 * <br/>
 * <br/>
 * We define exception handler methods for our controller classes (view layer). 
 * All we need is to annotate these methods with @ExceptionHandler annotation. 
 * This annotation takes Exception class as argument. So if we have defined one of these for Exception class, 
 * then all the exceptions thrown by our request handler method will have handled.
 * <br/>
 * <br/>
 * 
 * Notice. Exceptions from beans of other layers must be handled for caller beans (up level layer)
 * <br/>
 * <br/> 
 * Notice: This class does not handle any other types of exception, The HandlerExceptionResolver implementation handles them.
 * @author Edgar
 *
 */
//@EnableWebMvc
//@ControllerAdvice
//@Order(1) //HIGHEST PRIORITY: We have 2 exception handlers
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler{

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	/**
	 * for View layer Exception, I am returning appError.jsp as response page with http status code as 200.
	 * @param request
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(ViewException.class)
	public String handleViewException(HttpServletRequest request, Exception ex){
		logger.info("View Exception Occured:: URL="+request.getRequestURL());
		return "/home/appError";
	}
	
	/**
	 * For IOException, we are returning void with status code as 404, so our error-page (web.xml: /home/404.jsp) will be used in this case.
	 */
	@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="IOException occured")
	@ExceptionHandler(IOException.class)
	public void handleIOException(){
		logger.error("IOException handler executed");
		//returning 404 error code
	}
	
	  @ExceptionHandler
	  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	  public String handleException (Exception exception, Model model) {
	      model.addAttribute("error", exception.getMessage());
	      return "/home/appError";
	  }
}
