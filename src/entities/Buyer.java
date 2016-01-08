package entities;

public class Buyer extends Person {

	public Buyer(AutoRai rai, String name) {
		super(rai, name);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void run() {
		super.run();
		while(true){
			justLive(120000);
			
			getRai().buyerWantsToBuyACar(getPersonName());
			System.out.println(getPersonName()+" got in.");
			buyingACar();
			System.out.println(getPersonName()+" is done buying cars");
			getRai().buyerIsDoneBuying(getPersonName());
		}
	}
	/**
	 * simulates the time it costs to buy a car.
	 */
	public void buyingACar(){
		int ms = (int) (Math.random() * 7500);
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
