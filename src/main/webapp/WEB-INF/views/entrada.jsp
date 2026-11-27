<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="raiz" value="${pageContext.request.contextPath}"/>
<c:set var="titulo" value="Entrar"/>
<%-- the servlet (or the filter) leaves in the session which panel to open, a message and the form values (Sessao.abrirPainel) --%>
<c:set var="painelPedido" value="${sessionScope.painel}"/>
<c:set var="valores" value="${sessionScope.valoresPainel}"/>
<c:set var="abrirCadastro" value="${painelPedido == 'cadastro'}"/>
<c:set var="destinoLogin" value="${valores.destino}"/>
<c:set var="valoresLogin" value="${painelPedido == 'login' ? valores : null}"/>
<c:set var="valoresCadastro" value="${abrirCadastro ? valores : null}"/>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <%@ include file="/WEB-INF/views/fragmentos/head.jspf" %>
</head>
<body>

<div class="row g-0 min-vh-100">
    <!-- side hero image (decorativa, por isso alt vazio); some no celular -->
    <div class="col-lg-6 d-none d-lg-block vh-100 sticky-lg-top p-2">
        <img src="${raiz}/img/hero-image.png" alt="" class="w-100 h-100 object-fit-cover rounded-4">
    </div>

    <!-- login and signup sections -->
    <main class="col-lg-6 d-flex align-items-center justify-content-center px-4 py-5">
        <div class="acesso w-100">
            <header class="mb-4">
                <img src="${raiz}/img/logo.png" alt="IUVe" width="48" height="48" class="mb-3">
                <h1 class="h2 fw-bold mb-1">Boas-vindas ao IUVe</h1>
                <p>Sua plataforma de compras online.</p>
            </header>

            <%@ include file="/WEB-INF/views/fragmentos/mensagens.jspf" %>

            <!-- login panel -->
            <section id="loginPanel" data-painel aria-labelledby="tituloLogin" ${abrirCadastro ? 'hidden' : ''}>
                <h2 id="tituloLogin" class="h5 mb-3">Entrar</h2>

                <c:if test="${painelPedido == 'login' and not empty sessionScope.mensagemPainel}">
                    <div class="alert alert-${sessionScope.tipoMensagemPainel}" role="alert">
                        <c:out value="${sessionScope.mensagemPainel}"/>
                    </div>
                </c:if>

                <%@ include file="/WEB-INF/views/fragmentos/form-login.jspf" %>

                <p class="mt-4 mb-0">
                    Ainda não criou uma conta?
                    <button type="button" class="btn btn-link p-0 align-baseline" data-alternar="signupPanel">Criar conta</button>
                </p>
            </section>

            <!-- signup panel -->
            <section id="signupPanel" data-painel aria-labelledby="tituloSignup" ${abrirCadastro ? '' : 'hidden'}>
                <h2 id="tituloSignup" class="h5 mb-3">Criar conta</h2>

                <c:if test="${abrirCadastro and not empty sessionScope.mensagemPainel}">
                    <div class="alert alert-${sessionScope.tipoMensagemPainel}" role="alert">
                        <c:out value="${sessionScope.mensagemPainel}"/>
                    </div>
                </c:if>

                <form action="${raiz}/cadastro" method="post" class="needs-validation" novalidate>
                    <input type="hidden" name="destino" value="<c:out value='${valores.destino}'/>">

                    <div class="mb-3">
                        <label for="name" class="form-label">Nome completo</label>
                        <input type="text" class="form-control" id="name" name="nome"
                               value="<c:out value='${valoresCadastro.nome}'/>"
                               maxlength="150" autocomplete="name" required>
                        <div class="invalid-feedback">Informe seu nome completo.</div>
                    </div>
                    <div class="mb-3">
                        <label for="emailSignup" class="form-label">E-mail</label>
                        <input type="email" class="form-control" id="emailSignup" name="email"
                               value="<c:out value='${valoresCadastro.email}'/>"
                               maxlength="150" autocomplete="email" required>
                        <div class="invalid-feedback">Informe um e-mail válido.</div>
                    </div>
                    <div class="mb-3">
                        <label for="address" class="form-label">Endereço de entrega</label>
                        <input type="text" class="form-control" id="address" name="endereco"
                               value="<c:out value='${valoresCadastro.endereco}'/>"
                               maxlength="255" autocomplete="street-address" required>
                        <div class="invalid-feedback">Informe um endereço válido.</div>
                    </div>
                    <div class="mb-3">
                        <label for="loginSignup" class="form-label">Nome de usuário</label>
                        <input type="text" class="form-control" id="loginSignup" name="login"
                               value="<c:out value='${valoresCadastro.login}'/>"
                               minlength="6" maxlength="50" pattern="[A-Za-z0-9._\-]{6,50}"
                               autocomplete="username" autocapitalize="none" spellcheck="false"
                               aria-describedby="ajudaLoginCadastro" required>
                        <div id="ajudaLoginCadastro" class="form-text">
                            De 6 a 50 caracteres, entre letras, números, ponto, hífen ou sublinhado.
                        </div>
                        <div class="invalid-feedback">Use de 6 a 50 caracteres, sem espaços ou acentos.</div>
                    </div>
                    <div class="mb-3">
                        <label for="passwordSignup" class="form-label">Senha</label>
                        <div class="campo-senha">
                            <input type="password" class="form-control" id="passwordSignup" name="senha"
                                   minlength="6" maxlength="72" autocomplete="new-password" required>
                            <c:set var="campoSenha" value="passwordSignup"/>
                            <%@ include file="/WEB-INF/views/fragmentos/botao-senha.jspf" %>
                            <div class="invalid-feedback">A senha requer pelo menos 6 caracteres.</div>
                        </div>
                    </div>
                    <div class="mb-4">
                        <label for="confirmSignup" class="form-label">Confirme a senha</label>
                        <input type="password" class="form-control" id="confirmSignup"
                               name="confirmacaoSenha" data-confirma="passwordSignup" autocomplete="new-password" required>
                        <div class="invalid-feedback">As senhas não são iguais.</div>
                    </div>

                    <button type="submit" class="btn btn-primary w-100">Criar conta</button>
                </form>

                <p class="mt-4 mb-0">
                    Já tem conta?
                    <button type="button" class="btn btn-link p-0 align-baseline" data-alternar="loginPanel">Entrar</button>
                </p>
            </section>

            <!-- unauthenticated access link -->
            <p class="mt-3 mb-0"><a href="${raiz}/loja" class="link-secondary">Explorar os produtos sem criar conta</a></p>
        </div>
    </main>
</div>

<%@ include file="/WEB-INF/views/fragmentos/scripts.jspf" %>

<%-- clears the panel request --%>
<c:remove var="painel" scope="session"/>
<c:remove var="mensagemPainel" scope="session"/>
<c:remove var="tipoMensagemPainel" scope="session"/>
<c:remove var="valoresPainel" scope="session"/>
</body>
</html>