package com.prezi.spaghetti.haxe.gradle;

import java.io.File;

public interface MUnitTask {
	public File getTestApplication();

	public void setTestApplication(Object testApplication);

	public void testApplication(Object testApplication);

	public String getTestApplicationName();

	public void setTestApplicationName(String testApplicationName);

	public void testApplicationName(String testApplicationName);
}
