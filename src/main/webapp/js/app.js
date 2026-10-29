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
