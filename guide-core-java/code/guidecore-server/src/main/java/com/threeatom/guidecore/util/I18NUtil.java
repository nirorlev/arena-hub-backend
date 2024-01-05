package com.threeatom.guidecore.util;

import net.sf.cglib.core.Local;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@Component
public class I18NUtil {
	
	private static MessageSource messageSource;
	
	
	public I18NUtil(MessageSource messageSource) {
		I18NUtil.messageSource=messageSource;
	}
	
	public static String get(String msgKey) {
		return messageSource.getMessage(msgKey, null,LocaleContextHolder.getLocale());
	}

}
