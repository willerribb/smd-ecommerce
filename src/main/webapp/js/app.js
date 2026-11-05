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

// 3. Modais: ao abrir, o cursor vai para o primeiro campo vazio
document.querySelectorAll(".modal").forEach(modal => {
    modal.addEventListener("shown.bs.modal", () => {
        const campos = [...modal.querySelectorAll("input:not([type=hidden])")];
        const campo = campos.find(c => !c.value) || campos[0];
        if (campo) campo.focus();
    });
});

// 4. Modal que o servidor pediu para abrir (por exemplo, após um erro de login)
const modalPedido = document.querySelector(".modal[data-abrir-ao-carregar]");
if (modalPedido) {
    bootstrap.Modal.getOrCreateInstance(modalPedido).show();
}

// 5. Painéis alternáveis (login e cadastro na mesma página, sem mudar o endereço).
//    <button data-alternar="idDoPainel"> mostra o painel indicado e esconde os
//    outros elementos [data-painel] que estão no mesmo nível dele.
document.querySelectorAll("[data-alternar]").forEach(botao => {
    botao.addEventListener("click", () => {
        const destino = document.getElementById(botao.dataset.alternar);
        destino.parentElement.querySelectorAll(":scope > [data-painel]").forEach(painel => {
            painel.hidden = painel !== destino;
        });
        // leva o cursor ao primeiro campo do painel aberto (ajuda quem usa teclado e leitor de tela)
        const campo = destino.querySelector("input:not([type=hidden])");
        if (campo) campo.focus();
    });
});

// 6. Mostrar e ocultar senha: <button data-mostrar-senha="idDoCampo" aria-pressed="false">.
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