package org.openxava.test.tests;

import java.text.*;
import java.util.*;
import org.openxava.tests.*;

import com.gargoylesoftware.htmlunit.html.*;

/**
 * 
 * @author Javier Paniza
 */

public class IncidentTest extends ModuleTestBase {
	
	public IncidentTest(String testName) {
		super(testName, "Incident");		
	}

	public void testDiscussionEditor() throws Exception { 
		getWebClient().getOptions().setCssEnabled(true);
		
		assertListRowCount(0); 
		
		execute("CRUD.new");
		setValue("title", "THE JUNIT DISCUSSION");
		setValue("description", "This is the big jUnit discussion");
		
		assertDiscussionCommentsCount("discussion", 0);
		postDiscussionComment("discussion", "Hi, it's me");
		String timeFirstPost = getCurrentTime();
		assertDiscussionCommentsCount("discussion", 1);
		assertDiscussionCommentText("discussion", 0, getFormattedDiscussionComment("admin - Now", "Hi, it's me"));
		
		execute("CRUD.save");
		assertValue("title", "");
		assertValue("description", "");
		HtmlElement discussion = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Incident__editor_discussion"); 
		HtmlElement textarea = discussion.getElementsByTagName("textarea").get(0); 
		assertEquals("", textarea.getTextContent());
		
		assertDiscussionCommentsCount("discussion", 0);
			
		changeModule("SignIn");
		login("juan", "juan4");
		changeModule("Incident");
		
		execute("Mode.detailAndFirst");

		assertValue("title", "THE JUNIT DISCUSSION");
		assertValue("description", "This is the big jUnit discussion");

		assertDiscussionCommentsCount("discussion", 1);
		assertDiscussionCommentText("discussion", 0, getFormattedDiscussionComment("admin - " + timeFirstPost, "Hi, it's me"));
		postDiscussionComment("discussion", "Soy Juan"); 
		String timeSecondPost = getCurrentTime();
		
		execute("Mode.list");
		execute("Mode.detailAndFirst");

		assertValue("title", "THE JUNIT DISCUSSION");
		assertValue("description", "This is the big jUnit discussion");
		assertDiscussionCommentsCount("discussion", 2);
		assertDiscussionCommentText("discussion", 0, getFormattedDiscussionComment("admin - " + timeFirstPost, "Hi, it's me"));
		assertDiscussionCommentText("discussion", 1, getFormattedDiscussionComment("juan - " + timeSecondPost, "Soy Juan"));

		assertEquals(1, discussion.getElementsByTagName("textarea").size());
		assertEquals(1, discussion.getElementsByAttribute("input", "type", "button").size());	
		execute("Incident.disableDiscussion");
		discussion = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Incident__editor_discussion");
		assertEquals(0, discussion.getElementsByTagName("textarea").size());
		assertEquals(0, discussion.getElementsByAttribute("input", "type", "button").size());
		
		execute("CRUD.delete");
		assertNoErrors();
	}
	
	private String getCurrentTime() { 
		return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, new Locale(getLocale())).format(new java.util.Date());
	}
	
	private String getFormattedDiscussionComment(String head, String content) {
		return String.format("%s%n%s", head, content);
	}
}
