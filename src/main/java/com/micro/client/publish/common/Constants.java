package com.micro.client.publish.common;

public class Constants {

	public static String microRestEndpoint = System.getenv("MICRO_ENDPOINT");
	public static String hostName = System.getenv("HOSTNAME");
	public static String MACADDRESSFROMENV = System.getenv("MACADDRESS");
	public static String JWTTOKEN=System.getenv("JWTTOKEN");
	public static final String HTTP = "http://";
	public static final String KEY = "key";
	public static final String TYPE = "TYPE";
	public static final String VALUE = "value";
	public static final String HOSTNAME = "hostname";
	public static final String CONTAINER_ID = "containerID";
	public static final String MACADDRESS = "macAddress";
	public static final String PUBLISH_ENDPOINT = "/publish/docker?key=";
	public static final String ROOT_BASH_HISTORY = "/root/.bash_history";
	
	//Event Actions
	public static final String STARTACTION = "start";
	public static final String DESTROYACTION = "destroy";
	public static final String STOPACTION = "stop";
	public static final String NETWORK_CREATE = "create";
	public static final String NETWORK_CONNECT = "connect";
	public static final String NETWORK_DESTROY = "destroy";
	public static final String NETWORK_DISCONNECT = "disconnect";
	public static final String NETWORK_REMOVE = "remove";
	
	
}
