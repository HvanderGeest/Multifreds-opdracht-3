package entities;

public class Watcher extends Person {

	public Watcher(AutoRai rai, String name) {
		super(rai, name);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void run() {
		super.run();
		while(true){
			justLive(100000);
			System.out.println(getPersonName()+ " wants to go in");
			getRai().watcherWantsToGoIn();
			System.out.println(getPersonName()+" got in.");
			watchingBeautifullCars();
			System.out.println(getPersonName()+" is done watching cars");
			getRai().customerIsDoneWatching();
		}
		
		
	}
	
	public void watchingBeautifullCars(){
		int ms = (int)( Math.random() * 2000);
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	

}
