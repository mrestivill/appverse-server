package org.appverse.web.framework.backend.frontfacade.websocket.services.business;/*
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

import org.appverse.web.framework.backend.frontfacade.websocket.model.Portfolio;
import org.appverse.web.framework.backend.frontfacade.websocket.model.PortfolioPosition;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PortfolioServiceImpl implements PortfolioService {

 // user -> Portfolio
 private final Map<String, Portfolio> portfolioLookup = new HashMap<>();


 public PortfolioServiceImpl() {

  Portfolio portfolio = new Portfolio();
  portfolio.addPosition(new PortfolioPosition("Citrix Systems, Inc.", "CTXS", 24.30, 75));
  portfolio.addPosition(new PortfolioPosition("Dell Inc.", "DELL", 13.44, 50));
  portfolio.addPosition(new PortfolioPosition("Microsoft", "MSFT", 34.15, 33));
  portfolio.addPosition(new PortfolioPosition("Oracle", "ORCL", 31.22, 45));
  this.portfolioLookup.put("fabrice", portfolio);

  portfolio = new Portfolio();
  portfolio.addPosition(new PortfolioPosition("EMC Corporation", "EMC", 24.30, 75));
  portfolio.addPosition(new PortfolioPosition("Google Inc", "GOOG", 905.09, 5));
  portfolio.addPosition(new PortfolioPosition("VMware, Inc.", "VMW", 65.58, 23));
  portfolio.addPosition(new PortfolioPosition("Red Hat", "RHT", 48.30, 15));
  this.portfolioLookup.put("paulson", portfolio);
 }


 public Portfolio findPortfolio(String username) {
  Portfolio portfolio = this.portfolioLookup.get(username);
  if (portfolio == null) {
   throw new IllegalArgumentException(username);
  }
  return portfolio;
 }

}