public class Pagina {
    int numeroPaginaVirtual;
    private boolean bitReferencia; //R se establece siempre que se hace referencia a la página (se lee o se escribe).
    private boolean bitModificacion; //M se establece cuando se escribe en la página (es decir, se modifica).
 
    public Pagina(int numeroPaginaVirtual) {
        this.numeroPaginaVirtual = numeroPaginaVirtual;
        this.bitReferencia = false;  // Al cargar en memoria, el bit de referencia es 1
        this.bitModificacion = false; // Bit de modificación (si la página ha sido modificada)
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
