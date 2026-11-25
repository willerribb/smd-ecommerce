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
            <%-- Dois por linha no celular, três no tablet, quatro em telas grandes --%>
            <div class="row row-cols-2 row-cols-md-3 row-cols-lg-4 g-3 g-md-4">
                <c:forEach var="produto" items="${produtos}">
                    <div class="col">
                        <article class="card h-100 produto">
                            <%-- alt vazio: a descrição já está logo abaixo, e o leitor de tela a leria duas vezes --%>
                            <c:choose>
                                <c:when test="${not empty produto.foto}">
                                    <img src="${raiz}/<c:out value='${produto.foto}'/>" alt=""
                                         class="card-img-top produto-foto" loading="lazy">
                                </c:when>
                                <c:otherwise>
                                    <div class="card-img-top produto-foto produto-sem-foto" aria-hidden="true">
                                        <i class="fa fa-image"></i>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                            <div class="card-body d-flex flex-column">
                                <p class="produto-categoria small mb-1"><c:out value="${produto.categoria.nome}"/></p>
                                <h2 class="h6 mb-3"><c:out value="${produto.descricao}"/></h2>
                                <%-- mt-auto: o preço fica no pé do card, alinhado entre cards de alturas diferentes --%>
                                <p class="produto-preco mt-auto mb-0">
                                    <fmt:formatNumber value="${produto.preco}" type="currency"/>
                                </p>
                            </div>
                        </article>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</main>

<%@ include file="/WEB-INF/views/fragmentos/scripts.jspf" %>
</body>
</html>
