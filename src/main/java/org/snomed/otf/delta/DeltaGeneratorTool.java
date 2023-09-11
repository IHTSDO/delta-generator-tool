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
	public static final String LATEST_STATE_FLAG = "--latest-state";
	public static final String FIELD_DELIMITER = "\t";
	public static final String LINE_DELIMITER = "\r\n";
	public static final int IDX_ID = 0;
	public static final int IDX_EFFECTIVE_TIME = 1;
	public static final int NOT_SET = -1;
	//Although MS Windows use backslashes in their file paths, the standard for zip archive states
	//that file separators should always be the backslash
	private static final String BWD_SLASH = "\\\\";
	private static final String FWD_SLASH = "/";

	private final List<File> archives = new ArrayList<>();
	private int rowsExported = 0;
	private int latestComponentVersionsCollected = 0;
	private Path tempDir;
	private boolean outputLatestStates;
	
	private final Map<TinyUUID, ComponentData> componentDataMap = new HashMap<>();
	
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
			if (arg.equals(LATEST_STATE_FLAG)) {
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
				info("Previously seen effective times collected for " + componentDataMap.size() + " components");
				System.gc();
			} else {
				if (outputLatestStates) {
					info("First pass identifying latest state from " + archives.get(i));
					processNewArchive(archives.get(i), true);
					info("Latest versions collected for " + latestComponentVersionsCollected + " components");
					System.gc();
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
							TinyUUID idAsUUID = new TinyUUID(idET[IDX_ID]);
							ComponentData componentData = componentDataMap.get(idAsUUID);
							if (componentData == null) {
								componentData = new ComponentData();
								componentDataMap.put(idAsUUID, componentData);
							}
							componentData.addPreviouslySeenEffectiveTime(Integer.parseInt(idET[IDX_EFFECTIVE_TIME]));
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
						info("\t" + (firstPass?"First pass p":"P") + "rocessing " + p.getFileName());
						boolean isHeader = true;
						File outFile;
						BufferedWriter writer= null;
						if (!firstPass) {
							outFile = ensureFileExists(tempDir + File.separator + p.getParent() + File.separator + p.getFileName());
							writer = new BufferedWriter(new FileWriter(outFile, StandardCharsets.UTF_8));
						}
						reader = new BufferedReader(new InputStreamReader(zis, StandardCharsets.UTF_8));// Leave open otherwise zip stream is closed.
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
								TinyUUID idAsUUID = new TinyUUID(parts[IDX_ID]);
								int effectiveDate = Integer.parseInt(parts[IDX_EFFECTIVE_TIME]);
								
								ComponentData componentData = componentDataMap.get(idAsUUID);
								if (componentData == null) {
									componentData = new ComponentData();
									componentDataMap.put(idAsUUID, componentData);
								}
								
								if (firstPass && outputLatestStates) {
									if (componentData.latestComponentVersion == NOT_SET) {
										latestComponentVersionsCollected++;
									}
									// Collect the latest effectiveTime for each component.
									if (effectiveDate > componentData.latestComponentVersion) {
										componentData.latestComponentVersion = effectiveDate;
									}
								} else {
									//If we've not seen this at all, or not seen this effective time, consider outputting
									if (!componentData.hasPreviouslySeenEffectiveTime(effectiveDate)) {
										if (!outputLatestStates || 
												(outputLatestStates && componentData.latestComponentVersion == effectiveDate)) {
                                            assert writer != null;
                                            writer.write(line);
											writer.write(LINE_DELIMITER);
											rowsExported++;
										}
									}
								} 
							}
						}
						
						if (writer != null) {
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
			String zipFileName = Objects.requireNonNull(dirToZip.listFiles())[0].getName() + ".zip";
			int fileNameModifier = 1;
			while (new File(zipFileName).exists()) {
				zipFileName = Objects.requireNonNull(dirToZip.listFiles())[0].getName() + "_" + fileNameModifier++ + ".zip";
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
		Files.walkFileTree(path, new SimpleFileVisitor<>() {
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
	
	private static class ComponentData {
		public int latestComponentVersion = NOT_SET;
		private int[] previouslySeenEffectiveTimes;
		
		public boolean hasPreviouslySeenEffectiveTime(int et) {
			if (previouslySeenEffectiveTimes == null) {
				return false;
			}
            for (int previouslySeenEffectiveTime : previouslySeenEffectiveTimes) {
                if (et == previouslySeenEffectiveTime) {
                    return true;
                }
            }
			return false;
		}
		
		public void addPreviouslySeenEffectiveTime(int et) {
			if (previouslySeenEffectiveTimes == null) {
				previouslySeenEffectiveTimes = new int[]{et};
			} else if (!hasPreviouslySeenEffectiveTime(et)) {
				int newLength = previouslySeenEffectiveTimes.length + 1;
				previouslySeenEffectiveTimes = Arrays.copyOf(previouslySeenEffectiveTimes, newLength);
				previouslySeenEffectiveTimes[newLength -1] = et;
			}
		}
	}
	
	private static class TinyUUID {
		long leastSig;
		long mostSig;
		int hashCode;
		
		TinyUUID(String id) {
			this.hashCode = id.hashCode();
			if (id.contains("-")) {
				UUID uuid = UUID.fromString(id);
				this.leastSig = uuid.getLeastSignificantBits();
				this.mostSig = uuid.getMostSignificantBits();
			} else {
				mostSig = Long.parseLong(id);
			}
		}
		
		@Override
		public int hashCode() {
			return hashCode;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TinyUUID other = (TinyUUID)obj;
			return (this.leastSig == other.leastSig) && (this.mostSig == other.mostSig);
		}
	}
}
