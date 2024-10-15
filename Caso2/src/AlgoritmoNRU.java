public class AlgoritmoNRU extends Thread {
    private Memoria memoria;

    public AlgoritmoNRU(Memoria memoria) {
        this.memoria = memoria;
        
    }

    @Override
    public void run() {
        try {
            while (memoria.isEjecutando()) {
                
                Thread.sleep(2);

                memoria.resetearBitsDeReferencia();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
