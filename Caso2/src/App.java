import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);
        int opcion;
        

        // Bucle para mostrar el menú hasta que el usuario elija salir
        do {
            // Mostrar las opciones
            System.out.println("");
            System.out.println("Menú de opciones:");
            System.out.println("1. Generar referencias");
            System.out.println("2. Calcular datos buscados");
            System.out.println("3. Salir");
            System.out.print("Seleccione una opción: ");
            
            // Leer la opción del usuario
            opcion = scanner.nextInt();
            scanner.nextLine(); // Consumir la nueva línea

            // Procesar la opción seleccionada
            switch (opcion) {
                case 1:
                    System.out.print("Ingrese el tamaño de página: ");
                    String tamanioPagina = scanner.nextLine();

                    System.out.print("Ingrese el nombre del archivo de la imagen: ");
                    String nombreImagen = scanner.nextLine();
                    Imagen img = new Imagen("Caso2/src/" + nombreImagen);
                    int longitud= img.leerLongitud();
                    System.out.println("Longitud " + longitud);
                    char[] mensajeRecuperado = new char[longitud];
                    img.recuperar(mensajeRecuperado, longitud);
                    
                    System.out.println("Mensaje recuperado: " + new String(mensajeRecuperado));
                    
                    System.out.println("Imagen leída exitosamente.");
                    break;

                case 2:
                    System.out.print("Ingrese el numero de marcos de página: ");
                    int numMarcosPaginaStr = scanner.nextInt();

                    System.out.print("Ingrese el nombre del archivo de referencias: ");
                    String nombreArchivoReferencias = scanner.nextLine();

                    break;

                default:
                    System.out.println("Opción no válida. Por favor, seleccione una opción valida");
            }

        } while (opcion != 4);  // Repetir el menú hasta que el usuario elija salir

        scanner.close();
    }


}
