package org.appverse.web.framework.backend.frontfacade.websocket.support;/*
 Copyright (c) 2012 GFT Appverse, S.L., Sociedad Unipersonal.

 This Source Code Form is subject to the terms of the Appverse Public License 
 Version 2.0 (“APL v2.0”). If a copy of the APL was not distributed with this 
 file, You can obtain one at http://www.appverse.mobi/licenses/apl_v2.0.pdf. [^]

 Redistribution and use in source and binary forms, with or without modification, 
 are permitted provided that the conditions of the AppVerse Public License v2.0 
 are met.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. EXCEPT IN CASE OF WILLFUL MISCONDUCT OR GROSS NEGLIGENCE, IN NO EVENT
 SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT(INCLUDING NEGLIGENCE OR OTHERWISE) 
 ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 POSSIBILITY OF SUCH DAMAGE.
 */

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.tomcat.websocket.server.WsContextListener;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.SocketUtils;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.File;
import java.io.IOException;

public class TomcatWebSocketTestServer implements InitializingBean, DisposableBean {

 private final Tomcat tomcatServer;

 private final int port;

 private final AnnotationConfigWebApplicationContext serverContext;

 public TomcatWebSocketTestServer(Class<?>... serverConfigs) {
  this.port = SocketUtils.findAvailableTcpPort();
  Connector connector = new Connector(Http11NioProtocol.class.getName());
  connector.setPort(this.port);

  File baseDir = createTempDir("tomcat");
  String baseDirPath = baseDir.getAbsolutePath();

  this.tomcatServer = new Tomcat();
  this.tomcatServer.setBaseDir(baseDirPath);
  this.tomcatServer.setPort(this.port);
  this.tomcatServer.getService().addConnector(connector);
  this.tomcatServer.setConnector(connector);

  this.serverContext = new AnnotationConfigWebApplicationContext();
  this.serverContext.register(serverConfigs);

  Context context = this.tomcatServer.addContext("", System.getProperty("java.io.tmpdir"));
  context.addApplicationListener(WsContextListener.class.getName());
  Tomcat.addServlet(context, "dispatcherServlet", new DispatcherServlet(this.serverContext)).setAsyncSupported(true);
  context.addServletMapping("/", "dispatcherServlet");
 }

 private File createTempDir(String prefix) {
  try {
   File tempFolder = File.createTempFile(prefix + ".", "." + this.port);
   tempFolder.delete();
   tempFolder.mkdir();
   tempFolder.deleteOnExit();
   return tempFolder;
  }
  catch (IOException ex) {
   throw new RuntimeException("Unable to create temp directory", ex);
  }
 }

 public AnnotationConfigWebApplicationContext getServerContext() {
  return this.serverContext;
 }

 public String getWsBaseUrl() {
  return "ws://localhost:" + this.port;
 }

 @Override
 public void afterPropertiesSet() throws Exception {
  this.tomcatServer.start();
 }

 @Override
 public void destroy() throws Exception {
  this.tomcatServer.stop();
 }

}
