<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="raiz" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Entrar</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-body-tertiary">

<%@ include file="/WEB-INF/views/fragmentos/navbar.jspf" %>

<main class="container py-5" style="max-width: 28rem;">
    <h1 class="h3 mb-4">Entrar</h1>

    <%@ include file="/WEB-INF/views/fragmentos/mensagens.jspf" %>

    <form action="${raiz}/login" method="post">
        <div class="mb-3">
            <label for="login" class="form-label">Login</label>
            <%-- Após um erro, o login digitado volta preenchido e o cursor vai para a senha --%>
            <input type="text" class="form-control" id="login" name="login"
                   value="<c:out value='${param.login}'/>"
                   autocomplete="username" required ${empty param.login ? 'autofocus' : ''}>
        </div>
        <div class="mb-4">
            <label for="senha" class="form-label">Senha</label>
            <input type="password" class="form-control" id="senha" name="senha"
                   autocomplete="current-password" required ${empty param.login ? '' : 'autofocus'}>
        </div>
        <button type="submit" class="btn btn-primary w-100">Entrar</button>
    </form>

    <p class="text-center mt-4 mb-0">
        Ainda não tem conta? <a href="${raiz}/cadastro">Criar conta</a>
    </p>
</main>

</body>
</html>
