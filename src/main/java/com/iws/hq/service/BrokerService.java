package com.iws.hq.service;

import java.io.IOException;

import javax.servlet.ServletException;

import org.springframework.stereotype.Service;

@Service
public class BrokerService {
	private final long PERIOD_TIME_OF_WAIT = 100l;


	public BrokerService() {
		super();
	}

	public String getMessageByTopic(String topic) throws ServletException, IOException {
		String sessionID = TopicsHQ.getInstance().genNewSessionID();
		return getMessageByTopic(topic,sessionID);
		
	}
	
	public String getMessageByTopic(String topic, String sessionID)
			throws ServletException, IOException {

		Product msg = null;
		msg = TopicsHQ.getInstance().getTopic(sessionID, topic);

		if (null == msg && (!(null == topic || topic.length() < 1))) {
			try {
				synchronized (this) {
					this.wait(PERIOD_TIME_OF_WAIT);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			msg = TopicsHQ.getInstance().getTopic(sessionID, topic);
		}

		// request.setAttribute(CON_SID, sessionID);
		return null == msg ? "Null" : msg.getMessg();

	}

	public void postMessageByTopic(String topic, String message)
			throws ServletException, IOException {
		String info = TopicsHQ.getInstance().putTopic(topic, message);
		System.out.println("Post result:"+info);

	}

}