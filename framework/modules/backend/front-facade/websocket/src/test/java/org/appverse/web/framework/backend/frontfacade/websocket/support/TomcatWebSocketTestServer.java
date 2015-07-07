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
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.tomcat.websocket.server.WsContextListener;
import org.apache.tomcat.util.descriptor.web.ApplicationParameter;
import org.springframework.web.SpringServletContainerInitializer;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

public class TomcatWebSocketTestServer implements WebSocketTestServer {

 private static final String WS_APPLICATION_LISTENER =
         WsContextListener.class.getName();

 private final Tomcat tomcatServer;

 private final int port;

 private Context context;


 public TomcatWebSocketTestServer(int port) {

  this.port = port;

  Connector connector = new Connector(Http11NioProtocol.class.getName());
  connector.setPort(this.port);

  File baseDir = createTempDir("tomcat");
  String baseDirPath = baseDir.getAbsolutePath();

  this.tomcatServer = new Tomcat();
  this.tomcatServer.setBaseDir(baseDirPath);
  this.tomcatServer.setPort(this.port);
  this.tomcatServer.getService().addConnector(connector);
  this.tomcatServer.setConnector(connector);
 }

 private File createTempDir(String prefix) {
  try {
   File tempFolder = File.createTempFile(prefix + ".", "." + getPort());
   tempFolder.delete();
   tempFolder.mkdir();
   tempFolder.deleteOnExit();
   return tempFolder;
  }
  catch (IOException ex) {
   throw new RuntimeException("Unable to create temp directory", ex);
  }
 }

 public int getPort() {
  return this.port;
 }

 @Override
 public void deployConfig(WebApplicationContext cxt) {
  this.context = this.tomcatServer.addContext("", System.getProperty("java.io.tmpdir"));
  this.context.addApplicationListener(WS_APPLICATION_LISTENER);
  Tomcat.addServlet(context, "dispatcherServlet", new DispatcherServlet(cxt));
  this.context.addServletMapping("/", "dispatcherServlet");
 }

 public void deployConfig(Class<? extends WebApplicationInitializer>... initializers) {

  this.context = this.tomcatServer.addContext("", System.getProperty("java.io.tmpdir"));

  // Add Tomcat's DefaultServlet
  Wrapper defaultServlet = this.context.createWrapper();
  defaultServlet.setName("default");
  defaultServlet.setServletClass("org.apache.catalina.servlets.DefaultServlet");
  this.context.addChild(defaultServlet);

  // Ensure WebSocket support
  this.context.addApplicationListener(WS_APPLICATION_LISTENER);

  this.context.addServletContainerInitializer(
          new SpringServletContainerInitializer(), new HashSet<Class<?>>(Arrays.asList(initializers)));
 }

 public void undeployConfig() {
  if (this.context != null) {
   this.tomcatServer.getHost().removeChild(this.context);
  }
 }

 public void start() throws Exception {
  this.tomcatServer.start();
 }

 public void stop() throws Exception {
  this.tomcatServer.stop();
 }

}
