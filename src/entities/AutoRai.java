package entities;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AutoRai {
	private final int PLACE_IN_SHOWROOM = 10;
	private Lock lock;
	private Condition watcherCanEnter, buyerCanEnter, prioritiequeue;
	private int numberOfBuyersInArow = 0;
	private int numberOfCustomersInRoom = 0;
	private int buyersWaiting = 0;
	private int numberOfWatchersInPrioritieQueue = 0;
	private boolean watcherCanEnterBoolean = true;
	private boolean buyerCanEnterBoolean = true;
	private boolean prioritiequeueOpen = true;
	private final int  MAX_NUMBER_OF_BUYERS_IN_A_ROW = 4;
	
	
	public AutoRai(){
		lock = new ReentrantLock(true);
		watcherCanEnter = lock.newCondition();
		buyerCanEnter = lock.newCondition();
		prioritiequeue = lock.newCondition();
	}
	
	
	public void watcherWantsToGoIn(){
		lock.lock();
		try {
			while(!prioritiequeueOpen)
				prioritiequeue.await();
			
			numberOfWatchersInPrioritieQueue++;
			
			while(!watcherCanEnterBoolean)
				watcherCanEnter.await();
			
			numberOfWatchersInPrioritieQueue--;
			
			buyerCanEnterBoolean = false;
			numberOfCustomersInRoom++;
			numberOfBuyersInArow = 0;
			System.out.println("customers in room: "+numberOfCustomersInRoom);
			if(numberOfCustomersInRoom == PLACE_IN_SHOWROOM){
				watcherCanEnterBoolean = false;
			}
			
			
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
			
	}
	
	public void buyerWantsToBuyACar(String name){
		lock.lock();
		buyersWaiting++;
		System.out.println(name + " wants to buy a car");
		try {
			if(prioritiequeueOpen){
				//prioritiequeu not in progress.
				watcherCanEnterBoolean = false;
			}
			
		
			while(!buyerCanEnterBoolean ||  numberOfCustomersInRoom >0)
			buyerCanEnter.await();
			
			
			numberOfBuyersInArow++;
			
			
			buyersWaiting--;
			System.out.println("buyer entered room ");
			assert numberOfCustomersInRoom == 0 : "there are still customers in the room";
			watcherCanEnterBoolean = false;
			buyerCanEnterBoolean = false;
			
			
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
	
	public void customerIsDoneWatching(){
		lock.lock();
		numberOfCustomersInRoom--;
		System.out.println("customer left customers left in the room: "+ numberOfCustomersInRoom);
		signal();
		lock.unlock();
	}
	
	public void buyerIsDoneBuying(String name){
		lock.lock();
		System.out.println("left the room " + name);
		signal();
		lock.unlock();
	}
	
	private void signal(){
		if(!prioritiequeueOpen && numberOfWatchersInPrioritieQueue > 0){
			if(numberOfCustomersInRoom < PLACE_IN_SHOWROOM){
				watcherCanEnterBoolean = true;
				watcherCanEnter.signal();
			}
			return;
		} else {
			
			prioritiequeueOpen = true;
		}
		if(buyersWaiting > 0){
			if(numberOfBuyersInArow == MAX_NUMBER_OF_BUYERS_IN_A_ROW){
				numberOfBuyersInArow =0;
				if(numberOfWatchersInPrioritieQueue > 0){
					System.out.println(">>time for the people in the priority queue!!");
					prioritiequeueOpen = false;
					watcherCanEnterBoolean = true;
					watcherCanEnter.signalAll();
				} else {
					watcherCanEnterBoolean = true;
					buyerCanEnterBoolean = true;
					buyerCanEnter.signal();
				}
				
			} else if(numberOfCustomersInRoom == 0){
				//if numberOfCustomersInRoom ==0
				buyerCanEnterBoolean = true;
				buyerCanEnter.signal();
			}
		} else {
			watcherCanEnterBoolean = true;
			watcherCanEnter.signal();
		}
		
		
	}
	
	
	
	

}
