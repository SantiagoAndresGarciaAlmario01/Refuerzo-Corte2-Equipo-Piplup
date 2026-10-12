# 06 RF y RNF: Requisitos de AquaPort v2 con priorización MoSCoW

**Nivel:** Prinplup · **Sistema:** AquaPort v2 (15 drones de 3 tipos, asignación automática, alertas en tiempo real)

Los códigos continúan la numeración del proyecto (Piplup usó AP-01 a AP-06). Un **RF** dice qué hace el sistema; un **RNF** dice cómo de bien lo hace y siempre trae métrica, umbral, condición y forma de verificarlo.

## Requisitos Funcionales

Los 4 RF pedidos para las funcionalidades nuevas son **AP-07** (asignación automática), **AP-09** (alertas de fallo), **AP-10** (consulta de condiciones hídricas) y **AP-12** (gestión por el técnico). **AP-08** es el RF dado por el enunciado que entra en tensión con AP-07, y **AP-11** complementa las alertas.

| Código | Requisito funcional | Actor | Resultado observable | Dónde está en el código |
|---|---|---|---|---|
| **AP-07** | El sistema asigna automáticamente el drone **de mayor batería disponible** para cualquier misión hídrica, entre los drones aptos: disponibles, con batería ≥ 35 %, con capacidad para el peso de la carga y compatibles con las condiciones hídricas de la zona destino. | Sistema (lo dispara el Solicitante) | La misión pasa a `EN_TRANSITO` con un drone y el drone pasa a `EN_MISION`. | `AsignadorAutomatico`, `ValidadorAsignacion`, `MayorBateriaStrategy` |
| **AP-08** | Las misiones **CRÍTICAS** tienen prioridad absoluta: se atienden antes que las demás y reciben el drone **técnicamente más apto para la zona**, independientemente de la batería. | Sistema | En un lote, las CRÍTICAS se asignan primero y con un drone compatible que ya opera en la zona de partida cuando existe. | `asignarPorPrioridad`, `ZonaCercanaStrategy`, `PrioridadCriticaStrategy` |
| **AP-09** | Cuando un drone elegido está en **FALLO**, el sistema alerta al Centro de Control y al Técnico de Mantenimiento y busca otro drone para la misma misión. | Centro de Control, Técnico de Mantenimiento | Alerta registrada y la misión asignada a un drone alternativo. | `EventoMision.droneEnFallo`, `CentroControlObserver`, `TecnicoMantenimientoObserver` |
| **AP-10** | Antes de asignar, el sistema consulta las **condiciones hídricas** de la zona destino (agitación, profundidad requerida, temperatura) y descarta los tipos de drone que no pueden operar en ellas. | Sistema, API Condiciones Hídricas | Un superficial nunca se asigna con agua agitada; un semisumergido nunca a más de 1 m. | `ProveedorCondicionesHidricas`, `puedeOperarEn` de cada tipo |
| **AP-11** | Cuando una misión CRÍTICA no tiene ningún drone apto, el sistema emite una **alerta crítica** al Centro de Control. | Centro de Control ECI | Registro “ALERTA CRÍTICA: misión M-1043 sin drone disponible”. | `EventoMision.sinDrone`, `CentroControlObserver` |
| **AP-12** | El **Técnico de Mantenimiento** consulta las órdenes abiertas de los drones en FALLO y, al terminar la reparación, devuelve el drone a `DISPONIBLE` para que vuelva a ser candidato. | Técnico de Mantenimiento | La orden aparece en su lista; el drone reparado vuelve a la flota operable. | `TecnicoMantenimientoObserver.getOrdenesAbiertas`, `DroneAcuatico.cambiarEstado` |

## Requisitos No Funcionales

| Código | Atributo | Requisito medible | Verificación |
|---|---|---|---|
| **RNF-01** | Rendimiento | Asignar un lote de **10 misiones** sobre una flota de **30 drones** debe tardar **menos de 400 ms**. | `RendimientoAsignacionTest` con `assertTimeout(Duration.ofMillis(400), ...)` (puntos 13 y 14). |
| **RNF-02** | Fiabilidad / seguridad | El **100 %** de las asignaciones debe respetar batería ≥ 35 %, capacidad de carga del tipo y condiciones hídricas. | `ValidadorAsignacionTest` (34 %, mantenimiento, buceador con 400 g, agua agitada) y `AsignadorAutomaticoTest` con el límite 35 % / 34 %. |
| **RNF-03** | Disponibilidad de alertas | Cada evento de fallo debe llegar **exactamente una vez** a **cada** observador registrado. | `AsignacionConPatronesIntegracionTest` con `verify(..., times(1))`. |
| **RNF-04** | Mantenibilidad | Cobertura ≥ **80 %** de líneas y ramas **por clase** (JaCoCo), **0 bugs**, deuda técnica < **30 min** y duplicación < **5 %** en SonarQube en cada merge a `develop`. | `mvn verify` (falla si no se cumple la regla) y Quality Gate de SonarQube (puntos 13 y 14). |
| **RNF-05** | Extensibilidad | Agregar un tipo de drone nuevo no debe modificar **ninguna** clase existente del asignador, del validador ni de la fábrica. | Prueba OCP con `DroneSumergibleProfundo` (punto 04). |

## Priorización MoSCoW

| Requisito | Categoría | Justificación |
|---|---|---|
| AP-07 Asignación automática | **Must** | Es el cambio central de la v2; sin esto Prinplup es igual a Piplup. |
| AP-10 Condiciones hídricas | **Must** | Sin ellas la asignación automática puede mandar un superficial a agua agitada y perder la carga. |
| RNF-02 Asignaciones válidas | **Must** | Regla de seguridad del equipo y de las muestras. |
| AP-08 Prioridad CRÍTICA | **Should** | Muy importante para muestras urgentes, pero el sistema funciona sin ella (asignaría por batería). |
| AP-09 Alertas de fallo | **Should** | Evita que un drone en FALLO bloquee misiones; al principio se puede suplir con revisión manual. |
| RNF-01 < 400 ms | **Should** | Con 15 drones el riesgo es bajo; se fija para cuando la flota crezca. |
| RNF-03 Alertas exactamente una vez | **Should** | Alertas duplicadas generan órdenes de mantenimiento repetidas. |
| AP-12 Gestión por el técnico | **Should** | Sin ella los drones en FALLO no vuelven a la flota de forma controlada; al principio puede hacerse a mano. |
| AP-11 Alerta crítica sin drone | **Could** | Aporta trazabilidad; el operador también lo vería en el panel. |
| RNF-04 Calidad de código | **Could** (como requisito de producto) | Exigido al equipo, el usuario final no lo percibe directamente. |
| RNF-05 Extensibilidad | **Could** | Valor a futuro (Empoleon). |
| Rutas multi-etapa entre embalses | **Won't (esta versión)** | Se planea para Empoleon. |

## Tensión entre AP-07 y AP-08 y cómo se resolvió

- **AP-07** pide el drone de **mayor batería**: reparte el desgaste y deja margen para el regreso.
- **AP-08** pide el drone **técnicamente más apto para la zona** para las CRÍTICAS, independientemente de la batería: lo urgente es llegar rápido con un drone que ya opera ahí.

Aplicadas a la misma misión, las dos reglas pueden elegir drones distintos. Por ejemplo, para una CRÍTICA desde Laguna Sur, AP-07 elegiría AR-06 (88 %, Canal Central) y AP-08 elegiría AR-11 (81 %, Buceador compatible que ya está en Laguna Sur).

**Decisión:** no son contradictorios, pero hay que decidir cuál manda en cada caso; la regla depende de la prioridad. `PrioridadCriticaStrategy` usa `ZonaCercanaStrategy` para las CRÍTICAS y `MayorBateriaStrategy` para las demás, y `asignarPorPrioridad` procesa primero las CRÍTICAS para que no les gane un drone una misión menos urgente. El mínimo de 35 % se mantiene en los dos casos porque lo valida `ValidadorAsignacion` antes de cualquier estrategia.
