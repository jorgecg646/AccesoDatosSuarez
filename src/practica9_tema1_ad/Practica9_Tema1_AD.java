package practica9_tema1_ad;

import java.io.RandomAccessFile;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Practica9_Tema1_AD {
private File f;
    private List<String> campos;
    private List<Integer> camposLength;
    private long longReg;
    private long numReg = 0;

    public Practica9_Tema1_AD(String path, List<String> campos, List<Integer> camposLength) throws IOException {
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
            if (pos >= numReg) {
                numReg = pos + 1;
            }
            rndFile.setLength(numReg * longReg);
            rndFile.seek(pos * this.longReg);
            for (int i = 0; i < campos.size(); i++) {
                String nomCampo = campos.get(i);
                Integer longCampo = camposLength.get(i);
                String valorCampo = reg.getOrDefault(nomCampo, "");
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
                reg.put(campos.get(i), new String(bytes, "UTF-8").trim());
            }
        } catch (Exception ex) {
            System.out.println("Error al leer registro: " + ex.getMessage());
        }
        return reg;
    }

    public void modificar(long pos, Map<String, String> reg) {
        insertar(reg, pos);
    }

    public void anadirRegistro() {
        Scanner scanner = new Scanner(System.in);
        Map<String, String> nuevoRegistro = new HashMap<>();
        System.out.print("Ingrese el numero de registro donde desea insertar (0 a " + getNumReg() + "): ");
        long numRegistro = scanner.nextLong();
        scanner.nextLine();
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
        scanner.nextLine();
        Map<String, String> registroLeido = leerRegistro(numRegistroLeer);
        System.out.println("Registro leido: " + registroLeido);
    }

    public void modificarRegistro() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el numero de registro a modificar (0 a " + (getNumReg() - 1) + "): ");
        long numRegistroModificar = scanner.nextLong();
        scanner.nextLine();
        Map<String, String> registroModificar = new HashMap<>();
        for (String campo : campos) {
            System.out.print("Ingrese nuevo " + campo + ": ");
            String valor = scanner.nextLine();
            registroModificar.put(campo, valor);
        }
        modificar(numRegistroModificar, registroModificar);
        System.out.println("Registro modificado.");
    }

    public void seleccionarCampo() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el numero de registro: ");
        int regCampo = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Ingrese el nombre de la columna: ");
        String colCampo = scanner.nextLine();
        String valorCampo = selectCampo(regCampo, colCampo);
        System.out.println("Valor: " + valorCampo);
    }

    public void seleccionarColumna() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el nombre de la columna: ");
        String colSeleccionada = scanner.nextLine();
        List<String> valoresColumna = selectColumna(colSeleccionada);
        System.out.println("Valores de la columna: " + valoresColumna);
    }

    public void seleccionarRegistroComoLista() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el numero de registro: ");
        int regLista = scanner.nextInt();
        List<String> datosLista = selectRowList(regLista);
        System.out.println("Datos del registro: " + datosLista);
    }

    public void seleccionarRegistroComoMapa() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el numero de registro: ");
        int regMapa = scanner.nextInt();
        Map<String, String> datosMapa = selectRowMap(regMapa);
        System.out.println("Datos del registro: " + datosMapa);
    }

    public void actualizarRegistro() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el numero de registro a actualizar: ");
        int regUpdate = scanner.nextInt();
        scanner.nextLine();
        Map<String, String> registroActual = leerRegistro(regUpdate);
        System.out.print("Ingrese el nombre del campo que desea actualizar: ");
        String campoActualizar = scanner.nextLine();
        if (!campos.contains(campoActualizar)) {
            System.out.println("Campo no válido.");
            return;
        }
        System.out.print("Ingrese nuevo valor para " + campoActualizar + ": ");
        String nuevoValor = scanner.nextLine();
        update(regUpdate, campoActualizar, nuevoValor);
        System.out.println("Campo " + campoActualizar + " actualizado.");
    }

    public void eliminarRegistro() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el numero de registro a eliminar: ");
        int regDelete = scanner.nextInt();
        delete(regDelete);
        System.out.println("Registro eliminado.");
    }
    
    public String selectCampo(int numRegistro, String nomColumna) {
        Map<String, String> registro = leerRegistro(numRegistro);
        return registro.get(nomColumna);
    }

    public List<String> selectColumna(String nomColumna) {
        List<String> valores = new ArrayList<>();
        for (long i = 0; i < numReg; i++) {
            String valor = selectCampo((int) i, nomColumna);
            valores.add(valor);
        }
        return valores;
    }

    public List<String> selectRowList(int numRegistro) {
        Map<String, String> registro = leerRegistro(numRegistro);
        return new ArrayList<>(registro.values());
    }

    public Map<String, String> selectRowMap(int numRegistro) {
        return leerRegistro(numRegistro);
    }

    public void update(int row, Map<String, String> nuevoReg) {
        modificar(row, nuevoReg);
    }

    public void update(int row, String campo, String valor) {
        Map<String, String> registro = leerRegistro(row);
        registro.put(campo, valor);
        modificar(row, registro);
    }

    public void delete(int row) {
        Map<String, String> vacio = new HashMap<>();
        for (String campo : campos) {
            vacio.put(campo, "");
        }
        modificar(row, vacio);
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

        Practica9_Tema1_AD faa;
        try {
            faa = new Practica9_Tema1_AD("file_binario_2.dat", campos, camposLength);

            while (true) {
                mostrarMenu();
                int opcion = scanner.nextInt();
                scanner.nextLine(); // Consumir nueva línea

                ejecutarOpcion(opcion, faa);
            }
        } catch (IOException e) {
            System.out.print("EXCE " + e.getMessage());
        } catch (Exception e) {
            System.out.print("EXCE " + e.getMessage());
        }
    }

    public static void mostrarMenu() {
        System.out.println("1. Anadir registro");
        System.out.println("2. Leer registro");
        System.out.println("3. Modificar registro");
        System.out.println("4. Seleccionar campo");
        System.out.println("5. Seleccionar columna");
        System.out.println("6. Seleccionar registro como lista");
        System.out.println("7. Seleccionar registro como mapa");
        System.out.println("8. Actualizar registro");
        System.out.println("9. Eliminar registro");
        System.out.println("0. Salir");
        System.out.print("Seleccione una opcion: ");
    }

    public static void ejecutarOpcion(int opcion, Practica9_Tema1_AD faa) {
        switch (opcion) {
            case 1:
                faa.anadirRegistro();
                break;
            case 2:
                faa.leerRegistroMenu();
                break;
            case 3:
                faa.modificarRegistro();
                break;
            case 4:
                faa.seleccionarCampo();
                break;
            case 5:
                faa.seleccionarColumna();
                break;
            case 6:
                faa.seleccionarRegistroComoLista();
                break;
            case 7:
                faa.seleccionarRegistroComoMapa();
                break;
            case 8:
                faa.actualizarRegistro();
                break;
            case 9:
                faa.eliminarRegistro();
                break;
            case 0:
                System.exit(0);
            default:
                System.out.println("Opcion no válida.");
        }
    }
}
