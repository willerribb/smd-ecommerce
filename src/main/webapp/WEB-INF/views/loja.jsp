<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="raiz" value="${pageContext.request.contextPath}"/>
<c:set var="titulo" value="Loja"/>
<c:set var="menuAtivo" value="loja"/>
<%-- pt_BR: o preço sai como "R$ 1.234,56" no fmt:formatNumber dos cards --%>
<fmt:setLocale value="pt_BR"/>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <%@ include file="/WEB-INF/views/fragmentos/head.jspf" %>
</head>
<body class="bg-body-tertiary">

<%@ include file="/WEB-INF/views/fragmentos/navbar.jspf" %>

<main class="container py-5">
    <h1 class="h3 mb-4">Produtos</h1>

    <%@ include file="/WEB-INF/views/fragmentos/mensagens.jspf" %>

    <c:choose>
        <c:when test="${empty produtos}">
            <p class="text-body-secondary">Nenhum produto disponível no momento. Volte em breve.</p>
        </c:when>

        <c:otherwise>
            <%-- Cards da vitrine (foto, descrição, categoria e preço): próximo pacote --%>
        </c:otherwise>
    </c:choose>
</main>

<%@ include file="/WEB-INF/views/fragmentos/scripts.jspf" %>
</body>
</html>
