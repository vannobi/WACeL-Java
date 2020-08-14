package pe.edu.unsa.daisi.lis.cel.view;



import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Index Page Controller 
 */
@Controller
public class IndexController  extends GenericController{

	/**
	 * Map to index page - login
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "/", "/index" }, method = RequestMethod.GET)
	public String index(Model model) {
		return "/index";
	}
	
	

}
