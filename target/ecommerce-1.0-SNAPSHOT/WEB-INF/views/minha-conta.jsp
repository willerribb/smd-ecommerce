<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="raiz" value="${pageContext.request.contextPath}"/>
<c:set var="usuario" value="${sessionScope.usuarioLogado}"/>
<%--
    "secao" e "erro" vêm do servlet quando um formulário tem erro de validação.
    Nesse caso, a seção correspondente já abre expandida, com a mensagem, e o
    formulário de dados volta com o que foi digitado em vez dos dados salvos.
--%>
<c:set var="editandoDados" value="${secao == 'dados'}"/>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Minha conta</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-body-tertiary">

<%@ include file="/WEB-INF/views/fragmentos/navbar.jspf" %>

<main class="container py-4" style="max-width: 48rem;">
    <h1 class="h3 mb-4">Olá, <c:out value="${usuario.primeiroNome}"/></h1>

    <%@ include file="/WEB-INF/views/fragmentos/mensagens.jspf" %>

    <%-- ============================ Seus dados ============================ --%>
    <section class="bg-body border rounded p-4 mb-4">
        <div class="d-flex justify-content-between align-items-start gap-3 mb-3">
            <h2 class="h5 mb-0">Seus dados</h2>
            <button class="btn btn-outline-primary btn-sm" type="button"
                    data-bs-toggle="collapse" data-bs-target="#edicaoDados"
                    aria-expanded="${editandoDados}" aria-controls="edicaoDados">
                Editar dados
            </button>
        </div>

        <dl class="row mb-0">
            <dt class="col-sm-3">Nome</dt>
            <dd class="col-sm-9"><c:out value="${usuario.nome}"/></dd>
            <dt class="col-sm-3">E-mail</dt>
            <dd class="col-sm-9"><c:out value="${usuario.email}"/></dd>
            <dt class="col-sm-3">Endereço</dt>
            <dd class="col-sm-9"><c:out value="${usuario.endereco}" default="Não informado"/></dd>
            <dt class="col-sm-3">Login</dt>
            <dd class="col-sm-9 mb-0"><c:out value="${usuario.login}"/></dd>
        </dl>

        <div class="collapse ${editandoDados ? 'show' : ''}" id="edicaoDados">
            <hr class="my-4">
            <c:if test="${editandoDados}">
                <div class="alert alert-danger" role="alert"><c:out value="${erro}"/></div>
            </c:if>
            <form action="${raiz}/minha-conta/dados" method="post" novalidate>
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
                <div class="d-flex gap-2">
                    <button type="submit" class="btn btn-primary">Salvar alterações</button>
                    <a href="${raiz}/minha-conta" class="btn btn-outline-secondary">Cancelar</a>
                </div>
            </form>
        </div>
    </section>

    <%-- ============================== Senha =============================== --%>
    <section class="bg-body border rounded p-4 mb-4">
        <div class="d-flex justify-content-between align-items-start gap-3">
            <h2 class="h5 mb-0">Senha</h2>
            <button class="btn btn-outline-primary btn-sm" type="button"
                    data-bs-toggle="collapse" data-bs-target="#edicaoSenha"
                    aria-expanded="${secao == 'senha'}" aria-controls="edicaoSenha">
                Alterar senha
            </button>
        </div>

        <div class="collapse ${secao == 'senha' ? 'show' : ''}" id="edicaoSenha">
            <hr class="my-4">
            <c:if test="${secao == 'senha'}">
                <div class="alert alert-danger" role="alert"><c:out value="${erro}"/></div>
            </c:if>
            <form id="formSenha" action="${raiz}/minha-conta/senha" method="post" novalidate>
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
                               autocomplete="new-password" required>
                        <div class="invalid-feedback">As senhas não são iguais.</div>
                    </div>
                </div>
                <button type="submit" class="btn btn-primary">Alterar senha</button>
            </form>
        </div>
    </section>

    <%--
        ======================= Opções de administrador =======================
        Próxima etapa: as opções de gerência aparecem aqui SOMENTE para admins:

        <c:if test="${usuario.admin}">
            ... gerenciar produtos, categorias, vendas ...
        </c:if>
    --%>

    <%-- =========================== Excluir conta ========================== --%>
    <section class="bg-body border border-danger-subtle rounded p-4">
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
    </section>
</main>

<%-- Confirmação da exclusão --%>
<div class="modal fade" id="modalExcluir" tabindex="-1" aria-labelledby="tituloModalExcluir" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <form class="modal-content" action="${raiz}/minha-conta/excluir" method="post" novalidate>
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

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Validação no navegador antes de enviar (o servidor valida tudo de novo)
    const novaSenha = document.getElementById("novaSenha");
    const confirmacao = document.getElementById("confirmacaoSenha");
    function conferirSenhas() {
        confirmacao.setCustomValidity(confirmacao.value === novaSenha.value ? "" : "diferentes");
    }
    novaSenha.addEventListener("input", conferirSenhas);
    confirmacao.addEventListener("input", conferirSenhas);

    document.querySelectorAll("form[novalidate]").forEach(form => {
        form.addEventListener("submit", evento => {
            if (form.id === "formSenha") conferirSenhas();
            if (!form.checkValidity()) {
                evento.preventDefault();
                form.classList.add("was-validated");
            }
        });
    });

    // Ao abrir a confirmação de exclusão, coloca o cursor no campo de senha
    document.getElementById("modalExcluir").addEventListener("shown.bs.modal", () => {
        document.getElementById("senhaExclusao").focus();
    });
</script>
</body>
</html>
