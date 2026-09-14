# Diseño de la interfaz gráfica (Swing)

> **Estado: implementado.** Este documento describe el diseño final: una
> sola pantalla tipo tablero, en vez de las 4 pestañas separadas de una
> versión anterior. Capturas reales de la ejecución (no mockups) están en
> [`../ejecucion/interfaz-grafica/`](../ejecucion/interfaz-grafica/).

## Objetivo

Reemplazar el menú de consola de `TowerDefenseApp` por una ventana Swing
moderna y simple, **sin tocar** la lógica de `modelo/` y `negocio/`. La GUI
solo llama a los mismos métodos públicos de `Juego` que ya usa la consola.

## Tecnología

- **Java Swing** + `UIManager.setLookAndFeel(NimbusLookAndFeel)` al arrancar.
  Sin dependencias externas, compila igual que ahora (`javac` + `java`).
- Iconografía con **emojis** en labels/botones/títulos (no se cargan
  imágenes externas, cero assets que gestionar).
- Nuevo paquete `Java/src/gui/` con una clase de arranque
  `TowerDefenseGUI` (método `main`), separada de `TowerDefenseApp`
  (la consola se conserva intacta como alternativa).

## Estructura general de la ventana

`JFrame` único (~1180x760), con **todo visible a la vez** — sin pestañas
que escondan partes del juego:

```
┌────────────────────────────────────────────────────────────────────┐
│ 🎮 Tower Defense     Vida: ♥♥♥ | Torres: 1 | Oleada: 0/1 | Turno: 3 │
├───────────────────────────────────────┬──────────────────────────────┤
│  Campo de batalla                     │  Datos                      │
│  (mini mapa: ruta 0→20, pines de      │  [Torre] [Enemigo] [Oleada] │
│   torre con numero+nombre, enemigos   │  tabla de la pestaña activa │
│   como puntos)                        │                              │
│                                        │                              │
├───────────────────────────────────────┴──────────────────────────────┤
│ [+Registrar torre][x Eliminar]  [▶▶ Avanzar   ][Estado general]     │
│ [+Registrar oleada][▶ Iniciar]  [   turno     ][Reiniciar partida]  │
│                                  [             ][Salir]              │
└────────────────────────────────────────────────────────────────────┘
```

Refresco automático: después de cualquier acción (agregar torre, avanzar
turno, etc.) se refresca todo el tablero de una vez (`MainFrame.refrescarTodo()`).

## Campo de batalla (`RutaVisual`)

Mini mapa siempre visible de la ruta 0→20. Cada torre se dibuja como un
**pin** (círculo con su id en blanco + punta que toca la vía) con su
nombre debajo; los enemigos activos son puntos naranjas sobre su posición.
Esto reemplaza una versión anterior que solo dibujaba un triángulo sin
identificar cada torre.

## Panel de datos (`DatosPanel`)

Un `JTabbedPane` **compacto**, solo para las 3 tablas de datos (no para
formularios ni acciones): "Torre (n)", "Enemigo (n)", "Oleada (n)", con
el conteo en vivo en el título de cada pestaña.

## Formularios como diálogos (`DialogoTorre`, `DialogoOleada`)

Registrar torre u oleada abre una ventana emergente (`JDialog`) con el
formulario correspondiente y validación en línea (igual que antes, pero
ya no ocupa espacio permanente en la pantalla principal).

## Fin de partida (`PantallaFinPartida`)

Al ganar o perder aparece automáticamente un overlay de pantalla completa
(glass pane de `MainFrame`) con un efecto de aparición (fade + escala) y
el mensaje "🏆 ¡VICTORIA!" o "💀 DERROTA" en grande, más el detalle del
motivo. Se cierra con un clic. Antes esto solo se veía si el jugador
abría manualmente "Estado general".

## Estado general (`EstadoPanel`)

Se mantiene igual que en la versión anterior (tarjetas con turno, vidas,
torres, enemigos, oleadas, estado de la partida), pero ahora vive dentro
de un diálogo emergente en vez de una pestaña fija, para no ocupar
espacio permanente en el tablero principal.

## Botonera inferior

Todas las acciones del juego quedan visibles y accesibles en todo
momento (no hay que cambiar de pestaña para encontrarlas):
Registrar torre, Eliminar torre, Registrar oleada, Iniciar siguiente
oleada, Avanzar turno, Estado general, Reiniciar partida, Salir.

"Reiniciar partida" es nuevo: crea una partida (`Juego`) desde cero sin
cerrar la ventana, con confirmación previa.

## Paleta y estilo

- Base: Nimbus por defecto (grises/azulados), tema claro.
- Acentos de color por tipo de acción: verde (agregar/avanzar), rojo
  (eliminar/derrota), azul (registrar oleada), naranja (iniciar oleada).
- Tipografía: la de Nimbus, tamaño ligeramente mayor (13-14pt) para
  legibilidad; encabezados de sección en negrita.
- Sin imágenes externas: toda la identidad visual sale de emojis, color
  de acento y formas dibujadas con `Graphics2D` (pines, overlay).

## Estructura de archivos (implementada)

```
Java/src/gui/
  TowerDefenseGUI.java     -> main(), configura Nimbus y crea MainFrame
  MainFrame.java           -> JFrame, barra de estado, tablero, botonera, glass pane
  JuegoControl.java        -> adaptador entre la GUI y Juego (ver diagrama-arquitectura.md)
  EstiloGui.java           -> paleta, fuentes, botones e iconografía reutilizable
  RutaVisual.java          -> mini mapa de la ruta 0-20 con pines de torre y enemigos
  DatosPanel.java          -> pestañas cortas Torre/Enemigo/Oleada (solo tablas)
  DialogoTorre.java        -> formulario emergente para registrar una torre
  DialogoOleada.java       -> formulario emergente para registrar una oleada
  PantallaFinPartida.java  -> overlay de victoria/derrota (glass pane)
  EstadoPanel.java         -> tarjetas de estado general, dentro de un dialogo
```

Cada componente recibe `JuegoControl` (no `Juego` directamente) y, cuando
corresponde, un `Runnable onCambio` que dispara `MainFrame.refrescarTodo()`
tras cualquier acción que cambie el estado del juego. `JuegoControl` es
quien realmente llama a `Juego`, valida los formularios y convierte la
salida de texto de la consola en datos estructurados para las tablas — el
porqué de esta capa extra se explica en
[`diagrama-arquitectura.md`](diagrama-arquitectura.md).

## Mapeo completo menú consola → GUI

| # | Opción consola | Dónde vive en la GUI |
|---|---|---|
| 1 | Registrar torre | Botón "Registrar torre" -> diálogo con formulario |
| 2 | Mostrar torres | Panel de datos, pestaña "Torre" |
| 3 | Eliminar torre | Seleccionar fila en pestaña "Torre" + botón "Eliminar torre" |
| 4 | Registrar oleada | Botón "Registrar oleada" -> diálogo con formulario |
| 5 | Mostrar oleadas | Panel de datos, pestaña "Oleada" |
| 6 | Iniciar siguiente oleada | Botón "Iniciar siguiente oleada" |
| 7 | Avanzar turno | Botón "Avanzar turno" |
| 8 | Mostrar enemigos activos | Panel de datos, pestaña "Enemigo" |
| 9 | Mostrar estado general | Botón "Estado general" -> diálogo |
| 10 | Salir | Botón "Salir" o cerrar la ventana (con confirmación) |
