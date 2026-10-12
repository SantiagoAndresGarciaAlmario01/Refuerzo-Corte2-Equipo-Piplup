# Refuerzo Corte 2 - Equipo Piplup: AquaPort MVP

**Curso:** Diseño y Arquitectura de Software (DOSW) · ECI · 2026-2 · Prof. Andrés Martín Cantór Urrego
**Línea evolutiva:** Piplup (nivel básico) → Prinplup → Empoleon
**Proyecto:** **AquaPort**, sistema de drones acuáticos de la ECI para monitoreo y reparto de muestras de agua y sensores entre embalses, canales y zonas hídricas del campus.

## Enunciado Piplup (MVP)

4 drones **Aqua-Ranger 100**, zonas fijas (Embalse Norte, Canal Central, Laguna Sur, Punto Ribereño Este, Laboratorio Hídrico) y **asignación manual** por el operador.

```
DroneAcuatico(id:String, modelo:String, bateria:int, disponible:boolean, zona:String)
Mision(id:String, drone:DroneAcuatico, puntoPartida:String, puntoLlegada:String,
       tipoCarga:TipoCarga, estado:EstadoMision)
TipoCarga:    MUESTRA_AGUA, SENSOR, PAQUETE_LIGERO
EstadoMision: PENDIENTE, EN_TRANSITO, ENTREGADA, FALLIDA
```

## Retos

| # | Tema | Entregable | Carpeta |
|---|---|---|---|
| 01 | Streams & Lambdas | `ConsultorFlota` con 5 consultas + 10 pruebas | [01 Streams & Lambdas](01%20Streams%20%26%20Lambdas/) |
| 02 | GitHub y GitFlow | `.gitignore`, ramas `main`/`develop`/`feature/`, commits atómicos, PR | [02 GitHub y GitFlow](02%20GitHub%20y%20GitFlow/) |
| 03 | Patrones de Diseño | `Mision.Builder` con validación en `build()` + 9 pruebas | [03 Patrones de Diseño](03%20Patrones%20de%20Dise%C3%B1o/) |
| 04 | Principios SOLID | SRP + DIP, `RepositorioMisiones`, diagrama de clases | [04 Principios SOLID](04%20Principios%20SOLID/) |
| 05 | Diagrama de Contexto C4 | `docs/c4-contexto-piplup.png` | [05 Diagrama de Contexto C4](05%20Diagrama%20de%20Contexto%20C4/) |
| 06 | RF vs RNF | 3 RF, 3 RNF medibles, MoSCoW justificado | [06 RF vs RNF](06%20RF%20vs%20RNF/) |
| 07 | Plantilla DOSW | RF AP-01 completo | [07 Plantilla DOSW](07%20Plantilla%20DOSW/) |
| 08 | Manual de Identidad y UX/UI | Identidad, mock del panel, Nielsen | [08 Manual de Identidad y UXUI](08%20Manual%20de%20Identidad%20y%20UXUI/) |
| 09 | Agilismo y Jira | Épica → Feature → 3 HU → 3 subtareas + CSV de importación | [09 Agilismo y Jira](09%20Agilismo%20y%20Jira/) |
| 10 | Diagramas de Casos de Uso | `docs/diagrama-cu-piplup.png` | [10 Diagramas de Casos de Uso](10%20Diagramas%20de%20Casos%20de%20Uso/) |
| 11 | Mocks con IA | Proceso de 4 pasos, prompt y 3 estados del panel | [11 Mocks con IA](11%20Mocks%20con%20IA/) |
| 12 | TDD | `ValidadorMision` con Red → Green → Refactor (20 pruebas) | [12 TDD](12%20TDD/) |
| 13 | JaCoCo | Cobertura 100 % líneas y ramas + regla de 80 % | [13 JaCoCo](13%20JaCoCo/) |
| 14 | SonarQube | MVP integrado, configuración y smells resueltos | [14 SonarQube](14%20SonarQube/) |

Cada carpeta tiene su propio `README.md` con la justificación del reto. Las carpetas con código son proyectos Maven independientes (Java 17, JUnit 5.10):

```bash
cd "01 Streams & Lambdas" && mvn test
```

## Criterios del revisor DOSW (nivel Piplup) que se respetan en todo el código

- Compila sin errores ni warnings (`-Xlint:all`).
- Streams sin `for`/`while` ni mutación externa.
- PascalCase para clases, camelCase para métodos y variables, UPPER_SNAKE_CASE para constantes.
- Ninguna clase hace más de una cosa (SRP).
- Métodos de máximo 25 líneas.
- Nunca se retorna `null`: se usa `Optional` o se lanza excepción con mensaje en español.

## Ramas

`main` · `develop` · `feature/streams-consultor-flota` · **`Piplup`** (rama de entrega con los 14 puntos). Convención de commits: `tipo:Piplup-Punto NN - descripción`.
