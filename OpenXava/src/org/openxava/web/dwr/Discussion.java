package org.openxava.web.dwr;

import javax.servlet.http.*;

import org.openxava.jpa.*;
import org.openxava.util.*;
import org.openxava.web.editors.*;

/**
 * 
 * @author Javier Paniza
 * @since 5.6
 */
public class Discussion extends DWRBase {
	
	public void postComment(HttpServletRequest request, HttpServletResponse response, String application, String module, String discussionId, String commentContent) {
		try {
			initRequest(request, response, application, module); 
			DiscussionComment comment = new DiscussionComment();
			comment.setDiscussionId(discussionId);
			comment.setUserName(Users.getCurrent());
			comment.setComment(commentContent);
			XPersistence.getManager().persist(comment);
		}
		finally {
			XPersistence.commit();
			cleanRequest(); 
		}
	}

}
