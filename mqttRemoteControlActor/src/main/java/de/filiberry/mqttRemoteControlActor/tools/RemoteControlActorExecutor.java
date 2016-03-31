package de.filiberry.mqttRemoteControlActor.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import de.filiberry.mqttRemoteControlActor.RemoteControlModel;

public class RemoteControlActorExecutor {

	private static Logger log = Logger.getLogger(RemoteControlActorExecutor.class.getName());

	/**
	 * 
	 * @param config
	 * @param am
	 * @param decision
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void RunRCActionUnatended(String remoteUnit, int action, RemoteControlModel config)
			throws IOException, InterruptedException {
		ArrayList<String> commands = new ArrayList<String>();
		commands.add(config.getSendCommandPath() + "/send");
		commands.add("-u"); // The User Mode -> No sudo needed
		commands.add("-p " + config.getGpioPin()); // Send Data to this GPIO Pin
		commands.add(config.getSystemCode()); // Use this System Code
		commands.add(remoteUnit); // Send Action to this remote Unit
		commands.add("" + action); // 0 = off - 1 = on
		log.info(commands.get(0) + " " + commands.get(1) + " " + commands.get(2) + " " + commands.get(3) + " "
				+ commands.get(4) + " " + commands.get(5));
		// -----------
		ProcessBuilder builder = new ProcessBuilder(commands);
		builder.start();
		Thread.sleep(1000);
		log.info("Command ist pushed out...");
	}

}
