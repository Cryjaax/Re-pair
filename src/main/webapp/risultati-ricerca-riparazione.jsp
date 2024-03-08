<%@ page import="pojo.Riparazione" %>
<%@ page import="java.util.ArrayList" %>
<%--
  Created by IntelliJ IDEA.
  User: Giovanni Liguori
  Date: 07/03/24
  Time: 11:42
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
      <%
          char lettera = 'i';
          final ArrayList<Riparazione> listaRiparazioni = (ArrayList<Riparazione>) request.getServletContext().getAttribute("listaRiparazioni");
		  final String ricerca = request.getParameter("cerca-riparazione");
		  if (listaRiparazioni != null)
		  {
			  if (listaRiparazioni.size() == 1)
				  lettera = 'e';
            %><title><%=listaRiparazioni.size()%> Riparazion<%=lettera%></title>
          <%}
      %>
  </head>
  <body>
    <%
        for (Riparazione r : listaRiparazioni)
		{
			if (r.getId() == Integer.parseInt(ricerca))
            {
        %>
        <div>
            <%=r.getId()%> - <%=r.getMarca()%> <%=r.getModello()%>
        </div>
        <%}
		}
    %>
  </body>
</html>
