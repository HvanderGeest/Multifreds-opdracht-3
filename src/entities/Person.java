package entities;

public abstract class Person extends Thread {
	private AutoRai rai;
	private String name;
	
	public Person(AutoRai rai, String name){
		this.rai = rai;
		this.name = name;
	}
	
	protected void justLive(int maxMiliSeconds){
		int ms = (int) (Math.random() * maxMiliSeconds);
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getPersonName() {
		return name;
	}

	public AutoRai getRai() {
		return rai;
	}

}
