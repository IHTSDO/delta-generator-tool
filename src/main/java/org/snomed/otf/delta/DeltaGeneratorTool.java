package org.snomed.otf.delta;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Command line Java tool to extract an RF2 delta from after particular date, from an RF2 archive containing SNOMED Full files
 */
public class DeltaGeneratorTool
{
	public static final String LATEST_STATES_FLAG = "-latest-state";
	public static final String FIELD_DELIMITER = "\t";
	public static final String LINE_DELIMITER = "\r\n";
	public static final int IDX_EFFECTIVE_TIME = 1;
	//Although MS Windows use backslashes in their file paths, the standard for zip archive states
	//that file separators should always be the backslash
	private static final String BWD_SLASH = "\\\\";
	private static final String FWD_SLASH = "/";
	
	private static final DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	
	private String previousEffectiveDate = null;
	private String latestEffectiveDate = null;
	private String maxAllowableEffectiveDate = null;
	private File archive;
	private int rowsExported = 0;
	private Path tempDir;
	
	//Only used if -latest-state cmd line arg present
	private boolean outputLatestStates;
	private Map<String, Integer> latestComponentVersions = new HashMap<>();

	public static void main(String[] args) throws IOException {
		DeltaGeneratorTool app = new DeltaGeneratorTool();
		info("SNOMED International RF2 Delta Generator Tool");
		info("=============================================");
		info("");
		
		if (args.length < 2) {
			exit("Usage: java -jar DeltaGeneratorTool <previousEffectiveTime> <RF2 Archive Path> [<maximumEffectiveTime>] [-latest-state]");
		}
		
		try {
			dtFormatter.parse(args[0]);
			app.previousEffectiveDate = args[0];
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid previous effective time (yyyyMMdd): " + args[0]);
		}
		app.archive = validateFile(args[1]);
		
		if (args.length == 3 || args.length == 4) {
			try {
				if (args[2].equals(LATEST_STATES_FLAG) || (args.length == 4 && args[3].equals(LATEST_STATES_FLAG))) {
					app.outputLatestStates = true;
					info("Filtering for the latest state of each changed component.");
				}
				String maxEffectiveTime = null;
				if (args[2].matches("\\d+")) {
					maxEffectiveTime = args[2];
				} else if (args.length == 4 && args[3].matches("\\d+")) {
					maxEffectiveTime = args[3];
				}
				if (maxEffectiveTime != null) {
					dtFormatter.parse(maxEffectiveTime);
					app.maxAllowableEffectiveDate = maxEffectiveTime;
					info("Set optional latest effective date to be included: " + app.maxAllowableEffectiveDate);
				}
			} catch (Exception e) {
				throw new IllegalArgumentException("Invalid maximum effective time (yyyyMMdd): " + args[1]);
			}
		}
		app.tempDir = Files.createTempDirectory(app.getClass().getSimpleName());
		app.run();
	}

	private void run() throws IOException {
		info ("Processing " + archive.getName());
		long startTime = new Date().getTime();
		
		info("First Pass - gathering information...");
		processArchive(archive, true);
		
		if (outputLatestStates) {
			info("Latest versions collected for " + latestComponentVersions.size() + " components");
		}
		
		info("Extracting rows");
		processArchive(archive, false);
		
		info("Latest effective date detected in files: " + latestEffectiveDate);
		recursiveDeleteOnExit(tempDir);
		
		info("Creating archive");
		createArchive(tempDir.toFile());
		
		info("Processing Complete. Rows exported: " + rowsExported);
		long endTime = new Date().getTime();
		info("Time taken: " + ((endTime-startTime)/1000) + "s");
	}

	/**
	 * Reads the Full files within the RF2 archive, rows within the required effectiveTime range are copied to the temp directory.
	 * If the latestStates flag is set then the latest effectiveTime of each component is collected and returned by the method.
	 */
	private Map<String, Integer> processArchive(File archive, boolean firstPass) throws IOException {
		BufferedReader reader = null;
		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(archive))) {
			ZipEntry zipEntry = zis.getNextEntry();
			while (zipEntry != null) {
				if (!zipEntry.isDirectory()) {
					Path p = Paths.get(zipEntry.getName());
					if (p.getFileName().toString().contains("Full")) {
						boolean isHeader = true;
						File outFile = ensureFileExists(tempDir + File.separator + p.getParent() + File.separator + p.getFileName());
						reader = new BufferedReader(new InputStreamReader(zis, StandardCharsets.UTF_8));// Leave open otherwise zip stream is closed.
						try (BufferedWriter writer = new BufferedWriter(new FileWriter(outFile, StandardCharsets.UTF_8))) {
							String line;
							while ((line = reader.readLine()) != null) {
								if (isHeader) {
									if (!firstPass) {
										writer.write(line);
										writer.write(LINE_DELIMITER);
									}
									isHeader = false;
								} else {
									String[] parts = line.split(FIELD_DELIMITER);
									String effectiveDate = parts[IDX_EFFECTIVE_TIME];
									if (latestEffectiveDate == null || effectiveDate.compareTo(latestEffectiveDate) > 0) {
										latestEffectiveDate = effectiveDate;
									}
									//Is the effective date within our allowed date range?
									if (effectiveDate.compareTo(previousEffectiveDate) > 0 &&
											(maxAllowableEffectiveDate == null || effectiveDate.compareTo(maxAllowableEffectiveDate) <= 0)) {

										if (firstPass && outputLatestStates) {
											// Collect the latest effectiveTime for each component.
											String id = parts[0];
											Integer thisVersion = Integer.parseInt(effectiveDate);
											Integer latestVersion = latestComponentVersions.get(id);
											if (latestVersion == null || thisVersion > latestVersion) {
												latestComponentVersions.put(id, thisVersion);
											}
										} else if (latestComponentVersions == null || latestComponentVersions.get(parts[0]).toString().equals(effectiveDate)) {
												writer.write(line);
												writer.write(LINE_DELIMITER);
												rowsExported++;
										}
									}
								}
							}
						}
					}
				}
				zipEntry = zis.getNextEntry();
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return latestComponentVersions;
	}

	private static File validateFile(String filePath) throws IOException {
		File f = new File(filePath);
		if (!f.canRead() || f.isDirectory() || !filePath.endsWith(".zip")) {
			throw new IOException ("'" + filePath + "' could not be read as an archive file.");
		}
		return f;
	}

	public static File ensureFileExists(String fileName) throws IOException {
		File file = new File(fileName.replaceAll("Full", "Delta"));
		try {
			if (!file.exists()) {
				if (file.getParentFile() != null) {
					file.getParentFile().mkdirs();
				}
				file.createNewFile();
				info ("Creating " + file);
			}
		} catch (IOException e) {
			throw new IOException ("Failed to create file " + fileName, e);
		}
		return file;
	}

	public static void info(String msg) {
		System.out.println(msg);
	}
	
	private static void exit(String msg) {
		System.err.println(msg);
		System.exit(-1);
	}
	
	public void createArchive(File dirToZip) throws IOException {
		File outputFile;
		try {
			// The zip filename will be the name of the first thing in the zip location
			String zipFileName = dirToZip.listFiles()[0].getName() + ".zip";
			
			if (maxAllowableEffectiveDate != null) {
				zipFileName = zipFileName.replace(latestEffectiveDate, maxAllowableEffectiveDate);
			}
			int fileNameModifier = 1;
			while (new File(zipFileName).exists()) {
				zipFileName = dirToZip.listFiles()[0].getName() + "_" + fileNameModifier++ + ".zip";
			}
			outputFile = new File(zipFileName);
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outputFile));
			String rootLocation = dirToZip.getAbsolutePath() + File.separator;
			info("Creating archive : " + zipFileName + " from files found in " + rootLocation);
			addDir(rootLocation, dirToZip, out);
			out.close();
		} catch (IOException e) {
			throw new IOException("Failed to create archive from " + dirToZip, e);
		} 
		info("Created archive: " + outputFile);
	}
	
	public void addDir(String rootLocation, File dirObj, ZipOutputStream out) throws IOException {
		File[] files = dirObj.listFiles();
		byte[] tmpBuf = new byte[1024];

		for (File file : files) {
			if (file.isDirectory()) {
				addDir(rootLocation, file, out);
				continue;
			}
			FileInputStream in = new FileInputStream(file.getAbsolutePath());
			String relativePath = file.getAbsolutePath().substring(rootLocation.length()).replaceAll(BWD_SLASH, FWD_SLASH);
			if (maxAllowableEffectiveDate != null) {
				relativePath = relativePath.replaceAll(latestEffectiveDate, maxAllowableEffectiveDate);
			}
			out.putNextEntry(new ZipEntry(relativePath));
			int len;
			while ((len = in.read(tmpBuf)) > 0) {
				out.write(tmpBuf, 0, len);
			}
			out.closeEntry();
			in.close();
		}
	}
	
	//See https://stackoverflow.com/questions/15022219/does-files-createtempdirectory-remove-the-directory-after-jvm-exits-normally/20280989
	public static void recursiveDeleteOnExit(Path path) throws IOException {
		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
				file.toFile().deleteOnExit();
				return FileVisitResult.CONTINUE;
			}
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
				dir.toFile().deleteOnExit();
				return FileVisitResult.CONTINUE;
			}
		});
	}
}
