<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="raiz" value="${pageContext.request.contextPath}"/>
<c:set var="titulo" value="Produtos"/>
<c:set var="menuAtivo" value="produtos"/>
<fmt:setLocale value="pt_BR"/>
<%--
    produtoEditado vem do servlet quando um produto existente está sendo editado
    (?editar=ID, ou erro ao salvar uma alteração); num produto novo, fica vazio.

    Depois de um erro, os campos mostram o que foi digitado (${param}), e não o
    que está no banco: um preço como "abc" não caberia num Produto.
--%>
<c:set var="editando" value="${not empty produtoEditado}"/>
<c:set var="comErro" value="${not empty erro}"/>
<c:set var="valorDescricao" value="${comErro ? param.descricao : produtoEditado.descricao}"/>
<%-- Na edição, o preço aparece como se digita no Brasil: 1234,56 (sem o ponto de milhar) --%>
<c:if test="${editando}">
    <fmt:formatNumber var="precoDoBanco" value="${produtoEditado.preco}"
                      minFractionDigits="2" maxFractionDigits="2" groupingUsed="false"/>
</c:if>
<c:set var="valorPreco" value="${comErro ? param.preco : precoDoBanco}"/>
<c:set var="valorQuantidade" value="${comErro ? param.quantidade : produtoEditado.quantidade}"/>
<c:set var="valorCategoria" value="${comErro ? param.categoria : produtoEditado.categoria.id}"/>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <%@ include file="/WEB-INF/views/fragmentos/head.jspf" %>
</head>
<body class="bg-body-tertiary">

<%@ include file="/WEB-INF/views/fragmentos/navbar.jspf" %>

<main class="container py-5">
    <div class="row justify-content-center">
        <div class="col-xl-10">
            <nav aria-label="Trilha de navegação">
                <ol class="breadcrumb">
                    <li class="breadcrumb-item"><a href="${raiz}/minha-conta">Minha conta</a></li>
                    <li class="breadcrumb-item active" aria-current="page">Produtos</li>
                </ol>
            </nav>

            <h1 class="h3 mb-4">Produtos</h1>

            <%@ include file="/WEB-INF/views/fragmentos/mensagens.jspf" %>

            <%-- ===================== Formulário: novo ou edição ===================== --%>
            <section class="card mb-4" aria-labelledby="tituloFormProduto">
                <div class="card-body p-4">
                    <h2 id="tituloFormProduto" class="h5 mb-3">
                        ${editando ? 'Editar produto' : 'Novo produto'}
                    </h2>

                    <c:if test="${comErro}">
                        <div class="alert alert-danger" role="alert"><c:out value="${erro}"/></div>
                    </c:if>

                    <c:choose>
                        <c:when test="${empty categorias}">
                            <p class="mb-0">
                                Todo produto pertence a uma categoria.
                                <a href="${raiz}/admin/categorias">Crie a primeira categoria</a> para cadastrar produtos.
                            </p>
                        </c:when>

                        <c:otherwise>
                            <%-- multipart/form-data: o formato que permite enviar arquivos --%>
                            <form action="${raiz}/admin/produtos" method="post" enctype="multipart/form-data"
                                  class="needs-validation" novalidate>
                                <input type="hidden" name="acao" value="salvar">
                                <input type="hidden" name="id" value="${editando ? produtoEditado.id : 0}">

                                <div class="mb-3">
                                    <label for="descricaoProduto" class="form-label">Descrição</label>
                                    <input type="text" class="form-control" id="descricaoProduto" name="descricao"
                                           value="<c:out value='${valorDescricao}'/>" maxlength="255" required>
                                    <div class="invalid-feedback">Informe a descrição do produto.</div>
                                </div>

                                <div class="row g-3 mb-3">
                                    <div class="col-md-6">
                                        <label for="categoriaProduto" class="form-label">Categoria</label>
                                        <select class="form-select" id="categoriaProduto" name="categoria" required>
                                            <option value="">Escolha...</option>
                                            <c:forEach var="categoria" items="${categorias}">
                                                <option value="${categoria.id}" ${categoria.id == valorCategoria ? 'selected' : ''}>
                                                    <c:out value="${categoria.nome}"/>
                                                </option>
                                            </c:forEach>
                                        </select>
                                        <div class="invalid-feedback">Escolha uma categoria.</div>
                                    </div>
                                    <div class="col-sm-6 col-md-3">
                                        <label for="precoProduto" class="form-label">Preço</label>
                                        <div class="input-group has-validation">
                                            <span class="input-group-text">R$</span>
                                            <%--
                                                type="text", e não "number": no campo numérico o navegador controla
                                                o formato pelo idioma do sistema e recusava a vírgula. Aqui vale
                                                59,90 ou 59.90 ou 1.234,56; o servidor (lerPreco) entende os três.
                                                inputmode="decimal" abre o teclado numérico no celular.
                                                O pattern repete a regra do servidor para o aviso aparecer antes do envio.
                                            --%>
                                            <input type="text" class="form-control" id="precoProduto" name="preco"
                                                   value="<c:out value='${valorPreco}'/>" placeholder="0,00"
                                                   inputmode="decimal" maxlength="14" autocomplete="off"
                                                   pattern="\d{1,3}(\.\d{3})*(,\d{1,2})?|\d+([.,]\d{1,2})?" required>
                                            <div class="invalid-feedback">Informe um preço como 59,90, com até duas casas decimais.</div>
                                        </div>
                                    </div>
                                    <div class="col-sm-6 col-md-3">
                                        <label for="quantidadeProduto" class="form-label">Estoque</label>
                                        <input type="number" class="form-control" id="quantidadeProduto" name="quantidade"
                                               value="<c:out value='${valorQuantidade}'/>"
                                               min="0" step="1" inputmode="numeric" required>
                                        <div class="invalid-feedback">Informe a quantidade, zero ou mais.</div>
                                    </div>
                                </div>

                                <div class="mb-4">
                                    <label for="fotoProduto" class="form-label">Foto</label>
                                    <c:if test="${editando and not empty produtoEditado.foto}">
                                        <div class="d-flex align-items-center gap-3 mb-2">
                                            <img src="${raiz}/<c:out value='${produtoEditado.foto}'/>" alt="Foto atual"
                                                 class="miniatura miniatura-grande">
                                            <div class="form-check">
                                                <input class="form-check-input" type="checkbox" id="removerFoto"
                                                       name="removerFoto" value="sim"
                                                       ${comErro and not empty param.removerFoto ? 'checked' : ''}>
                                                <label class="form-check-label" for="removerFoto">Remover a foto atual</label>
                                            </div>
                                        </div>
                                    </c:if>
                                    <%--
                                        data-tamanho-maximo: o app.js avisa antes de enviar (2 MB = 2097152 bytes).
                                        data-limpar: o botão desfaz a escolha do arquivo (seção 8 do app.js);
                                        fica desativado enquanto nenhum arquivo foi escolhido.
                                    --%>
                                    <div class="input-group has-validation">
                                        <input type="file" class="form-control" id="fotoProduto" name="foto"
                                               accept="image/jpeg,image/png,image/webp" data-tamanho-maximo="2097152"
                                               aria-describedby="ajudaFoto">
                                        <button type="button" class="btn btn-outline-secondary" data-limpar="fotoProduto" disabled>
                                            Limpar<span class="visually-hidden"> a foto escolhida</span>
                                        </button>
                                        <div class="invalid-feedback">A foto deve ter até 2 MB.</div>
                                    </div>
                                    <div id="ajudaFoto" class="form-text">
                                        Opcional. JPG, PNG ou WebP, até 2 MB.
                                        <c:if test="${editando and not empty produtoEditado.foto}">Uma foto nova substitui a atual.</c:if>
                                        <c:if test="${comErro}">Se você tinha escolhido uma foto, escolha de novo.</c:if>
                                    </div>
                                </div>

                                <div class="d-flex gap-2">
                                    <button type="submit" class="btn btn-primary">
                                        ${editando ? 'Salvar alterações' : 'Criar produto'}
                                    </button>
                                    <c:if test="${editando}">
                                        <a href="${raiz}/admin/produtos" class="btn btn-outline-secondary">Cancelar</a>
                                    </c:if>
                                </div>
                            </form>
                        </c:otherwise>
                    </c:choose>
                </div>
            </section>

            <%-- ============================ Ativos ============================ --%>
            <section class="card mb-4" aria-labelledby="tituloAtivos">
                <div class="card-body p-4">
                    <h2 id="tituloAtivos" class="h5 mb-3">Ativos</h2>

                    <c:choose>
                        <c:when test="${empty produtosAtivos}">
                            <p class="text-body-secondary mb-0">
                                ${empty produtosInativos ? 'Nenhum produto cadastrado ainda.' : 'Nenhum produto ativo no momento.'}
                            </p>
                        </c:when>

                        <c:otherwise>
                            <div class="table-responsive">
                                <table class="table align-middle mb-0">
                                    <thead>
                                    <tr>
                                        <th scope="col">Produto</th>
                                        <th scope="col">Categoria</th>
                                        <th scope="col" class="text-end">Preço</th>
                                        <th scope="col" class="text-end">Estoque</th>
                                        <th scope="col" class="text-end">Ações</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach var="produto" items="${produtosAtivos}">
                                        <tr>
                                            <td>
                                                <div class="d-flex align-items-center gap-3">
                                                    <c:choose>
                                                        <c:when test="${not empty produto.foto}">
                                                            <img src="${raiz}/<c:out value='${produto.foto}'/>" alt=""
                                                                 class="miniatura" loading="lazy">
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="miniatura miniatura-vazia" aria-hidden="true">
                                                                <i class="fa fa-image"></i>
                                                            </span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                    <c:out value="${produto.descricao}"/>
                                                </div>
                                            </td>
                                            <td><c:out value="${produto.categoria.nome}"/></td>
                                            <td class="text-end text-nowrap">
                                                <fmt:formatNumber value="${produto.preco}" type="currency"/>
                                            </td>
                                            <td class="text-end">
                                                ${produto.quantidade}
                                                <c:if test="${produto.quantidade == 0}">
                                                    <div class="small text-body-secondary text-nowrap">fora da vitrine</div>
                                                </c:if>
                                            </td>
                                            <td class="text-end">
                                                <div class="d-inline-flex gap-2">
                                                    <a href="${raiz}/admin/produtos?editar=${produto.id}"
                                                       class="btn btn-sm btn-outline-secondary">
                                                        Editar<span class="visually-hidden"> <c:out value="${produto.descricao}"/></span>
                                                    </a>
                                                    <form action="${raiz}/admin/produtos" method="post" class="m-0"
                                                          data-confirmar="Excluir o produto <c:out value='${produto.descricao}'/>? Se ele já foi vendido, só sai da loja.">
                                                        <input type="hidden" name="acao" value="excluir">
                                                        <input type="hidden" name="id" value="${produto.id}">
                                                        <button type="submit" class="btn btn-sm btn-outline-danger">
                                                            Excluir<span class="visually-hidden"> <c:out value="${produto.descricao}"/></span>
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

            <%-- =========================== Inativos =========================== --%>
            <c:if test="${not empty produtosInativos}">
                <section class="card" aria-labelledby="tituloInativos">
                    <div class="card-body p-4">
                        <h2 id="tituloInativos" class="h5 mb-2">Inativos</h2>
                        <p class="text-body-secondary mb-3">
                            Produtos que já foram vendidos não podem ser apagados, para não mudar o histórico
                            de compras. Eles saem da loja e ficam aqui, de onde podem voltar.
                        </p>

                        <div class="table-responsive">
                            <table class="table align-middle mb-0">
                                <thead>
                                <tr>
                                    <th scope="col">Produto</th>
                                    <th scope="col">Categoria</th>
                                    <th scope="col" class="text-end">Ações</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="produto" items="${produtosInativos}">
                                    <tr>
                                        <td><c:out value="${produto.descricao}"/></td>
                                        <td><c:out value="${produto.categoria.nome}"/></td>
                                        <td class="text-end">
                                            <div class="d-inline-flex gap-2">
                                                <a href="${raiz}/admin/produtos?editar=${produto.id}"
                                                   class="btn btn-sm btn-outline-secondary">
                                                    Editar<span class="visually-hidden"> <c:out value="${produto.descricao}"/></span>
                                                </a>
                                                <form action="${raiz}/admin/produtos" method="post" class="m-0">
                                                    <input type="hidden" name="acao" value="reativar">
                                                    <input type="hidden" name="id" value="${produto.id}">
                                                    <button type="submit" class="btn btn-sm btn-outline-secondary">
                                                        Reativar<span class="visually-hidden"> <c:out value="${produto.descricao}"/></span>
                                                    </button>
                                                </form>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </section>
            </c:if>
        </div>
    </div>
</main>

<%@ include file="/WEB-INF/views/fragmentos/scripts.jspf" %>
</body>
</html>
