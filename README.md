# Refuerzo Corte 2 - AquaPort: Piplup → Prinplup → Empoleon

**Curso:** Diseño y Arquitectura de Software (DOSW) · ECI · 2026-2 · Prof. Andrés Martín Cantór Urrego
**Línea evolutiva:** Piplup (`v1.0.0`) → Prinplup (`v2.0.0`) → **Empoleon (`v3.0.0`)**
**Proyecto:** **AquaPort**, sistema de drones acuáticos de la ECI para monitoreo y reparto de muestras de agua y sensores.

## Enunciado Empoleon: AquaPort Enterprise

60 drones en 4 zonas hídricas interconectadas: **Embalse de Investigación** (buceadores), **Red de Canales** (superficiales), **Laguna de Reserva** (semisumergidos) y **Laboratorio Hídrico Central** (destino común). Las misiones recorren rutas multi-etapa con waypoints: un drone por tramo, traspaso de la muestra en cada waypoint, cadena de custodia sellada y reasignación automática ante fallos.

**Estándares:** arquitectura por capas (dominio sin dependencias externas), JaCoCo ≥ 85 % líneas / ≥ 75 % ramas, SonarQube 0 bugs / 0 vulnerabilidades / deuda < 15 min / duplicación < 3 %, métodos ≤ 15 líneas, clases ≤ 150 líneas.

## Retos

| # | Tema | Entregable | Rama feature |
|---|---|---|---|
| 01 | Streams & Lambdas | Eficiencia por zona (`teeing`), mejor historial, ruta con `reduce`, Collector propio, stream paralelo justificado · 22 pruebas | `feature/rutas-multietapa` |
| 02 | GitHub y GitFlow | Hook `commit-msg` (Conventional Commits), ruleset de `main`/`develop`, `CHANGELOG.md` generado, tag `v3.0.0` | `feature/arquitectura-capas` |
| 03 | Patrones de Diseño | Chain of Responsibility, Decorator y Adapter integrados · 33 pruebas con Mockito | `feature/arquitectura-capas` |
| 04 | Principios SOLID | Auditoría por principio, mapa de dependencias generado de los imports | `feature/arquitectura-capas` |
| 05 | C4 nivel 2 | `docs/c4-contenedores-empoleon.png` con protocolo y dato en cada flecha | `feature/arquitectura-capas` |
| 06 | RF y RNF | AP-13 a AP-17, 5 RNF de producción (SLA, reasignación, throughput), 5 tensiones | `feature/rutas-multietapa` |
| 07 | Plantilla DOSW | RF AP-15 con datos anidados, 10 pasos, 6 flujos alternos y reglas de custodia | `feature/rutas-multietapa` |
| 08 | Identidad y UX | Design system con tokens, contraste WCAG AA verificado y mapa de 6 pantallas | `feature/calidad-enterprise` |
| 09 | Agilismo y Jira | Roadmap de 3 sprints, capacidad, DoD Enterprise y retrospectiva | `feature/calidad-enterprise` |
| 10 | Casos de Uso | `docs/diagrama-cu-empoleon.png` con los 5 «extend» de fallo | `feature/rutas-multietapa` |
| 11 | Mocks con IA | 5 pantallas con datos reales del dominio y 5 prompts | `feature/calidad-enterprise` |
| 12 | TDD | 3 capas de pruebas, los 5 flujos alternos, Red → Green → Refactor · 103 pruebas, 98 %/99 % | `feature/rutas-multietapa` |
| 13 | JaCoCo + SonarQube | Quality gate 85/75 y Sonar Enterprise, 4 issues resueltos con un commit cada uno · 110 pruebas | `feature/calidad-enterprise` |
| 14 | Arquitectura por capas | Auditoría con `grep` y `ArquitecturaCapasTest`, 0 violaciones · 114 pruebas | `feature/arquitectura-capas` |

Cada carpeta tiene su `README.md`. Las carpetas con código son proyectos Maven independientes (Java 17, JUnit 5.10, Mockito 5, Jackson 2.15, JaCoCo 0.8.11):

```bash
cd "14 Arquitectura por Capas" && mvn verify
```

## Antes de hacer commits en este repositorio

```bash
git config core.hooksPath .githooks      # activa la validación de Conventional Commits
```

## Versiones

| Tag | Rama de entrega | Contenido |
|---|---|---|
| `v1.0.0` | `Piplup` | MVP: 4 drones, asignación manual |
| `v2.0.0` | `Prinplup` | 15 drones de 3 tipos, asignación automática y alertas |
| `v3.0.0` | `Empoleon` | 60 drones, 4 zonas, rutas multi-etapa y arquitectura por capas |

El detalle de cambios de la v3 está en [`CHANGELOG.md`](CHANGELOG.md).
