#!/usr/bin/env bash
# =====================================================================
# Roda uma vez, quando o Codespace é criado (postCreateCommand).
#   1. instala o PostgreSQL e cria o banco ecommerce_db com o createdb.sql
#   2. baixa o Tomcat 9 para ~/tomcat
#   3. cria o db.properties (que não vai para o Git)
# Depois disso, o iniciar.sh compila o projeto e sobe o site.
# =====================================================================
set -euo pipefail

RAIZ="$(cd "$(dirname "$0")/.." && pwd)"
TOMCAT_VERSAO="9.0.123"          # a mesma do IntelliJ
TOMCAT_DIR="$HOME/tomcat"

echo "==> Instalando o PostgreSQL"
# O "|| true" evita que um repositório extra da imagem fora do ar (já aconteceu
# com chaves vencidas) interrompa tudo; o do PostgreSQL é o do próprio Debian.
sudo apt-get update -qq || true
sudo DEBIAN_FRONTEND=noninteractive apt-get install -y -qq postgresql > /dev/null
sudo service postgresql start > /dev/null

# A mesma senha do db.properties abaixo. O banco só aceita conexões de
# dentro do Codespace, então uma senha simples não expõe nada.
sudo runuser -u postgres -- psql -qc "ALTER USER postgres PASSWORD 'postgres';"

echo "==> Criando o banco ecommerce_db"
bash "$RAIZ/.devcontainer/banco.sh" recriar

echo "==> Baixando o Tomcat $TOMCAT_VERSAO"
if [ ! -d "$TOMCAT_DIR" ]; then
    # archive.apache.org guarda todas as versões; os outros espelhos só têm a mais recente
    curl -fsSL "https://archive.apache.org/dist/tomcat/tomcat-9/v$TOMCAT_VERSAO/bin/apache-tomcat-$TOMCAT_VERSAO.tar.gz" \
        | tar -xz -C "$HOME"
    mv "$HOME/apache-tomcat-$TOMCAT_VERSAO" "$TOMCAT_DIR"
    # Tira as aplicações de exemplo do Tomcat. No lugar da página inicial
    # dele, a raiz do endereço passa a levar direto para a loja.
    rm -rf "$TOMCAT_DIR/webapps/"*
    mkdir -p "$TOMCAT_DIR/webapps/ROOT"
    printf '<!DOCTYPE html><meta http-equiv="refresh" content="0; url=/ecommerce/">\n' \
        > "$TOMCAT_DIR/webapps/ROOT/index.html"
fi

echo "==> Configurando o acesso ao banco"
DB_PROPERTIES="$RAIZ/src/main/resources/db.properties"
if [ ! -f "$DB_PROPERTIES" ]; then
    printf 'db.url=jdbc:postgresql://localhost:5432/ecommerce_db\ndb.usuario=postgres\ndb.senha=postgres\n' \
        > "$DB_PROPERTIES"
fi

echo "==> Instalação concluída"
