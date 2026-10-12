#!/usr/bin/env bash
# Genera CHANGELOG.md a partir de los commits Conventional Commits entre dos referencias.
# Uso: bash scripts/generar-changelog.sh v2.0.0 HEAD v3.0.0 > CHANGELOG.md
set -eu
desde=${1:?"falta la referencia inicial, por ejemplo v2.0.0"}
hasta=${2:-HEAD}
version=${3:-"sin versión"}
fecha=$(git log -1 --format=%cs "$hasta")
commits=$(git log --no-merges --reverse --format='%s|%h' "$desde..$hasta")

echo "# Changelog"
echo
echo "Generado con scripts/generar-changelog.sh a partir de los mensajes de commit (Conventional Commits)."
echo
echo "## $version ($fecha)"
for par in "feat:Funcionalidades" "fix:Correcciones" "refactor:Refactorizaciones" "test:Pruebas" \
           "docs:Documentación" "ci:Integración continua" "build:Construcción" "chore:Mantenimiento"; do
    tipo=${par%%:*}
    titulo=${par#*:}
    lineas=$(printf '%s\n' "$commits" | grep -E "^$tipo(\(|:)" || true)
    if [ -n "$lineas" ]; then
        echo
        echo "### $titulo"
        echo
        printf '%s\n' "$lineas" | sed -E 's/^[a-z]+\(([a-z0-9-]+)\)!?: (.*)\|(.*)$/- **\1**: \2 (`\3`)/; s/^[a-z]+!?: (.*)\|(.*)$/- \1 (`\2`)/'
    fi
done
