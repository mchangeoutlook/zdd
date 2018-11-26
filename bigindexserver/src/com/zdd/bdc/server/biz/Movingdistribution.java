package com.zdd.bdc.server.biz;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

import com.zdd.bdc.client.biz.Configclient;
import com.zdd.bdc.client.biz.Indexclient;
import com.zdd.bdc.client.util.STATIC;
import com.zdd.bdc.server.util.Filedatawalk;
import com.zdd.bdc.server.util.Filedatawalkresult;
import com.zdd.bdc.server.util.Filekvutil;
import com.zdd.bdc.server.util.Fileutil;

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
		while (Configclient.running) {

			if (Files.exists(STATIC.LOCAL_DATAFOLDER) && Files.isDirectory(STATIC.LOCAL_DATAFOLDER)) {

				String[] namespaces = STATIC.LOCAL_DATAFOLDER.toFile().list();
				for (String namespace : namespaces) {
					if (!Configclient.running) {
						break;
					} else {
						if (Files.isDirectory(STATIC.LOCAL_DATAFOLDER.resolve(namespace)) && !namespace.startsWith(".")) {
							String[] serverindexes = STATIC.LOCAL_DATAFOLDER.resolve(namespace).toFile().list();
							for (String serverindex : serverindexes) {
								if (!Configclient.running) {
									break;
								} else {
									if (Files.isDirectory(STATIC.LOCAL_DATAFOLDER.resolve(namespace).resolve(serverindex))
											&& !serverindex.startsWith(".")) {
										String targetiport = Configclient
												.getinstance(namespace, STATIC.REMOTE_CONFIG_BIGINDEX).read(serverindex);
										if (targetiport == null || targetiport.equals(STATIC.splitiport(ip, port))) {
											// do nothing
										} else {
											try {
												walkfolder(namespace, serverindex);
											} catch (Exception e) {
												System.out.println(new Date() + " ==== error in distributing ["
														+ STATIC.LOCAL_DATAFOLDER.resolve(namespace).resolve(serverindex)
																.toAbsolutePath().toString()
														+ "], will continue next folder");
												e.printStackTrace();
											}
										}
									} else {
										// do nothing
									}
								}
							}
						} else {
							// do nothing
						}
					}
				}

			}
			if (Configclient.running) {
				try {
					Thread.sleep(120000);
				} catch (InterruptedException e1) {
					// do nothing;
				}
			}
		}

		System.out.println(new Date() + " ==== stopped auto redistribution");
	}

	private void walkfolder(String namespace, String serverindex) throws Exception {
		Path progressfolder = STATIC.LOCAL_DATAFOLDER.toAbsolutePath().getParent().resolve("movingdistribution");
		if (!Files.exists(progressfolder)) {
			Files.createDirectories(progressfolder);
		}

		Files.walkFileTree(STATIC.LOCAL_DATAFOLDER.resolve(namespace).resolve(serverindex),
				new java.util.HashSet<FileVisitOption>(0), 1, new FileVisitor<Object>() {

					@Override
					public FileVisitResult postVisitDirectory(Object arg0, IOException arg1) throws IOException {
						if (Configclient.running) {
							return FileVisitResult.CONTINUE;
						} else {
							return FileVisitResult.TERMINATE;
						}

					}

					@Override
					public FileVisitResult preVisitDirectory(Object arg0, BasicFileAttributes arg1) throws IOException {
						if (Configclient.running) {
							return FileVisitResult.CONTINUE;
						} else {
							return FileVisitResult.TERMINATE;
						}
					}

					@Override
					public FileVisitResult visitFile(Object file, BasicFileAttributes arg1) throws IOException {
						if (Configclient.running) {
							Path filtersfolder = Paths.get(file.toString());

							if (!Files.isDirectory(filtersfolder)
									|| filtersfolder.getFileName().toString().startsWith(".")) {
								return FileVisitResult.CONTINUE;
							} else {
								String[] indexfiles = filtersfolder.toFile().list();
								for (String indexfilename : indexfiles) {
									Path indexfile = filtersfolder.resolve(indexfilename);
									if (!Files.isDirectory(indexfile)
											&& !indexfile.getFileName().toString().startsWith(".")
											&& Files.size(indexfile) > 0) {
										Path progressfile = progressfolder.resolve(namespace).resolve(serverindex)
												.resolve(filtersfolder.getFileName().toString()).resolve(indexfilename);
										try {
											if (Filekvutil.indexversion(indexfile) != null) {
												// don't move, delete directly
											} else {
												movefile(progressfile, namespace, indexfile);
											}

											Files.deleteIfExists(indexfile);
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
												Files.deleteIfExists(indexfile.getParent());
											} catch (Exception ex) {
												// do nothing
											}
											try {
												Files.deleteIfExists(indexfile.getParent().getParent());
												System.out.println(
														new Date() + " ==== redistributed [" + serverindex + "]");
											} catch (Exception ex) {
												// do nothing
											}
										} catch (Exception e) {
											StringWriter errors = new StringWriter();
											e.printStackTrace(new PrintWriter(errors));
											if (Files.exists(progressfile)) {
												try {
													Files.write(progressfile,
															STATIC.tobytes(System.lineSeparator() + new Date()
																	+ " ==== error in distributing file ["
																	+ indexfile.toAbsolutePath().toString() + "]:"
																	+ errors.toString()),
															StandardOpenOption.CREATE, StandardOpenOption.APPEND,
															StandardOpenOption.SYNC);
												} catch (Exception e1) {
													System.out.println(new Date() + " ==== error in distributing file ["
															+ indexfile.toAbsolutePath().toString()
															+ "], will continue next file");
													e.printStackTrace();
												}
											} else {
												if (!Files.exists(progressfile.getParent())) {
													Files.createDirectories(progressfile.getParent());
												}
												try {
													Files.write(progressfile,
															STATIC.tobytes(0 + System.lineSeparator() + new Date()
																	+ " ==== error in distributing file ["
																	+ indexfile.toAbsolutePath().toString() + "]:"
																	+ errors.toString()),
															StandardOpenOption.CREATE,
															StandardOpenOption.TRUNCATE_EXISTING,
															StandardOpenOption.SYNC);
												} catch (Exception e1) {
													System.out.println(new Date() + " ==== error in distributing file ["
															+ indexfile.toAbsolutePath().toString()
															+ "], will continue next file");
													e.printStackTrace();
												}
											}

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
						if (Configclient.running) {
							return FileVisitResult.CONTINUE;
						} else {
							return FileVisitResult.TERMINATE;
						}
					}
				});
	}

	private void movefile(Path progressfile, String namespace, Path indexfile) throws Exception {
		String[] filters = STATIC.splitenc(indexfile.getParent().getFileName().toString());
		
		Long thepagenum = null;
		try{
			thepagenum = Long.parseLong(filters[0]);
		}catch(Exception e) {
			return;
		}
		final Long pagenum = thepagenum;
		long processeddata = 0;
		if (Files.exists(progressfile)) {
			processeddata = Integer.parseInt(Files.readAllLines(progressfile, Charset.forName("UTF-8")).get(0));
		}

		final Long processednumofdata = processeddata;

		StringBuffer error = new StringBuffer();

		Fileutil.walkdata(indexfile, new Filedatawalk() {

			@Override
			public Filedatawalkresult data(long datasequence, long dataseqincludedeleted, byte[] v1,
					boolean isv1deleted, byte[] v2, boolean isv2deleted) {
				if (isv1deleted || isv2deleted || datasequence < processednumofdata) {
					return null;
				} else {
					try {
						String index = STATIC.tostring(v1);
						String key = STATIC.tostring(v2);
						Indexclient ic = Indexclient.getinstance(namespace, index);
						if (filters.length!=0) {
							for (int i=1;i<filters.length;i++) {
								ic.addfilter(filters[i]);
							}
						}
						if (pagenum == STATIC.PAGENUM_UNIQUE) {
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
						if (!Files.exists(progressfile.getParent())) {
							Files.createDirectories(progressfile.getParent());
						}
						Files.write(progressfile, STATIC.tobytes(String.valueOf(datasequence + 1)),
								StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
								StandardOpenOption.SYNC);
						return null;
					} catch (Exception e) {
						StringWriter errors = new StringWriter();
						e.printStackTrace(new PrintWriter(errors));
						error.append(new Date()
								+ " ==== error when distributing datasequence [" + datasequence + "]:"
								+ errors.toString());
						return new Filedatawalkresult(Filedatawalkresult.WALK_TERMINATE,
								Filedatawalkresult.DATA_DONOTHING, null, null);
					}
				}
			}

		}, false);
		if (error.length() != 0) {
			throw new Exception(error.toString());
		}
	}

}
