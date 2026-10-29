<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="raiz" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Criar conta</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-body-tertiary">

<%@ include file="/WEB-INF/views/fragmentos/navbar.jspf" %>

<main class="container py-5" style="max-width: 32rem;">
    <h1 class="h3 mb-1">Criar conta</h1>
    <p class="text-body-secondary mb-4">Cadastre-se para fazer suas compras.</p>

    <%@ include file="/WEB-INF/views/fragmentos/mensagens.jspf" %>

    <%-- Após um erro, os campos voltam preenchidos com o que foi digitado (exceto as senhas) --%>
    <form id="formCadastro" action="${raiz}/cadastro" method="post" novalidate>
        <div class="mb-3">
            <label for="nome" class="form-label">Nome completo</label>
            <input type="text" class="form-control" id="nome" name="nome"
                   value="<c:out value='${param.nome}'/>"
                   maxlength="150" autocomplete="name" required>
            <div class="invalid-feedback">Informe seu nome.</div>
        </div>

        <div class="mb-3">
            <label for="email" class="form-label">E-mail</label>
            <input type="email" class="form-control" id="email" name="email"
                   value="<c:out value='${param.email}'/>"
                   maxlength="150" autocomplete="email" required>
            <div class="invalid-feedback">Informe um e-mail válido.</div>
        </div>

        <div class="mb-3">
            <label for="endereco" class="form-label">Endereço de entrega</label>
            <input type="text" class="form-control" id="endereco" name="endereco"
                   value="<c:out value='${param.endereco}'/>"
                   maxlength="255" autocomplete="street-address" required>
            <div class="invalid-feedback">Informe seu endereço.</div>
        </div>

        <div class="mb-3">
            <label for="login" class="form-label">Login</label>
            <input type="text" class="form-control" id="login" name="login"
                   value="<c:out value='${param.login}'/>"
                   minlength="3" maxlength="50" pattern="[A-Za-z0-9._\-]{3,50}"
                   autocomplete="username" aria-describedby="ajudaLogin" required>
            <div id="ajudaLogin" class="form-text">
                De 3 a 50 caracteres: letras, números, ponto, hífen ou sublinhado.
            </div>
            <div class="invalid-feedback">Use de 3 a 50 caracteres, sem espaços ou acentos.</div>
        </div>

        <div class="row g-3 mb-4">
            <div class="col-sm-6">
                <label for="senha" class="form-label">Senha</label>
                <input type="password" class="form-control" id="senha" name="senha"
                       minlength="6" autocomplete="new-password" required>
                <div class="invalid-feedback">Use pelo menos 6 caracteres.</div>
            </div>
            <div class="col-sm-6">
                <label for="confirmacaoSenha" class="form-label">Confirme a senha</label>
                <input type="password" class="form-control" id="confirmacaoSenha" name="confirmacaoSenha"
                       autocomplete="new-password" required>
                <div class="invalid-feedback">As senhas não são iguais.</div>
            </div>
        </div>

        <button type="submit" class="btn btn-primary w-100">Criar conta</button>
    </form>

    <p class="text-center mt-4 mb-0">
        Já tem conta? <a href="${raiz}/login">Entrar</a>
    </p>
</main>

<script>
    // Validação no navegador (o servidor valida tudo de novo)
    const form = document.getElementById("formCadastro");
    const senha = document.getElementById("senha");
    const confirmacao = document.getElementById("confirmacaoSenha");

    function conferirSenhas() {
        confirmacao.setCustomValidity(confirmacao.value === senha.value ? "" : "diferentes");
    }
    senha.addEventListener("input", conferirSenhas);
    confirmacao.addEventListener("input", conferirSenhas);

    form.addEventListener("submit", evento => {
        conferirSenhas();
        if (!form.checkValidity()) {
            evento.preventDefault();
            form.classList.add("was-validated");
        }
    });
</script>
</body>
</html>
