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
	
	//Event Actions
	public static final Object STARTACTION = "start";
	public static final Object DESTROYACTION = "destroy";
	
	
}
