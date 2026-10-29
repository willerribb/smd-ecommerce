<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="raiz" value="${pageContext.request.contextPath}"/>
<c:set var="titulo" value="Editar perfil"/>
<c:set var="usuario" value="${sessionScope.usuarioLogado}"/>
<c:set var="editandoDados" value="${secao == 'dados'}"/>
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
            <h1 class="h3 mb-4">Editar perfil</h1>

            <%@ include file="/WEB-INF/views/fragmentos/mensagens.jspf" %>

            <%--
                <c:if test="${usuario.admin}">
                    ... gerenciar produtos, categorias, vendas ...
                </c:if>
            --%>

            <section class="card mb-4">
                <div class="card-body p-4">
                    <h2 class="h5 mb-3">Dados pessoais</h2>
                    <c:if test="${editandoDados}">
                        <div class="alert alert-danger" role="alert"><c:out value="${erro}"/></div>
                    </c:if>
                    <form action="${raiz}/minha-conta/dados" method="post" class="needs-validation" novalidate>
                        <div class="mb-3">
                            <label for="nome" class="form-label">Nome completo</label>
                            <input type="text" class="form-control" id="nome" name="nome"
                                   value="<c:out value='${editandoDados ? param.nome : usuario.nome}'/>"
                                   maxlength="150" autocomplete="name" required>
                            <div class="invalid-feedback">Informe seu nome.</div>
                        </div>
                        <div class="mb-3">
                            <label for="email" class="form-label">E-mail</label>
                            <input type="email" class="form-control" id="email" name="email"
                                   value="<c:out value='${editandoDados ? param.email : usuario.email}'/>"
                                   maxlength="150" autocomplete="email" required>
                            <div class="invalid-feedback">Informe um e-mail válido.</div>
                        </div>
                        <div class="mb-3">
                            <label for="endereco" class="form-label">Endereço de entrega</label>
                            <input type="text" class="form-control" id="endereco" name="endereco"
                                   value="<c:out value='${editandoDados ? param.endereco : usuario.endereco}'/>"
                                   maxlength="255" autocomplete="street-address" required>
                            <div class="invalid-feedback">Informe seu endereço.</div>
                        </div>
                        <div class="mb-4">
                            <label for="login" class="form-label">Login</label>
                            <input type="text" class="form-control" id="login" name="login"
                                   value="<c:out value='${editandoDados ? param.login : usuario.login}'/>"
                                   minlength="3" maxlength="50" pattern="[A-Za-z0-9._\-]{3,50}"
                                   autocomplete="username" required>
                            <div class="invalid-feedback">Use de 3 a 50 caracteres: letras, números, ponto, hífen ou sublinhado.</div>
                        </div>
                        <button type="submit" class="btn btn-primary">Salvar alterações</button>
                    </form>
                </div>
            </section>

            <section class="card mb-4">
                <div class="card-body p-4">
                    <h2 class="h5 mb-3">Alterar senha</h2>
                    <c:if test="${secao == 'senha'}">
                        <div class="alert alert-danger" role="alert"><c:out value="${erro}"/></div>
                    </c:if>
                    <form action="${raiz}/minha-conta/senha" method="post" class="needs-validation" novalidate>
                        <div class="mb-3">
                            <label for="senhaAtual" class="form-label">Senha atual</label>
                            <input type="password" class="form-control" id="senhaAtual" name="senhaAtual"
                                   autocomplete="current-password" required>
                            <div class="invalid-feedback">Informe sua senha atual.</div>
                        </div>
                        <div class="row g-3 mb-4">
                            <div class="col-sm-6">
                                <label for="novaSenha" class="form-label">Nova senha</label>
                                <input type="password" class="form-control" id="novaSenha" name="novaSenha"
                                       minlength="6" autocomplete="new-password" required>
                                <div class="invalid-feedback">Use pelo menos 6 caracteres.</div>
                            </div>
                            <div class="col-sm-6">
                                <label for="confirmacaoSenha" class="form-label">Confirme a nova senha</label>
                                <input type="password" class="form-control" id="confirmacaoSenha" name="confirmacaoSenha"
                                       data-confirma="novaSenha" autocomplete="new-password" required>
                                <div class="invalid-feedback">As senhas não são iguais.</div>
                            </div>
                        </div>
                        <button type="submit" class="btn btn-primary">Alterar senha</button>
                    </form>
                </div>
            </section>

            <section class="card border-danger-subtle">
                <div class="card-body p-4">
                    <h2 class="h5 mb-2">Excluir conta</h2>
                    <c:if test="${secao == 'excluir'}">
                        <div class="alert alert-danger" role="alert"><c:out value="${erro}"/></div>
                    </c:if>
                    <p class="text-body-secondary mb-3">
                        Ao excluir sua conta, você perde o acesso a ela e seus dados pessoais são apagados.
                        Para comprar de novo, será preciso criar uma conta nova.
                    </p>
                    <button type="button" class="btn btn-outline-danger"
                            data-bs-toggle="modal" data-bs-target="#modalExcluir">
                        Excluir minha conta
                    </button>
                </div>
            </section>
        </div>
    </div>
</main>

<div class="modal fade" id="modalExcluir" tabindex="-1" aria-labelledby="tituloModalExcluir" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <form class="modal-content needs-validation" action="${raiz}/minha-conta/excluir" method="post" novalidate>
            <div class="modal-header">
                <h2 class="modal-title h5" id="tituloModalExcluir">Excluir sua conta?</h2>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fechar"></button>
            </div>
            <div class="modal-body">
                <p>Essa ação não pode ser desfeita. Para confirmar, digite sua senha.</p>
                <label for="senhaExclusao" class="form-label">Senha</label>
                <input type="password" class="form-control" id="senhaExclusao" name="senha"
                       autocomplete="current-password" required>
                <div class="invalid-feedback">Digite sua senha para confirmar.</div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancelar</button>
                <button type="submit" class="btn btn-danger">Excluir conta</button>
            </div>
        </form>
    </div>
</div>

<%@ include file="/WEB-INF/views/fragmentos/scripts.jspf" %>
</body>
</html>
