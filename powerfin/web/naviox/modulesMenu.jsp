<%@include file="../xava/imports.jsp"%>

<jsp:useBean id="modules" class="com.openxava.naviox.Modules" scope="session"/>
<jsp:useBean id="folders" class="com.openxava.naviox.Folders" scope="session"/>

<div id="modules_list_header">

<% if (!folders.isRoot()) { %>
<a id="back_folder" href="javascript:naviox.goBack()">« <%=folders.getParentFolderLabel()%></a>
<% } %>

<%=folders.getFolderLabel()%>

<% if (!folders.isRoot()) { %>
<span id="back_folder_counterweight">&nbsp;</span>
<% } %>
</div>

<div id="modules_list_search_header">
<xava:message key="modules_search"/>
</div>

<div id="search_modules">
<input id="search_modules_text" type="text" size="38" placeholder='<xava:message key="search_modules"/>'/>
</div>

<div id="modules_list_core">
<jsp:include page="modulesList.jsp"/>
</div>
