package com.codingos.shirojwt.shiro;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;

public class CustomWebSubjectFactory extends DefaultWebSubjectFactory {

	@Override
	public Subject createSubject(SubjectContext context) {
		// 禁用session
		context.setSessionCreationEnabled(false);
		return super.createSubject(context);
	}
}
