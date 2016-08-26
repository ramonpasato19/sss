<%@ include file="imports.jsp"%>

<%@ page import="org.openxava.controller.meta.MetaAction" %>
<%@ page import="org.openxava.util.XavaPreferences"%>
<%@ page import="org.openxava.util.Is"%>
<%@ page import="com.openxava.phone.controller.PhoneManager"%>


<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%
org.openxava.controller.ModuleManager manager = (org.openxava.controller.ModuleManager) context.get(request, "manager", "org.openxava.controller.ModuleManager");
manager.setSession(session);
boolean onBottom = false;
String mode = request.getParameter("xava_mode"); 
if (mode == null) mode = manager.isSplitMode()?"detail":manager.getModeName();
boolean headerButtonBar = !manager.isSplitMode() || mode.equals("list");  
PhoneManager phoneManager = new PhoneManager(manager);
%>

<div class="<%=style.getButtonBar()%>">

	<%
	java.util.Stack previousViews = (java.util.Stack) context.get(request, "xava_previousViews"); 
	if (headerButtonBar && previousViews.isEmpty()) { 
		String positionClass = null;		
		java.util.Collection actions = manager.getMetaActionsMode();
		java.util.Iterator itActions = actions.iterator();
		boolean backButtonShown = false;
		while (itActions.hasNext()) {
			MetaAction action = (MetaAction) itActions.next();
			String modeNameAction = action.getName().startsWith("detail")?"detail":action.getName();
			if (!manager.isListMode() && !modeNameAction.equals(manager.getModeName())) {
			%>	
	<xava:link action="<%=action.getQualifiedName()%>">		
	<div class="phone-back-button">	
    	<div><span></span></div>
    	<p><%=action.getLabel()%></p>		
	</div>
	</xava:link>
			<%	
				backButtonShown = true;
				break;
			}
		}
		if (!backButtonShown && !manager.getModelName().equals("SignIn")) {
		%>
	<a href="../phone">
	<div class="phone-back-button">	
	   	<div><span></span></div>
	   	<p><xava:message key="back"/></p>		
	</div>
	</a>
		<%
		}
	}	
	else if (!previousViews.isEmpty()) {
		for (java.util.Iterator it = manager.getMetaActions().iterator(); it.hasNext(); ) {
			MetaAction action = (MetaAction) it.next();
			if ("cancel".equals(action.getName()) || "return".equals(action.getName()) || 
				"cancelar".equals(action.getName()) || "volver".equals(action.getName())) {
			%>
	<xava:link action="<%=action.getQualifiedName()%>">
		<div class="phone-cancel-action">					
			<p><%=action.getLabel()%></p>			
		</div>
	</xava:link>			
			<%
				break;
			}
		}
	}
	%>
	
	<span class="phone-title"> 
		<%=phoneManager.getTitle(request)%>
	</span>
	
	<% MetaAction defaultAction = phoneManager.getDefaultMetaAction(); %>
	<xava:link action="<%=defaultAction.getQualifiedName()%>">
		<div class="phone-default-action">					
			<p><%=defaultAction.getLabel()%></p>			
		</div>
	</xava:link>

</div>
