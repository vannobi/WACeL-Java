package pe.edu.unsa.daisi.lis.cel.util.exception.handling;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import pe.edu.unsa.daisi.lis.cel.util.exception.CustomException;
import pe.edu.unsa.daisi.lis.cel.util.exception.ErrorCode;

/**
 * For generic exceptions, most of the times we serve static pages. Spring Framework provides HandlerExceptionResolver interface that we can implement to create global exception handler. The reason behind this additional way to define global exception handler is that Spring framework also provides default implementation classes that we can define in our spring bean configuration file to get spring framework exception handling benefits.
 * <br/>
 * <br/>
 * Handle any other types of exception
 * @author Edgar
 *
 */
public class GenericHandlerExceptionResolver extends SimpleMappingExceptionResolver{
	@Override
	protected ModelAndView doResolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {
		String viewName=determineViewName(ex, request);
		
		if (viewName!=null) {
			Integer statusCode = super.determineStatusCode(request, viewName);
			if (statusCode != null) {
				applyStatusCodeIfPossible(request, response, statusCode);
			}
			//APPLICATION EXCEPTIONS
			if(ex instanceof CustomException) {
				CustomException customException = (CustomException) ex;
				request.setAttribute("error", "Error Code: " +customException.getCode().getId() );
			} else { //JAVA CHECKED (EXCEPTION) OR UNCHECKED (RUNTIME) EXCEPTIONS
				request.setAttribute("error", ErrorCode.UNEXPECTED_ERROR);
			}
								
			
			return getModelAndView(viewName, ex, request);
		}
		return null;
	}
}
