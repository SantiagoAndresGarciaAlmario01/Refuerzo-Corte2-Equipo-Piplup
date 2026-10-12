# 07 Plantilla DOSW: RF AP-07 “Asignar automáticamente el drone más adecuado”

**Nivel:** Prinplup · Mismo formato que AP-01 de Piplup, ahora con datos de entrada compuestos (sub-índices), salidas del sistema y flujos alternos de la asignación automática.

| Campo | Contenido |
|---|---|
| **Código** | AP-07 |
| **Nombre** | Asignar automáticamente el drone más adecuado a una misión |
| **Actor principal** | Sistema AquaPort (el caso lo inicia el Solicitante al registrar la solicitud) |
| **Actores secundarios** | API Condiciones Hídricas, Centro de Control ECI, Técnico de Mantenimiento |
| **Descripción** | A partir de la solicitud del Solicitante, el sistema consulta las condiciones del agua en la zona destino, filtra los drones aptos de la flota y elige uno con la estrategia activa (mayor batería; el más cercano si la misión es CRÍTICA). La misión queda `EN_TRANSITO` y el drone `EN_MISION`. |
| **Prioridad** | Must Have |
| **Precondiciones** | 1. Existe una solicitud de transporte con zonas, carga y prioridad.<br>2. La flota está registrada (15 drones de 3 tipos).<br>3. La API Condiciones Hídricas responde para la zona destino. |
| **Postcondiciones (éxito)** | 1. La misión tiene `estado = EN_TRANSITO` y un `droneAsignado`.<br>2. El drone asignado tiene `estado = EN_MISION` y no puede tomar otra misión.<br>3. El Centro de Control recibe el evento `MISION_ASIGNADA`. |
| **Postcondiciones (fracaso)** | La misión sigue `PENDIENTE` sin drone y se emite el evento `MISION_SIN_DRONE` (alerta crítica si la misión es CRÍTICA). |

## Datos de entrada

| Dato | Tipo | Obligatorio | Restricción |
|---|---|---|---|
| `id` | `String` | Sí | Formato `M-NNNN`, no vacío |
| `zonaPartida` | `Enum Zona(EMBALSE_NORTE, CANAL_CENTRAL, LAGUNA_SUR, RIBERA_ESTE, LAB_HIDRICO)` | Sí | - |
| `zonaDestino` | `Enum Zona(...)` | Sí | Distinta de `zonaPartida` |
| `carga` | Objeto compuesto | Sí | Ver sub-índices |
| `carga.peso` | `int` (gramos) | Sí | 1 a 1500 |
| `carga.tipo` | `Enum TipoCarga(MUESTRA_AGUA, SENSOR, PAQUETE_LIGERO, EQUIPO_MEDICION)` | Sí | - |
| `carga.prioridad` | `Enum Prioridad(CRITICA, ALTA, NORMAL, BAJA)` | No | Por defecto `NORMAL` |

## Datos de salida

| Dato | Tipo | Descripción |
|---|---|---|
| `mision.estado` | `Enum EstadoMision` | `EN_TRANSITO` si se asignó; `PENDIENTE` si no hubo drone |
| `droneAsignado.id` | `String` | Ej. `AR-11` |
| `droneAsignado.tipo` | `String` | `SUPERFICIAL`, `SEMISUMERGIDO` o `BUCEADOR` |
| `droneAsignado.bateria` | `int` | Porcentaje al momento de asignar |
| `razon` | `String` | Por qué se eligió (ej. “ya está en Laguna Sur, 81 %, puede operar a 10 m”) |
| `condiciones` | `CondicionesHidricas(agitacion: NivelAgitacion, profundidadRequeridaMetros: int, temperaturaCelsius: double)` | Condiciones con las que se tomó la decisión |

## Flujo básico

1. El Solicitante registra la solicitud con `zonaPartida`, `zonaDestino` y `carga` (peso, tipo, prioridad).
2. El sistema valida los datos de entrada (zonas distintas, peso entre 1 y 1500 g).
3. El sistema consulta a la API Condiciones Hídricas la `zonaDestino`.
4. El sistema filtra los drones aptos: `DISPONIBLE`, batería ≥ 35 %, capacidad ≥ `carga.peso` y tipo compatible con las condiciones.
5. El sistema elige un drone con la estrategia activa: el más cercano a `zonaPartida` si la misión es CRÍTICA, el de mayor batería en otro caso.
6. El sistema cambia el drone a `EN_MISION`, la misión a `EN_TRANSITO` y publica el evento `MISION_ASIGNADA`.
7. El sistema muestra al Operador y al Solicitante el código de misión, el drone asignado y la razón.

## Flujos alternos

| Id | Condición (paso) | Comportamiento del sistema |
|---|---|---|
| **FA-01 No hay drones disponibles** | Paso 4: la lista de aptos queda vacía | La misión queda `PENDIENTE`, se publica `MISION_SIN_DRONE`. Si es CRÍTICA el Centro de Control recibe “ALERTA CRÍTICA: misión M-1043 sin drone disponible” y el operador ve el banner de emergencia. |
| **FA-02 Condiciones hídricas adversas** | Paso 4: agitación ALTA o profundidad mayor a la soportada | Se descartan los tipos que no pueden operar (con agitación ALTA solo queda el semisumergido; a 10 m solo el buceador). Si ninguno puede, continúa en FA-01. |
| **FA-03 La carga supera la capacidad del tipo** | Paso 4: `carga.peso > capacidad` | Se descartan los drones con menor capacidad (superficial 500 g, buceador 300 g, semisumergido 1500 g). Si la carga solo cabe en un tipo que no puede operar en las condiciones, continúa en FA-01. |
| **FA-04 El drone elegido está en FALLO** | Paso 5: la estrategia elige un drone que entró en `FALLO` | Se publica `DRONE_EN_FALLO` (el técnico recibe la orden de mantenimiento), el drone se excluye y se repite el paso 5 con los demás aptos. |
| **FA-05 API de condiciones no responde** | Paso 3 | Se reintenta; si no hay respuesta se usan las condiciones más restrictivas (agitación ALTA) para no arriesgar la carga. |

## Reglas de negocio

| Código | Regla |
|---|---|
| **RN-01** | Ningún drone con batería menor al **35 %** puede ser asignado (se mantiene desde Piplup). |
| **RN-02** | Un **Buceador** no se asigna a cargas de más de **300 g**. |
| **RN-03** | Un **Superficial** solo opera en superficie con agitación BAJA. |
| **RN-04** | Un drone tiene como máximo una misión activa (`EN_TRANSITO`). |
| **RN-05** | En un lote, las misiones CRÍTICAS eligen drone antes que las demás. |

## Por qué así

- **Sub-índices en la carga**: peso, tipo y prioridad viajan juntos y cada uno tiene su restricción; escribir “carga: objeto” no permitiría validar nada.
- **`zonaDestino` es enum**: en Piplup era `String` y obligaba a validar texto; ahora una zona inválida no se puede ni construir.
- **Las salidas incluyen `razon`**: con asignación automática el operador necesita saber por qué el sistema eligió ese drone para confiar en la decisión (Nielsen #1, visibilidad del estado).
- FA-01 y FA-04 tienen prueba en `AsignadorAutomaticoTest`; FA-02 y FA-03 en `ValidadorAsignacionTest`. FA-05 queda especificado para cuando se implemente el cliente real de la API (hoy `ProveedorCondicionesHidricas` es una interfaz).
