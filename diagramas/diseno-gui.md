# Diseño de la interfaz gráfica (Swing)

> **Estado: implementado.** Este documento fue el plan aprobado antes de
> programar; la sección "Estructura de archivos" al final se actualizó
> para reflejar lo que realmente se construyó (se sumaron `JuegoControl`,
> `EstiloGui` y `RutaVisual`, que no estaban en el plan original). Capturas
> reales de cada pestaña: `gui-torres.png`, `gui-oleadas.png`,
> `gui-batalla.png`, `gui-estado.png` en esta misma carpeta.

## Objetivo

Reemplazar el menú de consola de `TowerDefenseApp` por una ventana Swing
moderna y simple, **sin tocar** la lógica de `modelo/` y `negocio/`. La GUI
solo llama a los mismos métodos públicos de `Juego` que ya usa la consola.

## Tecnología

- **Java Swing** + `UIManager.setLookAndFeel(NimbusLookAndFeel)` al arrancar.
  Sin dependencias externas, compila igual que ahora (`javac` + `java`).
- Iconografía con **emojis** en labels/botones/títulos de pestaña (no se
  cargan imágenes externas, cero assets que gestionar).
- Nuevo paquete `Java/src/gui/` con una clase de arranque
  `TowerDefenseGUI` (método `main`), separada de `TowerDefenseApp`
  (la consola se conserva intacta como alternativa).

## Estructura general de la ventana

`JFrame` único, tamaño fijo ~900x600, con:

- **Barra de estado superior** (siempre visible, fuera del `JTabbedPane`):
  `🎮 Tower Defense   |   ❤️ Vidas: 3   ⏱️ Turno: 4   🏆 Estado: En curso`
- **`JTabbedPane`** con 4 pestañas, cada una con su propio panel.
- Refresco automático: después de cualquier acción (agregar torre,
  avanzar turno, etc.) se refresca la pestaña activa y la barra de estado.

```
┌──────────────────────────────────────────────────────────────┐
│ 🎮 Tower Defense   ❤️ Vidas: 3   ⏱️ Turno: 4   🏆 En curso     │
├──────────────────────────────────────────────────────────────┤
│ [🏹 Torres] [🌊 Oleadas] [⚔️ Batalla] [📊 Estado]              │
├──────────────────────────────────────────────────────────────┤
│                                                                │
│                  (contenido de la pestaña activa)             │
│                                                                │
└──────────────────────────────────────────────────────────────┘
```

## Pestaña 1 — 🏹 Torres

Cubre las opciones 1, 2 y 3 del menú original (registrar / mostrar /
eliminar torre).

```
┌───────────────── 🏹 Torres defensivas ─────────────────┐
│  Nombre  [__________]   Tipo    [__________]           │
│  Posición[____]  Daño[____]  Rango[____]  Costo[____]   │
│                              [ ➕ Agregar torre ]        │
├──────────────────────────────────────────────────────────┤
│  Id │ Nombre     │ Tipo    │ Pos │ Daño │ Rango │ Costo  │
│  1  │ Arquero A  │ arquero │  2  │  5   │  3    │  10    │
│  2  │ Cañón B    │ canon   │  8  │ 20   │  2    │  30    │
├──────────────────────────────────────────────────────────┤
│                              [ 🗑️ Eliminar seleccionada ] │
└──────────────────────────────────────────────────────────┘
```

- Tabla (`JTable`) alimentada desde `torres.getTorres()` / `getTope()`.
- Eliminar toma el id de la fila seleccionada (no hay que tipearlo).
- Formulario con `JSpinner`/`JTextField` numéricos para evitar strings
  inválidos (reemplaza la validación manual de `leerEntero`).

## Pestaña 2 — 🌊 Oleadas

Cubre las opciones 4, 5 y 6 (registrar / mostrar oleada / iniciar
siguiente oleada).

```
┌───────────────── 🌊 Oleadas ─────────────────┐
│ Cantidad[____] Tipo[________] Vida[____] Vel[____] │
│                          [ ➕ Agregar oleada ]      │
├──────────────────────────────────────────────────────┤
│ Id │ Tipo    │ Cantidad │ Vida base │ Vel. base      │
│ 1  │ goblin  │    3     │    10     │      1         │
│ 2  │ orco    │    5     │    20     │      1         │
├──────────────────────────────────────────────────────┤
│              [ ▶️ Iniciar siguiente oleada ]           │
└────────────────────────────────────────────────────────┘
```

## Pestaña 3 — ⚔️ Batalla

Cubre las opciones 7 y 8 (avanzar turno / mostrar enemigos activos), y
es la pestaña principal durante la partida.

```
┌───────────────── ⚔️ Batalla ─────────────────┐
│                    [ ▶️ Avanzar turno ]        │
├──────────────────────────────────────────────────┤
│ 👾 Enemigos activos                              │
│ Id │ Tipo   │ Posición │ Vida │ Velocidad         │
│ 3  │ goblin │    5     │  10  │     1             │
│ 4  │ goblin │    2     │   5  │     1             │
├──────────────────────────────────────────────────┤
│ 📜 Registro del turno                             │
│ --- Turno 4 ---                                   │
│ Enemigo #2 destruido.                             │
│ Vidas del jugador: 3                              │
├──────────────────────────────────────────────────┘
```

- "Registro del turno" es un `JTextArea` de solo lectura dentro de un
  `JScrollPane`; se le agrega texto en vez de `System.out.println`
  (capturamos la salida o adaptamos `Juego` para devolver los mensajes,
  se decide al programar).
- Botón "Avanzar turno" se deshabilita si `isPartidaTerminada()`.

## Pestaña 4 — 📊 Estado general

Cubre la opción 9 (estado general), en formato de tarjetas simples en
vez de texto plano:

```
┌───────────── 📊 Estado general ─────────────┐
│  ⏱️ Turno actual        4                    │
│  ❤️ Vidas del jugador   3                    │
│  🏹 Torres registradas  2                    │
│  👾 Enemigos activos    2                    │
│  🌊 Oleadas registradas 2                    │
│  ▶️ Oleadas iniciadas   1                    │
│  🏆 Estado partida      En curso             │
└────────────────────────────────────────────────┘
```

- Al terminar la partida se muestra un banner grande: `🏆 ¡Victoria!`
  o `💀 Derrota` según `partidaGanada`.

## Salir (opción 10)

Se resuelve con el botón de cerrar (`X`) de la ventana; no necesita un
botón dedicado.

## Paleta y estilo

- Base: Nimbus por defecto (grises/azulados).
- Acentos de color solo en botones de acción principal (verde para
  "Agregar"/"Iniciar"/"Avanzar", rojo para "Eliminar").
- Tipografía: la de Nimbus, tamaño ligeramente mayor (13-14pt) para
  legibilidad; encabezados de sección en negrita.
- Sin imágenes externas: toda la identidad visual sale de emojis +
  color de acento, para mantenerlo simple.

## Estructura de archivos (implementada)

```
Java/src/gui/
  TowerDefenseGUI.java   -> main(), configura Nimbus y crea MainFrame
  MainFrame.java         -> JFrame, barra de estado, JTabbedPane
  JuegoControl.java      -> adaptador entre la GUI y Juego (ver diagrama-arquitectura.md)
  EstiloGui.java         -> paleta, fuentes, botones e iconografía reutilizable
  RutaVisual.java        -> mini mapa de la ruta 0-20 con torres y enemigos
  TorresPanel.java
  OleadasPanel.java
  BatallaPanel.java
  EstadoPanel.java
```

Cada panel recibe `JuegoControl` (no `Juego` directamente) y un
`Runnable onCambio` que dispara `MainFrame.refrescarTodo()` tras cualquier
acción que cambie el estado del juego. `JuegoControl` es quien realmente
llama a `Juego`, valida los formularios y convierte la salida de texto de
la consola en datos estructurados para las tablas — el porqué de esta
capa extra se explica en
[`diagrama-arquitectura.md`](diagrama-arquitectura.md).

## Mapeo completo menú consola → GUI

| # | Opción consola | Dónde vive en la GUI |
|---|---|---|
| 1 | Registrar torre | Pestaña Torres, formulario + botón Agregar |
| 2 | Mostrar torres | Pestaña Torres, tabla |
| 3 | Eliminar torre | Pestaña Torres, seleccionar fila + botón Eliminar |
| 4 | Registrar oleada | Pestaña Oleadas, formulario + botón Agregar |
| 5 | Mostrar oleadas | Pestaña Oleadas, tabla |
| 6 | Iniciar siguiente oleada | Pestaña Oleadas, botón |
| 7 | Avanzar turno | Pestaña Batalla, botón |
| 8 | Mostrar enemigos activos | Pestaña Batalla, tabla |
| 9 | Mostrar estado general | Pestaña Estado |
| 10 | Salir | Cerrar ventana |
