/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModulClassUtil {
	/**
	 * Gets the root folder of a module's sources during development
	 * 
	 * @param moduleId the module id
	 * @return the module's development folder is specified, else null
	 */
	private static final Logger log = LoggerFactory.getLogger(ModulClassUtil.class);
	public static File getDevelopmentDirectory(String moduleId) {
		String directory = System.getProperty(moduleId + ".development.directory");
		if (StringUtils.isNotBlank(directory)) {
			return new File(directory);
		}
		
		return null;
	}

	/**
	 * This loops over all FILES in this jar to get the package names. If there is an empty
	 * directory in this jar it is not returned as a providedPackage.
	 *
	 * @param file jar file to look into
	 * @return list of strings of package names in this jar
	 */
	public static Collection<String> getPackagesFromFile(File file) {
		
		// End early if we're given a non jar file
		if (!file.getName().endsWith(".jar")) {
			return Collections.emptySet();
		}
		
		Set<String> packagesProvided = new HashSet<>();
		
		JarFile jar = null;
		try {
			jar = new JarFile(file);
			
			Enumeration<JarEntry> jarEntries = jar.entries();
			while (jarEntries.hasMoreElements()) {
				JarEntry jarEntry = jarEntries.nextElement();
				if (jarEntry.isDirectory()) {
					// skip over directory entries, we only care about files
					continue;
				}
				String name = jarEntry.getName();
				
				// Skip over some folders in the jar/omod
				if (name.startsWith("lib") || name.startsWith("META-INF") || name.startsWith("web/module")) {
					continue;
				}
				
				Integer indexOfLastSlash = name.lastIndexOf("/");
				if (indexOfLastSlash <= 0) {
					continue;
				}
				String packageName = name.substring(0, indexOfLastSlash);
				
				packageName = packageName.replaceAll("/", ".");
				
				if (packagesProvided.add(packageName) && log.isTraceEnabled()) {
					log.trace("Adding module's jarentry with package: " + packageName);
				}
			}
			
			jar.close();
		}
		catch (IOException e) {
			log.error("Error while reading file: " + file.getAbsolutePath(), e);
		}
		finally {
			if (jar != null) {
				try {
					jar.close();
				}
				catch (IOException e) {
					// Ignore quietly
				}
			}
		}
		
		return packagesProvided;
	}

	/**
	 * Utility method to convert a {@link File} object to a local URL.
	 *
	 * @param file a file object
	 * @return absolute URL that points to the given file
	 * @throws MalformedURLException if file can't be represented as URL for some reason
	 */
	public static URL file2url(final File file) throws MalformedURLException {
		if (file == null) {
			return null;
		}
		try {
			return file.getCanonicalFile().toURI().toURL();
		}
		catch (MalformedURLException mue) {
			throw mue;
		}
		catch (IOException | NoSuchMethodError ioe) {
			throw new MalformedURLException("Cannot convert: " + file.getName() + " to url");
		}
	}

	/**
	 * Uses the runtime properties to determine if the core modules should be enforced or not.
	 *
	 * @return true if the core modules list can be ignored.
	 */
	public static boolean ignoreCoreModules() {
		String ignoreCoreModules = Context.getRuntimeProperties().getProperty(ModuleConstants.IGNORE_CORE_MODULES_PROPERTY,
		    "false");
		return Boolean.parseBoolean(ignoreCoreModules);
	}


	public static boolean matchRequiredVersions(String version, String versionRange) {
		// There is a null check so no risk in keeping the literal on the right side
		if (StringUtils.isNotEmpty(versionRange)) {
			String[] ranges = versionRange.split(",");
			for (String range : ranges) {
				// need to externalize this string
				String separator = "-";
				if (range.indexOf("*") > 0 || range.indexOf(separator) > 0 && (!isVersionWithQualifier(range))) {
					// if it contains "*" or "-" then we must separate those two
					// assume it's always going to be two part
					// assign the upper and lower bound
					// if there's no "-" to split lower and upper bound
					// then assign the same value for the lower and upper
					String lowerBound = range;
					String upperBound = range;
					
					int indexOfSeparator = range.indexOf(separator);
					while (indexOfSeparator > 0) {
						lowerBound = range.substring(0, indexOfSeparator);
						upperBound = range.substring(indexOfSeparator + 1);
						if (upperBound.matches("^\\s?\\d+.*")) {
							break;
						}
						indexOfSeparator = range.indexOf(separator, indexOfSeparator + 1);
					}
					
					// only preserve part of the string that match the following format:
					// - xx.yy.*
					// - xx.yy.zz*
					lowerBound = StringUtils.remove(lowerBound, lowerBound.replaceAll("^\\s?\\d+[\\.\\d+\\*?|\\.\\*]+", ""));
					upperBound = StringUtils.remove(upperBound, upperBound.replaceAll("^\\s?\\d+[\\.\\d+\\*?|\\.\\*]+", ""));
					
					// if the lower contains "*" then change it to zero
					if (lowerBound.indexOf("*") > 0) {
						lowerBound = lowerBound.replaceAll("\\*", "0");
					}
					
					// if the upper contains "*" then change it to maxRevisionNumber
					if (upperBound.indexOf("*") > 0) {
						upperBound = upperBound.replaceAll("\\*", Integer.toString(Integer.MAX_VALUE));
					}
					
					int lowerReturn = compareVersion(version, lowerBound);
					
					int upperReturn = compareVersion(version, upperBound);
					
					if (lowerReturn < 0 || upperReturn > 0) {
						log.debug("Version " + version + " is not between " + lowerBound + " and " + upperBound);
					} else {
						return true;
					}
				} else {
					if (compareVersion(version, range) < 0) {
						log.debug("Version " + version + " is below " + range);
					} else {
						return true;
					}
				}
			}
		}
		else {
			//no version checking if required version is not specified
			return true;
		}
		
		return false;
	}

	/**
	 * Expand the given <code>fileToExpand</code> jar to the <code>tmpModuleFile</code> directory
	 *
	 * If <code>name</code> is null, the entire jar is expanded. If<code>name</code> is not null,
	 * then only that path/file is expanded.
	 *
	 * @param fileToExpand file pointing at a .jar
	 * @param tmpModuleDir directory in which to place the files
	 * @param name filename inside of the jar to look for and expand
	 * @param keepFullPath if true, will recreate entire directory structure in tmpModuleDir
	 *            relating to <code>name</code>. if false will start directory structure at
	 *            <code>name</code>
	 * <strong>Should</strong> expand entire jar if name is null
	 * <strong>Should</strong> expand entire jar if name is empty string
	 * <strong>Should</strong> expand directory with parent tree if name is directory and keepFullPath is true
	 * <strong>Should</strong> expand directory without parent tree if name is directory and keepFullPath is false
	 * <strong>Should</strong> expand file with parent tree if name is file and keepFullPath is true
	 */
	public static void expandJar(File fileToExpand, File tmpModuleDir, String name, boolean keepFullPath) throws IOException {
		JarFile jarFile = null;
		InputStream input = null;
		String docBase = tmpModuleDir.getAbsolutePath();
		try {
			jarFile = new JarFile(fileToExpand);
			Enumeration<JarEntry> jarEntries = jarFile.entries();
			boolean foundName = (name == null);
			
			// loop over all of the elements looking for the match to 'name'
			while (jarEntries.hasMoreElements()) {
				JarEntry jarEntry = jarEntries.nextElement();
				if (name == null || jarEntry.getName().startsWith(name)) {
					String entryName = jarEntry.getName();
					// trim out the name path from the name of the new file
					if (!keepFullPath && name != null) {
						entryName = entryName.replaceFirst(name, "");
					}
					
					// if it has a slash, it's in a directory
					int last = entryName.lastIndexOf('/');
					if (last >= 0) {
						File parent = new File(docBase, entryName.substring(0, last));
						parent.mkdirs();
						log.debug("Creating parent dirs: " + parent.getAbsolutePath());
					}
					// we don't want to "expand" directories or empty names
					if (entryName.endsWith("/") || "".equals(entryName)) {
						continue;
					}
					input = jarFile.getInputStream(jarEntry);
					expand(input, docBase, entryName);
					input.close();
					input = null;
					foundName = true;
				}
			}
			if (!foundName) {
			log.debug("Unable to find: " + name + " in file " + fileToExpand.getAbsolutePath());
			}
			
		}
		catch (IOException e) {
			log.warn("Unable to delete tmpModuleFile on error", e);
			throw e;
		}
		finally {
			try {
				input.close();
			}
			catch (Exception e) { /* pass */}
			try {
				jarFile.close();
			}
			catch (Exception e) { /* pass */}
		}
	}

	/**
	 * Expand the given file in the given stream to a location (fileDir/name) The <code>input</code>
	 * InputStream is not closed in this method
	 *
	 * @param input stream to read from
	 * @param fileDir directory to copy to
	 * @param name file/directory within the <code>fileDir</code> to which we expand
	 *            <code>input</code>
	 * @return File the file created by the expansion.
	 * @throws IOException if an error occurred while copying
	 */
	private static File expand(InputStream input, String fileDir, String name) throws IOException {
		log.debug("expanding: {}", name);
		
		File file = new File(fileDir, name);
		FileOutputStream outStream = null;
		try {
			outStream = new FileOutputStream(file);
			OpenmrsUtil.copyFile(input, outStream);
		}
		finally {
			try {
				outStream.close();
			}
			catch (Exception e) { /* pass */}
		}
		
		return file;
	}

	/**
	 * Checks for qualifier version (i.e "-SNAPSHOT", "-ALPHA" etc. after maven version conventions)
	 *
	 * @param version String like 1.9.2-SNAPSHOT
	 * @return true if version contains qualifier
	 */
	public static boolean isVersionWithQualifier(String version) {
		Matcher matcher = Pattern.compile("(\\d+)\\.(\\d+)(\\.(\\d+))?(\\-([A-Za-z]+))").matcher(version);
		return matcher.matches();
	}

	/**
	 * Compares <code>version</code> to <code>value</code> version and value are strings like
	 * 1.9.2.0 Returns <code>0</code> if either <code>version</code> or <code>value</code> is null.
	 *
	 * @param version String like 1.9.2.0
	 * @param value String like 1.9.2.0
	 * @return the value <code>0</code> if <code>version</code> is equal to the argument
	 *         <code>value</code>; a value less than <code>0</code> if <code>version</code> is
	 *         numerically less than the argument <code>value</code>; and a value greater than
	 *         <code>0</code> if <code>version</code> is numerically greater than the argument
	 *         <code>value</code>
	 * <strong>Should</strong> correctly comparing two version numbers
	 * <strong>Should</strong> treat SNAPSHOT as earliest version
	 */
	public static int compareVersion(String version, String value) {
		try {
			if (version == null || value == null) {
				return 0;
			}
			
			List<String> versions = new ArrayList<>();
			List<String> values = new ArrayList<>();
			String separator = "-";
			
			// strip off any qualifier e.g. "-SNAPSHOT"
			int qualifierIndex = version.indexOf(separator);
			if (qualifierIndex != -1) {
				version = version.substring(0, qualifierIndex);
			}
			
			qualifierIndex = value.indexOf(separator);
			if (qualifierIndex != -1) {
				value = value.substring(0, qualifierIndex);
			}
			
			Collections.addAll(versions, version.split("\\."));
			Collections.addAll(values, value.split("\\."));
			
			// match the sizes of the lists
			while (versions.size() < values.size()) {
				versions.add("0");
			}
			while (values.size() < versions.size()) {
				values.add("0");
			}
			
			for (int x = 0; x < versions.size(); x++) {
				String verNum = versions.get(x).trim();
				String valNum = values.get(x).trim();
				Long ver = NumberUtils.toLong(verNum, 0);
				Long val = NumberUtils.toLong(valNum, 0);
				
				int ret = ver.compareTo(val);
				if (ret != 0) {
					return ret;
				}
			}
		}
		catch (NumberFormatException e) {
			log.error("Error while converting a version/value to an integer: " + version + "/" + value, e);
		}
		
		// default return value if an error occurs or elements are equal
		return 0;
	}
}
