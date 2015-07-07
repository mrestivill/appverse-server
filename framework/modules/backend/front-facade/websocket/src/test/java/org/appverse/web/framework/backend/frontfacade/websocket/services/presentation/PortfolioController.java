package org.appverse.web.framework.backend.frontfacade.websocket.services.presentation;/*
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
import org.appverse.web.framework.backend.frontfacade.websocket.model.Trade;
import org.appverse.web.framework.backend.frontfacade.websocket.services.business.PortfolioService;
import org.appverse.web.framework.backend.frontfacade.websocket.services.business.TradeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;

@Controller
public class PortfolioController {

 private static final Logger logger = LoggerFactory.getLogger(PortfolioController.class);

 private final PortfolioService portfolioService;

 private final TradeService tradeService;


 @Autowired
 public PortfolioController(PortfolioService portfolioService, TradeService tradeService) {
  this.portfolioService = portfolioService;
  this.tradeService = tradeService;
 }

 @SubscribeMapping("/positions")
 public List<PortfolioPosition> getPositions(Principal principal) throws Exception {
  logger.debug("Positions for " + principal.getName());
  Portfolio portfolio = this.portfolioService.findPortfolio(principal.getName());
  return portfolio.getPositions();
 }

 @MessageMapping("/trade")
 public void executeTrade(Trade trade, Principal principal) {
  trade.setUsername(principal.getName());
  logger.debug("Trade: " + trade);
  this.tradeService.executeTrade(trade);
 }

 @MessageExceptionHandler
 @SendToUser("/queue/errors")
 public String handleException(Throwable exception) {
  return exception.getMessage();
 }

}
