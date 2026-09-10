# Tower Defense: Listas Secuenciales, Doble y Circular en Java

## Descripcion

Prueba practica de Estructura de Datos (UTA, Nivel III): mini simulador de un
juego de **Tower Defense**. El objetivo pedagogico de la guia es evaluar el
uso de tres estructuras de datos lineales **implementadas a mano**, sin
`java.util`: lista secuencial (arreglo), lista doblemente enlazada y lista
simplemente enlazada circular.

### Enunciado

CASO DE ESTUDIO: TOWER DEFENSE CON LISTAS SECUENCIALES, DOBLES Y CIRCULARES EN JAVA

Disenar y desarrollar un mini sistema de juego en Java, inspirado en un
Tower Defense, que demuestre el uso correcto de tres estructuras de datos
lineales implementadas a mano (sin colecciones de `java.util`).

**Contexto:** el jugador defiende un territorio de oleadas de enemigos que
avanzan por una ruta hacia una base, usando torres defensivas que atacan
automaticamente a los enemigos dentro de su rango.

**Estructuras de datos obligatorias:**

1. **Lista secuencial** (arreglo) para las torres defensivas: `Torre` (id,
   nombre, tipo, posicion, danio, rango, costo); insertar, eliminar por id,
   buscar por id, mostrar todas, contar activas.
2. **Lista doblemente enlazada** con referencia a primero y ultimo para los
   enemigos activos: `NodoEnemigo` sobre `Enemigo` (id, tipo, vida,
   velocidad, posicion, recompensa); insertar al final, eliminar destruido,
   buscar por id, recorrer adelante y atras, actualizar posicion.
3. **Lista simplemente enlazada circular** con referencia a ultimo para las
   oleadas: `NodoOleada` sobre `Oleada` (idOleada, cantidadEnemigos,
   tipoEnemigo, vidaBase, velocidadBase); registrar, mostrar, avanzar a la
   siguiente oleada, reiniciar el ciclo.

**Reglas del juego:** ruta lineal de posiciones 0 a 20; los enemigos
aparecen en la posicion 0 y avanzan segun su velocidad en cada turno; las
torres atacan automaticamente a los enemigos dentro de su rango; un enemigo
con vida 0 se elimina; un enemigo que llega al final de la ruta resta una
vida al jugador; la partida termina cuando el jugador pierde todas sus
vidas o se completan todas las oleadas definidas.

**Menu requerido (10 opciones):** registrar torre defensiva, mostrar torres
registradas, eliminar torre, registrar oleada, mostrar oleadas, iniciar
siguiente oleada, avanzar turno, mostrar enemigos activos, mostrar estado
general del juego, salir.

**Restricciones tecnicas:** lenguaje obligatorio Java; estructuras
implementadas manualmente con arreglos y referencias entre objetos;
prohibido usar `ArrayList`, `LinkedList`, `Queue`, `Deque` o colecciones
similares; se permite `Scanner` y `String`; clases separadas en archivos
`.java`, con `TowerDefenseApp` como clase con el metodo `main`.

**Entregables:** archivos fuente `.java` compilables y ejecutables;
capturas de pantalla de la ejecucion; breve explicacion del uso de cada
estructura de datos; diagrama simple de clases o de estructuras
utilizadas; invitar al docente como colaborador del repositorio de GitHub.

### Solucion propuesta

| Estructura | Tipo | Para que sirve |
|---|---|---|
| Lista secuencial de torres | `Torre[]` + `int tope` | torres colocadas por el jugador |
| Lista doble de enemigos | `NodoEnemigo` (primero/ultimo) | enemigos activos que avanzan por la ruta |
| Lista circular de oleadas | `NodoOleada` (ultimo) | ciclo de oleadas por lanzar |

El programa tiene estas clases:

| Clase | Responsabilidad |
|---|---|
| `Torre` | Datos de una torre defensiva; calcula si un enemigo esta dentro de su rango |
| `Enemigo` | Datos de un enemigo; recibe danio y avanza por la ruta |
| `Oleada` | Definicion de una oleada (cantidad, tipo, vida y velocidad base) |
| `NodoEnemigo` | Envuelve a un `Enemigo` con `anterior`/`siguiente` para la lista doble |
| `NodoOleada` | Envuelve a una `Oleada` con `siguiente` para la lista circular |
| `ListaSecuencialTorres` | Arreglo estatico de `Torre` con `tope`; CRUD de torres |
| `ListaDobleEnemigos` | Lista doble de `NodoEnemigo`; insertar, eliminar, buscar, recorrer |
| `ListaCircularOleadas` | Lista circular de `NodoOleada`; registrar, mostrar, avanzar, reiniciar |
| `Juego` | Orquesta las tres estructuras y la logica de turnos |
| `TowerDefenseApp` | Clase con el `main` y el menu de 10 opciones |

### Modelo de dominio

- **`Torre`:** colocada en una posicion fija de la ruta; ataca
  automaticamente a cualquier `Enemigo` que entre en su rango
  (`dentroDeRango`).
- **`Enemigo`:** aparece en la posicion 0 de una ruta de longitud 20,
  avanza segun su velocidad en cada turno, recibe danio de las torres en
  rango y se elimina si su vida llega a 0 o si alcanza el final de la ruta
  (en ese caso resta una vida al jugador).
- **`Oleada`:** define cuantos enemigos, de que tipo, con que vida y
  velocidad base aparecen en un ciclo del juego.
- **`Juego`:** vida inicial del jugador 3, ruta de longitud 20. Cada turno
  genera enemigos de la oleada en curso, mueve a los enemigos activos,
  aplica el danio de las torres en rango, elimina a los destruidos,
  descuenta vidas a los que llegaron al final, y verifica si la partida
  termina (por vidas en 0 o por completar todas las oleadas registradas).

### Decisiones de diseno

- El enemigo y la torre no usan herencia ni polimorfismo: el enunciado no
  pide una jerarquia de tipos, solo tres estructuras de datos lineales.
- Las torres se guardan en un arreglo estatico de tamano fijo
  (`MAX_TORRES = 20`) con un contador `tope`, sin redimensionar, tal como
  pide la lista secuencial del enunciado.
- Los enemigos se guardan en una lista doblemente enlazada con referencia
  a `primero` y `ultimo`, lo que permite insertar al final en O(1) y
  recorrer en ambos sentidos, aunque el juego en si solo recorra hacia
  adelante en cada turno.
- Las oleadas se guardan en una lista circular donde el `siguiente` del
  ultimo nodo apunta siempre al primero; `reiniciarCiclo` existe como
  operacion aparte para reiniciar el ciclo explicitamente.
- La partida gana cuando se han iniciado tantas oleadas como las
  registradas y ya no quedan enemigos activos ni por generar; pierde si
  las vidas llegan a 0.
- Vida inicial del jugador: 3. Longitud de la ruta: 20 posiciones (0 a
  20). El enunciado no pide puntuacion, asi que no se implemento.
- Sin interfaz grafica: demo de consola con el menu de 10 opciones exacto
  que pide el enunciado.

### Conceptos aplicados

| Requisito | Java |
|---|---|
| Lista secuencial (arreglo) | `Torre[] torres` + `int tope` en `ListaSecuencialTorres` |
| Lista doblemente enlazada | `NodoEnemigo` con `anterior`/`siguiente`, primero/ultimo, en `ListaDobleEnemigos` |
| Lista simplemente enlazada circular | `NodoOleada` con referencia a `ultimo`, en `ListaCircularOleadas` |
| Encapsulamiento | atributos `private` con getters/setters en todas las clases de `modelo/` |
| Sin colecciones de `java.util` | Todo implementado a mano con arreglos y referencias entre objetos |

## Estructura del proyecto

Arquitectura en 3 paquetes:

- `Java/src/modelo/`  -> `Torre`, `Enemigo`, `Oleada`, `NodoEnemigo`, `NodoOleada`
- `Java/src/negocio/` -> `ListaSecuencialTorres`, `ListaDobleEnemigos`, `ListaCircularOleadas`, `Juego`
- `Java/src/app/`     -> `TowerDefenseApp`, clase con el `main`

## Diagramas

- Diagrama de clases: `diagramas/diagrama-clases.md` (fuente Mermaid) y `diagramas/diagrama-clases.png` (render)
- Captura de ejecucion: `diagramas/captura-ejecucion.png`

## Como ejecutar

Desde `Java/src`:

```bash
javac modelo/*.java negocio/*.java app/*.java -d ../out
cd ../out
java app.TowerDefenseApp
```

## Equipo

| Rol | Integrante |
|---|---|
| Lider | Chico Yunda Juan Carlos |
| Backend - Negocio y logica del juego | Chico Yunda Juan Carlos |
| Backend - Modelo y estructuras | Guanoquiza Aguaguiña Justin Alexander |
| Documentacion - Informe y diagramas | Tacuri Santillan Mónica Sara |
