<%@include file="../xava/imports.jsp"%>

<%@page import="java.util.Iterator"%>
<%@page import="org.openxava.application.meta.MetaModule"%>
<%@page import="org.openxava.util.Users"%>
<%@page import="org.openxava.util.Is"%>
<%@page import="com.openxava.naviox.Modules"%>
<%@page import="com.openxava.naviox.util.NaviOXPreferences"%>

<jsp:useBean id="modules" class="com.openxava.naviox.Modules" scope="session"/>

<% if (modules.hasModules()) { %>
<a id="show_modules" href=""><xava:message key="all_modules"/></a>	
<% } %>

<%
String allModulesClass = modules.hasModules()?"main-navigation-left-with-all-modules":"main-navigation-left-without-all-modules";
boolean showsIndexLink = modules.showsIndexLink();
%>

&nbsp; 
<div id="main_navigation_left" class="<%=allModulesClass%>">
<nobr>
&nbsp; 
<% if (Users.getCurrent() != null && showsIndexLink) { %>
	<a href="<%=request.getContextPath()%>/m/Index" class='<%="Index".equals(request.getParameter("module"))?"selected":""%>'>
		<i class="mdi mdi-home"></i>
	</a> 
<% } %>
<% 
for (Iterator it= modules.getTopModules().iterator(); it.hasNext();) {
	MetaModule module = (MetaModule) it.next();
	if (module.getName().equals("SignIn")) continue;
	if (showsIndexLink && module.getName().equals("Index")) continue; 
	String selected = module.getName().equals(request.getParameter("module"))?"selected":"";
%>		
	<a  href="<%=modules.getModuleURI(request, module)%>?retainOrder=true" class="<%=selected%>">
		<%=module.getLabel(request.getLocale())%>
	</a>
	
<%
}
%>
</nobr>
</div>

<span id="main_navigation_right">
<nobr>
<span 
	id="main_navigation_right_bridge1">&nbsp;&nbsp;&nbsp;</span><span 
	id="main_navigation_right_bridge2">&nbsp;&nbsp;&nbsp;</span><span 
	id="main_navigation_right_content">
<%
if (Is.emptyString(NaviOXPreferences.getInstance().getAutologinUser())) {
	String userName = Users.getCurrent();
	if (userName == null) {
%>
<% String selected = "SignIn".equals(request.getParameter("module"))?"selected":""; %>
<a href="<%=request.getContextPath()%>/m/SignIn" class="sign-in <%=selected%>">
		<xava:message key="signin"/>
</a>
<%
	}
	else {
%>
<a  href="<%=request.getContextPath()%>/naviox/signOut.jsp" class="sign-in"><xava:message key="signout"/> (<%=userName%>)</a>
<%
	}
} 
%>
</span></nobr></span>