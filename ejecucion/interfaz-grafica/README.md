# Ejecución en interfaz gráfica (Swing)

Capturas reales de `gui.TowerDefenseGUI` (o `app.Main`, opción 2),
mostrando el mismo recorrido que la versión de consola: registrar una
torre y una oleada, ganar una partida, perder otra, reiniciar y revisar
el estado general.

## 1. Tablero inicial

![Tablero inicial](01-dashboard-inicial.png)

Pantalla única con el campo de batalla (ruta 0→20), el panel de datos
con pestañas Torre/Enemigo/Oleada a la derecha, y todos los botones de
acción abajo — sin partida iniciada todavía.

## 2. Diálogo "Registrar torre"

![Diálogo registrar torre](02-dialogo-registrar-torre.png)

El botón "+ Registrar torre" abre un formulario emergente con nombre,
tipo, posición, daño, rango y costo, con validación antes de guardar.

## 3. Torre en el mapa

![Torre en el mapa](03-torre-en-el-mapa.png)

Tras agregarla, la torre aparece como un pin en el campo de batalla
(número de id + nombre debajo) y como fila en la pestaña "Torre" del
panel de datos.

## 4. Diálogo "Registrar oleada"

![Diálogo registrar oleada](04-dialogo-registrar-oleada.png)

Igual que con las torres, "+ Registrar oleada" abre un formulario con
cantidad, tipo, vida base y velocidad base.

## 5. Victoria

![Victoria](05-victoria.png)

Con una torre fuerte cerca del inicio de la ruta y una oleada de un solo
enemigo débil, al avanzar turnos la torre lo destruye antes de que
complete el recorrido. Aparece automáticamente el overlay de pantalla
completa "🏆 ¡VICTORIA!" — las vidas quedan intactas.

## 6. Estado general

![Estado general](06-estado-general.png)

El botón "Estado general" abre un resumen con turno, vidas, torres,
enemigos, oleadas y el estado de la partida en tarjetas.

## 7. Derrota

![Derrota](07-derrota.png)

En una partida sin torres, con enemigos veloces, varios llegan a la
base y las vidas bajan a 0. Aparece el mismo overlay de pantalla
completa, esta vez en rojo: "💀 DERROTA".

## 8. Confirmar reinicio

![Confirmar reinicio](08-confirmar-reinicio.png)

Cerrado el overlay, los botones de juego quedan bloqueados hasta que se
reinicia. "Reiniciar partida" pide confirmación antes de borrar el
progreso.

## 9. Partida reiniciada

![Partida reiniciada](09-partida-reiniciada.png)

Tras confirmar, el tablero vuelve al estado inicial (vidas, torres,
oleadas y turno en cero) sin cerrar la ventana.
