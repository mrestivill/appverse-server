package org.appverse.web.framework.backend.frontfacade.rest.beans;

/**
 * Class to represent an error with a code and message.
 * @author MCRZ
 *
 */
public class CredentialsVO {

 private String username;
 private String password;

 public String getUsername() {
  return username;
 }

 public void setUsername(String username) {
  this.username = username;
 }

 public String getPassword() {
  return password;
 }

 public void setPassword(String password) {
  this.password = password;
 }
}
