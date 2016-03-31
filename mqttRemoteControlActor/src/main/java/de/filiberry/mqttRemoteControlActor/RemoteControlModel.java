package de.filiberry.mqttRemoteControlActor;

public class RemoteControlModel {

	public static final int ON = 1;
	public static final int OFF = 0;

	private String systemCode;
	private int gpioPin;
	private String sendCommandPath;
	private String mqttHost;
	private String mqttTopic;

	public String getSystemCode() {
		return systemCode;
	}

	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}

	public int getGpioPin() {
		return gpioPin;
	}

	public void setGpioPin(int gpioPin) {
		this.gpioPin = gpioPin;
	}

	public String getSendCommandPath() {
		return sendCommandPath;
	}

	public void setSendCommandPath(String sendCommandPath) {
		this.sendCommandPath = sendCommandPath;
	}

	public String getMqttHost() {
		return mqttHost;
	}

	public void setMqttHost(String mqttHost) {
		this.mqttHost = mqttHost;
	}

	public String getMqttTopic() {
		return mqttTopic;
	}

	public void setMqttTopic(String mqttTopic) {
		this.mqttTopic = mqttTopic;
	}

}
