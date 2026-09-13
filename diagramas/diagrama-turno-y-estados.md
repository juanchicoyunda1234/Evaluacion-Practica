# Diagrama de secuencia (avanzar turno) y de estados (partida)

## Secuencia de `avanzarTurno()`

Detalla el orden exacto de las operaciones dentro de un turno. Este orden
importa: por ejemplo, `generarEnemigoDeOleada()` marca la oleada como
terminada apenas se genera el último enemigo (no un turno después), para
que `iniciarSiguienteOleada()` quede disponible de inmediato.

```mermaid
sequenceDiagram
    actor Jugador
    participant UI as Consola / GUI
    participant J as Juego
    participant LE as ListaDobleEnemigos
    participant LT as ListaSecuencialTorres

    Jugador->>UI: Avanzar turno
    UI->>J: avanzarTurno()
    J->>J: generarEnemigoDeOleada()
    alt quedan enemigos por generar en la oleada
        J->>LE: insertarFinal(nuevoEnemigo)
    end
    J->>LE: actualizarPosiciones()
    J->>LT: getTorres() / getTope()
    J->>J: aplicarAtaquesDeTorres()
    Note right of J: cada torre en rango<br/>daña a cada enemigo activo
    J->>LE: eliminarDestruido(id) por cada enemigo con vida 0
    J->>J: descontarVidasPorEnemigosQueLlegaron()
    Note right of J: enemigo con posicion >= 20:<br/>resta 1 vida y se elimina
    J->>J: verificarFinDePartida()
    J-->>UI: imprime resumen del turno
    UI-->>Jugador: muestra vidas, enemigos activos y estado
```

## Estados de la partida

```mermaid
stateDiagram-v2
    [*] --> EnCurso: new Juego(3 vidas)
    EnCurso --> EnCurso: avanzarTurno()
    EnCurso --> Derrota: vidasJugador <= 0
    EnCurso --> Victoria: se completan todas las oleadas sin bajas
    Derrota --> [*]
    Victoria --> [*]
```

Una vez en `Derrota` o `Victoria`, `partidaTerminada` queda en `true` y
tanto `avanzarTurno()` como `iniciarSiguienteOleada()` rechazan cualquier
acción nueva (en la GUI, los botones correspondientes se deshabilitan).
