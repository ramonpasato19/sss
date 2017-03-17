<%@ include file="imports.jsp"%>

<%@ page import="org.openxava.model.meta.MetaCollection" %>
<%@ page import="org.openxava.view.View" %>
<%@ page import="org.openxava.util.Is" %> 
<%@ page import="org.openxava.controller.meta.MetaAction" %>
<%@ page import="org.openxava.controller.meta.MetaControllers" %>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%
String viewObject = request.getParameter("viewObject");
viewObject = (viewObject == null || viewObject.equals(""))?"xava_view":viewObject;
View view = (View) context.get(request, viewObject);
String collectionName = request.getParameter("collectionName");
MetaCollection collection = view.getMetaModel().getMetaCollection(collectionName);
if (!(collection.hasCalculator() || collection.isSortable())) {
%>

<% 
View subview = view.getSubview(collectionName);
String viewName = viewObject + "_" + collectionName;
String idCollection = org.openxava.web.Collections.id(request, collectionName);
boolean collectionEditable = subview.isCollectionEditable();
boolean collectionMembersEditables = subview.isCollectionMembersEditables();
String lineAction = ""; 
if (collectionEditable || collectionMembersEditables) {
	lineAction = subview.getEditCollectionElementAction();
}
else {
	lineAction = subview.getViewCollectionElementAction();
}
context.put(request, viewName, subview);
String tabObject = org.openxava.web.Collections.tabObject(idCollection); 
org.openxava.tab.Tab tab = subview.getCollectionTab();

String tabPrefix = tabObject + "_";
tab.clearStyle();
int selectedRow = subview.getCollectionEditingRow();
if (selectedRow >= 0) {
	String cssClass=selectedRow%2==0?style.getListPairSelected():style.getListOddSelected();
	tab.setStyle(selectedRow, cssClass);
}
context.put(request, tabObject, tab);
%>

<div class="phone-frame-header"> 
	<span class="phone-frame-title"><%=collection.getLabel()%></span>
	<% 
	if (collectionEditable) { 
		String newAction = subview.getNewCollectionElementAction();
		if (!Is.emptyString(newAction)) {
	%>
	<xava:link action='<%=newAction%>' argv='<%="viewObject="+viewName%>'>
		<div class="phone-frame-action">
			<p><%=MetaControllers.getMetaAction(newAction).getLabel()%></p>			
		</div>
	</xava:link>
	<%  
		}
		String removeSelectedAction = subview.getRemoveSelectedCollectionElementsAction();
		if (!Is.emptyString(removeSelectedAction)) {
	%>
	<xava:link action='<%=removeSelectedAction%>' argv='<%="viewObject="+viewName%>'>
		<div class="phone-frame-action">
			<% 
			// We use the label of remove action instead of remove selected action because it's shorter
			String actionForLabel = subview.getRemoveCollectionElementAction();
			if (Is.emptyString(actionForLabel)) actionForLabel = removeSelectedAction;
			%>					
			<p><%=MetaControllers.getMetaAction(actionForLabel).getLabel()%></p>			
		</div>
	</xava:link>
	<%
		}
	} 
	%>	
</div>

<jsp:include page="list.jsp">
	<jsp:param name="collection" value="<%=idCollection%>"/>
	<jsp:param name="rowAction" value="<%=lineAction%>"/>
	<jsp:param name="tabObject" value="<%=tabObject%>"/>
	<jsp:param name="viewObject" value="<%=viewName%>"/>
</jsp:include>

<%
} // of: if (!(collection.hasCalculator() || collection.isSortable())) { 
%>

