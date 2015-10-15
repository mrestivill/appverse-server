/*
 Copyright (c) 2012 GFT Appverse, S.L., Sociedad Unipersonal.

 This Source Code Form is subject to the terms of the Appverse Public License 
 Version 2.0 (“APL v2.0�?). If a copy of the APL was not distributed with this 
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

/*
 * This class is added just because of the following Springfox issue in version 2.2.2:
 * https://github.com/springfox/springfox/issues/767
 * 
 * The previous issue is affecting this class which is fixed in master branch in springfox project:
 * https://github.com/springfox/springfox
 * 
 * This class needs to be removed from Appverse Web project once a newer version of 
 * SpringFox is released.
 */
package springfox.documentation.swagger.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class SecurityConfiguration {
  public static final SecurityConfiguration DEFAULT = new SecurityConfiguration();

  private String clientId;
  private String realm;
  private String appName;
  private String apiKey;
  private String clientSecret;

  private SecurityConfiguration() {
    this(null, null, null, null, null);
  }

  public SecurityConfiguration(String clientId, String realm, String appName, String apiKey, String clientSecret) {
    this.clientId = clientId;
    this.realm = realm;
    this.appName = appName;
    this.apiKey = apiKey;
    this.clientSecret = clientSecret;
  }

  @JsonProperty("clientId")
  public String getClientId() {
    return clientId;
  }

  @JsonProperty("realm")
  public String getRealm() {
    return realm;
  }

  @JsonProperty("appName")
  public String getAppName() {
    return appName;
  }

  @JsonProperty("apiKey")
  public String getApiKey() {
    return apiKey;
  }

  @JsonProperty("clientSecret")
  public String getClientSecret() {
    return clientSecret;
  }
  
}
