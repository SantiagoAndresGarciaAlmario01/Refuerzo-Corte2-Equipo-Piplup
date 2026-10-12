# 07 Plantilla DOSW: RF AP-01 “Registrar misión de transporte de muestra”

**Nivel:** Piplup

| Campo | Contenido |
|---|---|
| **Código** | AP-01 |
| **Nombre** | Registrar misión de transporte de muestra |
| **Actor** | Operador Hídrico |
| **Descripción** | Permite al Operador Hídrico registrar una solicitud de transporte entre dos zonas fijas del campus y asignarle manualmente un drone disponible con batería suficiente. El sistema genera un código de misión en estado `PENDIENTE`. |
| **Prioridad** | Must Have |
| **Precondiciones** | 1. El Operador Hídrico tiene sesión iniciada en AquaPort.<br>2. Debe existir al menos un drone con `disponible = true` y `bateria ≥ 35`.<br>3. Existe una solicitud de transporte del Solicitante (partida, llegada y tipo de carga). |
| **Postcondiciones** | 1. Existe una `Mision` con `estado = PENDIENTE` y un `id` único.<br>2. El drone asignado deja de estar disponible para otra misión.<br>3. El operador ve el código de misión generado. |

## Datos de entrada

| Dato | Tipo | Obligatorio | Restricción |
|---|---|---|---|
| `drone` | `DroneAcuatico(id:String, modelo:String, bateria:int, disponible:boolean, zona:String)` | Sí | `disponible = true` y `bateria ≥ 35` |
| `puntoPartida` | `String` | Sí | Una de: Embalse Norte, Canal Central, Laguna Sur, Punto Ribereño Este, Laboratorio Hídrico |
| `puntoLlegada` | `String` | Sí | Una de las 5 zonas fijas y distinta de `puntoPartida` |
| `tipoCarga` | `Enum(MUESTRA_AGUA, SENSOR, PAQUETE_LIGERO)` | Sí | - |

## Datos de salida

| Dato | Tipo | Descripción |
|---|---|---|
| `idMision` | `String` | Código de misión generado, formato `M-NNN` (p. ej. `M-016`) |
| `mision` | `Mision(id:String, drone:DroneAcuatico, puntoPartida:String, puntoLlegada:String, tipoCarga:TipoCarga, estado:EstadoMision)` | Misión creada con `estado = PENDIENTE` |
| `flotaDisponible` *(consulta del paso 2)* | `List<DroneAcuatico(id:String, bateria:int, zona:String)>` | Drones operables, ordenados de mayor a menor batería |

## Flujo básico

1. El Operador Hídrico selecciona la opción “Nueva misión”.
2. El sistema muestra la lista de drones disponibles con batería ≥ 35 % (`List<DroneAcuatico(id, bateria, zona)>`), ordenada de mayor a menor batería.
3. El Operador Hídrico ingresa `puntoPartida`, `puntoLlegada`, `tipoCarga` y selecciona el drone.
4. El sistema valida las zonas, que partida ≠ llegada, y la disponibilidad y batería del drone.
5. El sistema crea la misión en estado `PENDIENTE`, marca el drone como no disponible y muestra el código de misión generado.

## Flujos alternos

| Id | Condición (en el paso) | Comportamiento del sistema |
|---|---|---|
| **FA-01 Drone sin batería suficiente** | Paso 4: `drone.bateria < 35` | No crea la misión. Muestra: *“El drone AR-03 tiene batería insuficiente (18 %). Mínimo requerido: 35 %.”* y vuelve al paso 3 para elegir otro drone. |
| **FA-02 Zona de destino inválida** | Paso 4: `puntoLlegada` no pertenece a las 5 zonas fijas o es igual a `puntoPartida` | No crea la misión. Muestra: *“La zona 'X' no pertenece a las zonas fijas del campus”* (o *“El punto de partida y el de llegada deben ser diferentes”*) y vuelve al paso 3. |
| **FA-03 No hay drones disponibles** | Paso 2: la lista está vacía | Informa que no hay drones disponibles y el tiempo estimado del próximo en liberarse; ofrece dejar la solicitud en cola. |

## Reglas de negocio

| Código | Regla |
|---|---|
| **RN-01** | Un drone no puede tener más de una misión activa (`PENDIENTE` o `EN_TRANSITO`) simultáneamente. |
| **RN-02** | Solo se puede asignar un drone con batería ≥ 35 %. Si la batería está entre 35 % y 39 %, la asignación se permite pero el sistema emite una alerta de batería crítica. |

## Precondición vs. regla de negocio (por qué están separadas)

- *“Debe existir al menos un drone disponible con batería ≥ 35 %”* es **precondición**: se evalúa **antes** de iniciar; si no se cumple, el caso de uso ni siquiera arranca.
- *“Un drone no puede tener más de 1 misión activa”* es **regla de negocio**: es una restricción del dominio que se verifica **durante** la ejecución (paso 4) cada vez que se intenta asignar.

## Errores evitados a propósito

- No se usó **“Seleccionador”** como tipo: el drone, la zona y el tipo de carga tienen tipos reales (`DroneAcuatico(...)`, `String`, `Enum(...)`). Cómo se seleccionan en pantalla es decisión de UI, no del requisito.
- No se escribió **“Lista de objetos”**: toda lista indica su tipo y los atributos que contiene.
