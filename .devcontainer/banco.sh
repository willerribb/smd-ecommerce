#!/usr/bin/env bash
# db shortcuts for the codespace:
# no argument opens psql (\q to quit), "recriar" runs createdb.sql again, "demo" loads dados-demo.sql
set -euo pipefail

RAIZ="$(cd "$(dirname "$0")/.." && pwd)"
export PGPASSWORD="postgres"
export PGOPTIONS="--client-min-messages=warning" # hides the "does not exist, skipping" notices
PSQL=(psql -h localhost -U postgres -d ecommerce_db -v ON_ERROR_STOP=1)

case "${1:-}" in
    recriar)
        if ! psql -h localhost -U postgres -tAc "SELECT 1 FROM pg_database WHERE datname = 'ecommerce_db'" | grep -q 1; then
            # UTF-8 with portuguese sorting (ICU), or the default sorting if ICU isn't available
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