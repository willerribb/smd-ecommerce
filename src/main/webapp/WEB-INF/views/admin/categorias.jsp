<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="raiz" value="${pageContext.request.contextPath}"/>
<c:set var="titulo" value="Categorias"/>
<c:set var="menuAtivo" value="categorias"/>
<%--
    categoriaEditada vem do servlet em dois casos:
      ?editar=ID              a categoria do banco (id > 0)
      erro ao salvar          o que foi digitado (id 0 se era uma categoria nova)
    Por isso "editando" olha o id, e não só se o atributo existe.
--%>
<c:set var="editando" value="${not empty categoriaEditada and categoriaEditada.id != 0}"/>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <%@ include file="/WEB-INF/views/fragmentos/head.jspf" %>
</head>
<body class="bg-body-tertiary">

<%@ include file="/WEB-INF/views/fragmentos/navbar.jspf" %>

<main class="container py-5">
    <div class="row justify-content-center">
        <div class="col-lg-8">
            <nav aria-label="Trilha de navegação">
                <ol class="breadcrumb">
                    <li class="breadcrumb-item"><a href="${raiz}/minha-conta">Minha conta</a></li>
                    <li class="breadcrumb-item active" aria-current="page">Categorias</li>
                </ol>
            </nav>

            <h1 class="h3 mb-4">Categorias</h1>

            <%@ include file="/WEB-INF/views/fragmentos/mensagens.jspf" %>

            <%-- ===================== Formulário: nova ou edição ===================== --%>
            <section class="card mb-4" aria-labelledby="tituloFormCategoria">
                <div class="card-body p-4">
                    <h2 id="tituloFormCategoria" class="h5 mb-3">
                        ${editando ? 'Editar categoria' : 'Nova categoria'}
                    </h2>

                    <c:if test="${not empty erro}">
                        <div class="alert alert-danger" role="alert"><c:out value="${erro}"/></div>
                    </c:if>

                    <form action="${raiz}/admin/categorias" method="post" class="needs-validation" novalidate>
                        <input type="hidden" name="acao" value="salvar">
                        <input type="hidden" name="id" value="${editando ? categoriaEditada.id : 0}">

                        <div class="mb-3">
                            <label for="nomeCategoria" class="form-label">Nome</label>
                            <input type="text" class="form-control" id="nomeCategoria" name="nome"
                                   value="<c:out value='${categoriaEditada.nome}'/>"
                                   maxlength="100" required>
                            <div class="invalid-feedback">Informe o nome da categoria.</div>
                        </div>

                        <div class="d-flex gap-2">
                            <button type="submit" class="btn btn-primary">
                                ${editando ? 'Salvar alterações' : 'Criar categoria'}
                            </button>
                            <c:if test="${editando}">
                                <a href="${raiz}/admin/categorias" class="btn btn-outline-secondary">Cancelar</a>
                            </c:if>
                        </div>
                    </form>
                </div>
            </section>

            <%-- ============================ Lista ============================ --%>
            <section class="card" aria-labelledby="tituloListaCategorias">
                <div class="card-body p-4">
                    <h2 id="tituloListaCategorias" class="h5 mb-3">Categorias cadastradas</h2>

                    <c:choose>
                        <c:when test="${empty categorias}">
                            <p class="text-body-secondary mb-0">Nenhuma categoria cadastrada ainda.</p>
                        </c:when>

                        <c:otherwise>
                            <div class="table-responsive">
                                <table class="table align-middle mb-0">
                                    <thead>
                                    <tr>
                                        <th scope="col">Nome</th>
                                        <th scope="col" class="text-end">Ações</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach var="categoria" items="${categorias}">
                                        <tr>
                                            <td><c:out value="${categoria.nome}"/></td>
                                            <td class="text-end">
                                                <div class="d-inline-flex gap-2">
                                                    <%-- O texto escondido diz ao leitor de tela QUAL categoria
                                                         o botão edita; sem ele, ouviria "Editar, Editar, Editar..." --%>
                                                    <a href="${raiz}/admin/categorias?editar=${categoria.id}"
                                                       class="btn btn-sm btn-outline-secondary">
                                                        Editar<span class="visually-hidden"> <c:out value="${categoria.nome}"/></span>
                                                    </a>

                                                    <%-- Exclusão por POST (nunca por link), com confirmação no app.js --%>
                                                    <form action="${raiz}/admin/categorias" method="post" class="m-0"
                                                          data-confirmar="Excluir a categoria <c:out value='${categoria.nome}'/>?">
                                                        <input type="hidden" name="acao" value="excluir">
                                                        <input type="hidden" name="id" value="${categoria.id}">
                                                        <button type="submit" class="btn btn-sm btn-outline-danger">
                                                            Excluir<span class="visually-hidden"> <c:out value="${categoria.nome}"/></span>
                                                        </button>
                                                    </form>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </section>
        </div>
    </div>
</main>

<%@ include file="/WEB-INF/views/fragmentos/scripts.jspf" %>
</body>
</html>
