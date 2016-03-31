/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.filiberry.mqttRemoteControlActor;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

import de.filiberry.mqttRemoteControlActor.tools.RemoteControlActorExecutor;

public class Activator implements BundleActivator, ManagedService, MqttCallback {

	private ServiceRegistration serviceReg;
	private Logger log = Logger.getLogger(this.getClass().getName());
	private MqttClient client = null;

	private RemoteControlModel remoteControlModel;

	@Override
	public void start(BundleContext context) {
		remoteControlModel = new RemoteControlModel();
		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		properties.put(Constants.SERVICE_PID, "mqttRemoteControlActor");
		serviceReg = context.registerService(ManagedService.class.getName(), this, properties);
	}

	@Override
	public void stop(BundleContext context) {
		log.info("The mqttRemoteControlActor Bundle stopped.");
		if (client != null && client.isConnected()) {
			try {
				client.disconnect();
			} catch (MqttException e) {
				log.warning(e.getMessage());
			}
		}
		client = null;
	}

	/**
	 * 
	 */
	@Override
	public void updated(Dictionary properties) throws ConfigurationException {
		if (properties == null) {
			log.info("mqttRemoteControlActor config is null - Please give me a config File in Karaf/etc");
			return;
		}
		remoteControlModel.setGpioPin(new Integer("" + properties.get("GPIO_PIN")));
		remoteControlModel.setMqttHost((String) properties.get("MQTT_HOST"));
		remoteControlModel.setMqttTopic((String) properties.get("MQTT_TOPIC"));
		remoteControlModel.setSendCommandPath((String) properties.get("SEND_COMMAND_PATH"));
		remoteControlModel.setSystemCode((String) properties.get("SYSTEM_CODE"));
		log.info("mqttRemoteControlActor Config was set.");
		connectToBroker();
	}

	/**
	 * 
	 */
	private boolean connectToBroker() {
		try {
			log.info("Listen on Topic :" + remoteControlModel.getMqttTopic() + " on Host:"
					+ remoteControlModel.getMqttHost());

			if (client == null) {
				client = new MqttClient(remoteControlModel.getMqttHost(), "mqttRCActor");
			}
			if (!client.isConnected()) {
				client.connect();
			}
			client.subscribe(remoteControlModel.getMqttTopic());
			client.setCallback(this);
			log.info("mqttRemoteControlActor connected !");
			// --

		} catch (MqttException e) {
			client = null;
			log.info(e.getMessage());
			return false;
		}
		return true;

	}

	@Override
	public void connectionLost(Throwable arg0) {
		log.warning("mqttRemoteControlActor Connection to Broker is LOST");
		boolean isConnected = false;
		while (!isConnected) {
			log.info("Wait a Minute before reconnect...");
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e1) {
				log.warning("Wait Thread was interrupted");
				e1.printStackTrace();
			}
			isConnected = connectToBroker();
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		boolean messageValid = false;
		// --
		try {
			log.info("Message on Topic " + topic + " Arrived...");
			String remoteChannel = topic.substring(remoteControlModel.getMqttTopic().length() - 1);
			String data = new String(message.getPayload()).trim();
			// --

			if (data.equalsIgnoreCase("ON")) {
				RemoteControlActorExecutor.RunRCActionUnatended(remoteChannel, RemoteControlModel.ON,
						remoteControlModel);
				messageValid = true;
			}
			if (data.equalsIgnoreCase("OFF")) {
				RemoteControlActorExecutor.RunRCActionUnatended(remoteChannel, RemoteControlModel.OFF,
						remoteControlModel);
				messageValid = true;
			}
			if (!messageValid) {
				log.warning("Message ist not on or off");
				log.info("publish : on or off Topic " + remoteControlModel.getMqttTopic()
						+ " replace + with rmote Unit 0..5 ");
			}
		} catch (Exception e) {
			log.info(e.getMessage());
		}
	}

}