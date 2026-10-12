# 09 Agilismo y Jira: Roadmap Enterprise en 3 sprints

**Nivel:** Empoleon · **Proyecto Jira:** AP1 · **Estimación:** Fibonacci (1, 2, 3, 5, 8) · Épica multi-sprint, capacidad por sprint, DoD Enterprise y retrospectiva

## Roadmap

```
Épica: AquaPort Enterprise: red multi-embalse con rutas multi-etapa
│
├── Sprint 1 · Arquitectura por capas y patrones Enterprise ........ 26 pts
├── Sprint 2 · Rutas multi-etapa y coordinación de zonas ........... 28 pts
└── Sprint 3 · Calidad: JaCoCo ≥ 85 %, SonarQube con deuda < 15 min .. 23 pts
```

| Sprint | Objetivo (Sprint Goal) | Capacidad | Comprometido | Rama `feature/` |
|---|---|---|---|---|
| **1** | *Dejar el dominio sin dependencias externas y la validación, la telemetría y la API hídrica resueltas con Chain, Decorator y Adapter.* | 28 | 26 | `feature/arquitectura-capas` |
| **2** | *Ejecutar una misión multi-etapa de punta a punta con reasignación automática y cadena de custodia sellada.* | 28 | 28 | `feature/rutas-multietapa` |
| **3** | *Cerrar la v3 con JaCoCo ≥ 85 %/75 %, el Quality Gate Enterprise en verde, el design system accesible y la release 3.0.0.* | 28 | 23 | `feature/calidad-enterprise` |

**Capacidad:** 3 integrantes × 2 semanas. La velocidad de referencia es el sprint de Prinplup: se comprometieron 32 puntos con la capacidad justa y el cierre (evidencias de Jira y SonarQube) quedó apretado (ver retrospectiva). Por eso la capacidad baja a 28 y el Sprint 3 deja 5 puntos de holgura para el análisis de SonarQube, que depende de corregir lo que el análisis encuentre.

## Historias por sprint

### Sprint 1 · Arquitectura por capas y patrones

| HU | Historia | Pts |
|---|---|---|
| HU-11 | Separar el código en dominio, aplicación e infraestructura | 5 |
| HU-12 | Validar cada tramo con una cadena de reglas (Chain of Responsibility) | 5 |
| HU-13 | Registrar telemetría y sellar la custodia con decoradores | 5 |
| HU-14 | Aislar la API hídrica con un adaptador | 3 |
| HU-15 | Validar Conventional Commits y proteger `main` y `develop` | 3 |
| HU-16 | Diagrama de contenedores C4 | 2 |
| HU-17 | Auditoría automática de capas | 3 |

### Sprint 2 · Rutas multi-etapa y coordinación de zonas

| HU | Historia | Pts |
|---|---|---|
| HU-18 | Planificar la ruta multi-etapa con un drone por tramo | 8 |
| HU-19 | Desviar un tramo por condición hídrica adversa | 5 |
| HU-20 | Reasignar el tramo ante fallo o batería crítica en un waypoint | 5 |
| HU-21 | Cadena de custodia sellada por tramo | 5 |
| HU-22 | Dejar la misión en espera si el destino está inactivo | 2 |
| HU-23 | Reportes de eficiencia por zona | 3 |

### Sprint 3 · Calidad

| HU | Historia | Pts |
|---|---|---|
| HU-24 | Quality gate de JaCoCo 85 % líneas y 75 % ramas | 3 |
| HU-25 | Quality gate Enterprise en SonarQube | 5 |
| HU-26 | Design system con contraste WCAG AA | 5 |
| HU-27 | Mocks del flujo multi-etapa | 5 |
| HU-28 | Pruebas de rendimiento de reasignación y throughput | 3 |
| HU-29 | Changelog y release v3.0.0 | 2 |

HU-18 es la única de 8 puntos: concentra el `reduce` del plan, la cadena de validación y el desvío. Ninguna historia pasa de 8; si alguna se hubiera estimado en 13 se habría partido antes del sprint.

## Definition of Done Enterprise

Una historia está terminada solo si:

1. Compila sin warnings (`-Xlint:all`) y `mvn verify` pasa.
2. JaCoCo ≥ **85 %** de líneas y ≥ **75 %** de ramas (la regla hace fallar el build).
3. SonarQube: 0 bugs, 0 vulnerabilidades, deuda técnica < **15 min**, duplicación < **3 %**.
4. `ArquitecturaCapasTest` en verde: el dominio no importa librerías externas.
5. Métodos ≤ 15 líneas, clases ≤ 150 líneas, sin `null` como retorno.
6. Commits en Conventional Commits (el hook los valida) y, en la lógica nueva, el historial muestra Red → Green → Refactor.
7. PR a `develop` con revisión aprobada por otro integrante.
8. C4, plantilla DOSW y README del punto actualizados si la historia los afecta.
9. Criterios de aceptación verificados.

## Retrospectiva del sprint de Prinplup

Formato: qué funcionó, qué no funcionó y qué se cambia. Escrita a partir de lo que pasó al cargar y cerrar ese sprint.

| Qué funcionó | Qué no funcionó | Qué se cambia en Empoleon |
|---|---|---|
| Dividir el trabajo en 3 ramas `feature/` con carpetas distintas: los 3 PR se fusionaron sin conflictos. | La importación por CSV a Jira falló con la columna *Parent*: la épica no acepta padre y “Principal” pedía claves que aún no existían. Las subtareas se crearon a mano y costó tiempo al final del sprint. | El CSV de Empoleon solo trae tipo, resumen, descripción y puntos; la vinculación a la épica y a cada sprint se hace en el tablero, al planear, no al cerrar. |
| Escribir las pruebas con Mockito antes del `AsignadorAutomatico` dejó el ciclo Red → Green → Refactor visible en los commits. | Las evidencias (capturas de Jira y SonarQube) se subieron con commits directos a la rama de entrega `Prinplup`, fuera del flujo de PR; `main` y `develop` no las tienen y uno de esos commits quedó con el mensaje genérico “Delete”. | Se protegen `main` y `develop` con un ruleset y el hook de Conventional Commits rechaza mensajes como “Delete” en los commits locales (punto 02); lo que se edite desde la web de GitHub entra por PR a `develop`. |
| El Quality Gate de SonarQube se configuró antes de analizar, así los umbrales estaban claros desde el principio. | Levantar SonarQube con Docker dio error la primera vez y el análisis quedó para el último día. | El análisis de SonarQube se mueve a una historia propia del Sprint 3 (HU-25), con holgura de capacidad. |
| Los mocks usaron los mismos datos que el código (FlotaEjemplo), sin inconsistencias entre UI y pruebas. | Se comprometieron 32 puntos con la capacidad justa: no quedó margen para imprevistos. | La capacidad de cada sprint se fija en 28 puntos y el Sprint 3 queda en 23. |

## Cargarlo en Jira (AP1)

1. **Backlog → Crear sprint** tres veces: `AP1 Sprint 3 - Empoleon arquitectura`, `AP1 Sprint 4 - Empoleon rutas`, `AP1 Sprint 5 - Empoleon calidad`, cada uno con su objetivo.
2. Importar `jira-import-empoleon.csv` (*Issue Type*, *Summary*, *Description*, *Story Points*).
3. Vincular las 19 historias a la épica y arrastrarlas a su sprint según las tablas de arriba.
4. En **Timeline / Roadmap** se ven la épica y los 3 sprints.

**Pendiente:** captura del roadmap en `capturas/jira-roadmap-empoleon.png`.
