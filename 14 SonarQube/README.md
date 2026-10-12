# 14 SonarQube: Análisis de AquaPort v2 y Quality Gate

**Nivel:** Prinplup · **Meta del Quality Gate:** 0 bugs · 0 vulnerabilidades · deuda técnica < 30 min · duplicación < 5 % · cobertura ≥ 80 %

## Qué se analiza

Un solo proyecto Maven con todo lo construido en los puntos 01, 03, 04, 12 y 13:

```
src/main/java/edu/eci/dosw/aquaport
├── AquaPortApp.java               # raíz de composición (main)
├── FlotaEjemplo.java              # los 15 drones de ejemplo
├── modelo/       DroneAcuatico (+3 tipos) · Mision (+Builder) · enums · CondicionesHidricas
├── fabrica/      FabricaDrones · CreadorDrone                       (Factory Method)
├── estrategia/   EstrategiaSeleccion + 3 estrategias                 (Strategy)
├── observacion/  PublicadorEventosMision · EventoMision · 2 observadores (Observer)
├── servicio/     AsignadorAutomatico · ValidadorAsignacion · ProveedorCondicionesHidricas
└── consulta/     ConsultorFlota                                       (Streams)
src/test/java/...  # 87 pruebas (JUnit 5 + Mockito)
```

## Cómo correr el análisis

```bash
docker compose up -d                                   # http://localhost:9000
# My Account → Security → Generate Token (Global Analysis Token)
mvn clean verify sonar:sonar -Dsonar.token=TU_TOKEN
```

Configuración en el `pom.xml`: `sonar.projectKey=aquaport-prinplup` (proyecto distinto al de Piplup en el mismo servidor), ruta del XML de JaCoCo y `sonar.coverage.exclusions` solo para `AquaPortApp` y `FlotaEjemplo`.

## Quality Gate configurado

En **Quality Gates → Create**, nombre `AquaPort Prinplup`, condiciones sobre **Overall Code**, y asignarlo al proyecto en **Project Settings → Quality Gate**:

| Métrica | Operador | Valor |
|---|---|---|
| Bugs | is greater than | 0 |
| Vulnerabilities | is greater than | 0 |
| Technical Debt | is greater than | 30 min |
| Duplicated Lines (%) | is greater than | 5 |
| Coverage | is less than | 80 |

## Issues resueltos (uno por commit)

Se integró primero `AquaPortApp` tal como se escribió para probar el flujo (commit `feat:Prinplup-Punto 14 - Integrar AquaPort v2 para analisis`) y después se corrigió cada grupo de issues en su propio commit. El mensaje de cada commit explica **causa raíz, corrección y por qué**.

| # | Regla | Causa raíz | Corrección | Por qué |
|---|---|---|---|---|
| 1 | **java:S106** Standard outputs should not be used directly to log anything | `AquaPortApp` imprimía con `System.out.println` el resultado de las consultas y el registro del Centro de Control. | `java.util.logging.Logger` (`LOGGER`) en lugar de `System.out`. | La salida estándar no tiene niveles, no se puede apagar ni redirigir a un archivo, y en un servidor se pierde. |
| 2 | **java:S2629** Logging arguments should not require evaluation | Al pasar a `Logger`, el mensaje se armaba concatenando antes de saber si el nivel INFO estaba activo. | Sobrecarga con `Supplier`: `LOGGER.info(() -> "Disponibles por tipo: " + ...)`. | Con el nivel apagado no se recorre la flota ni se arma el texto. |
| 3 | **java:S1612** Lambdas should be replaced with method references | `map(m -> m.getId())` y `forEach(linea -> LOGGER.info(linea))`. | `map(Mision::getId)` y `forEach(LOGGER::info)`. | La referencia dice directamente qué se aplica; es el estilo del resto del proyecto (criterio del revisor DOSW). |
| 4 | **Duplicación** (bloques duplicados de Sonar) | Las 3 misiones de ejemplo repetían la misma cadena de 7 llamadas del Builder; solo cambiaban id, zona, peso y prioridad. | Método privado `misionHaciaLaboratorio(id, partida, peso, prioridad)`. | Una regla repetida 3 veces se corrige en un lugar y se olvida en los otros dos; además `main` pasa de 21 a 17 líneas y queda dentro del límite de 20 del revisor. |
| 5 | **java:S1118** Utility classes should not have public constructors | `AquaPortApp` solo tiene métodos estáticos pero se podía instanciar. | Clase `final` con constructor privado. | Deja claro que es un punto de entrada, no un objeto del dominio. |

Commits:

```
refactor:Prinplup-Punto 14 - Sonar java:S106 y java:S2629: usar Logger con Supplier
refactor:Prinplup-Punto 14 - Sonar java:S1612, duplicacion y java:S1118 en AquaPortApp
```

Nunca se usó `@SuppressWarnings`: suprimir un issue lo esconde, no lo resuelve.

## Lo que ya venía limpio por diseño

- Ningún método retorna `null` (`Optional` en estrategias, consultas y `Mision.getDrone()`).
- Sin `switch` sobre tipos de drone (Factory con registro) y sin `instanceof` (polimorfismo en `puedeOperarEn`).
- Constantes con nombre para los umbrales (`BATERIA_MINIMA_OPERACION = 35`, `PESO_MAXIMO_GRAMOS = 1500`, capacidades por tipo).
- Campos `private final` e inyección por constructor con `Objects.requireNonNull`.

## Estado esperado

| Métrica | Valor esperado |
|---|---|
| Bugs / Vulnerabilities | 0 / 0 |
| Code Smells | 0 |
| Deuda técnica | 0 min (límite 30 min) |
| Duplicación | 0 % (límite 5 %) |
| Cobertura | 100 % en las clases con lógica |

**Pendiente:** correr el análisis y guardar la captura del dashboard con el Quality Gate en verde en `evidencias/sonarqube-dashboard-prinplup.png`. Si el servidor reporta otro issue (las reglas activas dependen de la versión), se corrige con un commit nuevo con el mismo formato.
