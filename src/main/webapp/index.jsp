<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <%@ include file="/WEB-INF/views/fragmentos/head.jspf" %>
  </head>
  <body>
      <%@ include file="/WEB-INF/views/fragmentos/navbar.jspf" %>
      <main class="container">
          <%@ include file="/WEB-INF/views/fragmentos/mensagens.jspf" %>
          ... conteúdo da página ...
      </main>
      <%@ include file="/WEB-INF/views/fragmentos/scripts.jspf" %>
  </body>
</html>
