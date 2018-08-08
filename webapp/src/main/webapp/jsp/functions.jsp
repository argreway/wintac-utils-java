<%@ page import="com.sentryfire.WebUtilities" %><%--
  Created by IntelliJ IDEA.
  User: argreway
  Date: 8/8/18
  Time: 2:27 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    WebUtilities webUtilities = new WebUtilities();
    if (request.getParameter("activity-log") != null)
    {
        webUtilities.getActivityLog();
    }
    else if (request.getParameter("") != null)
    {

    }
%>
