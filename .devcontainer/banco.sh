#!/usr/bin/env bash
# =====================================================================
# Atalhos para o banco no Codespace:
#   bash .devcontainer/banco.sh           abre o psql no ecommerce_db (\q para sair)
#   bash .devcontainer/banco.sh recriar   apaga tudo e recria com o createdb.sql
#   bash .devcontainer/banco.sh demo      acrescenta os dados de demonstração
# =====================================================================
set -euo pipefail

RAIZ="$(cd "$(dirname "$0")/.." && pwd)"
export PGPASSWORD="postgres"
export PGOPTIONS="--client-min-messages=warning"   # esconde os avisos "does not exist, skipping"
PSQL=(psql -h localhost -U postgres -d ecommerce_db -v ON_ERROR_STOP=1)

case "${1:-}" in
    recriar)
        if ! psql -h localhost -U postgres -tAc "SELECT 1 FROM pg_database WHERE datname = 'ecommerce_db'" | grep -q 1; then
            # UTF-8 com ordenação em português (ICU): "Ética" vem antes de "Zebra" no ORDER BY.
            # Se o PostgreSQL não tiver ICU, cria em UTF-8 com a ordenação padrão.
            createdb -h localhost -U postgres -E UTF8 -T template0 --locale=C.UTF-8 \
                     --locale-provider=icu --icu-locale=pt-BR ecommerce_db 2> /dev/null \
                || createdb -h localhost -U postgres -E UTF8 -T template0 --locale=C.UTF-8 ecommerce_db
        fi
        "${PSQL[@]}" -q -f "$RAIZ/database/createdb.sql"
        echo "Banco recriado: só o administrador iuveadmin (senha admin123)."
        if [ -d "$HOME/iuve-fotos" ]; then
            echo "As fotos antigas continuam em ~/iuve-fotos, sem uso. Para apagá-las: rm -rf ~/iuve-fotos"
        fi
        ;;
    demo)
        "${PSQL[@]}" -q -f "$RAIZ/database/dados-demo.sql"
        echo "Dados de demonstração carregados."
        ;;
    "")
        "${PSQL[@]}"
        ;;
    *)
        echo "Uso: bash .devcontainer/banco.sh [recriar|demo]"
        exit 1
        ;;
esac
