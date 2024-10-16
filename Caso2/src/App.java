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
                    String ruta = "../archivos/" + nombreImagen;

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
                        String archivoReferencias = "../archivos/referencias.txt";
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
                    String rutaArchivoReferencias = "Caso2/archivos/" + nombreArchivoReferencias;
                     // Crear la memoria simulada
                    // Memoria memoria = new Memoria(numMarcosPaginaStr);
                    // AlgoritmoNRU algoritmoNRU = new AlgoritmoNRU(memoria);
                    // algoritmoNRU.start();  // Iniciar el hilo para el NRU

                    // Leer el archivo de referencias
                    // Memoria memoria=null;
                    ArrayList<String> referencias = new ArrayList<>();
                    int P=0;
                    int NF=0;
                    int NC=0;
                    int NR=0;
                    int NP=0;
                    boolean cargarMem=false;
                    int contador=0;
                    try (Scanner fileScanner = new Scanner(new File(rutaArchivoReferencias))) {
                        while (fileScanner.hasNextLine()) {
                            String linea = fileScanner.nextLine();
                            contador++;
                            // if (cargarMem) {
                            //     memoria = new Memoria(P,NP,numMarcosPagina);
                            //     cargarMem=false;                                
                            // }

                            // Verificar si la línea contiene una referencia (imagen o mensaje)
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
                                        NP = Integer.parseInt(partes[1]);
                                        cargarMem=true;
                                        break;
                                    default:
                                        System.out.println("Metadato no reconocido: " + partes[0]);
                                }
                            }
                            // Simular el acceso a la memoria
                            //Pagina pagina = new Pagina(paginaId);
                            //memoria.accesoPagina(pagina, esEscritura);
                        }

                    } catch (FileNotFoundException e) {
                        System.out.println("Archivo de referencias no encontrado: " + e.getMessage());
                    }
                    Memoria memoria = new Memoria(P,NP,numMarcosPagina);
                    
                    ActualizadorPaginas actualizador = new ActualizadorPaginas(memoria, referencias);
                    AlgoritmoNRU algoritmoNRU = new AlgoritmoNRU(memoria);

                    actualizador.start();
                    algoritmoNRU.start();

                    // Esperar a que ambos threads terminen
                    actualizador.join();
                    algoritmoNRU.join();
                    System.out.println("Fallos de página: " + memoria.getFallosDePagina());
                    break;

                default:
                    System.out.println("Opción no válida. Por favor, seleccione una opción válida");
            }

        } while (opcion != 3);

        scanner.close();
    }
}
