<% Servlets.setCharacterEncoding(request, response); %>

<%@include file="../xava/imports.jsp"%>

<%@page import="org.openxava.web.servlets.Servlets"%>
<%@page import="org.openxava.util.Locales"%>
<%@page import="com.openxava.naviox.web.NaviOXStyle"%>
<%@page import="com.openxava.naviox.util.Organizations"%>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="folders" class="com.openxava.naviox.Folders" scope="session"/>

<%
Organizations.setPersistenceDefaultSchema(request.getSession()); 
Locales.setCurrent(request);
String version = org.openxava.controller.ModuleManager.getVersion();
folders.setApplicationNameAsRootLabel(true);
%>

<!DOCTYPE html>

<head>
	<title><%=folders.getApplicationLabel()%></title>
	<meta name='apple-mobile-web-app-capable' content='yes'/>	 
	<meta name='viewport' content='width=device-width, initial-scale=1, maximum-scale=1'>				
	<link href="<%=request.getContextPath()%>/phone/style/phone.css" rel="stylesheet" type="text/css">
	<script type='text/javascript' src='<%=request.getContextPath()%>/xava/js/dwr-engine.js?ox=<%=version%>'></script>
	<script type='text/javascript' src='<%=request.getContextPath()%>/dwr/interface/Modules.js?ox=<%=version%>'></script>
	<script type='text/javascript' src='<%=request.getContextPath()%>/dwr/interface/Folders.js?ox=<%=version%>'></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/xava/js/jquery.js?ox=<%=version%>"></script>
</head>

<body>

	<table id="modules_list_box" width="100%">
		<tr id="modules_list_content">
			<td>
				<jsp:include page="modulesMenu.jsp"/>
			</td>						
		</tr>
	</table>

	<script type='text/javascript' src='<%=request.getContextPath()%>/naviox/js/typewatch.js'></script>
	<script type='text/javascript' src='<%=request.getContextPath()%>/naviox/js/naviox.js'></script>
	
	<script>
	$(function() {
		naviox.init();
	});
	</script>	

</body>
</html>