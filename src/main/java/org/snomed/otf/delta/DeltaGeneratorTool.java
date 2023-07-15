package org.snomed.otf.delta;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Command line Java tool to extract an RF2 delta as the difference between two SNOMED-CT RF2 archive containing Full files
 */
public class DeltaGeneratorTool
{
	public static final String LATEST_STATES_FLAG = "--latest-state";
	public static final String FIELD_DELIMITER = "\t";
	public static final String LINE_DELIMITER = "\r\n";
	public static final int IDX_ID = 0;
	public static final int IDX_EFFECTIVE_TIME = 1;
	//Although MS Windows use backslashes in their file paths, the standard for zip archive states
	//that file separators should always be the backslash
	private static final String BWD_SLASH = "\\\\";
	private static final String FWD_SLASH = "/";

	private List<File> archives = new ArrayList<>();
	private int rowsExported = 0;
	private Path tempDir;
	private boolean outputLatestStates;
	private Map<String, Integer> latestComponentVersions = new HashMap<>();
	private Map<String, Set<Integer>> previouslySeenComponents = new HashMap<>();

	public static void main(String[] args) throws IOException {
		DeltaGeneratorTool app = new DeltaGeneratorTool();
		info("");
		info("");
		info("SNOMED International RF2 Delta Generator Tool mk ii");
		info("========================================================");
		info("");
		
		if (args.length < 2) {
			exit("Usage: java -jar DeltaGeneratorTool <old RF2 Archive Path> <new RF2 Archive Path>  [--latest-state]");
		}
		
		for (String arg : args) {
			if (arg.equals(LATEST_STATES_FLAG)) {
				app.outputLatestStates = true;
				info("Filtering for the latest state of each changed component.");
			} else {
				//It's necessary to have multiple old archives when we move from packaging
				//as an Extension, to being an Edition.  So don't assume there will only be two.
				app.archives.add(validateFile(arg));
			}
		}

		if (app.archives.size() < 2) {
			exit("Delta Generator Tool requires two archive packages to work - old and new");
		}

		app.tempDir = Files.createTempDirectory(app.getClass().getSimpleName());
		app.run();
	}

	private void run() throws IOException {
		long startTime = new Date().getTime();
		//sortArchives();
		int lastArchive = archives.size() - 1;
		for (int i=0 ; i <= lastArchive ; i++) {
			if (i != lastArchive) {
				processOldArchive(archives.get(i));
			} else {
				if (outputLatestStates) {
					info("First pass identifying latest state from " + archives.get(i));
					processNewArchive(archives.get(i), true);
					info("Latest versions collected for " + latestComponentVersions.size() + " components");
				}
				info("Extracting new rows from " + archives.get(i));
				processNewArchive(archives.get(i), false);
			}
		}
		
		recursiveDeleteOnExit(tempDir);
		
		info("Creating archive from " + tempDir);
		createArchive(tempDir.toFile());
		info("Processing Complete. Rows exported: " + rowsExported);
		long endTime = new Date().getTime();
		info("Time taken: " + ((endTime-startTime)/1000) + "s");
		try { Thread.sleep(1000); } catch (InterruptedException e) {}
	}

	private void processOldArchive(File archive) throws IOException {
		info("Processing previous archive: " + archive.getName());
		BufferedReader reader = null;
		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(archive))) {
			ZipEntry zipEntry = zis.getNextEntry();
			while (zipEntry != null) {
				if (!zipEntry.isDirectory()) {
					Path p = Paths.get(zipEntry.getName());
					if (p.getFileName().toString().contains("Full")) {
						info("\tProcessing " + p.getFileName());
						reader = new BufferedReader(new InputStreamReader(zis, StandardCharsets.UTF_8));// Leave open otherwise zip stream is closed.
						reader.readLine();  //Ditch the header
						String line;
						while ((line = reader.readLine()) != null) {
							String[] idET = split(line);
							//Have we seen this id before?
							Set<Integer> etsSeen = previouslySeenComponents.get(idET[IDX_ID]);
							if (etsSeen == null) {
								etsSeen = new HashSet<>();
								previouslySeenComponents.put(idET[IDX_ID], etsSeen);
							}
							etsSeen.add(Integer.parseInt(idET[IDX_EFFECTIVE_TIME]));
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
	}

	private String[] split(String line) {
		int firstTabIdx = line.indexOf(FIELD_DELIMITER);
		int secondTabIdx = line.indexOf(FIELD_DELIMITER, firstTabIdx + 1);
		return new String[] { line.substring(0, firstTabIdx) , 
							  line.substring(firstTabIdx + 1, secondTabIdx) };
	}

	/**
	 * Reads the Full files within the RF2 archive, rows within the required effectiveTime range are copied to the temp directory.
	 * If the latestStates flag is set then the latest effectiveTime of each component is collected and returned by the method.
	 */
	private void processNewArchive(File archive, boolean firstPass) throws IOException {
		BufferedReader reader = null;
		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(archive))) {
			ZipEntry zipEntry = zis.getNextEntry();
			while (zipEntry != null) {
				if (!zipEntry.isDirectory()) {
					Path p = Paths.get(zipEntry.getName());
					if (p.getFileName().toString().contains("Full")) {
						info("\t" + (firstPass?"First Pass ":"") + "Processing " + p.getFileName());
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
									String[] parts = split(line);
									String id = parts[IDX_ID];
									Integer effectiveDate = Integer.parseInt(parts[IDX_EFFECTIVE_TIME]);
								
									if (firstPass && outputLatestStates) {
										// Collect the latest effectiveTime for each component.
										Integer latestVersion = latestComponentVersions.get(id);
										if (latestVersion == null || effectiveDate > latestVersion) {
											latestComponentVersions.put(id, effectiveDate);
										}
									} else {
										//Is this a row that we have not seen in any of the old archives?
										Set<Integer> previouslySeenETs = previouslySeenComponents.get(id);
										
										//If we've not seen this at all, or not seen this effective time, consider outputting
										if (previouslySeenETs == null || !previouslySeenETs.contains(effectiveDate)) {
											if (!outputLatestStates || 
													(outputLatestStates && latestComponentVersions.get(parts[0]).toString().equals(effectiveDate))) {
												writer.write(line);
												writer.write(LINE_DELIMITER);
												rowsExported++;
											}
										}
									} 
								}
							}
							writer.flush();
							writer.close();
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
				info ("\tCreating " + file.getName());
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
			
			int fileNameModifier = 1;
			while (new File(zipFileName).exists()) {
				zipFileName = dirToZip.listFiles()[0].getName() + "_" + fileNameModifier++ + ".zip";
			}
			outputFile = new File(zipFileName);
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outputFile));
			String rootLocation = dirToZip.getAbsolutePath() + File.separator;
			info("Creating archive: " + zipFileName);
			addDir(rootLocation, dirToZip, out);
			out.close();
		} catch (IOException e) {
			throw new IOException("Failed to create archive from " + dirToZip, e);
		} 
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
