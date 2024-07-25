import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Principal {
    public static void main(String[] args) {
        SantaBack santaBack = new SantaBack();
        
        for (int i = 0; i < 9; i++) {
            new Thread(new Reno(i, santaBack)).start();
        }
        for (int i = 0; i < 12; i++) {
            new Thread(new Duende(i, santaBack)).start();
        }
        new Thread(new Claus(santaBack)).start();      
    }
}

class SantaBack {
    private Claus claus;
    private Semaphore dealDuente;
    private Semaphore dealReno;
    private int COLA_DUENDES = 3;
    private int COLA_RENOS = 9;
    
    public SantaBack() {
        this.dealDuente = new Semaphore(COLA_DUENDES);
        this.dealReno = new Semaphore(COLA_RENOS);
        this.claus = new Claus(this); // Inicializar claus aquí
    }

    public void run () throws InterruptedException {
        if (dealReno.availablePermits() == 0) {
            claus.tratarReno();
            salirRenos();
        } else if (dealDuente.availablePermits() == 0) {
            claus.tratarDuende();
            salirDuendes();
        } else {
            claus.dormir();
        }
    }

    synchronized void formarDuendes() {
        try {
            dealDuente.acquire(); // Adquirir primero
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void salirDuendes() {
        dealDuente.release(COLA_DUENDES); // Liberar todos los permisos a la vez
    }

    synchronized void formarRenos(){
        try {
            dealReno.acquire(); // Adquirir primero
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void salirRenos() {
        dealReno.release(COLA_RENOS); // Liberar todos los permisos a la vez
    }
}

class Claus implements Runnable {
    private SantaBack colaClaus;

    public Claus(SantaBack colaClaus) {
        this.colaClaus = colaClaus;
    }

    @Override
    public void run() {
        while (true) {
            try {
                colaClaus.run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void dormir() throws InterruptedException {
        System.out.println("Claus duerme");
        Thread.sleep((int) (Math.random() * 10000));
    }

    synchronized void tratarDuende() {
        System.out.println("Santa Claus ayuda a los duendes");
    }

    synchronized void tratarReno() {
        System.out.println("Claus trabaja con los renos");
    }
}

class Reno implements Runnable {
    private int id;
    private SantaBack colaReno;

    public Reno(int id, SantaBack colaReno) {
        this.id = id;
        this.colaReno = colaReno;
    }

    @Override
    public void run() {
        while (true) {
            try {
                esperar();
                colaReno.formarRenos();
                formarReno();
            } catch (InterruptedException ex) {
                Logger.getLogger(Reno.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    synchronized void esperar() throws InterruptedException {
        Thread.sleep((int) (Math.random() * 25000));
    }

    private void formarReno() {
        System.out.println("Reno " + id + " llega a la cola");
    }
}

class Duende implements Runnable {
    private int id;
    private SantaBack colaDuende;

    public Duende(int id, SantaBack colaDuende) {
        this.id = id;
        this.colaDuende = colaDuende;
    }

    @Override
    public void run() {
        while (true) {
            try {
                esperar();
                colaDuende.formarDuendes();
                formarDuende();
            } catch (InterruptedException ex) {
                Logger.getLogger(Duende.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    synchronized void esperar() throws InterruptedException {
        Thread.sleep((int) (Math.random() * 30000));
    }

    private void formarDuende() {
        System.out.println("Duende " + id + " llega a la cola");
    }
}