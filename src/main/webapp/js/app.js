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

// Página restaurada pelo Voltar do navegador (o "bfcache", uma cópia da página
// inteira guardada na memória): recarrega, para não mostrar alguém como logado
// depois de sair da conta. Complementa o SemCacheFilter, que impede o cache comum.
window.addEventListener("pageshow", evento => {
    if (evento.persisted) {
        window.location.reload();
    }
});

// 1. Validação no navegador para formulários com a classe "needs-validation".
//    O was-validated (que pinta os campos de vermelho e de azul) só entra quando
//    o envio é barrado. Num envio que segue para o servidor, ele sai: senão, os
//    campos ficavam azuis, com o ícone de OK, durante a espera, e um login com a
//    senha errada parecia ter dado certo até a página voltar com o erro.
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

// 2. Campos de confirmação: <input data-confirma="idDoCampoOriginal">.
//    O campo fica inválido enquanto for diferente do original, e o vermelho aparece
//    ao vivo, sem esperar o envio. Enquanto o que foi digitado ainda é o começo da
//    senha original, a pessoa está no meio da digitação e não há aviso; o aviso
//    vem quando o texto diverge ou quando ela sai do campo (final = true).
function conferirConfirmacao(campo, final) {
    const original = document.getElementById(campo.dataset.confirma);
    const iguais = campo.value === original.value;
    const digitando = !final && original.value.startsWith(campo.value);
    campo.setCustomValidity(iguais ? "" : "As senhas não são iguais.");
    campo.classList.toggle("is-invalid", campo.value !== "" && !iguais && !digitando);
}
document.querySelectorAll("[data-confirma]").forEach(campo => {
    const original = document.getElementById(campo.dataset.confirma);
    campo.addEventListener("input", () => conferirConfirmacao(campo, false));
    campo.addEventListener("change", () => conferirConfirmacao(campo, true));
    original.addEventListener("input", () => {
        if (campo.value !== "") conferirConfirmacao(campo, true);
    });
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

// 7. Arquivos com tamanho máximo: <input type="file" data-tamanho-maximo="bytes">.
//    Avisa assim que o arquivo é escolhido e impede o envio; o servidor confere
//    de novo (Fotos.TAMANHO_MAXIMO), porque o navegador pode ser contornado.
document.querySelectorAll("input[type=file][data-tamanho-maximo]").forEach(campo => {
    campo.addEventListener("change", () => {
        const arquivo = campo.files[0];
        const grandeDemais = arquivo !== undefined && arquivo.size > Number(campo.dataset.tamanhoMaximo);
        campo.setCustomValidity(grandeDemais ? "Arquivo grande demais." : "");
        campo.classList.toggle("is-invalid", grandeDemais);
    });
});

// 8. Desfazer a escolha de um arquivo: <button data-limpar="idDoCampo">.
//    O campo de arquivo não tem um "x" próprio; depois de escolher, só dava para
//    escolher outro. O botão fica desativado enquanto não há arquivo escolhido.
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
    atualizar();   // o navegador pode ter mantido um arquivo ao voltar para a página
});

// 9. Campos marcados pelo servidor: <input class="is-invalid" data-erro-servidor>.
//    O servidor recusou o valor (um nome de usuário já em uso, por exemplo) e a
//    página mostra o campo em vermelho, com a mensagem abaixo. Assim que a pessoa
//    começa a corrigir, o vermelho sai, e a mensagem volta a ser a de formato
//    (data-mensagem-padrao), caso o navegador precise mostrá-la depois.
document.querySelectorAll("[data-erro-servidor]").forEach(campo => {
    campo.addEventListener("input", () => {
        campo.classList.remove("is-invalid");
        campo.removeAttribute("aria-invalid");
        const mensagem = campo.parentElement.querySelector(".invalid-feedback[data-mensagem-padrao]");
        if (mensagem) mensagem.textContent = mensagem.dataset.mensagemPadrao;
    }, {once: true});
});
