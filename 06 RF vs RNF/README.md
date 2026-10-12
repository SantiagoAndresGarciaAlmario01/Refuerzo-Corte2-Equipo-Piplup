# 06 RF y RNF: Requisitos del MVP con priorización MoSCoW

**Nivel:** Piplup · **Sistema:** AquaPort MVP (4 drones Aqua-Ranger 100, zonas fijas, asignación manual)

Un **RF** dice *qué hace* el sistema (actor + acción + resultado observable). Un **RNF** dice *cómo de bien* lo hace y **debe ser medible**: métrica + umbral + condición + forma de medirlo.

## Requisitos Funcionales

| Código | Requisito funcional | Actor | Resultado observable |
|---|---|---|---|
| **RF-01** | El Operador Hídrico consulta la flota y el sistema muestra los drones disponibles con batería ≥ 35 %, ordenados de mayor a menor batería, con su ID, batería y zona. | Operador Hídrico | Lista ordenada de drones operables en pantalla. |
| **RF-02** | El Operador Hídrico registra una misión de transporte indicando punto de partida, punto de llegada, tipo de carga y drone asignado, y el sistema genera un código de misión en estado `PENDIENTE`. | Operador Hídrico | Código de misión (p. ej. `M-016`) visible y misión en la lista del día. |
| **RF-03** | El Operador Hídrico cancela una misión que está en estado `PENDIENTE` y el sistema libera el drone asignado. | Operador Hídrico | La misión desaparece de pendientes y el drone vuelve a “disponible”. |

## Requisitos No Funcionales

| Código | Atributo | Requisito no funcional medible |
|---|---|---|
| **RNF-01** | Rendimiento | La consulta de drones disponibles (RF-01) debe retornar en **menos de 200 ms** con una flota de **hasta 10 drones**, medido con JUnit 5 `assertTimeout(Duration.ofMillis(200), …)`. |
| **RNF-02** | Usabilidad / prevención de errores | El sistema debe impedir el **100 %** de las asignaciones de drones con batería < 35 % o no disponibles (el control queda deshabilitado), verificado con pruebas unitarias del `ValidadorMision` que cubran ambos casos y sus valores límite (34 % y 35 %). |
| **RNF-03** | Mantenibilidad | El código del MVP debe mantener **≥ 80 % de cobertura de líneas** (JaCoCo) y **0 bugs y 0 vulnerabilidades** en SonarQube en cada merge a `develop`, verificado por el reporte de `mvn verify`. |

## Priorización MoSCoW

| Requisito | Categoría | Justificación |
|---|---|---|
| RF-01 Ver flota disponible | **Must Have** | Sin ver la flota el operador no puede elegir drone; es el primer paso del flujo básico del MVP. |
| RF-02 Registrar misión | **Must Have** | Es la razón de ser del sistema: sin registrar la misión no hay transporte que gestionar. |
| RF-03 Cancelar misión pendiente | **Should Have** | Es importante para corregir asignaciones erróneas (Nielsen #3), pero el MVP puede salir con un procedimiento manual temporal. |
| RNF-02 Impedir asignaciones inválidas | **Must Have** | Asignar un drone con batería crítica pone en riesgo el equipo y la muestra; es una regla de seguridad del dominio. |
| RNF-01 Respuesta < 200 ms | **Should Have** | Mejora la experiencia, pero con 4 drones el riesgo de lentitud es bajo; no bloquea la salida del MVP. |
| RNF-03 Cobertura ≥ 80 % y Sonar limpio | **Could Have** *(como requisito de producto)* | Aporta calidad interna y es exigido por el curso como práctica del equipo, pero el usuario final no percibe directamente el valor; se prioriza después de las funciones esenciales. |
| *Asignación automática, rutas multi-etapa* | **Won't Have (esta versión)** | Fuera del alcance del MVP; se planean para Prinplup y Empoleon. |

## Por qué estos RNF sí son requisitos

“El sistema debe ser rápido” no se puede aprobar ni reprobar. Cada RNF aquí indica **qué se mide** (tiempo, % de asignaciones bloqueadas, % de cobertura), **el umbral** (200 ms, 100 %, 80 %), **la condición** (≤ 10 drones, en cada merge) y **cómo se verifica** (prueba JUnit, reporte JaCoCo/SonarQube).
