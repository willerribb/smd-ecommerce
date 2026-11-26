#!/usr/bin/env bash
# =====================================================================
# Roda a cada início do Codespace (postStartCommand), inclusive o primeiro.
# Também serve para publicar mudanças: depois de um git pull, rode
#     bash .devcontainer/iniciar.sh
#   1. liga o PostgreSQL (ele não volta sozinho quando o Codespace reinicia)
#   2. compila o projeto com o Maven
#   3. para o Tomcat, troca a versão publicada e liga de novo
# =====================================================================
set -euo pipefail

RAIZ="$(cd "$(dirname "$0")/.." && pwd)"
TOMCAT_DIR="$HOME/tomcat"
export CATALINA_PID="$TOMCAT_DIR/tomcat.pid"   # permite parar o Tomcat pelo número do processo

echo "==> Ligando o PostgreSQL"
sudo service postgresql start > /dev/null

echo "==> Compilando (na primeira vez, o Maven baixa as dependências)"
cd "$RAIZ"
mvn -q -B package

echo "==> Publicando no Tomcat"
if [ -f "$CATALINA_PID" ]; then
    "$TOMCAT_DIR/bin/catalina.sh" stop 15 -force > /dev/null 2>&1 || true
fi
rm -rf "$TOMCAT_DIR/webapps/ecommerce" "$TOMCAT_DIR/webapps/ecommerce.war"
cp "$RAIZ"/target/*.war "$TOMCAT_DIR/webapps/ecommerce.war"   # o nome do .war vira o /ecommerce do endereço

# setsid e nohup: o Tomcat continua rodando depois que este script termina
setsid nohup "$TOMCAT_DIR/bin/catalina.sh" start > /dev/null 2>&1 < /dev/null

echo "==> Esperando o site responder"
for _ in $(seq 1 60); do
    # -f: só conta como pronto se a página abrir de verdade (um erro 404 ou 500 não vale)
    if curl -sf -o /dev/null http://localhost:8080/ecommerce/; then
        echo "==> Pronto. O site está na porta 8080 (aba PORTS), no caminho /ecommerce/"
        exit 0
    fi
    sleep 1
done

echo "!!  O site não respondeu em 60 segundos. Veja o log: tail -50 $TOMCAT_DIR/logs/catalina.out"
exit 1
