# mqttRemoteControlActor

Der MQTT Remote Control Actor schaltet Funk Steckdosen an oder aus über eine 
MQTT Nachricht.

Dieses Bundle wird in Apache Karaf deployt.

in der Configurationsdatei kann der MQTT Host, der Topic und Parameter für die Funk Steckdosen
eingestellt werden.

Wenn eine entsprechende MQTT Nachricht bei registrierten Broker eintrifft, wird
das "send" Command von  "Wiring Pi" ausgeführt.

Siehe http://www.raspberrypi-tutorials.de/software/funksteckdosen-mit-dem-raspberry-pi-schalten.html
und
https://github.com/xkonni/raspberry-remote
