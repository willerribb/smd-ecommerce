#!/usr/bin/env bash
# runs every time the codespace starts (postStartCommand).
# starts postgres, builds the project with maven and (re)starts tomcat.
# after a git pull, run it again: bash .devcontainer/iniciar.sh
set -euo pipefail

RAIZ="$(cd "$(dirname "$0")/.." && pwd)"
TOMCAT_DIR="$HOME/tomcat"
export CATALINA_PID="$TOMCAT_DIR/tomcat.pid" # used to stop tomcat

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
cp "$RAIZ"/target/*.war "$TOMCAT_DIR/webapps/ecommerce.war" # the .war name becomes /ecommerce

# setsid and nohup keep tomcat running after the script ends
setsid nohup "$TOMCAT_DIR/bin/catalina.sh" start > /dev/null 2>&1 < /dev/null

echo "==> Esperando o site responder"
for _ in $(seq 1 60); do
    # -f fails on 404 or 500
    if curl -sf -o /dev/null http://localhost:8080/ecommerce/; then
        echo "==> Pronto. O site está na porta 8080 (aba PORTS), no caminho /ecommerce/"
        exit 0
    fi
    sleep 1
done

echo "!!  O site não respondeu em 60 segundos. Veja o log: tail -50 $TOMCAT_DIR/logs/catalina.out"
exit 1