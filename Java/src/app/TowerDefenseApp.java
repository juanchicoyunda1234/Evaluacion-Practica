package app;

import java.util.Scanner;

import modelo.Oleada;
import modelo.Torre;
import negocio.Juego;

public class TowerDefenseApp {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Juego juego = new Juego(3);
        int contadorIdTorre = 1;
        int contadorIdOleada = 1;
        boolean salir = false;

        while (!salir) {
            System.out.println();
            System.out.println("===== TOWER DEFENSE =====");
            System.out.println("1. Registrar torre defensiva");
            System.out.println("2. Mostrar torres registradas");
            System.out.println("3. Eliminar torre");
            System.out.println("4. Registrar oleada");
            System.out.println("5. Mostrar oleadas");
            System.out.println("6. Iniciar siguiente oleada");
            System.out.println("7. Avanzar turno");
            System.out.println("8. Mostrar enemigos activos");
            System.out.println("9. Mostrar estado general del juego");
            System.out.println("10. Salir");
            System.out.print("Elige una opcion: ");

            int opcion = leerEntero(sc);

            switch (opcion) {
                case 1:
                    contadorIdTorre = registrarTorre(sc, juego, contadorIdTorre);
                    break;
                case 2:
                    juego.mostrarTorres();
                    break;
                case 3:
                    eliminarTorre(sc, juego);
                    break;
                case 4:
                    contadorIdOleada = registrarOleada(sc, juego, contadorIdOleada);
                    break;
                case 5:
                    juego.mostrarOleadas();
                    break;
                case 6:
                    juego.iniciarSiguienteOleada();
                    break;
                case 7:
                    juego.avanzarTurno();
                    break;
                case 8:
                    juego.mostrarEnemigosActivos();
                    break;
                case 9:
                    juego.mostrarEstadoGeneral();
                    break;
                case 10:
                    salir = true;
                    System.out.println("Cerrando Tower Defense.");
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }
        }

        sc.close();
    }

    private static int registrarTorre(Scanner sc, Juego juego, int contadorId) {
        System.out.print("Nombre de la torre: ");
        String nombre = sc.nextLine();
        System.out.print("Tipo (arquero, canon, mago...): ");
        String tipo = sc.nextLine();
        System.out.print("Posicion en la ruta: ");
        int posicion = leerEntero(sc);
        System.out.print("Danio: ");
        int danio = leerEntero(sc);
        System.out.print("Rango: ");
        int rango = leerEntero(sc);
        System.out.print("Costo: ");
        int costo = leerEntero(sc);

        Torre torre = new Torre(contadorId, nombre, tipo, posicion, danio, rango, costo);
        boolean insertada = juego.registrarTorre(torre);

        if (insertada) {
            System.out.println("Torre registrada con id " + contadorId);
            return contadorId + 1;
        }

        System.out.println("No se pudo registrar la torre, capacidad maxima alcanzada.");
        return contadorId;
    }

    private static void eliminarTorre(Scanner sc, Juego juego) {
        System.out.print("Id de la torre a eliminar: ");
        int id = leerEntero(sc);
        boolean eliminada = juego.eliminarTorre(id);
        System.out.println(eliminada ? "Torre eliminada." : "No se encontro una torre con ese id.");
    }

    private static int registrarOleada(Scanner sc, Juego juego, int contadorId) {
        System.out.print("Cantidad de enemigos en la oleada: ");
        int cantidad = leerEntero(sc);
        System.out.print("Tipo de enemigo: ");
        String tipo = sc.nextLine();
        System.out.print("Vida base: ");
        int vidaBase = leerEntero(sc);
        System.out.print("Velocidad base: ");
        int velocidadBase = leerEntero(sc);

        Oleada oleada = new Oleada(contadorId, cantidad, tipo, vidaBase, velocidadBase);
        juego.registrarOleada(oleada);
        System.out.println("Oleada registrada con id " + contadorId);
        return contadorId + 1;
    }

    private static int leerEntero(Scanner sc) {
        while (!sc.hasNextInt()) {
            System.out.print("Ingresa un numero valido: ");
            sc.next();
        }
        int valor = sc.nextInt();
        sc.nextLine();
        return valor;
    }
}
