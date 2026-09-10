```mermaid
classDiagram
    class ClaseBase {
        #atributoComun
        +metodoAbstracto()*
    }
    class ClaseHija1 {
        -atributoPropio1
        +metodoAbstracto()
    }
    class ClaseHija2 {
        -atributoPropio2
        +metodoAbstracto()
    }
    class Gestor {
        -ClaseBase[] elementos
        -int cantidad
        +registrar(ClaseBase) boolean
        +mostrarTodos()
    }
    ClaseBase <|-- ClaseHija1
    ClaseBase <|-- ClaseHija2
    Gestor o-- ClaseBase
```
