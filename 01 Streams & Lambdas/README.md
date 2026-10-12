# 01 Streams & Lambdas: Coordinar rutas multi-etapa entre 60 drones

**Nivel:** Empoleon · **Red:** 60 drones (20 por tipo) en 4 zonas · **Herramientas:** `groupingBy`, `teeing`, `flatMap`, `reduce`, `Collector` propio, stream paralelo

## Qué cambió respecto a Prinplup

| Prinplup | Empoleon |
|---|---|
| 15 drones, 5 zonas sueltas | 60 drones en 4 zonas interconectadas por canales (`MapaHidrico`) |
| Misión de un solo trayecto | `MisionRuta` con origen, hasta 3 waypoints y destino; se parte en `Tramo`s |
| `averagingInt` para promedios | `Collector` propio que agrupa y promedia en una sola pasada |
| Selección de un drone | Un drone distinto por cada tramo, construido con `reduce` |

## Las 4 consultas del reto

| # | Consulta | Clase / método | Operaciones |
|---|---|---|---|
| 1 | **Eficiencia de cada zona** (entregadas / totales) | `ConsultorRed.eficienciaPorZona` | `groupingBy(destino, EnumMap, teeing(filtering(ENTREGADA, counting()), counting(), division))` |
| 2 | **Drone con mejor historial** de entregas exitosas | `ConsultorRed.droneConMejorHistorial` | `filter(exitosa)` + `groupingBy(droneId, counting())` + `max` por cantidad (desempate: id menor). Retorna `Optional<String>` |
| 3 | **Ruta óptima multi-etapa**: drone disponible más cercano por tramo | `PlanificadorRuta.planificar` | `reduce(PlanRuta.vacio(), agregarTramo, soloSecuencial)` y por tramo `filter(disponible).filter(apto).min(cercanía, luego batería)` |
| 4 | **Collector personalizado**: batería promedio por zona en una pasada | `BateriaPromedioPorZona` | Implementa `Collector<Drone, Map<Zona,long[]>, Map<Zona,Double>>` |

Extra: `ConsultorRed.cargaDeTrabajoPorZona` es el ejemplo del enunciado (`flatMap` de los waypoints de las misiones `EN_TRANSITO` + `groupingBy(zona, counting())`).

### Por qué `teeing` en la eficiencia

Para cada zona hacen falta dos conteos sobre el mismo grupo (entregadas y totales). `teeing` los calcula en el mismo recorrido y los combina con `(entregadas, total) -> (double) entregadas / total`. Con dos streams separados habría que recorrer la lista dos veces y luego cruzar dos mapas.

### Por qué `reduce` en la ruta

Un drone no puede tomar dos tramos de la misma ruta. La elección del tramo 2 depende de lo que se eligió en el tramo 1, así que el estado tiene que viajar de un paso al siguiente sin variables externas. `PlanRuta` es inmutable y sirve de acumulador: `plan.agregar(tramo, drone)` retorna un plan nuevo con los drones ya usados.

```java
return desviador.tramosSeguros(mision.tramos()).stream()
        .reduce(PlanRuta.vacio(), (plan, tramo) -> agregarTramo(plan, tramo, carga, flota),
                PlanificadorRuta::soloSecuencial);
```

El combinador `soloSecuencial` lanza `UnsupportedOperationException`: el plan depende del orden de los tramos y no tiene sentido construirlo en paralelo.

### El Collector personalizado

```java
public final class BateriaPromedioPorZona implements Collector<Drone, Map<Zona, long[]>, Map<Zona, Double>> {
    supplier()    -> new EnumMap<>(Zona.class)              // acumulador: zona -> [suma, cantidad]
    accumulator() -> sumar(acumulado, zona, bateria, 1)
    combiner()    -> une dos acumuladores sumando suma y cantidad
    finisher()    -> suma / cantidad por zona
}
```

`groupingBy(getZona, averagingInt(getBateria))` daría el mismo resultado, pero el reto pide escribir el Collector. Escribirlo deja ver las 4 piezas que Java usa por dentro. El combinador permite que funcione en paralelo: la prueba `bateriaPromedioPorZona_paralelo_mismoResultado` compara 600 drones en secuencial y en paralelo.

## Cuándo usar stream paralelo (y cuándo no)

| Caso | ¿Paralelo? | Por qué |
|---|---|---|
| `lecturasBajoUmbral` sobre el historial de telemetría (miles de lecturas) | **Sí** (`parallelStream`) | Cada lectura se evalúa sola, no hay estado compartido, `count()` combina sin importar el orden y el volumen crece con cada tramo recorrido. |
| Promedios, eficiencia o mejor historial sobre 60 drones | No | Con 60 elementos el costo de dividir el trabajo entre hilos es mayor que el cálculo. |
| Planificar la ruta (`reduce`) | No, nunca | Cada tramo depende de los drones elegidos en los anteriores; el orden importa. |
| Ejecutar la ruta (cambiar estado de drones, custodia) | No, nunca | Tiene efectos secundarios y la cadena de custodia exige orden estricto. |

## Flota de ejemplo (`FlotaEnterprise`)

| Drones | Tipo | Zona natural | Reserva en Lab. Central |
|---|---|---|---|
| AR-01 a AR-20 | Buceador | Embalse de Investigación | AR-16 a AR-20 |
| AR-21 a AR-40 | Superficial | Red de Canales | AR-36 a AR-40 |
| AR-41 a AR-60 | Semisumergido | Laguna de Reserva | AR-56 a AR-60 |

Batería determinista: `30 + (n * 37) % 70` (entre 30 % y 99 %), así cualquiera puede reproducir los resultados. `ConsultasRedApp` imprime la batería promedio por zona (Embalse 64,7 %, Canales 67,3 %, Laguna 65,3 %, Lab. Central 59,3 %) y el mejor historial.

## Pruebas (22)

`ConsultorRedTest` (7), `PlanificadorRutaTest` (6), `DesviadorRutaTest` (4), `PlanRutaTest` (3) y `FlotaEnterpriseTest` (2). Incluyen: zona con 1 de 2 entregas, historial sin entregas exitosas, misiones que no están en tránsito, Collector secuencial frente a paralelo y 10 000 lecturas de telemetría.

## Criterios del revisor

- Ningún `for`/`while` en las consultas; ninguna mutación de variables externas dentro de un stream.
- Lambdas de máximo 2 líneas; las más largas se extrajeron (`agregarTramo`, `combinar`, `promedios`).
- Métodos de máximo 15 líneas, clases de máximo 150.
- Sin `null`: las consultas sin resultado retornan `Optional`.

```bash
mvn test
mvn compile exec:java -Dexec.mainClass=edu.eci.dosw.aquaport.infraestructura.configuracion.ConsultasRedApp
```
