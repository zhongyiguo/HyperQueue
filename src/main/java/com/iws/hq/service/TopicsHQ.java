package com.iws.hq.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.lang.String;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;




public class TopicsHQ {
	private static TopicsHQ instance=null;	
	
	private final Lock lock = new ReentrantLock();
	private final Condition full = lock.newCondition();
	private final Condition empty = lock.newCondition();

	private static final int MAX_LENGTH_OF_QUEUE = 100;
	private static final int LENGTH_OF_CHUNK = 10;
	

	private  final long 	PERIOD_TIME_OF_CLEANINGUP = 100000l; 
	private  final long 	PERIOD_TIME_OF_EXPIRED = 200000l;
	private  final long 	PERIOD_TIME_OF_NOACTION = 300000l; 
	private  final String   PREFIX_OF_SESSION_ID = "SID";
	
	public static Map<String,ArrayList<Product>> topicsQueue = new HashMap<String,ArrayList<Product>>();// <topic, queue>
	private static Map<String, Map<String, Integer>> consumersLocMap = new HashMap<String,Map<String, Integer>>();// <topic, <sessionID,offset>>
	private static CleanUp cleanUp = null;
	
	private ExecutorService threadPool = Executors.newCachedThreadPool();
	
	public static TopicsHQ getInstance() {
	if(instance==null) {
	synchronized(TopicsHQ.class) {
		if(instance==null)
			instance = new TopicsHQ();		
	}
	}
	return instance;
	}
	
	private TopicsHQ() {
				
	}
	
	public String putTopic(String topic,String message){
		long currentTime = System.currentTimeMillis();               
		
		if(topic!=null && topic.length()>0){ 			

			ArrayList<Product> queue = topicsQueue.get(topic);
			if( queue == null){
				queue = new ArrayList<Product>();                  
				topicsQueue.put( topic, queue);
			}
			
			Product pd = new Product(message,currentTime);
			Producer producer = new Producer(topic, pd);			
			threadPool.execute(producer);
			return ("[Topic="+topic+ ",msg="+message+"]-->has been added.");
			}else{
				return "Topic can not be empty!";
			}
	}

	
	public Product getTopic(String sessionID,String topic){
			if (null==topic || topic.length()<1){
				return null;
			}        
			if (null==sessionID || sessionID.length()<1){
				return null;
			} 
			Product msg=null;	
				int offset = 0;
					
				Map<String,Integer> loc=consumersLocMap.get(topic);
				
				if( loc!=null ){					
						if (loc.get(sessionID)!=null){
							offset = loc.get(sessionID);						
						}
				}else{
					loc = new HashMap<String,Integer>();
				}
				
				ArrayList<Product> queue=topicsQueue.get(topic);
				if (null==queue || queue.size()==0){//empty message				
				}else{	
					if (offset<queue.size()){
						msg = queue.get(offset);
					}else{//no mew message					
					}							
				
				if (offset<queue.size()){
					offset++;					
						loc.put(sessionID, offset);					
					
					consumersLocMap.put(topic, loc);
				}						
				}
				
				return msg;
		
		
	}
	
	public String genNewSessionID(){
		return (PREFIX_OF_SESSION_ID + String.valueOf(System.currentTimeMillis()));
	}

	public void doCleanUp(){		
		if (null==cleanUp){
			cleanUp = new CleanUp();
			threadPool.execute(cleanUp);
			//cleanUp.start();					
		}else{
			if (!cleanUp.isAlive()){
				threadPool.execute(cleanUp);
				//cleanUp.start();		
			}
		}

	}
	//clean the expired message
	class CleanUp extends Thread{		
		public CleanUp(){		
		}
	    
 		public void run(){				
			while(true){			
				try {									
					Thread.sleep(PERIOD_TIME_OF_CLEANINGUP);	
					long currentTime = System.currentTimeMillis();
					if(topicsQueue.isEmpty() == false){
						synchronized(topicsQueue){
							Iterator<Entry<String, ArrayList<Product>>> iterator = topicsQueue.entrySet().iterator();
						
								while (iterator.hasNext()) {
								Map.Entry<String,ArrayList<Product>>  entry = iterator.next();
								String topic = entry.getKey();
								ArrayList<Product> queue = entry.getValue();
								int length = queue.size();
								int numOfDel = 0;
								if (null==queue||queue.size()<1){
									//empty
								}else{
									synchronized(queue){
										Iterator<Product> pIterator = queue.iterator();
										Product p;
										while (pIterator.hasNext()){
											p = pIterator.next();
											if (null==p || ((currentTime-p.getTimeStamp())>PERIOD_TIME_OF_EXPIRED)){												
												pIterator.remove();
												numOfDel++;
											}else{
												break;
											} 
										}						
									}
								}
								if (numOfDel>0) {
									lock.lock();
									try{	
										synchronized(full){
											full.notifyAll();	
										}								
									}finally{
										lock.unlock();
									}
								}
								if( consumersLocMap.isEmpty() == false ){
									Map<String, Integer> mapOfConsumer = consumersLocMap.get(topic);
									synchronized(mapOfConsumer){
									Iterator<Entry<String, Integer>> iteratorC = mapOfConsumer.entrySet().iterator();
									while (iteratorC.hasNext()) {
										Map.Entry<String,Integer>  entryC = iteratorC.next();
										int offset =  entryC.getValue();
										int newOffset = offset - numOfDel;
										if(newOffset<0)  newOffset = 0;									
										entryC.setValue(newOffset);		
									}
									}
								}						
							}
						}
					}					
			
				} catch (InterruptedException e) {				
					e.printStackTrace();
				}
			}
		}
	}

	

	class Producer extends Thread{
		private String topic;
		private Product message = null;
		public Producer(String topic, Product message ){
			this.topic = topic; 
			this.message = message;  
		}


		public String getTopic(){
			return topic;
		}

		public void run(){
			ArrayList<Product> queue=topicsQueue.get(this.topic);
			if (null==queue){
				queue = new ArrayList<Product>();
				topicsQueue.put(this.topic, queue);
			}
			  
				lock.lock();
				try{
					if(queue.size() == MAX_LENGTH_OF_QUEUE){
						System.out.println("warning:queue is full!");
						full.await();
					}
					queue.add(this.message);
				}catch(InterruptedException ie){
					System.out.println("producer is interrupted!");
				}finally{
					lock.unlock();
				}
			

		}

	}
}
