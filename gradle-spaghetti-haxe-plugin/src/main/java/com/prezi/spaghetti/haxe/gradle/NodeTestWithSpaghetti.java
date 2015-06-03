package com.prezi.spaghetti.haxe.gradle;

import com.google.common.base.Throwables;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import sun.net.www.protocol.file.FileURLConnection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class NodeTestWithSpaghetti extends MUnitWithSpaghetti {

	@Override
	protected void prepareEnvironment(File workDir) throws IOException {
		copyCompiledTest(workDir);
		URL url = this.getClass().getResource("/munit_node_resources");
		copyResourcesRecursively(url, workDir);
	}

	public void copyResourcesRecursively(URL originUrl, File destination) throws IOException {
		URLConnection urlConnection = originUrl.openConnection();
		if (urlConnection instanceof JarURLConnection) {
			copyJarResourcesRecursively(destination, (JarURLConnection) urlConnection);
		} else if (urlConnection instanceof FileURLConnection) {
			FileUtils.copyDirectory(new File(originUrl.getPath()), destination);
		} else {
			throw new RuntimeException("URLConnection[" + urlConnection.getClass().getSimpleName() +
					"] is not a recognized/implemented connection type.");
		}
	}

	public void copyJarResourcesRecursively(File destination, JarURLConnection jarConnection ) throws IOException {
		JarFile jarFile = jarConnection.getJarFile();
		for (JarEntry entry : Collections.list(jarFile.entries())) {
			if (entry.getName().startsWith(jarConnection.getEntryName())) {
				String fileName = StringUtils.removeStart(entry.getName(), jarConnection.getEntryName());
				if (!entry.isDirectory()) {
					InputStream entryInputStream = null;
					try {
						entryInputStream = jarFile.getInputStream(entry);
						IOUtils.copy(entryInputStream, new FileOutputStream(new File(destination, fileName)));
					} finally {
						IOUtils.closeQuietly(entryInputStream);
					}
				} else {
					FileUtils.forceMkdir(new File(destination, fileName));
				}
			}
		}
	}
}
