# 09 Agilismo y Jira: Sprint de Prinplup

**Nivel:** Prinplup · **Proyecto Jira:** AP1 (el mismo de Piplup) · **Estimación:** puntos de historia en Fibonacci (1, 2, 3, 5, 8, 13)

## Objetivo del sprint

> **Completar el asignador automático con Strategy y las alertas de fallo con Observer, de modo que una misión se asigne sola al drone apto y los fallos lleguen al Centro de Control y al técnico.**

Duración: 2 semanas · Capacidad del equipo: 32 puntos · Comprometido: **32 puntos**.

## Jerarquía

```
ÉPICA  Digitalizar el monitoreo y reparto hídrico de la ECI (la misma de Piplup)
└── FEATURE  Asignación automática y alertas de la flota acuática v2
    ├── HU-04  Crear drones por tipo con una fábrica                       3 pts
    ├── HU-05  Asignar automáticamente el drone más adecuado               8 pts
    │   ├── ST  Implementar ValidadorAsignacion (estado, 35 %, capacidad, condiciones)
    │   ├── ST  Escribir AsignadorAutomaticoTest con Mockito (Red)
    │   ├── ST  Implementar AsignadorAutomatico con EstrategiaSeleccion inyectada (Green)
    │   └── ST  Refactorizar y cubrir ramas hasta 100 % en JaCoCo
    ├── HU-06  Atender primero las misiones CRÍTICAS                       5 pts
    ├── HU-07  Consultar condiciones hídricas antes de asignar             5 pts
    ├── HU-08  Notificar al técnico cuando un drone falla                  5 pts
    │   ├── ST  Crear ObservadorMision, EventoMision y PublicadorEventosMision
    │   ├── ST  Implementar TecnicoMantenimientoObserver (abre orden en DRONE_EN_FALLO)
    │   └── ST  Prueba de integración: 2 observadores mock notificados exactamente 1 vez
    ├── HU-09  Alertar al Centro de Control por misión CRÍTICA sin drone   3 pts
    └── HU-10  Consultar la flota agrupada por tipo                        3 pts
```

## Historias de usuario

| Id | Historia | MoSCoW | Puntos | Por qué esos puntos |
|---|---|---|---|---|
| HU-04 | Como administrador ECI, quiero registrar drones de los 3 tipos desde un único punto de creación para que agregar un tipo nuevo no obligue a modificar el resto del sistema. | Must | 3 | Jerarquía + fábrica; poco riesgo. |
| HU-05 | Como solicitante, quiero que el sistema asigne automáticamente el drone apto de mayor batería según la zona, la carga y las condiciones del agua para que mi muestra salga sin esperar a que el operador revise toda la flota. | Must | 8 | Es el núcleo: validador, estrategia, eventos y caso de FALLO; la más incierta. |
| HU-06 | Como operador hídrico, quiero que las misiones CRÍTICAS se asignen antes y al drone más cercano para que las muestras urgentes lleguen en el menor tiempo posible. | Should | 5 | Resuelve la tensión AP-07/AP-08; requiere composición de estrategias. |
| HU-07 | Como operador hídrico, quiero que el sistema consulte agitación, profundidad y temperatura antes de asignar para que nunca se envíe un drone que no puede operar en esas condiciones. | Must | 5 | Integración con sistema externo (abstraída con interfaz). |
| HU-08 | Como técnico de mantenimiento, quiero recibir una orden cuando un drone entra en FALLO para repararlo sin que la misión quede bloqueada. | Should | 5 | Observer + reasignación. |
| HU-09 | Como operador del Centro de Control, quiero una alerta crítica cuando una misión CRÍTICA no tiene drone para coordinar una solución manual a tiempo. | Could | 3 | Reutiliza el Observer de HU-08. |
| HU-10 | Como operador hídrico, quiero ver los 15 drones agrupados por tipo para decidir rápido sin revisar una lista plana. | Should | 3 | Consultas Stream. |

### Criterios de aceptación de HU-05

1. **Dado** que AR-06 (88 %) y AR-09 (55 %) son semisumergidos disponibles, **cuando** llega una misión NORMAL de 900 g con agitación MEDIA, **entonces** se asigna AR-06 y la misión queda `EN_TRANSITO`.
2. **Dado** que la estrategia elige AR-14 y AR-14 está en `FALLO`, **cuando** se asigna la misión, **entonces** el técnico recibe una orden para AR-14 y la misión se asigna a otro drone apto.
3. **Dado** que solo existe AR-07 con 34 %, **cuando** llega una misión, **entonces** no se asigna y se publica `MISION_SIN_DRONE`.

## Definition of Done

Una historia está terminada solo si cumple todo:

- El código compila sin warnings (`-Xlint:all`).
- Las pruebas unitarias pasan (JUnit 5 + Mockito) con `mvn verify`.
- JaCoCo reporta ≥ 80 % de cobertura de líneas para la feature (la regla del punto 13 hace fallar el build).
- El PR a `develop` tiene revisión aprobada por al menos otro miembro del equipo.
- El diagrama C4 y la plantilla DOSW están actualizados si la historia los afecta.
- Ningún método supera 20 líneas ni ninguna clase 200; sin `null` como retorno.
- Para las clases de lógica, el historial muestra Red → Green → Refactor.
- Los criterios de aceptación de la HU fueron verificados.

## Velocidad y capacidad

En el sprint de Piplup el equipo cerró 10 puntos (HU de 3, 5 y 2) con un alcance mucho menor. Para Prinplup se estimó la capacidad en 32 puntos: 3 integrantes, 2 semanas y la base del dominio ya construida. Las HU de 8 puntos (HU-05) se dejaron como la única de ese tamaño; si una historia se estimara en 13 se partiría antes de entrar al sprint.

## Relación entre HU y ramas

| HU | Rama `feature/` |
|---|---|
| HU-04, HU-10 | `feature/factory-drones` |
| HU-05, HU-06, HU-07 | `feature/asignacion-automatica` |
| HU-08, HU-09 | `feature/alertas-centro-control` |

<img width="1535" height="331" alt="image" src="https://github.com/user-attachments/assets/55851429-fb96-4063-80f5-f26d9837f4d0" />

