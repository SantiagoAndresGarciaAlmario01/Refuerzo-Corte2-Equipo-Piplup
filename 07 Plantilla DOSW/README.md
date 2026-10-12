# 07 Plantilla DOSW: RF AP-15 “Ejecutar misión multi-etapa con waypoints”

**Nivel:** Empoleon · Datos anidados (waypoints, carga y resultado por tramo), flujo básico de 10 pasos, flujos de reasignación y reglas de coordinación.

| Campo | Contenido |
|---|---|
| **Código** | AP-15 |
| **Nombre** | Ejecutar misión multi-etapa con waypoints |
| **Actor principal** | Solicitante (crea la solicitud) · Sistema AquaPort (ejecuta) |
| **Actores secundarios** | Operador Hídrico, API Condiciones Hídricas, Centro de Control ECI, Sistema de Alertas, LIMS, Técnico de Mantenimiento |
| **Descripción** | Lleva una carga desde una zona de origen hasta un destino pasando por waypoints. Cada tramo lo recorre un drone distinto, elegido como el más cercano que aprueba la cadena de validación. En cada waypoint la muestra cambia de drone y el traspaso queda en la cadena de custodia sellada. |
| **Prioridad** | Must Have |
| **Precondiciones** | 1. La solicitud tiene origen, destino y carga válidos.<br>2. La red de 60 drones está registrada en `FlotaEnterprise`.<br>3. Las zonas de la ruta existen en el `MapaHidrico`. |
| **Postcondiciones (éxito)** | 1. La misión queda `ENTREGADA`.<br>2. Hay un `RegistroCustodia` sellado por cada tramo, en orden.<br>3. La custodia se envía al LIMS y cada drone vuelve a `DISPONIBLE` en la zona donde terminó. |
| **Postcondiciones (fracaso)** | La misión queda `EN_ESPERA` (destino inactivo) o `FALLIDA` (sin drone apto o custodia interrumpida), con el evento correspondiente publicado. |

## Datos de entrada

| Nombre | Tipo de campo | Reglas | Oblig. |
|---|---|---|---|
| `id` | String | Formato `ME-NNNN` | Sí |
| `origen` | Enum Zona(EMBALSE_INVESTIGACION, RED_CANALES, LAGUNA_RESERVA, LAB_CENTRAL) | - | Sí |
| `waypoints` | Lista | 0 a 3 elementos, en orden de paso | No |
| `waypoints[i].id` | String | Formato `WP-N`, no vacío | Sí (por waypoint) |
| `waypoints[i].zona` | Enum Zona | Distinta de la parada anterior | Sí (por waypoint) |
| `destino` | Enum Zona | Distinta de la última parada | Sí |
| `carga` | - | - | Sí |
| `carga.peso` | Integer | Entre 1 y 1500 gramos | Sí |
| `carga.tipo` | Enum(MUESTRA_AGUA, SENSOR, PAQUETE_LIGERO, EQUIPO_MEDICION) | - | Sí |
| `carga.prioridad` | Enum(CRITICA, ALTA, NORMAL, BAJA) | - | Sí |

## Datos de salida

| Nombre | Tipo de campo | Reglas |
|---|---|---|
| `estado` | Enum(ENTREGADA, EN_ESPERA, FALLIDA) | Estado final de la misión |
| `tramos` | Lista | Calculada por el sistema (puede tener más elementos que paradas − 1 si hubo desvío) |
| `tramos[i].orden` | Integer | Empieza en 1, consecutivo |
| `tramos[i].origen` / `tramos[i].destino` | Enum Zona | Unidas por un canal navegable |
| `tramos[i].droneAsignado.id` | String | Formato `AR-XX` |
| `tramos[i].droneAsignado.tipo` | String | SUPERFICIAL / SEMISUMERGIDO / BUCEADOR |
| `custodia` | Lista | Un registro por tramo recorrido |
| `custodia[i].droneId` | String | Drone que llevó la muestra en ese tramo |
| `custodia[i].sello` | String | SHA-256 en hexadecimal (64 caracteres) |
| `eventos` | Lista de EventoRuta(tipo, misionId, detalle) | Publicados a los observadores durante la ejecución |

## Flujo básico

1. El Solicitante registra la misión con origen, waypoints, destino y carga.
2. El sistema valida los datos (máximo 3 waypoints, paradas consecutivas distintas, peso entre 1 y 1500 g).
3. El sistema verifica que la zona de destino esté activa.
4. El sistema divide la ruta en tramos: origen → waypoint 1 → … → destino.
5. Por cada tramo consulta a la API las condiciones del canal (nivel, turbidez, oleaje).
6. Por cada tramo elige el drone disponible más cercano a la zona de inicio que aprueba la cadena de validación (batería ≥ 35 %, capacidad, zona destino activa, puede operar en esas condiciones). Desempate: mayor batería. Un drone no repite tramo.
7. El sistema confirma el plan y registra la misión ante el Centro de Control.
8. Para cada tramo, el drone asignado lo recorre; el sistema descuenta batería, mueve el drone y registra la lectura de telemetría.
9. Al llegar al waypoint el sistema registra el traspaso en la cadena de custodia con el sello del tramo y publica `WAYPOINT_ALCANZADO`.
10. Al completar el último tramo la misión queda `ENTREGADA`, se publica `MISION_ENTREGADA` y la custodia se envía al LIMS.

## Flujos alternos

| Id | Condición (paso) | Comportamiento |
|---|---|---|
| **FA-01 Fallo de drone en waypoint intermedio** | Paso 8: el drone del siguiente tramo está en `FALLO` | Se busca el drone apto más cercano que no esté en el plan; si existe, se publica `TRAMO_REASIGNADO`, se abre una orden de mantenimiento y se continúa en el paso 8. Si no existe, la misión queda `FALLIDA`. |
| **FA-02 Condición hídrica adversa en un tramo** | Paso 5: el canal del tramo no es navegable (< 0,5 m) o la turbidez es ≥ 50 NTU | El tramo se reemplaza por dos tramos a través de la zona intermedia más corta, se renumeran los tramos y se publica `DESVIO_POR_CONDICIONES`. Si no hay zona intermedia, el tramo se mantiene y la cadena lo rechaza en el paso 6. |
| **FA-03 Zona de destino temporalmente inactiva** | Paso 3 | No se planifica; la misión queda `EN_ESPERA` y se publica `ZONA_DESTINO_INACTIVA`. Se reintenta cuando el Administrador reactive la zona. |
| **FA-04 Batería crítica en mitad de ruta** | Paso 8: el drone del siguiente tramo bajó de 20 % | Se publica `BATERIA_CRITICA_EN_RUTA` y se reasigna igual que FA-01. |
| **FA-05 Cadena de custodia interrumpida** | Paso 9: el resultado del tramo llega sin sello o con un orden que no corresponde | Se publica `CUSTODIA_INTERRUMPIDA`, la misión queda `FALLIDA` y se alerta al Centro de Control. |
| **FA-06 Sin drone apto para un tramo** | Paso 6 | La misión queda `FALLIDA` con `MISION_FALLIDA` indicando cuántos tramos quedaron sin drone. |

## Reglas de negocio

| Código | Regla |
|---|---|
| **RN-01** | Ningún drone con batería menor al **35 %** puede ser asignado a un tramo (se mantiene desde Piplup). |
| **RN-02** | Un **Buceador** no se asigna a cargas de más de **300 g**; un Superficial a más de 500 g. |
| **RN-03** | Un drone recorre **un solo tramo** por misión. |
| **RN-04 · Cadena de custodia** | Cada tramo genera exactamente un registro con drone, origen, destino y sello SHA-256 del resultado. Los registros se aceptan solo en orden consecutivo. Un registro sin sello o fuera de orden invalida la misión: la muestra no se entrega al LIMS. |
| **RN-05 · Momento de la reasignación** | La reasignación solo ocurre en un waypoint, antes de que empiece el tramo; nunca en mitad de un tramo, para que la custodia no tenga dos drones en el mismo registro. |
| **RN-06 · Umbrales de reasignación automática** | Se reasigna si el drone está en `FALLO` o si su batería es **< 20 %**. Entre 20 % y 34 % el drone puede terminar el tramo que ya tiene asignado, pero no recibe tramos nuevos (RN-01). |
| **RN-07 · Criterio del reemplazo** | El reemplazo es el drone disponible más cercano a la zona de inicio del tramo, aprobado por la misma cadena de validación y que no esté en el plan de la misión. |
| **RN-08 · Coordinación de zonas** | Solo se usan canales del mapa hídrico. Embalse y Laguna no tienen canal directo: se pasa por la Red de Canales. |

## Trazabilidad con el código y las pruebas

| Elemento | Clase | Prueba |
|---|---|---|
| Flujo básico | `ServicioMisionMultiEtapa`, `PlanificadorRuta`, `EjecutorRuta` | `ejecutar_rutaNormal_entregadaConCustodiaCompleta` |
| FA-01 | `ReasignadorTramo` | `ejecutar_falloEnWaypoint_reasignaYEntrega`, `ejecutar_falloEnWaypointSinReemplazo_fallida` |
| FA-02 | `DesviadorRuta` | `ejecutar_condicionAdversaEnTramo_desviaRuta` |
| FA-03 | `ServicioMisionMultiEtapa` | `ejecutar_zonaDestinoInactiva_enEspera` |
| FA-04 | `ReasignadorTramo` | `ejecutar_bateriaCriticaEnRuta_reasigna` |
| FA-05 | `CadenaCustodia` | `ejecutar_custodiaInterrumpida_fallida` |
| FA-06 | `PlanRuta.estaCompleto` | `ejecutar_planIncompleto_fallida` |
