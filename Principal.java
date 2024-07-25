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
    private Semaphore paseDuentes;
    private Semaphore paseRenos;
    private int DUENDES = 3;
    private int RENOS = 9;
    
    public SantaBack() {
        this.paseDuentes = new Semaphore(DUENDES);
        this.paseRenos = new Semaphore(RENOS);
    }

    // Para un solo hilo Claus
    public void run () throws InterruptedException {
        if (paseRenos.availablePermits() == 0) {
            System.out.println("Claus trabaja con los renos");
            Thread.sleep(5000);
            paseRenos.release(RENOS); // Liberar todos los permisos a la vez
        } else if (paseDuentes.availablePermits() == 0) {
            System.out.println("Santa Claus ayuda a los duendes");
            Thread.sleep(1000);
            paseDuentes.release(DUENDES); // Liberar todos los permisos a la vez
        } else {
            System.out.println("Claus duerme");
            Thread.sleep((int) (Math.random() * 1000));
        }
    }

    // Para varios hilos Duendes 'synchronized'
    synchronized void formarDuendes() {
        try {
            paseDuentes.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Para varios hilos Renos 'synchronized'
    synchronized void formarRenos(){
        try {
            paseRenos.acquire(); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Claus implements Runnable {
    private SantaBack paseClaus;

    public Claus(SantaBack paseClaus) {
        this.paseClaus = paseClaus;
    }

    @Override
    public void run() {
        while (true) {
            try {
                paseClaus.run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Reno implements Runnable {
    private int id;
    private SantaBack paseReno;

    public Reno(int id, SantaBack paseReno) {
        this.id = id;
        this.paseReno = paseReno;
    }

    @Override
    public void run() {
        while (true) {
            try {
                esperar();
                paseReno.formarRenos();
                formarReno();
            } catch (InterruptedException ex) {
                Logger.getLogger(Reno.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void esperar() throws InterruptedException {
        Thread.sleep((int) (Math.random() * 25000));
    }

    private void formarReno() {
        System.out.println("Reno " + id + " llega a la fila");
    }
}

class Duende implements Runnable {
    private int id;
    private SantaBack paseDuende;

    public Duende(int id, SantaBack paseDuende) {
        this.id = id;
        this.paseDuende = paseDuende;
    }

    @Override
    public void run() {
        while (true) {
            try {
                esperar();
                paseDuende.formarDuendes();
                formarDuende();
            } catch (InterruptedException ex) {
                Logger.getLogger(Duende.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void esperar() throws InterruptedException {
        Thread.sleep((int) (Math.random() * 30000));
    }

    private void formarDuende() {
        System.out.println("Duende " + id + " llega a la fila");
    }
}