#!/usr/bin/env bash
# Auditoría de capas con grep: imprime el reporte de dependencias y termina con código 1 si hay violaciones.
# Uso (desde la carpeta "14 Arquitectura por Capas"): bash scripts/auditar-capas.sh > docs/reporte-dependencias.txt
set -u
RAIZ="src/main/java/edu/eci/dosw/aquaport"
violaciones=0

seccion() { printf '\n== %s ==\n' "$1"; }

seccion "Imports por capa (paquete importado -> cantidad)"
for capa in dominio aplicacion infraestructura; do
  echo "[$capa]"
  grep -rhoE '^import [a-z][a-zA-Z0-9_.]*\.' "$RAIZ/$capa" \
    | sed -E 's/^import //; s/\.[A-Z].*//; s/\.$//' \
    | sed -E 's/^(edu\.eci\.dosw\.aquaport\.[a-z]+).*/\1/; s/^(java\.[a-z]+).*/\1/; s/^(com\.fasterxml\.jackson).*/\1/' \
    | sort | uniq -c | sort -rn | sed 's/^/   /'
done

seccion "Regla 1: dominio solo importa java.* y su propio paquete"
r1=$(grep -rnE '^import ' "$RAIZ/dominio" | grep -vE 'import (java\.|edu\.eci\.dosw\.aquaport\.dominio\.)')
if [ -n "$r1" ]; then echo "$r1"; violaciones=$((violaciones + 1)); else echo "OK: 0 imports externos en dominio"; fi

seccion "Regla 2: aplicacion no importa infraestructura ni librerías externas"
r2=$(grep -rnE '^import ' "$RAIZ/aplicacion" | grep -vE 'import (java\.|edu\.eci\.dosw\.aquaport\.(dominio|aplicacion)\.)')
if [ -n "$r2" ]; then echo "$r2"; violaciones=$((violaciones + 1)); else echo "OK: aplicacion depende solo de dominio"; fi

seccion "Regla 3: ningún new de infraestructura fuera de infraestructura"
clases=$(find "$RAIZ/infraestructura" -name '*.java' -exec basename {} .java \; | paste -sd'|')
r3=$(grep -rnE "new ($clases)\(" "$RAIZ/dominio" "$RAIZ/aplicacion")
if [ -n "$r3" ]; then echo "$r3"; violaciones=$((violaciones + 1)); else echo "OK: 0 instancias de infraestructura en dominio o aplicacion"; fi

seccion "Regla 4: inyección por constructor (sin setters de dependencias ni campos mutables en servicios)"
r4=$(grep -rnE '^\s{4}private [A-Z][A-Za-z<>, ]* [a-z][A-Za-z]*;' "$RAIZ/aplicacion")
r4b=$(grep -rnE 'public void set[A-Z]' "$RAIZ/aplicacion")
if [ -n "$r4$r4b" ]; then echo "$r4$r4b"; violaciones=$((violaciones + 1)); else echo "OK: todos los campos de aplicacion son final y no hay setters"; fi

seccion "Resultado"
if [ "$violaciones" -eq 0 ]; then echo "0 violaciones de capa"; else echo "$violaciones regla(s) con violaciones"; exit 1; fi
