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
            System.out.println("3. Esconder mensaje en imagen");
            System.out.println("4. Salir");
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
                    String ruta = "caso2/archivos/" + nombreImagen;

                    try {
                        // Crear una instancia de la clase Imagen
                        Imagen img = new Imagen(ruta);

                        // Leer la longitud del mensaje
                        int longitud = img.leerLongitud();
                        System.out.println("Longitud del mensaje: " + longitud);

                        char[] mensajeRecuperado = new char[longitud];
                        ArrayList<String> referencias = new ArrayList<>();

                        // Recuperar el mensaje y registrar las referencias
                        char[] mensajeFinal = img.recuperar(mensajeRecuperado, longitud, tamanioPagina);

                        System.out.println("Mensaje recuperado: " + new String(mensajeFinal));
                        System.out.println("Longitud mensaje: " + mensajeFinal.length);

                        // Escribir el archivo de referencias
                        String archivoReferencias = "caso2/archivos/referencias.txt";
                        img.guardarReferencias(archivoReferencias);
                        System.out.println(Imagen.contador);
                        
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
                    Memoria memoria = new Memoria(P,NP+1,numMarcosPagina);
                    
                    ActualizadorPaginas actualizador = new ActualizadorPaginas(memoria, referencias);
                    AlgoritmoNRU algoritmoNRU = new AlgoritmoNRU(memoria);

                    actualizador.start();
                    algoritmoNRU.start();

                    // Esperar a que ambos threads terminen
                    actualizador.join();
                    algoritmoNRU.join();
                    int hits=memoria.getHits();
                    int accesos=memoria.getAccesos();
                    double porcentajeAcceso = ((double) hits / accesos) * 100;
                    String res = String.format("%.2f", porcentajeAcceso); 
                    System.out.println(" "); 
                    System.out.println("Marcos asignados: " + numMarcosPagina);
                    System.out.println("Total referencias: " + referencias.size());
                    System.out.println("Fallos de página: " + memoria.getFallosDePagina());
                    System.out.println("Hits: " + hits);
                    //System.out.println("Porcentaje de Accesos: " + res + "%"); ;
                    break;

                case 3:
                System.out.print("Ingrese el tamaño de página (en bytes): ");
                int tamanioPagina2 = scanner.nextInt();
                scanner.nextLine();

                System.out.print("Ingrese el nombre del archivo de la imagen con el mensaje: ");
                String imagen = scanner.nextLine();
                String ruta2 = "caso2/archivos/" + imagen;
                try {
                    // Crear una instancia de la clase Imagen
                    Imagen img = new Imagen(ruta2);

                    // Leer la longitud del mensaje
                    int longitud = img.leerLongitud();
                    System.out.println("Longitud del mensaje: " + longitud);

                    char[] mensajeRecuperado = new char[longitud];
                    ArrayList<String> referencias2 = new ArrayList<>();

                    // Recuperar el mensaje y registrar las referencias
                    char[] mensajeFinal = img.recuperar(mensajeRecuperado, longitud, tamanioPagina2);
                    int nuevoTamanio = 2000;
                    char[] mensajeFinalReducido = new char[nuevoTamanio];

                    // Copiar los primeros 4648 caracteres al nuevo arreglo
                    System.arraycopy(mensajeFinal, 0, mensajeFinalReducido, 0, nuevoTamanio);

                    Imagen img2 = new Imagen("caso2/archivos/caso2-parrots.bmp");
                    img2.esconder(mensajeFinalReducido, mensajeFinalReducido.length);
                    img2.escribirImagen("caso2/archivos/parrots_2000.bmp");

                    System.out.println("Mensaje recuperado: " + new String(mensajeFinalReducido));
                    System.out.println("Longitud mensaje reducido: " + mensajeFinalReducido.length);
                    
                    } 
                    catch (ArithmeticException e) {
                        System.out.println("Error al procesar la imagen: " + e.getMessage());
                    }

                default:
                    System.out.println("Opción no válida. Por favor, seleccione una opción válida");
            }

        } while (opcion != 4);

        scanner.close();
    }
}
