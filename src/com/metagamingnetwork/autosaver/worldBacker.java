package com.metagamingnetwork.autosaver;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.*;


public class worldBacker {
    private String BACKUPS = "BACKUPS";
    final int BUFFER = 2048;
    
    public worldBacker(){
    	//CHECK IF BACKUP FOLDER EXISTS IF SO DO NOTHING IF NOT CREATE~
    	File dir = new File(BACKUPS);
    	if(!dir.exists()){
    		try{
    			dir.mkdir();
    		} catch (SecurityException se){
    		}
    	}
    	List<File> fileList = new ArrayList<File>();
    	File directoryToZip = new File(".");
    	getAllFiles(directoryToZip, fileList);
    	writeZipFile(directoryToZip, fileList);
    }
    
	public worldBacker(autosaver plugin){
		//CHECK IF BACKUP FOLDER EXISTS IF SO DO NOTHING IF NOT CREATE~
		File dir = new File(BACKUPS);
		if(!dir.exists()){
			try{
				dir.mkdir();
				plugin.getLogger().info("Creating Backup Directory: " + BACKUPS);
			} catch (SecurityException se){
				plugin.getLogger().info("Could not create backup directory!");
			}
		}
		List<File> fileList = new ArrayList<File>();
		File directoryToZip = new File(".");
		getAllFiles(directoryToZip, fileList);
		writeZipFile(directoryToZip, fileList);
	}
	public static void getAllFiles(File dir, List<File> fileList) {
			File[] files = dir.listFiles();
			for (File file : files) {
				fileList.add(file);
				if (file.isDirectory() && !file.getName().equalsIgnoreCase("backups")) {
					//System.out.println("directory:" + file.getCanonicalPath());
					getAllFiles(file, fileList);
				} else {
					//System.out.println("     file:" + file.getCanonicalPath());
				}
			}
	}

	public void writeZipFile(File directoryToZip, List<File> fileList) {

		try {
			FileOutputStream fos = new FileOutputStream("./"+BACKUPS+"\\MC-BACKUP["+ new SimpleDateFormat("yyyyMMdd-hhmm").format(new Date())+"].zip");
			ZipOutputStream zos = new ZipOutputStream(fos);

			for (File file : fileList) {
				if (!file.isDirectory()) { // we only zip files, not directories
					addToZip(directoryToZip, file, zos);
				}
			}

			zos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void addToZip(File directoryToZip, File file, ZipOutputStream zos) throws FileNotFoundException,
			IOException {

		FileInputStream fis = new FileInputStream(file);

		// we want the zipEntry's path to be a relative path that is relative
		// to the directory being zipped, so chop off the rest of the path
		String zipFilePath = file.getCanonicalPath().substring(directoryToZip.getCanonicalPath().length() + 1,
				file.getCanonicalPath().length());
		ZipEntry zipEntry = new ZipEntry(zipFilePath);
		zos.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}

		zos.closeEntry();
		fis.close();
	}
}
