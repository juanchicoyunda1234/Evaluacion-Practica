package app;

import java.util.Scanner;

import gui.TowerDefenseGUI;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("===== TOWER DEFENSE =====");
        System.out.println("1. Modo terminal");
        System.out.println("2. Modo grafico (GUI)");
        System.out.print("Elige un modo: ");

        int opcion = TowerDefenseApp.leerEntero(sc);

        if (opcion == 2) {
            sc.close();
            TowerDefenseGUI.main(args);
        } else {
            TowerDefenseApp.ejecutar(sc);
            sc.close();
        }
    }
}
