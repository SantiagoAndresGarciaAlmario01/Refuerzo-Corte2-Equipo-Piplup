# 01 Streams & Lambdas: Consultas sobre la flota de 15 drones

**Nivel:** Prinplup · **Flota:** 15 drones de 3 tipos (Superficial, Semisumergido, Buceador) · **Herramientas:** Streams, Collectors, `Optional`, referencias a métodos

## Qué cambió respecto a Piplup

| Piplup | Prinplup |
|---|---|
| 4 drones Aqua-Ranger 100 iguales (`record`) | 15 drones de 3 tipos con jerarquía (`DroneAcuatico` abstracta) |
| `disponible: boolean` | `EstadoDrone` (DISPONIBLE, EN_MISION, RECARGANDO, MANTENIMIENTO, SUMERGIDO, FALLO) |
| Zonas como `String` | `enum Zona` (se eliminan errores de escritura y comparaciones de texto) |
| Misiones sin prioridad | `Prioridad` CRITICA, ALTA, NORMAL, BAJA |
| `filter`, `sorted`, `map`, `count` | además `groupingBy`, `averagingInt`, `partitioningBy`, `flatMap`, comparador compuesto |

## Las 5 consultas de `ConsultorFlota`

| # | Consulta | Operaciones | Por qué así |
|---|---|---|---|
| 1 | Drones **disponibles agrupados por tipo** | `filter(DroneAcuatico::estaDisponible)` + `groupingBy(getTipo, TreeMap::new, toList())` | El `TreeMap` deja los tipos en orden alfabético, así el resultado es estable y se puede probar sin depender del orden de un `HashMap`. |
| 2 | **Drone óptimo** para las condiciones de una zona | 3 `filter` (disponible, batería ≥ 35 %, `puedeOperarEn`) + `max(comparingInt(getBateria))` | Retorna `Optional<DroneAcuatico>`: si ningún tipo sirve (por ejemplo 80 m de profundidad) no hay `null`, hay `Optional.empty()`. Cada tipo decide si puede operar (polimorfismo), el stream no pregunta por el tipo. |
| 3 | **Promedio de batería por tipo** | `groupingBy(getTipo, TreeMap::new, averagingInt(getBateria))` | Un solo recorrido calcula los tres promedios. |
| 4 | **Separar misiones CRÍTICAS** de las demás | `partitioningBy(Mision::esCritica)` | `partitioningBy` siempre crea las dos llaves (`true` y `false`), incluso vacías; con `groupingBy` la llave `true` podría no existir y obligaría a validar `null`. |
| 5 | **Zonas únicas** cubiertas por la flota activa | `filter(ESTADOS_ACTIVOS::contains)` + `map(getZona)` + `toCollection(EnumSet)` | “Activa” = DISPONIBLE, EN_MISION o SUMERGIDO. El `EnumSet` elimina duplicados y conserva el orden del `enum`. |

### Extras pedidos por el nivel Prinplup

- **Comparador compuesto** (`ordenarPorPrioridadYBateria`): `Comparator.comparing(Mision::getPrioridad).thenComparing(porBateriaDescendente)`. Primero CRITICA, luego ALTA, NORMAL y BAJA; a igual prioridad va primero la misión cuyo drone tiene más batería. Una misión sin drone cuenta como batería 0 (`map(...).orElse(0)`), sin `null`.
- **`flatMap`** (`idsDeDronesAsignados`): `map(Mision::getDrone).flatMap(Optional::stream)` aplana los `Optional` y descarta las misiones que aún no tienen drone, sin un `if`.

## Flota de ejemplo (`FlotaEjemplo`)

| Tipo | Drones (batería, zona, estado) |
|---|---|
| Superficial (500 g, solo agua tranquila) | AR-01 92 % EN disp. · AR-02 45 % CC disp. · AR-03 18 % LS recargando · AR-04 73 % RE disp. · AR-05 60 % LAB en misión |
| Semisumergido (1500 g, aguanta agua agitada, ≤ 1 m) | AR-06 88 % CC disp. · AR-07 34 % EN disp. · AR-08 70 % LS mantenimiento · AR-09 55 % RE disp. · AR-10 97 % EN en misión |
| Buceador (300 g, hasta 30 m, no con agitación alta) | AR-11 81 % LS disp. · AR-12 35 % LAB disp. · AR-13 64 % EN sumergido · AR-14 50 % CC fallo · AR-15 77 % RE disp. |

Los valores incluyen a propósito los límites de batería (34 % y 35 %) y todos los estados, para que las pruebas cubran los casos borde.

## Salida de `AquaPortStreamsApp`

```
1. Disponibles por tipo:
   BUCEADOR -> [AR-11, AR-12, AR-15]   SEMISUMERGIDO -> [AR-06, AR-07, AR-09]   SUPERFICIAL -> [AR-01, AR-02, AR-04]
2. Óptimo para Canal Central agitado: AR-06
3. Promedio de batería por tipo: {BUCEADOR=61.4, SEMISUMERGIDO=68.8, SUPERFICIAL=57.6}
4. Críticas / demás: {false=[M-702, M-704], true=[M-701, M-703]}
5. Zonas de la flota activa: [EMBALSE_NORTE, CANAL_CENTRAL, LAGUNA_SUR, RIBERA_ESTE, LAB_HIDRICO]
```

## Pruebas (12)

`ConsultorFlotaTest` cubre cada consulta con su caso normal y su caso borde: tipo sin disponibles, agua agitada (gana AR-06), 10 m de profundidad (gana un buceador), profundidad imposible (`Optional.empty()`), partición sin críticas, drone en FALLO que no aporta zona, comparador compuesto y `flatMap` con misiones sin drone.

## Criterios del revisor que se cumplen

- Ningún `for`/`while` en las consultas y ninguna mutación de estado externo dentro de los streams.
- Referencias a métodos (`DroneAcuatico::getTipo`, `Optional::stream`) en lugar de lambdas equivalentes.
- Ningún método retorna `null`; las consultas que pueden no tener resultado retornan `Optional`.
- Métodos de menos de 20 líneas.

## Ejecutar

```bash
mvn test
mvn compile exec:java -Dexec.mainClass=edu.eci.dosw.aquaport.AquaPortStreamsApp
```
