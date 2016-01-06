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
			justLive(100000);
			
			getRai().buyerWantsToBuyACar(getPersonName());
			System.out.println(getPersonName()+" got in.");
			buyingACar();
			System.out.println(getPersonName()+" is done buying cars");
			getRai().buyerIsDoneBuying(getPersonName());
		}
	}
	
	public void buyingACar(){
		int ms = (int) (Math.random() * 10000);
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
