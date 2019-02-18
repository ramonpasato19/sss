<%-- 
Put here HTML to include just after the organization name.
--%>
<div id="accounting_date">
	<% String accountingDate = modules.getAccountingDate(); %>
	Fecha: <%=accountingDate%>
	<img id="logo_organization" src="<%=request.getContextPath()%>/images/<%=modules.getLogoName(request)%>"/>
</div>