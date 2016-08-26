<%@ include file="imports.jsp"%>

<%@ page import="org.openxava.tab.impl.IXTableModel" %>
<%@ page import="org.openxava.util.Strings" %>
<%@ page import="org.openxava.util.XavaPreferences" %>
<%@ page import="org.openxava.model.meta.MetaProperty" %>
<%@ page import="org.openxava.web.WebEditors" %>
<%@ page import="org.openxava.util.Is" %>
<%@ page import="org.openxava.web.Ids" %>
<%@ page import="org.openxava.controller.meta.MetaAction" %>
<%@ page import="org.openxava.controller.meta.MetaControllers" %>
<%@ page import="org.openxava.web.Actions" %>
<%@ page import="org.openxava.util.Users" %>
<%@ page import="java.util.prefs.Preferences" %>
<%@ page import="org.openxava.util.XavaResources"%>
<%@ page import="com.openxava.phone.util.ListElementIterator"%>
<%@ page import="com.openxava.phone.util.ListElement"%> 

<jsp:useBean id="errors" class="org.openxava.util.Messages" scope="request"/>
<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%! private final static int ROW_COUNT = 50; %>

<%
org.openxava.controller.ModuleManager manager = (org.openxava.controller.ModuleManager) context.get(request, "manager", "org.openxava.controller.ModuleManager");
String collection = request.getParameter("collection"); 
String id = "list";
String collectionArgv = "";
String prefix = "";
String tabObject = request.getParameter("tabObject"); 
tabObject = (tabObject == null || tabObject.equals(""))?"xava_tab":tabObject;
if (collection != null && !collection.equals("")) {
	id = collection;
	collectionArgv=",collection="+collection;
	prefix = tabObject + "_"; 
}
org.openxava.tab.Tab tab = (org.openxava.tab.Tab) context.get(request, tabObject);
tab.setRequest(request); 
String action=request.getParameter("rowAction");
action=action==null?manager.getEnvironment().getValue("XAVA_LIST_ACTION"):action;
String viewObject = request.getParameter("viewObject");
String actionArgv = viewObject != null && !viewObject.equals("")?",viewObject=" + viewObject:"";
viewObject = (viewObject == null || viewObject.equals(""))?"xava_view":viewObject; 
org.openxava.view.View view = (org.openxava.view.View) context.get(request, viewObject);
String sfilter = request.getParameter("filter");
boolean filter = !"false".equals(sfilter);
String displayFilter=""; 
String imageFilter="hide-filter";
String filterMessage="hide_filters";
if (!tab.isFilterVisible()) {
	displayFilter="none"; 
	imageFilter ="show-filter"; 
	filterMessage="show_filters";
}
String lastRow = request.getParameter("lastRow");
boolean singleSelection="true".equalsIgnoreCase(request.getParameter("singleSelection"));
String onSelectCollectionElementAction = view.getOnSelectCollectionElementAction();
MetaAction onSelectCollectionElementMetaAction = Is.empty(onSelectCollectionElementAction) ? null : MetaControllers.getMetaAction(onSelectCollectionElementAction);
String selectedRowStyle = style.getSelectedRowStyle(); 
String rowStyle = "";
int totalSize = -1; 
tab.reset();
boolean loadMore = false;
Object initListRowCount = request.getAttribute("xava.initListRowCount");
int limit = initListRowCount == null?tab.getPageRowCount():ROW_COUNT;
if (limit < ROW_COUNT) limit = ROW_COUNT;
String listClass = collection==null?style.getList():"phone-frame"; 
%>
<table id="<xava:id name='<%=id%>'/>" class="<%=listClass%>" <%=style.getListCellSpacing()%> style="<%=style.getListStyle()%>">
<%
int f = 0;
String prefixIdRow = Ids.decorate(request, prefix);
for (java.util.Iterator it = new ListElementIterator(tab, view, request, errors); it.hasNext();) {
	if (f == limit) {
		loadMore = true;
		break;
	}
	ListElement el = (ListElement) it.next();
	String checked=tab.isSelected(f)?"checked='true'":"";
	String actionOnClick = Actions.getActionOnClick(
			request.getParameter("application"), request.getParameter("module"), 
			onSelectCollectionElementAction, f, viewObject, prefixIdRow + f,
			selectedRowStyle, rowStyle, 
			onSelectCollectionElementMetaAction, tabObject);
%>  
<tr id="<%=prefixIdRow%><%=f%>" >
<td class="phone-list-element-check"">
	<INPUT type="<%=singleSelection?"RADIO":"CHECKBOX"%>" name="<xava:id name='xava_selected'/>" value="<%=prefix + "selected"%>:<%=f%>" <%=checked%> <%=actionOnClick%> />
</td>
<td class="phone-list-element">
	<xava:link action='<%=action%>' argv='<%="row=" + (f++) + actionArgv%>'>	
		<div class="phone-list-element-header"><%=el.getHeader()%></div>
		<div class="phone-list-element-subheader"><%=el.getSubheader()%></div>
		<div class="phone-list-element-content"><%=el.getContent()%></div>
	</xava:link>
</td>
<td class="phone-list-element-pointer"><img src="<%=request.getContextPath()%>/<%=style.getImagesFolder()%>/chevron.png"/></td>
</tr>
<%
}

if (loadMore) {
%>
<tr id="load_more_elements" class="more-elements">
<td colspan="3" class="phone-list-element">
	<xava:link action='List.setPageRowCount' argv='<%="rowCount=" + (limit + ROW_COUNT)%>'>
		<div onclick="$('#loading_more_elements').show(); $('#load_more_elements').hide();">
			<span >
				<xava:message key="load_more"/>...
			</span>	
		</div>	
	</xava:link>
</td>
</tr>
<tr id="loading_more_elements" class="more-elements" style="display:none;">
<td colspan="2" class="phone-list-element"><xava:message key="loading"/>...</td>
<td class="phone-list-element-pointer"><img src="<%=request.getContextPath()%>/naviox/images/loading.gif"/></td>
</tr>	
<%
}
%>
</table>