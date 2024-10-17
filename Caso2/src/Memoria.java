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

        if (tablaPaginas.get(paginaId) == -1) {
            // La página no está en memoria física, entonces hay fallo de página
            this.fallosDePagina++;

    
            //cargarPaginaEnMemoriaFisica(paginaId, esEscritura);
            //cargarPaginaEnMemoriaFisica2(paginaId, esEscritura);
            //cargarPaginaEnMemoriaFisica3(paginaId, esEscritura); //473
            cargarPaginaEnMemoriaFisica4(paginaId, esEscritura);
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
                System.out.println("Página " + paginaId + " cargada en el marco de página " + marco.getKey());
                //Marcar como referenciada
                marcarReferencia(paginaId, esEscritura);
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
            //Marcar como referenciada
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

    private void cargarPaginaEnMemoriaFisica3(int paginaId, boolean esEscritura){
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

    private void cargarPaginaEnMemoriaFisica4(int paginaId, boolean esEscritura) {
        // Buscar un marco libre en la memoria física
        for (Map.Entry<Integer, Integer> marco : memoriaFisica.entrySet()) {
            if (marco.getValue() == -1) {
                // Si hay un marco libre, cargar la página directamente
                memoriaFisica.put(marco.getKey(), paginaId);
                tablaPaginas.put(paginaId, marco.getKey());
                System.out.println("Página " + paginaId + " cargada en el marco de página " + marco.getKey());
                // Marcar la página como referenciada y modificada si es necesario
                marcarReferencia(paginaId, esEscritura);
                return;
            }
        }
    
        // Crear las clases para clasificar las páginas
        Map<Integer, List<Pagina>> clases = new HashMap<>();
        for (int i = 0; i < 4; i++) {
            clases.put(i, new ArrayList<Pagina>());
        }
    
        // Clasificar las páginas de la memoria física en las 4 clases de NRU
        for (Map.Entry<Integer, Integer> marco : memoriaFisica.entrySet()) {
            int paginaEnMemoria = marco.getValue();
            Pagina pagina = memoriaVirtual.get(paginaEnMemoria);
    
                // Clase 0: No referenciada, no modificada
                if (!pagina.getBitReferencia() && !pagina.getBitModificacion()) {
                    clases.get(0).add(pagina);
                }
                // Clase 1: No referenciada, modificada
                else if (!pagina.getBitReferencia() && pagina.getBitModificacion()) {
                    clases.get(1).add(pagina);
                }
                // Clase 2: Referenciada, no modificada
                else if (pagina.getBitReferencia() && !pagina.getBitModificacion()) {
                    clases.get(2).add(pagina);
                }
                // Clase 3: Referenciada, modificada
                else if (pagina.getBitReferencia() && pagina.getBitModificacion()) {
                    clases.get(3).add(pagina);
                }
        }
    
        // Seleccionar la clase más baja no vacía para el reemplazo
        for (int i = 0; i < 4; i++) {
            if (!clases.get(i).isEmpty()) {
                // Seleccionar la primera página de la clase encontrada (orden secuencial)
                Pagina paginaReemplazo = clases.get(i).get(0);
    
                // Obtener el marco donde está la página a reemplazar
                int marcoReemplazar = tablaPaginas.get(paginaReemplazo.numeroPaginaVirtual);
                int paginaReemplazada = memoriaFisica.get(marcoReemplazar);
    
                // Imprimir la información de reemplazo
                System.out.println("Reemplazando página " + paginaReemplazada + " del marco " + marcoReemplazar + " por la página " + paginaId);
    
                // Reemplazar la página
                memoriaFisica.put(marcoReemplazar, paginaId);
                tablaPaginas.put(paginaId, marcoReemplazar);
                tablaPaginas.put(paginaReemplazada, -1);  // Marcar la página reemplazada como no cargada
    
                // Marcar la nueva página como referenciada (y modificada si es escritura)
                marcarReferencia(paginaId, esEscritura);
                return;
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
        
        // for (int marco : memoriaFisica.keySet()) {
        //     int paginaId = memoriaFisica.get(marco);
        //     if (paginaId != -1) {
        //         Pagina pagina = memoriaVirtual.get(paginaId);
        //         pagina.resetearBitDeReferencia();
        //     }
        // }

        for( Pagina pagina : memoriaVirtual){
            pagina.resetearBitDeReferencia();
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
