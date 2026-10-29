<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="raiz" value="${pageContext.request.contextPath}"/>
<c:set var="titulo" value="Início"/>
<c:set var="menuAtivo" value="home"/>
<fmt:setLocale value="pt_BR"/>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <%@ include file="/WEB-INF/views/fragmentos/head.jspf" %>
</head>
<body>

<%@ include file="/WEB-INF/views/fragmentos/navbar.jspf" %>

<header class="bg-body-tertiary border-bottom py-5 text-center">
    <div class="container py-4">
        <h1 class="display-5 fw-bold">Seja bem-vindo ao IUVe</h1>
    </div>
</header>

<main class="container py-5">
    <%@ include file="/WEB-INF/views/fragmentos/mensagens.jspf" %>

    <h2 class="h4 mb-4">Produtos</h2>

    <c:choose>
        <c:when test="${empty produtos}">
            <p class="text-center text-body-secondary py-5 mb-0">Nada para ver aqui, ainda :P</p>
        </c:when>

        <c:otherwise>
        </c:otherwise>
    </c:choose>
</main>

<%@ include file="/WEB-INF/views/fragmentos/scripts.jspf" %>
</body>
</html>
