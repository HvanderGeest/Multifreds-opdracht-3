import entities.AutoRai;
import entities.Buyer;
import entities.Watcher;

public class APL {
	private final static int NR_OF_WATCHERS = 100;
	private final static int NR_OF_BUYERS = 15;

	public static void main(String[] args) {
		AutoRai show = new AutoRai();
		System.out.println(">>>START VAN DE SIMULATIE<<<");
		for(int i = 0; i < NR_OF_BUYERS; i++){
			Buyer b = new Buyer(show, "Buyer nr: "+i);
			b.start();
		}
		for(int i = 0; i < NR_OF_WATCHERS; i++){
			Watcher w = new Watcher(show, "Watcher nr: "+i);
			w.start();
		}
		
		

	}

}
