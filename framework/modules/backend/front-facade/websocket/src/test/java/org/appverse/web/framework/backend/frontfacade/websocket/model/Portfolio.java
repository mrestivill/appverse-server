package org.appverse.web.framework.backend.frontfacade.websocket.model;/*
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Portfolio {

 private final Map<String,PortfolioPosition> positionLookup = new LinkedHashMap<String,PortfolioPosition>();


 public List<PortfolioPosition> getPositions() {
  return new ArrayList<PortfolioPosition>(positionLookup.values());
 }

 public void addPosition(PortfolioPosition position) {
  this.positionLookup.put(position.getTicker(), position);
 }

 public PortfolioPosition getPortfolioPosition(String ticker) {
  return this.positionLookup.get(ticker);
 }

 /**
  * @return the updated position or null
  */
 public PortfolioPosition buy(String ticker, int sharesToBuy) {
  PortfolioPosition position = this.positionLookup.get(ticker);
  if ((position == null) || (sharesToBuy < 1)) {
   return null;
  }
  position = new PortfolioPosition(position, sharesToBuy);
  this.positionLookup.put(ticker, position);
  return position;
 }

 /**
  * @return the updated position or null
  */
 public PortfolioPosition sell(String ticker, int sharesToSell) {
  PortfolioPosition position = this.positionLookup.get(ticker);
  if ((position == null) || (sharesToSell < 1) || (position.getShares() < sharesToSell)) {
   return null;
  }
  position = new PortfolioPosition(position, -sharesToSell);
  this.positionLookup.put(ticker, position);
  return position;
 }

}