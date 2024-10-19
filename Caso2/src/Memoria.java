import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Memoria {
    private List<Pagina> memoriaVirtual;  
    private Map<Integer, Integer> tablaPaginas;
    private Map<Integer, Integer> memoriaFisica; //primer integer es el numero del marco, y el segundo es el numero de pagina
    private int fallosDePagina=0;
    private int hits=0;
    private boolean ejecutando;

    public Memoria(int tamanoPagina,int numPaginas, int numMarcos){
        this.memoriaVirtual = new ArrayList<Pagina>();
        this.tablaPaginas = new HashMap<Integer, Integer>();
        this.memoriaFisica = new HashMap<Integer, Integer>();
        for(int i=0;i<numPaginas;i++){
            memoriaVirtual.add(new Pagina(i));
            tablaPaginas.put(i, -1);
        }

        for(int i=0;i<numMarcos;i++){
            memoriaFisica.put(i, -1);
        }
        this.ejecutando = true;
    }

    public synchronized void accesoPagina(int paginaId, boolean esEscritura) {

        if (tablaPaginas.get(paginaId) == -1) {
            // La página no está en memoria física, entonces hay fallo de página
            // Y se busca la pagina a reemplazar
            this.fallosDePagina++;
            cargarPaginaEnMemoriaFisica(paginaId, esEscritura);

        } else {

            this.hits++;
            // Marcar que fue referenciada
            marcarReferencia(paginaId, esEscritura);
        }
    }

    private synchronized void cargarPaginaEnMemoriaFisica(int paginaId, boolean esEscritura) {
        // Buscar un marco libre en la memoria física
        for (Map.Entry<Integer, Integer> marco : memoriaFisica.entrySet()) {
            if (marco.getValue() == -1) {

                memoriaFisica.put(marco.getKey(), paginaId);
                tablaPaginas.put(paginaId, marco.getKey());
                //Marcar como referenciada
                marcarReferencia(paginaId, esEscritura);
                return;
            }
        }

        Integer paginaClase0 = null;
        Integer paginaClase1 = null;
        Integer paginaClase2 = null;
        Integer paginaClase3 = null;
    
        for (Map.Entry<Integer, Integer> marco : memoriaFisica.entrySet()) {
            int paginaEnMemoria = marco.getValue();
            Pagina pagina = memoriaVirtual.get(paginaEnMemoria);
    
            // Clase 0: No referenciada, no modificada
            if (!pagina.getBitReferencia() && !pagina.getBitModificacion()) {
                paginaClase0 = marco.getKey();
                break;
            }
            // Clase 1: No referenciada, modificada
            if (!pagina.getBitReferencia() && pagina.getBitModificacion() && paginaClase1 == null) {
                paginaClase1 = marco.getKey();
            }
            // Clase 2: Referenciada, no modificada
            if (pagina.getBitReferencia() && !pagina.getBitModificacion() && paginaClase2 == null) {
                paginaClase2 = marco.getKey();
            }
            // Clase 3: Referenciada, modificada
            if (pagina.getBitReferencia() && pagina.getBitModificacion() && paginaClase3 == null) {
                paginaClase3 = marco.getKey();
            }
        }
    
        int marcoReemplazar;
        if (paginaClase0 != null) {
            marcoReemplazar = paginaClase0;  // Preferir Clase 0
        } else if (paginaClase1 != null) {
            marcoReemplazar = paginaClase1;  
        } else if (paginaClase2 != null) {
            marcoReemplazar = paginaClase2;  
        } else {
            marcoReemplazar = paginaClase3;  // Si no hay Clase 0, 1, o 2, usar Clase 3
        }
    
        // Reemplazar la página
        int paginaReemplazada = memoriaFisica.get(marcoReemplazar);
    
        // Cargar la nueva página en ese marco
        memoriaFisica.put(marcoReemplazar, paginaId);
        tablaPaginas.put(paginaId, marcoReemplazar);
        tablaPaginas.put(paginaReemplazada, -1);
    
        // Marcar la nueva página como referenciada y 
        // en caso de ser W como modificada
        marcarReferencia(paginaId, esEscritura);

    }

    public synchronized void marcarReferencia(int paginaId, boolean esEscritura) {
        Pagina pagina = memoriaVirtual.get(paginaId);
        pagina.marcarComoAccedida(); 

        if (esEscritura) {
            pagina.marcarComoModificada();
        }
    }

    public synchronized void resetearBitsDeReferencia() {
        
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

    public int getHits(){
        return hits;
    }

}