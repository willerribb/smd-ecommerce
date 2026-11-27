#!/usr/bin/env bash
# runs once, when the codespace is created (postCreateCommand):
# installs postgres, creates the db, downloads tomcat 9 and creates db.properties
set -euo pipefail

RAIZ="$(cd "$(dirname "$0")/.." && pwd)"
TOMCAT_VERSAO="9.0.123" # same version as in intellij
TOMCAT_DIR="$HOME/tomcat"

echo "==> Instalando o PostgreSQL"
# || true: a broken extra repo in the image (yarn) shouldn't stop the install
sudo apt-get update -qq || true
sudo DEBIAN_FRONTEND=noninteractive apt-get install -y -qq postgresql > /dev/null
sudo service postgresql start > /dev/null

# same password as the db.properties below (the db only accepts connections from inside the codespace)
sudo runuser -u postgres -- psql -qc "ALTER USER postgres PASSWORD 'postgres';"

echo "==> Criando o banco ecommerce_db"
bash "$RAIZ/.devcontainer/banco.sh" recriar

echo "==> Baixando o Tomcat $TOMCAT_VERSAO"
if [ ! -d "$TOMCAT_DIR" ]; then
    # archive.apache.org keeps the old versions
    curl -fsSL "https://archive.apache.org/dist/tomcat/tomcat-9/v$TOMCAT_VERSAO/bin/apache-tomcat-$TOMCAT_VERSAO.tar.gz" \
        | tar -xz -C "$HOME"
    mv "$HOME/apache-tomcat-$TOMCAT_VERSAO" "$TOMCAT_DIR"
    # removes tomcat's example apps, and the root redirects to /ecommerce/
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