package pe.edu.unsa.daisi.lis.cel.util;

import java.util.Locale;

import org.springframework.context.i18n.LocaleContextHolder;



/**
 * A  class used to get user's locale from request.
 */
public final class CustomUserLocal {

	/**
	 * This method gets user's locale from request <br/>
	 * The content of the LocaleContextHolder corresponds by default to the locale specified within the Web request.
	 */
	public static Locale getCurrentUserLocal( ) { 
		Locale locale = LocaleContextHolder.getLocale();
		return locale;
	}	
}