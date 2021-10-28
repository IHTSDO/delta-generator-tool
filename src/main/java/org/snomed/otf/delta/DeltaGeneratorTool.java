package org.snomed.otf.delta;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.zip.*;

/**
 * Command line Java tool to extract an RF2 delta from after particular date, from a archive containing SNOMED Full files
 */
public class DeltaGeneratorTool
{
	public static String FIELD_DELIMITER = "\t";
	public static String LINE_DELIMITER = "\r\n";
	public static int IDX_EFFECTIVE_TIME = 1;
	private static DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	private String previousEffectiveDate = null;
	private String latestEffectiveDate = null;
	private String maxAllowableEffectiveDate = null;
	private File archive;
	private int rowsExported = 0;
	private Path tempDir;
	
	public static void main(String[] args) throws IOException {
		DeltaGeneratorTool app = new DeltaGeneratorTool();
		info("SNOMED International RF2 Delta Generator Tool");
		info("=============================================");
		info("");
		
		if (args.length < 2) {
			exit("Usage: java -jar DeltaGeneratorTool <previousEffectiveTime> <RF2 Archive Path> [<maximumEffectiveTime>]");
		}
		
		try {
			dtFormatter.parse(args[0]);
			app.previousEffectiveDate = args[0];
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid previous effective time (yyyyMMdd): " + args[0]);
		}
		app.archive = validateFile(args[1]);
		
		if (args.length == 3) {
			try {
				dtFormatter.parse(args[2]);
				app.maxAllowableEffectiveDate = args[2];
				info("Set optional latest effective date to be included: " + app.maxAllowableEffectiveDate);
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
		ZipInputStream zis = new ZipInputStream(new FileInputStream(archive));
		ZipEntry ze = zis.getNextEntry();
		BufferedReader br = null;
		try {
			while (ze != null) {
				if (!ze.isDirectory()) {
					Path p = Paths.get(ze.getName());
					if (p.getFileName().toString().contains("Full")) {
						boolean isHeader = true;
						File outFile = ensureFileExists(tempDir + File.separator + p.getParent() + File.separator + p.getFileName());
						br = new BufferedReader(new InputStreamReader(zis, StandardCharsets.UTF_8));
						try (FileWriter fw = new FileWriter(outFile, StandardCharsets.UTF_8);
								BufferedWriter bw = new BufferedWriter(fw)) {
							String line = null;
							while ((line = br.readLine()) != null) {
								if (isHeader) {
									bw.write(line);
									bw.write(LINE_DELIMITER);
									rowsExported++;
									isHeader = false;
								} else {
									String effectiveDate = line.split(FIELD_DELIMITER)[IDX_EFFECTIVE_TIME];
									if (latestEffectiveDate == null || effectiveDate.compareTo(latestEffectiveDate) > 0) {
										latestEffectiveDate = effectiveDate;
									}
									//Is the effective date within our allowed date range?
									if (effectiveDate.compareTo(previousEffectiveDate) > 0 && 
											(maxAllowableEffectiveDate == null || effectiveDate.compareTo(maxAllowableEffectiveDate) <= 0)) {
										bw.write(line);
										bw.write(LINE_DELIMITER);
										rowsExported++;
									}
								}
							}
							bw.flush();
						} 
					}
				}
				ze = zis.getNextEntry();
			}
		} finally {
			try{
				zis.closeEntry();
				zis.close();
				br.close();
			} catch (Exception e){} //Well, we tried.
		}
		info("Latest effective date detected in files: " + latestEffectiveDate);
		recursiveDeleteOnExit(tempDir);
		createArchive(tempDir.toFile());
		info("Processing Complete. Rows exported: " + rowsExported);
		long endTime = new Date().getTime();
		info("Time taken: " + ((endTime-startTime)/1000) + "s");
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
				info ("Writing " + file);
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

		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				addDir(rootLocation, files[i], out);
				continue;
			}
			FileInputStream in = new FileInputStream(files[i].getAbsolutePath());
			String relativePath = files[i].getAbsolutePath().substring(rootLocation.length());
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
