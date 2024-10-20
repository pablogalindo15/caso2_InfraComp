import java.io.File;
import java.io.FileNotFoundException;
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
                    //Ruta archivo windows
                    String ruta = "Caso2/archivos/" + nombreImagen;
                    //Ruta archivo macOS
                    // String ruta = "../archivos/" + nombreImagen;

                    try {
                        // Crear una instancia de la clase Imagen
                        Imagen img = new Imagen(ruta);

                        // Leer la longitud del mensaje
                        int longitud = img.leerLongitud();
                        System.out.println("Longitud del mensaje: " + longitud);


                        char[] mensajeRecuperado = new char[longitud];
                        

                        // Recuperar el mensaje y registrar las referencias
                        img.recuperar(mensajeRecuperado, longitud, tamanioPagina);

                        System.out.println("Mensaje recuperado: " + new String(mensajeRecuperado));
                        System.out.println("Fin mensaje");

                        // // Escribir el archivo de referencias
                        //WINDOWS para acceder al archivo
                        String archivoReferencias = "Caso2/archivos/referencias.txt";
                        //MACOS para acceder al archivo
                        // String archivoReferencias = "../archivos/referencias.txt";
                        img.guardarReferencias(archivoReferencias);
                        
                        } 
                        catch (ArithmeticException e) {
                            System.out.println("Error al procesar la imagen: " + e.getMessage());
                        }
                    
                    break;

                case 2:
                    System.out.print("Ingrese el número de marcos de página: ");
                    int numMarcosPagina = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Ingrese el nombre del archivo de referencias: ");
                    String nombreArchivoReferencias = scanner.nextLine();
                    //MACOS para acceder al archivo
                    //String rutaArchivoReferencias = "../archivos/" + nombreArchivoReferencias;
                    //WINDOWS para acceder al archivo
                    String rutaArchivoReferencias = "Caso2/archivos/" + nombreArchivoReferencias;
                    
                    
                    

                    ArrayList<String> referencias = new ArrayList<>();
                    int P=0;
                    int NF=0;
                    int NC=0;
                    int NR=0;
                    int NP=0;
                    boolean cargarMem=false;
                    try (Scanner fileScanner = new Scanner(new File(rutaArchivoReferencias))) {
                        while (fileScanner.hasNextLine()) {
                            String linea = fileScanner.nextLine();

                            if (linea.startsWith("Imagen") || linea.startsWith("Mensaje")) {

                                referencias.add(linea);

                            }else{
                                String[] partes = linea.split("=");
                                switch (partes[0]) {
                                    case "P":
                                        P = Integer.parseInt(partes[1]);
                                        break;
                                    case "NF":
                                        NF = Integer.parseInt(partes[1]);
                                        break;
                                    case "NC":
                                        NC = Integer.parseInt(partes[1]);
                                        break;
                                    case "NR":
                                        NR = Integer.parseInt(partes[1]);
                                        break;
                                    case "NP":
                                        NP = (int) Double.parseDouble(partes[1]);
                                        cargarMem=true;
                                        break;
                                    default:
                                        System.out.println("Metadato no reconocido: " + partes[0]);
                                }
                            }
                        }

                    } catch (FileNotFoundException e) {
                        System.out.println("Archivo de referencias no encontrado: " + e.getMessage());
                    }
                    Memoria memoria = new Memoria(P,NP+1,numMarcosPagina);
                    
                    ActualizadorPaginas actualizador = new ActualizadorPaginas(memoria, referencias);
                    AlgoritmoNRU algoritmoNRU = new AlgoritmoNRU(memoria);

                    actualizador.start();
                    algoritmoNRU.start();

                    // Esperar a que ambos threads terminen
                    actualizador.join();
                    algoritmoNRU.join();
                    int hits=memoria.getHits();
                    System.out.println("tamaño de página: " + P);
                    System.out.println("Número de marcos de página: " + numMarcosPagina);
                    System.out.println("Número de referencias: " + NR);
                    System.out.println("Fallos de página: " + memoria.getFallosDePagina());
                    System.out.println("Hits: " + hits);
                    break;
                case 3: 
                System.out.println("Se finalizó correctamente.");
                scanner.close();
                break;
                default:
                    System.out.println("Opción no válida. Por favor, seleccione una opción válida");
            }

        } while (opcion != 3);

        scanner.close();
    }
}
