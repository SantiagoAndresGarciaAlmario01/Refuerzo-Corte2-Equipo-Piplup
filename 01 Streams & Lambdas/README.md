# 01 Streams & Lambdas: Filtrar y ordenar la flota de drones acuáticos

**Nivel:** Piplup · **Proyecto:** AquaPort MVP · **Operaciones:** `filter` · `map` · `sorted` · `toList` · `anyMatch` · `count` · `max`

## Contexto del reto

Antes de asignar una misión, el Operador Hídrico necesita consultar la flota: ver qué drones están disponibles, ordenarlos por batería y saber cuántos tienen energía suficiente (≥ 35 %) para operar.

## Estructura

```
01 Streams & Lambdas/
├── pom.xml
└── src
    ├── main/java/edu/eci/dosw/aquaport
    │   ├── AquaPortStreamsApp.java   # main que imprime las 5 consultas
    │   ├── consulta/ConsultorFlota.java   # un método por consulta
    │   └── modelo/DroneAcuatico.java   # record del enunciado
    └── test/java/edu/eci/dosw/aquaport/consulta/ConsultorFlotaTest.java
```

## Las 5 consultas

| # | Pregunta del operador | Método | Cadena del Stream | Terminal |
|---|---|---|---|---|
| 1 | Disponibles con batería ≥ 35 %, de mayor a menor | `obtenerDisponiblesConBateriaSuficiente` | `filter(disponible)` → `filter(batería ≥ 35)` → `sorted(comparingInt(bateria).reversed())` | `toList()` |
| 2 | IDs de los drones disponibles | `obtenerIdsDisponibles` | `filter(disponible)` → `map(id)` | `toList()` |
| 3 | ¿Hay algún drone disponible con batería ≥ 35 %? | `existeDroneDisponibleConBateriaSuficiente` | `filter(disponible)` | `anyMatch(batería ≥ 35)` |
| 4 | ¿Cuántos drones están disponibles? | `contarDisponibles` | `filter(disponible)` | `count()` |
| 5 | Drone con mayor batería de la flota | `obtenerDroneConMayorBateria` | - | `max(comparingInt(bateria))` → `Optional<DroneAcuatico>` |

### Salida con la flota de ejemplo

```
1. Disponibles con batería >= 35% (mayor a menor):
   AR-01 -> 92%
   AR-04 -> 73%
   AR-02 -> 45%
2. IDs disponibles: [AR-01, AR-02, AR-04]
3. ¿Existe drone operable?: true
4. Drones disponibles: 3
5. Mayor batería: AR-01 (92%)
```

## Decisiones de diseño (justificación)

- **Un Stream por consulta, de una sola cadena.** Cada método crea su propio `flota.stream()` y lo cierra con una única operación terminal. Ningún Stream se reutiliza (eso lanzaría `IllegalStateException`).
- **Cero `for`/`while` y cero estado mutable externo.** No hay contadores del tipo `int[] count = {0}`; el conteo lo hace `count()` y la existencia `anyMatch()`, que además corta en cuanto encuentra el primer caso (short-circuit).
- **Referencias a método en vez de lambdas largas.** `DroneAcuatico::disponible`, `DroneAcuatico::id`, `this::tieneBateriaSuficiente`. La regla de batería vive en un método privado con nombre descriptivo, no repetida en dos lambdas.
- **`Comparator.comparingInt(...).reversed()`** para el orden descendente: evita el error clásico de restar enteros en el comparador y no hace boxing.
- **Constante `BATERIA_MINIMA_OPERACION = 35`** en vez de número mágico.
- **La consulta 5 retorna `Optional`, nunca `null`.** Si la flota está vacía el operador recibe `Optional.empty()` y el `main` lo muestra como “flota vacía”.
- **Validación de parámetros al inicio:** una flota `null` lanza `NullPointerException` con mensaje en español (`Objects.requireNonNull`).
- **El `record` valida sus invariantes** (id obligatorio, batería 0–100) en el constructor compacto, así ninguna consulta recibe un drone inválido.
- **SRP:** `ConsultorFlota` solo responde preguntas sobre la flota; la impresión vive en `AquaPortStreamsApp`.

## Pruebas (10)

Cubren el ejemplo del enunciado y casos edge: batería exactamente en 35 % (límite incluido), drone con batería alta pero no disponible, flota vacía → `Optional.empty()`, flota nula y batería fuera de rango.

## Cómo ejecutarlo

```bash
mvn test
mvn -q compile exec:java -Dexec.mainClass=edu.eci.dosw.aquaport.AquaPortStreamsApp   # o ejecutar el main desde el IDE
```
