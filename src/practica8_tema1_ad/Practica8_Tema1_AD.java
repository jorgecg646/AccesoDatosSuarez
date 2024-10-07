package practica8_tema1_ad;

import java.io.RandomAccessFile;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Practica8_Tema1_AD {

    private File f;
    private List<String> campos;
    private List<Integer> camposLength;
    private long longReg;
    private long numReg = 0;

    public Practica8_Tema1_AD(String path, List<String> campos, List<Integer> camposLength) throws IOException {
        this.campos = campos;
        this.camposLength = camposLength;
        this.f = new File(path);
        this.longReg = 0;

        for (Integer campo : camposLength) {
            this.longReg += campo;
        }

        if (f.exists()) {
            this.numReg = f.length() / this.longReg;
        }
    }

    public long getNumReg() {
        return numReg;
    }

    public void insertar(Map<String, String> reg) throws IOException {
        insertar(reg, this.numReg++);
    }

    public void insertar(Map<String, String> reg, long pos) {
        try (RandomAccessFile rndFile = new RandomAccessFile(this.f, "rws")) {
            // Si estamos insertando más allá del último registro existente, actualizamos numReg
            if (pos >= numReg) {
                numReg = pos + 1; // Aumentar numReg si estamos insertando más allá
            }

            // Asegúrate de que la posición está dentro del rango válido
            rndFile.setLength(numReg * longReg); // Asegurarse de que el archivo tiene el tamaño correcto
            rndFile.seek(pos * this.longReg);

            int total = campos.size();
            for (int i = 0; i < total; i++) {
                String nomCampo = campos.get(i);
                Integer longCampo = camposLength.get(i);
                String valorCampo = reg.get(nomCampo);

                if (valorCampo == null) {
                    valorCampo = "";
                }

                String valorCampoForm = String.format("%1$-" + longCampo + "s", valorCampo);
                rndFile.write(valorCampoForm.getBytes("UTF-8"), 0, longCampo);
            }
        } catch (Exception ex) {
            System.out.println("Error al insertar: " + ex.getMessage());
        }
    }


    public Map<String, String> leerRegistro(long pos) {
        Map<String, String> reg = new HashMap<>();
        try (RandomAccessFile rndFile = new RandomAccessFile(this.f, "r")) {
            rndFile.seek(pos * this.longReg);

            for (int i = 0; i < campos.size(); i++) {
                byte[] bytes = new byte[camposLength.get(i)];
                rndFile.read(bytes);
                String valorCampo = new String(bytes, "UTF-8").trim();
                reg.put(campos.get(i), valorCampo);
            }
        } catch (Exception ex) {
            System.out.println("Error al leer registro: " + ex.getMessage());
        }
        return reg;
    }

    public void modificar(long pos, Map<String, String> reg) {
        insertar(reg, pos);
    }

    public void añadirRegistro() {
        Scanner scanner = new Scanner(System.in);
        Map<String, String> nuevoRegistro = new HashMap<>();

        System.out.print("Ingrese el numero de registro donde desea insertar (0 a " + getNumReg() + "): ");
        long numRegistro = scanner.nextLong();
        scanner.nextLine(); // Consumir nueva línea

        for (String campo : campos) {
            System.out.print("Ingrese " + campo + ": ");
            String valor = scanner.nextLine();
            nuevoRegistro.put(campo, valor);
        }
        insertar(nuevoRegistro, numRegistro);
        System.out.println("Registro anadido.");
    }

    public void leerRegistroMenu() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el numero de registro a leer (0 a " + (getNumReg() - 1) + "): ");
        long numRegistroLeer = scanner.nextLong();
        scanner.nextLine(); // Consumir nueva línea
        Map<String, String> registroLeido = leerRegistro(numRegistroLeer);
        System.out.println("Registro leído: " + registroLeido);
    }

    public void modificarRegistro() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el numero de registro a modificar (0 a " + (getNumReg() - 1) + "): ");
        long numRegistroModificar = scanner.nextLong();
        scanner.nextLine(); // Consumir nueva línea
        Map<String, String> registroModificar = new HashMap<>();
        for (String campo : campos) {
            System.out.print("Ingrese nuevo " + campo + ": ");
            String valor = scanner.nextLine();
            registroModificar.put(campo, valor);
        }
        modificar(numRegistroModificar, registroModificar);
        System.out.println("Registro modificado.");
    }

    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);
        List<String> campos = new ArrayList<>();
        List<Integer> camposLength = new ArrayList<>();

        campos.add("DNI");
        campos.add("NOMBRE");
        campos.add("DIRECCION");
        campos.add("CP");

        camposLength.add(9);
        camposLength.add(32);
        camposLength.add(32);
        camposLength.add(5);

        Practica8_Tema1_AD faa;
        try {
            faa = new Practica8_Tema1_AD("file_binario_2.dat", campos, camposLength);

            while (true) {
                System.out.println("\nMenu:");
                System.out.println("1. Anadir registro");
                System.out.println("2. Leer registro");
                System.out.println("3. Modificar registro");
                System.out.println("4. Salir");
                System.out.print("Seleccione una opcion: ");
                int opcion = scanner.nextInt();
                scanner.nextLine(); // Consumir nueva línea

                switch (opcion) {
                    case 1:
                        faa.añadirRegistro();
                        break;

                    case 2:
                        faa.leerRegistroMenu();
                        break;

                    case 3:
                        faa.modificarRegistro();
                        break;

                    case 4:
                        System.out.println("Saliendo...");
                        scanner.close();
                        return;

                    default:
                        System.out.println("Opción no válida. Inténtelo de nuevo.");
                }
            }
        } catch (IOException e) {
            System.out.print("EXCE " + e.getMessage());
        } catch (Exception e) {
            System.out.print("EXCE " + e.getMessage());
        }
    }
}
