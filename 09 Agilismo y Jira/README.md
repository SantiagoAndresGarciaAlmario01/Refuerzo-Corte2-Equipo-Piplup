# 09 Agilismo y Jira: Organizar el desarrollo del MVP

**Nivel:** Piplup · **Jerarquía:** Épica → Feature → Historia de Usuario → Subtarea

## Estructura propuesta

```
ÉPICA  AP-E1  Digitalizar el monitoreo y reparto hídrico de la ECI mediante
              una flota de drones acuáticos supervisada
└── FEATURE  AP-F1  Gestión de la flota de drones acuáticos del campus
    ├── HU  AP-HU1  Ver flota disponible
    ├── HU  AP-HU2  Asignar drone a misión
    │   ├── ST  AP-ST1  Implementar consulta Stream de drones disponibles con batería ≥ 35 % en la zona de partida
    │   ├── ST  AP-ST2  Implementar Mision.Builder con validación en build() (campos obligatorios y drone disponible)
    │   └── ST  AP-ST3  Escribir pruebas JUnit (Red→Green) de ValidadorMision para batería 34 %/35 % y drone no disponible
    └── HU  AP-HU3  Cancelar misión pendiente
```

## Épica

**AP-E1: Digitalizar el monitoreo y reparto hídrico de la ECI mediante una flota de drones acuáticos supervisada.**
Objetivo estratégico que no cabe en un sprint: incluye el MVP (Piplup) y su evolución a flota autónoma (Prinplup) y red multi-embalse (Empoleon).

## Feature

**AP-F1: Gestión de la flota de drones acuáticos del campus.**
Aporta valor completo al Operador Hídrico: ver la flota, asignar drones a misiones y corregir asignaciones.

## Historias de Usuario

| Id | Historia | Prioridad | Puntos |
|---|---|---|---|
| **AP-HU1** | **Como** operador hídrico, **quiero** ver qué drones están disponibles con su nivel de batería y zona **para** asignar la misión al drone más adecuado según la zona de partida. | Must | 3 |
| **AP-HU2** | **Como** operador hídrico, **quiero** asignar manualmente un drone disponible a una misión de transporte **para** que la muestra o el sensor llegue a su destino sin usar un drone con batería crítica. | Must | 5 |
| **AP-HU3** | **Como** operador hídrico, **quiero** cancelar una misión que aún está en estado PENDIENTE **para** corregir una asignación errónea y liberar el drone. | Should | 2 |

### Subtareas de AP-HU2 (asignables a una sola persona)

| Id | Subtarea | Responsable sugerido |
|---|---|---|
| AP-ST1 | Implementar consulta Stream que filtra drones disponibles con batería ≥ 35 % en la zona de partida y los ordena de mayor a menor batería | Integrante 1 |
| AP-ST2 | Implementar `Mision.Builder` con validación en `build()`: campos obligatorios no nulos/vacíos y drone disponible | Integrante 2 |
| AP-ST3 | Escribir pruebas JUnit 5 (Red→Green) de `ValidadorMision` para batería 34 % / 35 %, drone no disponible y zona inválida | Integrante 3 |

### Criterios de aceptación de AP-HU2 (Dado / Cuando / Entonces)

1. **Dado** que AR-01 está disponible con 92 % de batería, **cuando** el operador lo asigna a una misión de Embalse Norte a Laboratorio Hídrico, **entonces** el sistema crea la misión en estado `PENDIENTE`, muestra su código (p. ej. `M-016`) y AR-01 deja de aparecer como disponible.
2. **Dado** que AR-03 tiene 18 % de batería, **cuando** el operador intenta asignarlo, **entonces** el sistema no permite la asignación y muestra “El drone AR-03 tiene batería insuficiente (18 %). Mínimo requerido: 35 %.”

## Flujo del tablero

`To Do` → `In Progress` → `In Review` (Pull Request a `develop`) → `Done`. Cada subtarea se mueve cuando cambia su estado real; una HU pasa a `Done` solo cuando sus subtareas están en `Done` y sus criterios de aceptación se verificaron.

## Evidencia en Jira

Proyecto: AP1 (https://mail-team-lu7li63q.atlassian.net/jira/software/projects/AP1/boards/5)

<img width="1561" height="232" alt="image" src="https://github.com/user-attachments/assets/d81aad60-d448-41d7-8c24-022dc11359e1" />
<img width="378" height="881" alt="image" src="https://github.com/user-attachments/assets/1a6713f4-73b6-4333-8eaa-1547c766cc6d" />

