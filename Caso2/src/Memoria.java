import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Memoria {
    private List<Pagina> memoriaVirtual;  
    //private int numMarcos; // Número de marcos de página en la memoria física
    private Map<Integer, Integer> tablaPaginas;
    private Map<Integer, Integer> memoriaFisica; //primer integer es el numero del marco, y el segundo es el numero de pagina
    private int fallosDePagina=0;
    private int hits=0;
    private boolean ejecutando;
    private int accesos=0;
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
        accesos++;
        if(tablaPaginas.get(paginaId) == null){
            System.out.println("Pagina 1170");
        }

        if (tablaPaginas.get(paginaId) == -1) {
            // La página no está en memoria física, entonces hay fallo de página
            this.fallosDePagina++;

    
            //cargarPaginaEnMemoriaFisica(paginaId, esEscritura);
            cargarPaginaEnMemoriaFisica2(paginaId, esEscritura);
            //cargarPaginaEnMemoriaFisica3(paginaId, esEscritura);
        } else {

            this.hits++;
            // Marcar que fue referenciada
            marcarReferencia(paginaId, esEscritura);
        }
    }

    private synchronized void cargarPaginaEnMemoriaFisica2(int paginaId, boolean esEscritura) {
        // Buscar un marco libre en la memoria física
        for (Map.Entry<Integer, Integer> marco : memoriaFisica.entrySet()) {
            if (marco.getValue() == -1) {

                memoriaFisica.put(marco.getKey(), paginaId);
                tablaPaginas.put(paginaId, marco.getKey());
                System.out.println("Página " + paginaId + " cargada en el marco de página " + marco.getKey());
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
        System.out.println("Reemplazando página " + paginaReemplazada + " en el marco " + marcoReemplazar);
    
        // Cargar la nueva página en ese marco
        memoriaFisica.put(marcoReemplazar, paginaId);
        tablaPaginas.put(paginaId, marcoReemplazar);
        tablaPaginas.put(paginaReemplazada, -1);  // Marcar la página reemplazada como no cargada
    
        // Marcar la nueva página como referenciada y posiblemente modificada
        marcarReferencia(paginaId, esEscritura);

    }

    private synchronized void cargarPaginaEnMemoriaFisica3(int paginaId, boolean esEscritura){
        // Buscar un marco libre en la memoria física
       for (Map.Entry<Integer, Integer> marco : memoriaFisica.entrySet()) {
           if (marco.getValue() == -1) {

               memoriaFisica.put(marco.getKey(), paginaId);
               tablaPaginas.put(paginaId, marco.getKey());
               System.out.println("Páginaaa " + paginaId + " cargada en el marco de página " + marco.getKey());
               //Marcar como referenciada
               marcarReferencia(paginaId, esEscritura);
               return;
           }
       }

       Map<Integer, List<Pagina>> clases;
       clases = new HashMap<Integer, List<Pagina>>();
       for(int i=0;i<4;i++){
           clases.put(i, new ArrayList<Pagina>());
       }


       for(int i=0;i<4; i++){
           for(Map.Entry<Integer, Integer> marco : memoriaFisica.entrySet()){
               int paginaEnMemoria = marco.getValue();
               Pagina pagina = memoriaVirtual.get(paginaEnMemoria);
               // Clase 0: No referenciada, no modificada
               if (!pagina.getBitReferencia() && !pagina.getBitModificacion() && i==0 && tablaPaginas.get(pagina.numeroPaginaVirtual) != -1) {
                   clases.get(i).add(pagina);
               }
               // Clase 1: No referenciada, modificada
               if (!pagina.getBitReferencia() && pagina.getBitModificacion() && i==1 && tablaPaginas.get(pagina.numeroPaginaVirtual) != -1) {
                   clases.get(i).add(pagina);
               }
               // Clase 2: Referenciada, no modificada
               if (pagina.getBitReferencia() && !pagina.getBitModificacion() && i==2 && tablaPaginas.get(pagina.numeroPaginaVirtual) != -1) {
                   clases.get(i).add(pagina);
               }
               // Clase 3: Referenciada, modificada
               if (pagina.getBitReferencia() && pagina.getBitModificacion() && i==3 && tablaPaginas.get(pagina.numeroPaginaVirtual) != -1) {
                   clases.get(i).add(pagina);
               }
           }
           if (clases.get(i).size() > 0) {
               Pagina paginaReemplazo= clases.get(i).get(0);

               int marcoReemplazar = tablaPaginas.get(paginaReemplazo.numeroPaginaVirtual);
               int paginaReemplazada = memoriaFisica.get(marcoReemplazar);
               System.out.println("Reemplazando página " + paginaReemplazada + " del marco " + marcoReemplazar+ " por la pagina "+paginaId);
       
               // Cargar la nueva página en ese marco
               memoriaFisica.put(marcoReemplazar, paginaId);
               tablaPaginas.put(paginaId, marcoReemplazar);
               tablaPaginas.put(paginaReemplazada, -1);  // Marcar la página reemplazada como no cargada
               //Marcar como referenciada
               marcarReferencia(paginaId, esEscritura);
               break;
               
           }
       }
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

    public int getAccesos(){
        return accesos;
    }

}