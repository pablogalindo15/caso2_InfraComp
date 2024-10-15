import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Memoria {
    private List<Pagina> memoriaVirtual;  
    //private int numMarcos; // Número de marcos de página en la memoria física
    private Map<Integer, Integer> tablaPaginas;
    private Map<Integer, Integer> memoriaFisica; //primer integer es el numero de pagina, y el segundo es el numero de marco de pagina
    private int fallosDePagina=0;
    private boolean ejecutando;
    //private Map<Integer, Integer> memoriaVirtual;
    //private int fallosDePagina = 0;

    public Memoria(int tamanoPagina,int numPaginas, int numMarcos){
        this.memoriaVirtual = new ArrayList<Pagina>();
        this.tablaPaginas = new HashMap<Integer, Integer>();
        this.memoriaFisica = new HashMap<Integer, Integer>();
        for(int i=0;i<numPaginas;i++){
            memoriaVirtual.add(new Pagina(i, tamanoPagina));
            tablaPaginas.put(i, -1);
        }

        for(int i=0;i<numMarcos;i++){
            memoriaFisica.put(i, -1);
        }
        this.ejecutando = true;
    }

    public synchronized void accesoPagina(int paginaId, boolean esEscritura) {
        if(tablaPaginas.get(paginaId) == null){
            System.out.println("Pagina 1170");
        }
        if (tablaPaginas.get(paginaId) == -1) {
            // La página no está en memoria física, entonces hay fallo de página
            this.fallosDePagina++;

    
            cargarPaginaEnMemoriaFisica(paginaId);
        } else {

            // Marcar que fue referenciada
            Pagina pagina = memoriaVirtual.get(paginaId);
            pagina.marcarComoAccedida(); 

            if (esEscritura) {
                pagina.marcarComoModificada();
            }
        }
    }

    private synchronized void cargarPaginaEnMemoriaFisica(int paginaId) {
        // Buscar un marco libre en la memoria física
        for (Map.Entry<Integer, Integer> marco : memoriaFisica.entrySet()) {
            if (marco.getValue() == -1) {

                memoriaFisica.put(marco.getKey(), paginaId);
                tablaPaginas.put(paginaId, marco.getKey());
                System.out.println("Página " + paginaId + " cargada en el marco de página " + marco.getKey());
                return;
            }
        }

        int marcoReemplazar = -1;



        //Elegir la pagina a reeeplazar así o de otra forma
        //Siempre usa el marco 0 para hacer el reemplazo de la página
        for (Map.Entry<Integer, Integer> marco : memoriaFisica.entrySet()) {
            int paginaEnMemoria = marco.getValue();
            Pagina pagina = memoriaVirtual.get(paginaEnMemoria);

            // Clase 0: No referenciada, no modificada
            if (!pagina.getBitReferencia() && !pagina.getBitModificacion()) {
                marcoReemplazar = marco.getKey();
                break;
            }
            //Clase 1 (No referenciada, modificada)
            if (!pagina.getBitReferencia() && pagina.getBitModificacion()) {
                marcoReemplazar = marco.getKey();
                break;
            }
            // Clase 2 (Referenciada, no modificada)
            if (pagina.getBitReferencia() && !pagina.getBitModificacion()) {
                marcoReemplazar = marco.getKey();
                break;
            }
            //Clase 3 (Referenciada, modificada)
            if (pagina.getBitReferencia() && pagina.getBitModificacion()) {
                marcoReemplazar = marco.getKey();
                break;
            }

        }

        if (marcoReemplazar != -1) {
            int paginaReemplazada = memoriaFisica.get(marcoReemplazar);
            System.out.println("Reemplazando página " + paginaReemplazada + " en el marco " + marcoReemplazar);

            // Cargar la nueva página en ese marco
            memoriaFisica.put(marcoReemplazar, paginaId);
            tablaPaginas.put(paginaId, marcoReemplazar);
            tablaPaginas.put(paginaReemplazada, -1);  // Marcar la página reemplazada como no cargada
            fallosDePagina++;
        }

    }

    public void resetearBitsDeReferencia() {
        
        for (int marco : memoriaFisica.keySet()) {
            int paginaId = memoriaFisica.get(marco);
            if (paginaId != -1) {
                Pagina pagina = memoriaVirtual.get(paginaId);
                pagina.resetearBitDeReferencia();
            }
        }
    }

    public int getFallosDePagina() {
        return fallosDePagina;
    }

    public void detenerHilo() {
        ejecutando = false; 
    }

    public boolean isEjecutando() {
        return ejecutando;
    }

}
