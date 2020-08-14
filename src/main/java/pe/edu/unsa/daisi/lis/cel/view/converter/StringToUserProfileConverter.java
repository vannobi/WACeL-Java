package pe.edu.unsa.daisi.lis.cel.view.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import pe.edu.unsa.daisi.lis.cel.domain.model.user.UserProfile;



/**
 * A converter class used in views to map id's to actual userProfile objects.
 */
@Component
public class StringToUserProfileConverter implements Converter<Object, UserProfile>{

	static final Logger logger = LoggerFactory.getLogger(StringToUserProfileConverter.class);
	
	/**
	 * Gets UserProfile by Id
	 * @see org.springframework.core.convert.converter.Converter#convert(java.lang.Object)
	 */
	public UserProfile convert(Object element) {
		Long id = Long.parseLong((String)element);
		UserProfile profile= new UserProfile();
		profile.setId(id);
		logger.info("Profile : {}",profile);
		return profile;
	}
	
}