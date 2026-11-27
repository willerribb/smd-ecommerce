// IUVe scripts. every function checks if the page has the elements it needs,
// so the same file works for all pages

// focus on the first empty field (or the first field, if none is empty)
function focarPrimeiroCampo(elemento) {
    const campos = [...elemento.querySelectorAll("input:not([type=hidden])")];
    const campo = campos.find(c => !c.value) || campos[0];
    if (campo) campo.focus();
}

// reloads a page restored by the back button (bfcache), so it doesn't show the user logged in after logout
function recarregarAoVoltar() {
    window.addEventListener("pageshow", evento => {
        if (evento.persisted) {
            window.location.reload();
        }
    });
}

// bootstrap validation for forms with "needs-validation".
// was-validated only goes in when the browser blocks the submit
function validarFormularios() {
    document.querySelectorAll("form.needs-validation").forEach(form => {
        form.addEventListener("submit", evento => {
            form.querySelectorAll("[data-confirma]").forEach(campo => conferirConfirmacao(campo, true));
            if (form.checkValidity()) {
                form.classList.remove("was-validated");
            } else {
                evento.preventDefault();
                evento.stopPropagation();
                form.classList.add("was-validated");
            }
        });
    });
}

// password confirmation: <input data-confirma="idDoCampoOriginal">
// while the user is still typing the start of the password, no error is shown
function conferirConfirmacao(campo, final) {
    const original = document.getElementById(campo.dataset.confirma);
    const iguais = campo.value === original.value;
    const digitando = !final && original.value.startsWith(campo.value);
    campo.setCustomValidity(iguais ? "" : "As senhas não são iguais.");
    campo.classList.toggle("is-invalid", campo.value !== "" && !iguais && !digitando);
}

function conferirSenhas() {
    document.querySelectorAll("[data-confirma]").forEach(campo => {
        const original = document.getElementById(campo.dataset.confirma);
        campo.addEventListener("input", () => conferirConfirmacao(campo, false));
        campo.addEventListener("change", () => conferirConfirmacao(campo, true));
        original.addEventListener("input", () => {
            if (campo.value !== "") conferirConfirmacao(campo, true);
        });
    });
}

// focus when the login dropdown or a modal opens
function focarAoAbrir() {
    document.querySelectorAll(".dropdown-entrar").forEach(dropdown => {
        dropdown.addEventListener("shown.bs.dropdown", () => {
            focarPrimeiroCampo(dropdown.querySelector(".dropdown-menu"));
        });
    });
    document.querySelectorAll(".modal").forEach(modal => {
        modal.addEventListener("shown.bs.modal", () => focarPrimeiroCampo(modal));
    });
}

// login and signup panels: <button data-alternar="idDoPainel">
function alternarPaineis() {
    document.querySelectorAll("[data-alternar]").forEach(botao => {
        botao.addEventListener("click", () => {
            const destino = document.getElementById(botao.dataset.alternar);
            destino.parentElement.querySelectorAll(":scope > [data-painel]").forEach(painel => {
                painel.hidden = painel !== destino;
            });
            focarPrimeiroCampo(destino);
        });
    });
}

// show/hide password: <button data-mostrar-senha="idDoCampo">
function mostrarSenha() {
    document.querySelectorAll("[data-mostrar-senha]").forEach(botao => {
        const campo = document.getElementById(botao.dataset.mostrarSenha);
        botao.addEventListener("click", () => {
            const mostrar = campo.type === "password";
            campo.type = mostrar ? "text" : "password";
            botao.setAttribute("aria-pressed", String(mostrar));
        });
    });
}

// asks for confirmation before submitting: <form data-confirmar="Pergunta?">
function confirmarEnvio() {
    document.querySelectorAll("form[data-confirmar]").forEach(form => {
        form.addEventListener("submit", evento => {
            if (!window.confirm(form.dataset.confirmar)) {
                evento.preventDefault();
            }
        });
    });
}

// max file size: <input type="file" data-tamanho-maximo="bytes">. the server checks it again
function limitarArquivos() {
    document.querySelectorAll("input[type=file][data-tamanho-maximo]").forEach(campo => {
        campo.addEventListener("change", () => {
            const arquivo = campo.files[0];
            const grandeDemais = arquivo !== undefined && arquivo.size > Number(campo.dataset.tamanhoMaximo);
            campo.setCustomValidity(grandeDemais ? "Arquivo grande demais." : "");
            campo.classList.toggle("is-invalid", grandeDemais);
        });
    });
}

// clears the chosen file: <button data-limpar="idDoCampo">
function limparArquivos() {
    document.querySelectorAll("[data-limpar]").forEach(botao => {
        const campo = document.getElementById(botao.dataset.limpar);
        const atualizar = () => { botao.disabled = campo.files.length === 0; };
        campo.addEventListener("change", atualizar);
        botao.addEventListener("click", () => {
            campo.value = "";
            campo.setCustomValidity("");
            campo.classList.remove("is-invalid");
            atualizar();
            campo.focus();
        });
        atualizar(); // the browser may keep the file when going back to the page
    });
}

// fields refused by the server: <input class="is-invalid" data-erro-servidor>
// the red goes away when the user starts typing, and the message goes back to data-mensagem-padrao
function limparErroServidor() {
    document.querySelectorAll("[data-erro-servidor]").forEach(campo => {
        campo.addEventListener("input", () => {
            campo.classList.remove("is-invalid");
            campo.removeAttribute("aria-invalid");
            const mensagem = campo.parentElement.querySelector(".invalid-feedback[data-mensagem-padrao]");
            if (mensagem) mensagem.textContent = mensagem.dataset.mensagemPadrao;
        }, {once: true});
    });
}

recarregarAoVoltar();
validarFormularios();
conferirSenhas();
focarAoAbrir();
alternarPaineis();
mostrarSenha();
confirmarEnvio();
limitarArquivos();
limparArquivos();
limparErroServidor();
