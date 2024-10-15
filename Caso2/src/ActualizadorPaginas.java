import java.util.ArrayList;

public class ActualizadorPaginas extends Thread {
    private Memoria memoria;
    ArrayList<String> referencias = new ArrayList<>();// Lista de referencias cargadas del archivo

    public ActualizadorPaginas(Memoria memoria, ArrayList<String> referencias) {
        this.memoria = memoria;
        this.referencias = referencias;
    }

    @Override
    public void run() {
        try {
            for (String referencia : referencias) {
                
                Thread.sleep(1);

                // Procesar la referencia
                String[] referenciaP = referencia.split(",");
                int numeroPagina = Integer.parseInt(referenciaP[1]);
                int desplazamiento= Integer.parseInt(referenciaP[2]);
                String tipo = referenciaP[3];  // "R" o "W" 
                boolean esEscritura = tipo.equals("W");

                memoria.accesoPagina(numeroPagina, esEscritura);
            }
            memoria.detenerHilo();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
