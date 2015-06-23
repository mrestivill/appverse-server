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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.appverse.web.framework.backend.frontfacade.websocket.model.PortfolioPosition;
import org.appverse.web.framework.backend.frontfacade.websocket.model.Portfolio;
import org.appverse.web.framework.backend.frontfacade.websocket.model.Trade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;


@Service
public class TradeServiceImpl implements TradeService {

 private static final Log logger = LogFactory.getLog(TradeServiceImpl.class);

 private final SimpMessageSendingOperations messagingTemplate;

 private final PortfolioService portfolioService;

 private final List<TradeResult> tradeResults = new CopyOnWriteArrayList<>();


 @Autowired
 public TradeServiceImpl(SimpMessageSendingOperations messagingTemplate, PortfolioService portfolioService) {
  this.messagingTemplate = messagingTemplate;
  this.portfolioService = portfolioService;
 }

 /**
  * In real application a trade is probably executed in an external system, i.e. asynchronously.
  */
 @Override
 public void executeTrade(Trade trade) {

  Portfolio portfolio = this.portfolioService.findPortfolio(trade.getUsername());
  String ticker = trade.getTicker();
  int sharesToTrade = trade.getShares();

  PortfolioPosition newPosition = (trade.getAction() == Trade.TradeAction.Buy) ?
          portfolio.buy(ticker, sharesToTrade) : portfolio.sell(ticker, sharesToTrade);

  if (newPosition == null) {
   String payload = "Rejected trade " + trade;
   this.messagingTemplate.convertAndSendToUser(trade.getUsername(), "/queue/errors", payload);
   return;
  }

  this.tradeResults.add(new TradeResult(trade.getUsername(), newPosition));
 }

 @Scheduled(fixedDelay=1500)
 public void sendTradeNotifications() {

  Map<String, Object> map = new HashMap<>();
  map.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);

  for (TradeResult result : this.tradeResults) {
   if (System.currentTimeMillis() >= (result.timestamp + 1500)) {
    logger.debug("Sending position update: " + result.position);
    this.messagingTemplate.convertAndSendToUser(result.user, "/queue/position-updates", result.position, map);
    this.tradeResults.remove(result);
   }
  }
 }




 private static class TradeResult {

  private final String user;
  private final PortfolioPosition position;
  private final long timestamp;

  public TradeResult(String user, PortfolioPosition position) {
   this.user = user;
   this.position = position;
   this.timestamp = System.currentTimeMillis();
  }
 }

}
