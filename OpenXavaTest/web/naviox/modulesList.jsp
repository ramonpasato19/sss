<%@page import="java.util.Iterator"%>
<%@page import="org.openxava.application.meta.MetaModule"%>
<%@page import="com.openxava.naviox.model.Folder"%>

<jsp:useBean id="modules" class="com.openxava.naviox.Modules" scope="session"/>
<jsp:useBean id="folders" class="com.openxava.naviox.Folders" scope="session"/> 

<% 
folders.setModules(modules); 

for (Iterator it= folders.getSubfolders().iterator(); it.hasNext();) {
	Folder folder = (Folder) it.next();
%>	
	<a  href="javascript:naviox.goFolder('<%=folder.getId()%>')">
	<div class="folder-row " >	
		<div class="folder-name">
			<%=folder.getLabel()%>
			<span class="folder-icon">&nbsp;&nbsp;&nbsp;</span>
		</div>		
	</div>	
	</a>
	
<%	
}
%>

<% if (folders.isRoot()) { %>
	<jsp:include page="selectModules.jsp">
		<jsp:param name="bookmarkModules" value="true"/>
	</jsp:include>
<% } %>

<jsp:include page="selectModules.jsp">
	<jsp:param name="folderModules" value="true"/>
</jsp:include>