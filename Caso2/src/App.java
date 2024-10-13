import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("");
            System.out.println("Menú de opciones:");
            System.out.println("1. Generar referencias");
            System.out.println("2. Calcular datos buscados");
            System.out.println("3. Salir");
            System.out.print("Seleccione una opción: ");
            
            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1:
                    System.out.print("Ingrese el tamaño de página (en bytes): ");
                    int tamanioPagina = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Ingrese el nombre del archivo de la imagen: ");
                    String nombreImagen = scanner.nextLine();
                    String ruta = "../archivos/" + nombreImagen;

                    try {
                        // Crear una instancia de la clase Imagen
                        Imagen img = new Imagen(ruta);

                        // Leer la longitud del mensaje
                        int longitud = img.leerLongitud();
                        System.out.println("Longitud del mensaje: " + longitud);

                        char[] mensajeRecuperado = new char[longitud];
                        ArrayList<String> referencias = new ArrayList<>();

                        // Recuperar el mensaje y registrar las referencias
                        img.recuperar(mensajeRecuperado, longitud, tamanioPagina, referencias);

                        System.out.println("Mensaje recuperado: " + new String(mensajeRecuperado));

                        // Escribir el archivo de referencias
                        String archivoReferencias = "../archivos/referencias.txt";
                        try (FileWriter fw = new FileWriter(archivoReferencias)) {
                            // Escribir datos generales
                            fw.write("TP: " + tamanioPagina + "\n");
                            fw.write("NF: " + img.getAlto() + "\n");
                            fw.write("NC: " + img.getAncho() + "\n");
                            fw.write("NR: " + referencias.size() + "\n");

                            // Calcular el número de páginas necesarias
                            int numPaginasImagen = (img.getAlto() * img.getAncho() * 3 + tamanioPagina - 1) / tamanioPagina;
                            int numPaginasMensaje = (longitud * 8 + tamanioPagina - 1) / tamanioPagina;
                            int numPaginasTotales = numPaginasImagen + numPaginasMensaje;

                            fw.write("NP: " + numPaginasTotales + "\n");

                            // Escribir las referencias
                            for (String referencia : referencias) {
                                fw.write(referencia + "\n");
                            }

                            System.out.println("Archivo de referencias generado exitosamente.");
                        } catch (IOException e) {
                            System.out.println("Error al escribir el archivo de referencias: " + e.getMessage());
                        }
                    } catch (ArithmeticException e) {
                        System.out.println("Error al procesar la imagen: " + e.getMessage());
                    }
                    break;

                case 2:
                    System.out.print("Ingrese el número de marcos de página: ");
                    int numMarcosPaginaStr = scanner.nextInt();

                    System.out.print("Ingrese el nombre del archivo de referencias: ");
                    String nombreArchivoReferencias = scanner.nextLine();

                    break;

                default:
                    System.out.println("Opción no válida. Por favor, seleccione una opción válida");
            }

        } while (opcion != 3);

        scanner.close();
    }
}
