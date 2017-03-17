<%@page import="org.openxava.util.XavaPreferences"%>

<%if (view.isEditable() || 
		!(!view.isEditable() && !XavaPreferences.getInstance().isShowIconForViewReadOnly())
	) { %>
	<i class='<%=style.getRequiredIcon()%> <%=p.isRequired()?"mdi mdi-marker-check":""%>'></i>
<%} %>

<span id="<xava:id name='<%="error_image_" + p.getQualifiedName()%>'/>">
	<i class='<%=style.getErrorIcon()%> mdi mdi-alert-circle' <%=errors.memberHas(p)?"":"style='visibility:hidden;'"%>></i>
</span>

