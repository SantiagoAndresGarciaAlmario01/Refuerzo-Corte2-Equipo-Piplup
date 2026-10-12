# 14 Arquitectura por capas: Auditoría final de AquaPort Enterprise

**Nivel:** Empoleon · `dominio` · `aplicacion` · `infraestructura` · reporte de dependencias con `grep` y prueba automática de capas

## Estructura

```
edu.eci.dosw.aquaport
├── dominio/                  ← solo Java estándar
│   ├── modelo/               Drone, DroneAcuatico (+3 tipos), MisionRuta (+Builder), Tramo, Waypoint,
│   │                         CadenaCustodia, MapaHidrico, CondicionesHidricas, Carga, enums
│   ├── puerto/               ProveedorCondicionesHidricas, EstadoZonas, RegistroTelemetria, RepositorioMisiones
│   ├── validacion/           Chain of Responsibility: EslabonValidacion + 4 validadores
│   ├── decorador/            Decorator: DroneDecorador, DroneConMonitoreo, DroneConCifrado
│   ├── fabrica/              Factory Method: FabricaDrones, CreadorDrone
│   └── evento/               EventoRuta, TipoEventoRuta, ObservadorRuta
├── aplicacion/               ← casos de uso; depende solo de dominio
│   ├── planificacion/        PlanificadorRuta, DesviadorRuta, PlanRuta, CriterioAptitud
│   ├── ejecucion/            ServicioMisionMultiEtapa (AP-15), EjecutorRuta, ReasignadorTramo, PublicadorEventosRuta
│   └── consulta/             ConsultorRed, BateriaPromedioPorZona
└── infraestructura/          ← implementaciones de puertos, adaptadores y configuración
    ├── api/                  AdaptadorAPIHidrica (Jackson), ClienteHttpAPIHidrica (java.net.http)
    ├── persistencia/         RepositorioMisionesEnMemoria, EstadoZonasEnMemoria
    ├── telemetria/           RegistroTelemetriaEnMemoria
    ├── notificacion/         CentroControlObservador, TecnicoMantenimientoObservador
    └── configuracion/        ConfiguracionAquaPort (raíz de composición), FlotaEnterprise, *App
```

**Dirección de dependencias:** `infraestructura → aplicacion → dominio`. El dominio no conoce a nadie.

## Herramienta 1: revisión con grep

[`scripts/auditar-capas.sh`](scripts/auditar-capas.sh) cuenta los imports de cada capa y revisa 4 reglas. Salida completa en [`docs/reporte-dependencias.txt`](docs/reporte-dependencias.txt):

```
== Imports por capa (paquete importado -> cantidad) ==
[dominio]
        38 java.util
        38 edu.eci.dosw.aquaport.dominio
         2 java.security
         1 java.nio
[aplicacion]
        49 edu.eci.dosw.aquaport.dominio
        40 java.util
         7 edu.eci.dosw.aquaport.aplicacion
[infraestructura]
        46 edu.eci.dosw.aquaport.dominio
        28 java.util
         9 edu.eci.dosw.aquaport.aplicacion
         5 edu.eci.dosw.aquaport.infraestructura
         4 java.net
         4 com.fasterxml.jackson
         ...
== Regla 1: dominio solo importa java.* y su propio paquete ==
OK: 0 imports externos en dominio
== Regla 2: aplicacion no importa infraestructura ni librerías externas ==
OK: aplicacion depende solo de dominio
== Regla 3: ningún new de infraestructura fuera de infraestructura ==
OK: 0 instancias de infraestructura en dominio o aplicacion
== Regla 4: inyección por constructor (sin setters de dependencias ni campos mutables en servicios) ==
OK: todos los campos de aplicacion son final y no hay setters
== Resultado ==
0 violaciones de capa
```

El script termina con código 1 si encuentra una violación, así que también sirve en un pipeline de CI.

## Herramienta 2: prueba automática (`ArquitecturaCapasTest`)

Las mismas reglas como prueba JUnit, sin librerías extra: lee los `.java` de `src/main/java` y compara cada import con lo permitido para su capa.

| Prueba | Regla |
|---|---|
| `dominio_noDependeDeNadaExterno` | Imports de `dominio` solo `java.*` y `dominio.*` |
| `aplicacion_noDependeDeInfraestructura` | Imports de `aplicacion` solo `java.*`, `dominio.*` y `aplicacion.*` |
| `capasInternas_noInstancianInfraestructura` | Ningún archivo de dominio o aplicación menciona `edu.eci.dosw.aquaport.infraestructura` |
| `aplicacion_camposFinales` | Todo campo de instancia en aplicación es `private final` (inyección por constructor) |

Se eligió esta opción en lugar de ArchUnit para no agregar una dependencia solo para la auditoría; las reglas son las mismas y fallan igual en `mvn test`. Si el proyecto crece a reglas más finas (ciclos entre paquetes, nombres por capa), migrar a ArchUnit es directo.

## Mapa de dependencias

![Mapa de dependencias](docs/mapa-dependencias.png)

Generado a partir de los imports reales (el mismo del punto 04). Ninguna flecha sale del cluster de dominio hacia afuera ni del de aplicación hacia infraestructura.

## Resultado de la auditoría

| Verificación | Resultado |
|---|---|
| Imports de librerías externas en `dominio` | **0** |
| Imports de `infraestructura` en `dominio` o `aplicacion` | **0** |
| `new` de clases de infraestructura fuera de `infraestructura` | **0** (todas se crean en `ConfiguracionAquaPort`) |
| Servicios con inyección por campo o setter | **0** |
| Jackson | solo en infraestructura: el adaptador y su respuesta (`api`) y el `ObjectMapper` que crea la raíz de composición (`configuracion`) |
| `java.net.http` | solo en `ClienteHttpAPIHidrica` |
| Clases / máximo de líneas por clase | 72 / 141 (límite 150) |
| Pruebas | 114, todas en verde, con JaCoCo ≥ 85 % / 75 % |

## Por qué el dominio puede ejecutarse sin dependencias

`CadenaValidacionTest` y `DecoradoresTest` construyen todo el dominio con Mockito sobre los puertos; `PlanificadorRutaTest` lo hace con lambdas. Ninguna de esas pruebas necesita Jackson, HTTP ni base de datos. Si mañana la API hídrica cambia de formato, solo cambia `AdaptadorAPIHidrica`; si las misiones pasan a PostgreSQL, solo se agrega un `RepositorioMisiones` en infraestructura y una línea en `ConfiguracionAquaPort`.

```bash
mvn verify                                   # 114 pruebas, incluida ArquitecturaCapasTest
bash scripts/auditar-capas.sh                # reporte de dependencias
mvn compile exec:java -Dexec.mainClass=edu.eci.dosw.aquaport.infraestructura.configuracion.AquaPortEnterpriseApp
```
