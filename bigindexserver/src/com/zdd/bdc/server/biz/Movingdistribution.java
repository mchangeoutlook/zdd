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
import java.util.Vector;

import com.zdd.bdc.client.biz.Configclient;
import com.zdd.bdc.client.biz.Indexclient;
import com.zdd.bdc.client.util.CS;
import com.zdd.bdc.server.util.Filedatawalk;
import com.zdd.bdc.server.util.Filedatawalkresult;
import com.zdd.bdc.server.util.Filekvutil;
import com.zdd.bdc.server.util.Fileutil;
import com.zdd.bdc.server.util.SS;

public class Movingdistribution extends Thread {

	private String ip = null;
	private String port = null;

	private String iportpendingkey = null;

	public Movingdistribution(String theip, String theport) {
		ip = theip;
		port = theport;
		iportpendingkey = CS.splitiport(ip, port);
	}

	@Override
	public void run() {
		System.out.println(new Date() + " ==== started auto redistribution");
		while (!SS.REMOTE_CONFIGVAL_PENDING
				.equals(Configclient.getinstance(CS.NAMESPACE_CORE, SS.REMOTE_CONFIG_PENDING).read(iportpendingkey))) {

			if (Files.exists(SS.LOCAL_DATAFOLDER) && Files.isDirectory(SS.LOCAL_DATAFOLDER)) {

				String[] namespaces = SS.LOCAL_DATAFOLDER.toFile().list();
				for (String namespace : namespaces) {
					if (SS.REMOTE_CONFIGVAL_PENDING.equals(Configclient
							.getinstance(CS.NAMESPACE_CORE, SS.REMOTE_CONFIG_PENDING).read(iportpendingkey))) {
						break;
					} else {
						if (Files.isDirectory(SS.LOCAL_DATAFOLDER.resolve(namespace)) && !namespace.startsWith(".")) {
							String[] serverindexes = SS.LOCAL_DATAFOLDER.resolve(namespace).toFile().list();
							for (String serverindex : serverindexes) {
								if (SS.REMOTE_CONFIGVAL_PENDING
										.equals(Configclient.getinstance(CS.NAMESPACE_CORE, SS.REMOTE_CONFIG_PENDING)
												.read(iportpendingkey))) {
									break;
								} else {
									if (Files.isDirectory(SS.LOCAL_DATAFOLDER.resolve(namespace).resolve(serverindex))
											&& !serverindex.startsWith(".")) {
										String targetiport = Configclient
												.getinstance(namespace, CS.REMOTE_CONFIG_BIGINDEX).read(serverindex);
										if (targetiport == null || targetiport.equals(CS.splitiport(ip, port))) {
											// do nothing
										} else {
											try {
												walkfolder(namespace, serverindex);
											} catch (Exception e) {
												System.out.println(new Date() + " ==== error in distributing ["
														+ SS.LOCAL_DATAFOLDER.resolve(namespace).resolve(serverindex)
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
			if (!SS.REMOTE_CONFIGVAL_PENDING.equals(Configclient
					.getinstance(CS.NAMESPACE_CORE, SS.REMOTE_CONFIG_PENDING).read(CS.splitiport(ip, port)))) {
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
		Path progressfolder = SS.LOCAL_DATAFOLDER.toAbsolutePath().getParent().resolve("movingdistribution");
		if (!Files.exists(progressfolder)) {
			Files.createDirectories(progressfolder);
		}

		Files.walkFileTree(SS.LOCAL_DATAFOLDER.resolve(namespace).resolve(serverindex),
				new java.util.HashSet<FileVisitOption>(0), 1, new FileVisitor<Object>() {

					@Override
					public FileVisitResult postVisitDirectory(Object arg0, IOException arg1) throws IOException {
						if (!SS.REMOTE_CONFIGVAL_PENDING.equals(Configclient
								.getinstance(CS.NAMESPACE_CORE, SS.REMOTE_CONFIG_PENDING).read(iportpendingkey))) {
							return FileVisitResult.CONTINUE;
						} else {
							return FileVisitResult.TERMINATE;
						}

					}

					@Override
					public FileVisitResult preVisitDirectory(Object arg0, BasicFileAttributes arg1) throws IOException {
						if (!SS.REMOTE_CONFIGVAL_PENDING.equals(Configclient
								.getinstance(CS.NAMESPACE_CORE, SS.REMOTE_CONFIG_PENDING).read(iportpendingkey))) {
							return FileVisitResult.CONTINUE;
						} else {
							return FileVisitResult.TERMINATE;
						}
					}

					@Override
					public FileVisitResult visitFile(Object file, BasicFileAttributes arg1) throws IOException {
						if (!SS.REMOTE_CONFIGVAL_PENDING.equals(Configclient
								.getinstance(CS.NAMESPACE_CORE, SS.REMOTE_CONFIG_PENDING).read(iportpendingkey))) {
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
															SS.tobytes(System.lineSeparator() + errors.toString()),
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
															SS.tobytes(0 + System.lineSeparator() + errors.toString()),
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
						if (!SS.REMOTE_CONFIGVAL_PENDING.equals(Configclient
								.getinstance(CS.NAMESPACE_CORE, SS.REMOTE_CONFIG_PENDING).read(iportpendingkey))) {
							return FileVisitResult.CONTINUE;
						} else {
							return FileVisitResult.TERMINATE;
						}
					}
				});
	}

	private void movefile(Path progressfile, String namespace, Path indexfile) throws Exception {
		Vector<String> filterspagenum = SS.filtersandpagenum(indexfile.getParent().getFileName().toString());
		Vector<String> filters = new Vector<String>(filterspagenum.size() - 1);
		for (int i = 0; i < filterspagenum.size() - 1; i++) {
			if (!filterspagenum.get(i).trim().isEmpty()) {
				filters.add(filterspagenum.get(i));
			}
		}
		long pagenum = Long.parseLong(filterspagenum.get(filterspagenum.size() - 1));

		long processeddata = 0;
		if (Files.exists(progressfile)) {
			processeddata = Integer.parseInt(Files.readAllLines(progressfile, Charset.forName("UTF-8")).get(0));
		}

		final Long processednumofdata = processeddata;

		Fileutil.walkdata(indexfile, new Filedatawalk() {

			@Override
			public Filedatawalkresult data(long datasequence, long dataseqincludedeleted, byte[] v1,
					boolean isv1deleted, byte[] v2, boolean isv2deleted) {
				if (isv1deleted || isv2deleted || datasequence < processednumofdata) {
					return null;
				} else {
					try {
						String index = SS.tostring(v1);
						String key = SS.tostring(v2);
						Indexclient ic = Indexclient.getinstance(namespace, index);
						if (!filters.isEmpty()) {
							ic.filters(filters.size());
							for (String filter : filters) {
								ic.add(filter);
							}
						}
						if (pagenum == -1) {
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
						Files.write(progressfile, SS.tobytes(String.valueOf(datasequence + 1)),
								StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
								StandardOpenOption.SYNC);
						return null;
					} catch (Exception e) {
						StringWriter errors = new StringWriter();
						e.printStackTrace(new PrintWriter(errors));
						try {
							if (!Files.exists(progressfile.getParent())) {
								Files.createDirectories(progressfile.getParent());
							}
							Files.write(progressfile,
									SS.tobytes(datasequence + System.lineSeparator() + errors.toString()),
									StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
									StandardOpenOption.SYNC);
						} catch (Exception e1) {
							System.out.println(new Date() + " ==== error in distributing file ["
									+ indexfile.toAbsolutePath().toString() + "] datasequence [" + datasequence
									+ "], will continue next file");
							e.printStackTrace();
						}
						return new Filedatawalkresult(Filedatawalkresult.WALK_TERMINATE,
								Filedatawalkresult.DATA_DONOTHING, null, null);
					}
				}
			}

		}, false);
	}

}
