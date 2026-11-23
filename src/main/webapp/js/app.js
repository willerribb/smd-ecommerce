// Comportamentos comuns a todas as páginas do IUVe.
// Cada seção procura os elementos pelos atributos data-* (ou classes) e não faz
// nada se a página não os tiver, então o mesmo arquivo serve para todas.

// Leva o cursor ao primeiro campo vazio de um elemento (ou ao primeiro, se todos
// estiverem preenchidos). Ajuda quem usa teclado e leitor de tela.
function focarPrimeiroCampo(elemento) {
    const campos = [...elemento.querySelectorAll("input:not([type=hidden])")];
    const campo = campos.find(c => !c.value) || campos[0];
    if (campo) campo.focus();
}

// 1. Validação no navegador para formulários com a classe "needs-validation".
document.querySelectorAll("form.needs-validation").forEach(form => {
    form.addEventListener("submit", evento => {
        form.querySelectorAll("[data-confirma]").forEach(conferirConfirmacao);
        if (!form.checkValidity()) {
            evento.preventDefault();
            evento.stopPropagation();
        }
        form.classList.add("was-validated");
    });
});

// 2. Campos de confirmação: <input data-confirma="idDoCampoOriginal"> ficam inválidos enquanto o valor for diferente do original.
function conferirConfirmacao(campo) {
    const original = document.getElementById(campo.dataset.confirma);
    campo.setCustomValidity(campo.value === original.value ? "" : "As senhas não são iguais.");
}
document.querySelectorAll("[data-confirma]").forEach(campo => {
    const original = document.getElementById(campo.dataset.confirma);
    campo.addEventListener("input", () => conferirConfirmacao(campo));
    original.addEventListener("input", () => conferirConfirmacao(campo));
});

// 3. Foco ao abrir o dropdown "Entrar" da navbar e os modais (como o de excluir conta).
//    O Bootstrap dispara o evento no botão que abre o dropdown, e ele "sobe" até a
//    div .dropdown-entrar, onde é ouvido aqui.
document.querySelectorAll(".dropdown-entrar").forEach(dropdown => {
    dropdown.addEventListener("shown.bs.dropdown", () => {
        focarPrimeiroCampo(dropdown.querySelector(".dropdown-menu"));
    });
});
document.querySelectorAll(".modal").forEach(modal => {
    modal.addEventListener("shown.bs.modal", () => focarPrimeiroCampo(modal));
});

// 4. Painéis alternáveis (login e cadastro na mesma página, sem mudar o endereço).
//    <button data-alternar="idDoPainel"> mostra o painel indicado e esconde os
//    outros elementos [data-painel] que estão no mesmo nível dele.
document.querySelectorAll("[data-alternar]").forEach(botao => {
    botao.addEventListener("click", () => {
        const destino = document.getElementById(botao.dataset.alternar);
        destino.parentElement.querySelectorAll(":scope > [data-painel]").forEach(painel => {
            painel.hidden = painel !== destino;
        });
        focarPrimeiroCampo(destino);
    });
});

// 5. Mostrar e ocultar senha: <button data-mostrar-senha="idDoCampo" aria-pressed="false">.
//    O aria-pressed informa o estado ao leitor de tela, e o CSS usa o mesmo
//    atributo para trocar o ícone (olho aberto ou riscado).
document.querySelectorAll("[data-mostrar-senha]").forEach(botao => {
    const campo = document.getElementById(botao.dataset.mostrarSenha);
    botao.addEventListener("click", () => {
        const mostrar = campo.type === "password";
        campo.type = mostrar ? "text" : "password";
        botao.setAttribute("aria-pressed", String(mostrar));
    });
});

// 6. Confirmação antes de enviar: <form data-confirmar="Pergunta?">.
//    Usado nas exclusões do administrador; se a pessoa cancelar, o envio é barrado.
document.querySelectorAll("form[data-confirmar]").forEach(form => {
    form.addEventListener("submit", evento => {
        if (!window.confirm(form.dataset.confirmar)) {
            evento.preventDefault();
        }
    });
});
