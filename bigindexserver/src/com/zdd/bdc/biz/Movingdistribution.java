package com.zdd.bdc.biz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.Vector;

import com.zdd.bdc.util.STATIC;

public class Movingdistribution extends Thread {

	private String ip = null;
	private String port = null;

	public Movingdistribution(String theip, String theport) {
		ip = theip;
		port = theport;
	}

	@Override
	public void run() {
		System.out.println(new Date() + " ==== started auto redistribution");
		while (!STATIC.REMOTE_CONFIGVAL_PENDING
				.equals(Configclient.getinstance(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIGFILE_PENDING)
						.read(ip + STATIC.SPLIT_IP_PORT + port))) {
			if (Files.exists(STATIC.LOCAL_DATAFOLDER) && Files.isDirectory(STATIC.LOCAL_DATAFOLDER)) {
				try {
					Path progressfolder = STATIC.LOCAL_DATAFOLDER.toAbsolutePath().getParent()
							.resolve("movingdistribution");
					if (!Files.exists(progressfolder)) {
						Files.createDirectories(progressfolder);
					}
					Files.walkFileTree(STATIC.LOCAL_DATAFOLDER, new FileVisitor<Object>() {

						@Override
						public FileVisitResult postVisitDirectory(Object arg0, IOException arg1) throws IOException {
							if (!STATIC.REMOTE_CONFIGVAL_PENDING.equals(
									Configclient.getinstance(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIGFILE_PENDING)
											.read(ip + STATIC.SPLIT_IP_PORT + port))) {
								return FileVisitResult.CONTINUE;
							} else {
								return FileVisitResult.TERMINATE;
							}

						}

						@Override
						public FileVisitResult preVisitDirectory(Object arg0, BasicFileAttributes arg1)
								throws IOException {
							if (!STATIC.REMOTE_CONFIGVAL_PENDING.equals(
									Configclient.getinstance(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIGFILE_PENDING)
											.read(ip + STATIC.SPLIT_IP_PORT + port))) {
								return FileVisitResult.CONTINUE;
							} else {
								return FileVisitResult.TERMINATE;
							}
						}

						@Override
						public FileVisitResult visitFile(Object file, BasicFileAttributes arg1) throws IOException {
							if (!STATIC.REMOTE_CONFIGVAL_PENDING.equals(
									Configclient.getinstance(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIGFILE_PENDING)
											.read(ip + STATIC.SPLIT_IP_PORT + port))) {
								Path pathfile = Paths.get(file.toString());
								if (!Files.isDirectory(pathfile)
										&& !pathfile.getFileName().toString().startsWith(".")) {
									Path progressfile = progressfolder
											.resolve(pathfile.toAbsolutePath().getParent().getParent().getParent()
													.getFileName().toString())
											.resolve(pathfile.toAbsolutePath().getParent().getParent().getFileName()
													.toString())
											.resolve(pathfile.toAbsolutePath().getParent().getFileName().toString())
											.resolve(pathfile.getFileName().toString());
									int numoflines = 0;
									BufferedReader br = null;
									try {
										String namespace = URLDecoder.decode(
												pathfile.getParent().getParent().getParent().getFileName().toString(),
												"UTF-8");
										String serverindex = URLDecoder.decode(
												pathfile.getParent().getParent().getFileName().toString(), "UTF-8");
										String[] filterspagenum = pathfile.getParent().getFileName().toString()
												.split(STATIC.SPLIT_A_B);
										Vector<String> filters = new Vector<String>(filterspagenum.length - 1);
										for (int i = 0; i < filterspagenum.length - 1; i++) {
											if (!filterspagenum[i].isEmpty()) {
												filters.add(URLDecoder.decode(filterspagenum[i], "UTF-8"));
											}
										}
										long pagenum = Long.parseLong(filterspagenum[filterspagenum.length - 1]);
										br = Files.newBufferedReader(pathfile, Charset.forName("UTF-8"));
										String line = br.readLine();
										numoflines++;
										if (line != null && !line.trim().isEmpty()) {
											String targetiport = Configclient
													.getinstance(namespace, STATIC.REMOTE_CONFIGFILE_BIGINDEX)
													.read(serverindex);
											if (targetiport == null
													|| targetiport.equals(ip + STATIC.SPLIT_IP_PORT + port)) {
												return FileVisitResult.CONTINUE;
											}
										}

										if (Files.exists(progressfile)) {
											int processedlines = Integer.parseInt(
													Files.readAllLines(progressfile, Charset.forName("UTF-8")).get(0));
											while (line != null && numoflines < processedlines) {
												line = br.readLine();
												numoflines++;
											}
										}

										if (line != null) {

											if (!Files.exists(progressfile.getParent())) {
												Files.createDirectories(progressfile.getParent());
											}

											while (line != null && !line.trim().isEmpty()) {
												String[] indexkey = line.split(STATIC.SPLIT_A_B);
												String index = URLDecoder.decode(indexkey[0], "UTF-8");
												String key = URLDecoder.decode(indexkey[1], "UTF-8");
												Indexclient ic = Indexclient.getinstance(namespace, index);
												if (!filters.isEmpty()) {
													ic = ic.filters(filters.size());
													for (String filter : filters) {
														ic.add(filter);
													}
												}
												if (pagenum == STATIC.PAGENUM_UNIQUEINDEX) {
													try {
														ic.createunique(key);
													} catch (Exception e) {
														if (!e.getMessage().contains("duplicate")) {
															throw e;
														}
													}
												} else {
													ic.create(key, pagenum);
												}
												line = br.readLine();
												numoflines++;
												Files.write(progressfile, String.valueOf(numoflines).getBytes("UTF-8"),
														StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
														StandardOpenOption.SYNC);
												
											}
										}
										Files.deleteIfExists(pathfile);
										Files.deleteIfExists(progressfile);
										try {
											Files.deleteIfExists(progressfile.getParent());
										} catch (Exception ex) {
											// do nothing
										}
										try {
											Files.deleteIfExists(progressfile.getParent().getParent());
										} catch (Exception ex) {
											// do nothing
										}
										try {
											Files.deleteIfExists(pathfile.getParent());
										} catch (Exception ex) {
											// do nothing
										}
										try {
											Files.deleteIfExists(pathfile.getParent().getParent());
											System.out
													.println(new Date() + " ==== redistributed [" + serverindex + "]");
										} catch (Exception ex) {
											// do nothing
										}
									} catch (Exception e) {
										StringWriter errors = new StringWriter();
										e.printStackTrace(new PrintWriter(errors));
										Files.write(progressfile,
												(numoflines + System.lineSeparator() + errors.toString())
														.getBytes("UTF-8"),
												StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
												StandardOpenOption.SYNC);
									} finally {
										if (br != null) {
											try {
												br.close();
											} catch (IOException ex) {
												// do nothing;
											}
										}
									}
								}
								return FileVisitResult.CONTINUE;
							} else {
								return FileVisitResult.TERMINATE;
							}
						}

						@Override
						public FileVisitResult visitFileFailed(Object arg0, IOException arg1) throws IOException {
							if (!STATIC.REMOTE_CONFIGVAL_PENDING.equals(
									Configclient.getinstance(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIGFILE_PENDING)
											.read(ip + STATIC.SPLIT_IP_PORT + port))) {
								return FileVisitResult.CONTINUE;
							} else {
								return FileVisitResult.TERMINATE;
							}
						}
					});
				} catch (Exception e) {
					//
				}
			}
			if (!STATIC.REMOTE_CONFIGVAL_PENDING
					.equals(Configclient.getinstance(STATIC.NAMESPACE_CORE, STATIC.REMOTE_CONFIGFILE_PENDING)
							.read(ip + STATIC.SPLIT_IP_PORT + port))) {
				try {
					Thread.sleep(120000);
				} catch (InterruptedException e1) {
					// do nothing;
				}
			}
		}
		System.out.println(new Date() + " ==== stopped auto redistribution");
	}

}
