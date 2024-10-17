public class Pagina {
    int numeroPaginaVirtual;
    private boolean bitReferencia; //R se establece siempre que se hace referencia a la página (se lee o se escribe).
    private boolean bitModificacion; //M se establece cuando se escribe en la página (es decir, se modifica).
    private Byte[] tamanio;
 
    public Pagina(int numeroPaginaVirtual, int tamanio) {
        this.numeroPaginaVirtual = numeroPaginaVirtual;
        this.bitReferencia = false;  
        this.bitModificacion = false;
        this.tamanio = new Byte[tamanio];

    }

    public void marcarComoModificada() {
        this.bitModificacion = true;
    }

    public void resetearBitDeReferencia() {
        this.bitReferencia = false;
    }

    public void marcarComoAccedida() {
        this.bitReferencia = true;
    }

    public boolean getBitReferencia() {
        return this.bitReferencia;
    }

    public boolean getBitModificacion() {
        return this.bitModificacion;
    }
}
