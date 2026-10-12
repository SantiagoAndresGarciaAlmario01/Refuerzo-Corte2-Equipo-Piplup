# 06 RF y RNF: Requisitos Enterprise con SLA, trazabilidad y recuperación ante fallos

**Nivel:** Empoleon · **Sistema:** AquaPort Enterprise (60 drones, 4 zonas, rutas multi-etapa) · Los códigos continúan la numeración del proyecto (Prinplup llegó hasta AP-12).

## Requisitos funcionales

| Código | Requisito | Actor | Resultado observable | En el código |
|---|---|---|---|---|
| **AP-13** | **Coordinación entre zonas.** El sistema traslada una carga entre zonas usando solo canales navegables del mapa hídrico; si dos zonas no tienen canal directo (Embalse y Laguna), la ruta pasa por la zona intermedia más corta. | Sistema | La ruta Embalse → Laguna se ejecuta como Embalse → Red de Canales → Laguna. | `MapaHidrico`, `DesviadorRuta` |
| **AP-14** | **Reasignación automática ante fallo en waypoint.** Si el drone que debe tomar el siguiente tramo está en FALLO o tiene menos de 20 % de batería, el sistema asigna el drone apto más cercano que no esté en el plan y avisa al técnico. | Sistema, Técnico | Evento `TRAMO_REASIGNADO`, orden de mantenimiento abierta y la ruta continúa. | `ReasignadorTramo` |
| **AP-15** | **Ejecutar misión multi-etapa con waypoints.** El sistema recibe origen, hasta 3 waypoints, destino y carga; planifica un drone por tramo validado por la cadena (batería, capacidad, zona activa, condiciones) y lo ejecuta tramo a tramo. | Solicitante, Operador | Misión `ENTREGADA` con un registro por tramo. | `ServicioMisionMultiEtapa`, `PlanificadorRuta`, `EjecutorRuta` |
| **AP-16** | **Trazabilidad de la cadena de custodia.** Cada traspaso de la muestra en un waypoint queda registrado en orden, con el drone, el tramo y un sello SHA-256; un registro fuera de orden o sin sello interrumpe la misión. | Laboratorio (LIMS) | Lista de `RegistroCustodia` completa y sellada; `CUSTODIA_INTERRUMPIDA` si no. | `CadenaCustodia`, `DroneConCifrado` |
| **AP-17** | **Reportes de eficiencia por zona.** El sistema calcula por zona la proporción de misiones entregadas, la batería promedio de la flota y la carga de trabajo de waypoints en tránsito. | Personal ECI | Tabla por zona (por ejemplo, Lab. Central 97 %). | `ConsultorRed`, `BateriaPromedioPorZona` |

## Requisitos no funcionales (métricas de producción)

| Código | Atributo | Requisito medible | Verificación |
|---|---|---|---|
| **RNF-E1** | Disponibilidad (SLA) | El servicio de rutas debe estar disponible el **99,5 %** del tiempo mensual (máximo 3 h 36 min de caída al mes), excluyendo ventanas de mantenimiento avisadas con 48 h. | Monitor de salud cada 60 s sobre el endpoint de la aplicación; reporte mensual. |
| **RNF-E2** | Tiempo de reasignación | Desde que un drone entra en FALLO en un waypoint hasta que el tramo tiene un drone nuevo: **≤ 5 s** en producción (p95) y **< 500 ms** en el núcleo sin red. | `RendimientoRedTest.ejecutar_reasignacionPorFallo_menosDe500ms` con `assertTimeout`. |
| **RNF-E3** | Throughput | Soportar **20 misiones multi-etapa simultáneas** sobre la red de 60 drones; el núcleo debe planificar y ejecutar 20 misiones en **< 2 s**. | `RendimientoRedTest.ejecutar_veinteMisiones_menosDeDosSegundos`. |
| **RNF-E4** | Integridad de la custodia | El **100 %** de los registros de custodia deben estar en orden y sellados con SHA-256; cero muestras aceptadas con custodia incompleta. | `CadenaCustodiaTest` y flujo alterno 5 de `FlujoMultiEtapaIntegracionTest`. |
| **RNF-E5** | Mantenibilidad | JaCoCo **≥ 85 %** de líneas y **≥ 75 %** de ramas; SonarQube **0 bugs, 0 vulnerabilidades, deuda < 15 min, duplicación < 3 %** en cada merge a `develop`. | `mvn verify` (regla JaCoCo) y Quality Gate del punto 13. |

## Priorización MoSCoW

| Requisito | Categoría | Justificación |
|---|---|---|
| AP-15 Misión multi-etapa | **Must** | Es el enunciado de Empoleon; sin rutas por tramos no hay versión Enterprise. |
| AP-16 Cadena de custodia | **Must** | Una muestra sin custodia completa no tiene validez para el laboratorio: entregarla es lo mismo que perderla. |
| AP-14 Reasignación automática | **Must** | Con 60 drones y rutas de varios tramos la probabilidad de un fallo en ruta es alta; sin reasignación la misión entera se pierde. |
| RNF-E4 Integridad de custodia | **Must** | Va de la mano con AP-16. |
| AP-13 Coordinación entre zonas | **Should** | Las rutas con canal directo funcionan sin ella; es necesaria para Embalse ↔ Laguna. |
| RNF-E2 Reasignación ≤ 5 s | **Should** | Una muestra de agua tolera unos segundos de espera en un waypoint, no minutos. |
| RNF-E3 20 misiones simultáneas | **Should** | Es la carga esperada en temporada de muestreo; fuera de ella hay menos. |
| RNF-E1 SLA 99,5 % | **Should** | El laboratorio puede reprogramar en caso de caída, pero no a diario. |
| AP-17 Reportes de eficiencia | **Could** | Aporta a la gestión, no a la operación diaria. |
| RNF-E5 Calidad de código | **Could** (como requisito de producto) | Es exigencia del equipo y del curso; el usuario no lo percibe directamente. |
| Planificación con costo de energía por distancia | **Won't (v3)** | Hoy el consumo es fijo por tramo; queda para una versión posterior. |

## Tensiones entre requisitos y cómo se resolvieron

| # | Tensión | Por qué chocan | Resolución |
|---|---|---|---|
| 1 | **AP-14 (reasignar) vs AP-16 (custodia)** | Cada reasignación agrega un traspaso de la muestra que la custodia debe registrar; reasignar en mitad de un tramo dejaría un tramo con dos drones y sin un registro claro. | Solo se reasigna **en waypoints, antes de empezar el tramo** (`ReasignadorTramo.droneListo`). El tramo siempre lo recorre un solo drone y su resultado sale sellado. |
| 2 | **AP-15 (drone más cercano) vs AP-13 (coordinación entre zonas)** | Elegir siempre el más cercano puede dejar una zona sin drones disponibles para su siguiente misión. | La cercanía se mide a la zona de inicio del tramo, así que normalmente gana un drone de esa zona; además 15 drones quedan de reserva en el Lab. Central. La carga por zona se vigila con AP-17 (`cargaDeTrabajoPorZona`). |
| 3 | **AP-13/AP-15 (desvío por condiciones) vs RNF-E3 (throughput)** | Un desvío convierte 1 tramo en 2: usa un drone más y más tiempo. | Solo se desvía si el canal es adverso (no navegable o turbidez ≥ 50 NTU). Se publica `DESVIO_POR_CONDICIONES` para que el operador sepa por qué la ruta es más larga. |
| 4 | **AP-14 (reasignar rápido) vs RNF-E4 (integridad)** | Para reasignar en menos de 5 s es tentador saltarse validaciones. | El reemplazo pasa por la misma cadena de validación que el plan original; el tiempo medido (< 500 ms en el núcleo) muestra que la validación completa cabe dentro del límite. |
| 5 | **AP-17 (reportes) vs RNF-E3 (throughput)** | Las consultas sobre el historial de telemetría crecen con cada tramo y compiten con la operación. | Las consultas pesadas usan stream paralelo (`lecturasBajoUmbral`) y en producción leen de la base de misiones, no del flujo en vivo. |
