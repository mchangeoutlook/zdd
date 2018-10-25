package com.zdd.bdc.server.biz;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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

	public Movingdistribution(String theip, String theport) {
		ip = theip;
		port = theport;
	}

	@Override
	public void run() {
		StringBuffer ipportsb = new StringBuffer();
		try {
			ipportsb.append(CS.splitiport(ip, port));
		}catch(Exception e) {
			System.out.println(new Date() + " ==== error terminated auto redistribution");
			e.printStackTrace();
			return;
		}
		final String ipportpending = ipportsb.toString();
		System.out.println(new Date() + " ==== started auto redistribution");
		while (!SS.REMOTE_CONFIGVAL_PENDING
				.equals(Configclient.getinstance(CS.NAMESPACE_CORE, SS.REMOTE_CONFIG_PENDING)
						.read(ipportpending))) {
			if (Files.exists(SS.LOCAL_DATAFOLDER) && Files.isDirectory(SS.LOCAL_DATAFOLDER)) {
				try {
					Path progressfolder = SS.LOCAL_DATAFOLDER.toAbsolutePath().getParent()
							.resolve("movingdistribution");
					if (!Files.exists(progressfolder)) {
						Files.createDirectories(progressfolder);
					}
					Files.walkFileTree(SS.LOCAL_DATAFOLDER, new FileVisitor<Object>() {

						@Override
						public FileVisitResult postVisitDirectory(Object arg0, IOException arg1) throws IOException {
							if (!SS.REMOTE_CONFIGVAL_PENDING.equals(
									Configclient.getinstance(CS.NAMESPACE_CORE, SS.REMOTE_CONFIG_PENDING)
											.read(ipportpending))) {
								return FileVisitResult.CONTINUE;
							} else {
								return FileVisitResult.TERMINATE;
							}

						}

						@Override
						public FileVisitResult preVisitDirectory(Object arg0, BasicFileAttributes arg1)
								throws IOException {
							if (!SS.REMOTE_CONFIGVAL_PENDING.equals(
									Configclient.getinstance(CS.NAMESPACE_CORE, SS.REMOTE_CONFIG_PENDING)
											.read(ipportpending))) {
								return FileVisitResult.CONTINUE;
							} else {
								return FileVisitResult.TERMINATE;
							}
						}

						@Override
						public FileVisitResult visitFile(Object file, BasicFileAttributes arg1) throws IOException {
							if (!SS.REMOTE_CONFIGVAL_PENDING.equals(
									Configclient.getinstance(CS.NAMESPACE_CORE, SS.REMOTE_CONFIG_PENDING)
											.read(ipportpending))) {
								Path pathfile = Paths.get(file.toString());
								if (!Files.isDirectory(pathfile) && !pathfile.getFileName().toString().startsWith(".")
										&& Files.size(pathfile) > 0) {
									Path progressfile = progressfolder
											.resolve(pathfile.toAbsolutePath().getParent().getParent().getParent()
													.getFileName().toString())
											.resolve(pathfile.toAbsolutePath().getParent().getParent().getFileName()
													.toString())
											.resolve(pathfile.toAbsolutePath().getParent().getFileName().toString())
											.resolve(pathfile.getFileName().toString());
									try {
										String namespace = pathfile.getParent().getParent().getParent().getFileName().toString();
										String serverindex = pathfile.getParent().getParent().getFileName().toString();
										String targetiport = Configclient
												.getinstance(namespace, CS.REMOTE_CONFIG_BIGINDEX)
												.read(serverindex);
										if (targetiport == null
												|| targetiport.equals(CS.splitiport(ip, port))) {
											return FileVisitResult.CONTINUE;
										}
										if (Filekvutil.indexversion(pathfile)!=null) {
											//don't move, delete directly
										} else {
											Vector<String> filterspagenum = SS.filtersandpagenum(pathfile.getParent().getFileName().toString());
											Vector<String> filters = new Vector<String>(filterspagenum.size() - 1);
											for (int i = 0; i < filterspagenum.size() - 1; i++) {
												if (!filterspagenum.get(i).trim().isEmpty()) {
													filters.add(filterspagenum.get(i));
												}
											}
											long pagenum = Long.parseLong(filterspagenum.get(filterspagenum.size() - 1));
											
											long processeddata = 0;
											if (Files.exists(progressfile)) {
												processeddata = Integer.parseInt(Files
														.readAllLines(progressfile, Charset.forName("UTF-8")).get(0));
											}
											
											final Long processednumofdata = processeddata;
											
											Fileutil.walkdata(pathfile, new Filedatawalk() {
	
												@Override
												public Filedatawalkresult data(long datasequence, long dataseqincludedeleted, byte[] v1, boolean isv1deleted, byte[] v2,
														boolean isv2deleted) {
													if (isv1deleted||isv2deleted||datasequence<processednumofdata) {
														return null;
													} else {
														try {
														String index = SS.tostring(v1);
														String key = SS.tostring(v2);
														Indexclient ic = Indexclient.getinstance(namespace, index);
														if (!filters.isEmpty()) {
															ic = ic.filters(filters.size());
															for (String filter : filters) {
																ic.add(filter);
															}
														}
														if (pagenum == -1) {
															try {
																ic.createunique(key);
															} catch (Exception e) {
																if (!e.getMessage().contains("duplicate")) {
																	//
																}
															}
														} else {
															ic.create(key, pagenum);
														}
														if (!Files.exists(progressfile.getParent())) {
															Files.createDirectories(progressfile.getParent());
														}
														Files.write(progressfile,
																SS.tobytes(String.valueOf(datasequence+1)),
																StandardOpenOption.CREATE,
																StandardOpenOption.TRUNCATE_EXISTING,
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
																	SS.tobytes((datasequence+1) + System.lineSeparator() + errors.toString()),
																	StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
																	StandardOpenOption.SYNC);
															}catch(Exception e1) {
																//do nothing
															}
															return new Filedatawalkresult(Filedatawalkresult.WALK_TERMINATE, Filedatawalkresult.DATA_DONOTHING, null, null);
															
														}
													}
												}
												
											}, false);
										}
										
										try {
											Files.deleteIfExists(progressfile.getParent());
										} catch (Exception ex) {
											// do nothing
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
										if (Files.exists(progressfile)) {
											try {
												Files.write(progressfile,
														SS.tobytes(System.lineSeparator() + errors.toString()),
														StandardOpenOption.CREATE, StandardOpenOption.APPEND,
														StandardOpenOption.SYNC);
											} catch (Exception e1) {
												//do nothing
											}
										} else {
											if (!Files.exists(progressfile.getParent())) {
												Files.createDirectories(progressfile.getParent());
											}
											try {
												Files.write(progressfile,
														SS.tobytes(0 + System.lineSeparator() + errors.toString()),
														StandardOpenOption.CREATE, StandardOpenOption.SYNC);
											} catch (Exception e1) {
												//do nothing
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
							if (!SS.REMOTE_CONFIGVAL_PENDING.equals(
									Configclient.getinstance(CS.NAMESPACE_CORE, SS.REMOTE_CONFIG_PENDING)
											.read(ipportpending))) {
								return FileVisitResult.CONTINUE;
							} else {
								return FileVisitResult.TERMINATE;
							}
						}
					});
				} catch (Exception e) {
					//do nothing
				}
			}
			if (!SS.REMOTE_CONFIGVAL_PENDING
					.equals(Configclient.getinstance(CS.NAMESPACE_CORE, SS.REMOTE_CONFIG_PENDING)
							.read(CS.splitiport(ip, port)))) {
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
