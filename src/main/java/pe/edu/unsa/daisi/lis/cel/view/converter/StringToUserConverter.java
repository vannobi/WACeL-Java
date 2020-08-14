package pe.edu.unsa.daisi.lis.cel.view.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import pe.edu.unsa.daisi.lis.cel.domain.model.user.User;



/**
 * A converter class used in views to map id's to actual User objects.
 */
@Component
public class StringToUserConverter implements Converter<Object, User>{

	static final Logger logger = LoggerFactory.getLogger(StringToUserConverter.class);
	
	/**
	 * Gets User by Id
	 * @see org.springframework.core.convert.converter.Converter#convert(java.lang.Object)
	 */
	public User convert(Object element) {
		Long id = Long.parseLong((String)element);
		User user=  new User();
		user.setId(id);
		logger.info("User : {}",user);
		return user;
	}
	
}