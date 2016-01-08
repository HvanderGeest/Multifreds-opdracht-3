package entities;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AutoRai {
	private final int PLACE_IN_SHOWROOM = 10;
	private Lock lock;
	private Condition watcherCanEnter, buyerCanEnter, prioritiequeue;
	private int numberOfBuyersInArow = 0;
	private int numberOfWatchersInRoom = 0;
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
	
	/**
	 * called by a watcher when he wants to go in the autorai
	 */
	public void watcherWantsToGoIn(){
		lock.lock();
		try {
			while(!prioritiequeueOpen){
				prioritiequeue.await();
			}
			
			numberOfWatchersInPrioritieQueue++;
			
			while(!watcherCanEnterBoolean){
				watcherCanEnter.await();
			}
			//watcher entered the room
			numberOfWatchersInPrioritieQueue--;
			
			buyerCanEnterBoolean = false;
			numberOfWatchersInRoom++;
			numberOfBuyersInArow = 0;
			System.out.println("watchers in room: "+numberOfWatchersInRoom);
			if(numberOfWatchersInRoom == PLACE_IN_SHOWROOM){
				watcherCanEnterBoolean = false; //showroom is full
			}
			
			
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
			
	}
	/**
	 * called by a buyer when he wants to enter the autorai
	 * @param name name of the buyer
	 */
	public void buyerWantsToBuyACar(String name){
		lock.lock();
		buyersWaiting++;
		System.out.println(name + " wants to buy a car");
		try {
			if(prioritiequeueOpen){
				//prioritiequeu not in progress.
				watcherCanEnterBoolean = false;
			}
			
			while(!buyerCanEnterBoolean ||  numberOfWatchersInRoom >0){
				buyerCanEnter.await();
			}
			//buyer entered the room
			numberOfBuyersInArow++;
			buyersWaiting--;
			System.out.println("buyer entered room, buyers in a row without prio queue: "+numberOfBuyersInArow);
			assert numberOfWatchersInRoom == 0 : "there are still watchers in the room";
			watcherCanEnterBoolean = false;
			buyerCanEnterBoolean = false;
			
			
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
	/**
	 * called by a watcher when he is done watching cars.
	 */
	public void customerIsDoneWatching(){
		lock.lock();
		numberOfWatchersInRoom--;
		System.out.println("watcher left, watchers left in the room: "+ numberOfWatchersInRoom);
		signal();
		lock.unlock();
	}
	/**
	 * called by a buyer when he is done buying cars.
	 * @param name name of the buyer
	 */
	public void buyerIsDoneBuying(String name){
		lock.lock();
		System.out.println("left the room " + name);
		signal();
		lock.unlock();
	}
	
	/**
	 * checks who needs to be signaled
	 */
	private void signal(){
		if(!prioritiequeueOpen){
			//priority queue is still in progress.
			if(numberOfWatchersInPrioritieQueue == 0 && numberOfWatchersInRoom ==0){
				//everybody from the priority queue watched cars
				System.out.println(">>priority queue is over");
				prioritiequeueOpen = true; 
				prioritiequeue.signalAll(); //for performance you can instead of signal all, 
											//only signal PLACE_IN_SHOWROOM times.
			} else if(numberOfWatchersInRoom < PLACE_IN_SHOWROOM){
				watcherCanEnterBoolean = true; //new watcher from the priority queue can enter.
				watcherCanEnter.signal();
				return;
			}
			
			
		}
		if(buyersWaiting > 0){ // potentially a buyer needs to enter
			if(numberOfBuyersInArow == MAX_NUMBER_OF_BUYERS_IN_A_ROW){
				//maximum buyers in a row time for the priority queue
				numberOfBuyersInArow =0;
				if(numberOfWatchersInPrioritieQueue > 0){
					//there are people in the priority queue
					System.out.println(">>time for the people in the priority queue!!");
					prioritiequeueOpen = false;
					watcherCanEnterBoolean = true;
					watcherCanEnter.signalAll(); 
					//for performance you can instead of signal all, only signal PLACE_IN_SHOWROOM times.
				} else {
					//there are no people in the priority que another buyer can enter
					System.out.println(">>prio queue not needed");
					buyerCanEnterBoolean = true;
					buyerCanEnter.signal();
				}
				
			} else{
				//next visitor is a buyer.
				buyerCanEnterBoolean = true;
				watcherCanEnterBoolean = false;
				buyerCanEnter.signal();
			}
		} else {
			watcherCanEnterBoolean = true;
			buyerCanEnterBoolean = true; //in the buyerWantsToBuyACar there is a check if there are 0 people in the room
			watcherCanEnter.signal();
			//there are no buyers waiting so no buyers need to be signalled
		}
		
		
	}
	
	
	
	

}
