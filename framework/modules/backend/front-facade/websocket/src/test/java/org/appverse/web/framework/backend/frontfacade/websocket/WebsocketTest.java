package org.appverse.web.framework.backend.frontfacade.websocket;/*
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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.appverse.web.framework.backend.frontfacade.websocket.autoconfigure.FrontFacadeWebSocketAutoConfiguration;
import org.appverse.web.framework.backend.frontfacade.websocket.model.Trade;
import org.appverse.web.framework.backend.frontfacade.websocket.support.TestPrincipal;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.JsonPathExpectationsHelper;

import static org.junit.Assert.*;

import java.nio.charset.Charset;
import java.util.HashMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        FrontFacadeWebSocketAutoConfiguration.class
})
public class WebsocketTest {

  @Autowired
  private AbstractSubscribableChannel clientInboundChannel;

  @Autowired
  private AbstractSubscribableChannel clientOutboundChannel;

  @Autowired
  private AbstractSubscribableChannel brokerChannel;

  private TestChannelInterceptor clientOutboundChannelInterceptor;

  private TestChannelInterceptor brokerChannelInterceptor;


  @Before
  public void setUp() throws Exception {

   this.brokerChannelInterceptor = new TestChannelInterceptor();
   this.clientOutboundChannelInterceptor = new TestChannelInterceptor();

   this.brokerChannel.addInterceptor(this.brokerChannelInterceptor);
   this.clientOutboundChannel.addInterceptor(this.clientOutboundChannelInterceptor);
  }


  @Test
  public void getPositions() throws Exception {

   StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
   headers.setSubscriptionId("0");
   headers.setDestination("/app/positions");
   headers.setSessionId("0");
   headers.setUser(new TestPrincipal("fabrice"));
   headers.setSessionAttributes(new HashMap<String, Object>());
   Message<byte[]> message = MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());

   this.clientOutboundChannelInterceptor.setIncludedDestinations("/app/positions");
   this.clientInboundChannel.send(message);

   Message<?> reply = this.clientOutboundChannelInterceptor.awaitMessage(5);
   assertNotNull(reply);

   StompHeaderAccessor replyHeaders = StompHeaderAccessor.wrap(reply);
   assertEquals("0", replyHeaders.getSessionId());
   assertEquals("0", replyHeaders.getSubscriptionId());
   assertEquals("/app/positions", replyHeaders.getDestination());

   String json = new String((byte[]) reply.getPayload(), Charset.forName("UTF-8"));
   new JsonPathExpectationsHelper("$[0].company").assertValue(json, "Citrix Systems, Inc.");
   new JsonPathExpectationsHelper("$[1].company").assertValue(json, "Dell Inc.");
   new JsonPathExpectationsHelper("$[2].company").assertValue(json, "Microsoft");
   new JsonPathExpectationsHelper("$[3].company").assertValue(json, "Oracle");
  }

  @Test
  public void executeTrade() throws Exception {

   Trade trade = new Trade();
   trade.setAction(Trade.TradeAction.Buy);
   trade.setTicker("DELL");
   trade.setShares(25);

   byte[] payload = new ObjectMapper().writeValueAsBytes(trade);

   StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SEND);
   headers.setDestination("/app/trade");
   headers.setSessionId("0");
   headers.setUser(new TestPrincipal("fabrice"));
   headers.setSessionAttributes(new HashMap<String, Object>());
   Message<byte[]> message = MessageBuilder.createMessage(payload, headers.getMessageHeaders());

   this.brokerChannelInterceptor.setIncludedDestinations("/user/**");
   this.clientInboundChannel.send(message);

   Message<?> positionUpdate = this.brokerChannelInterceptor.awaitMessage(5);
   assertNotNull(positionUpdate);

   StompHeaderAccessor positionUpdateHeaders = StompHeaderAccessor.wrap(positionUpdate);
   assertEquals("/user/fabrice/queue/position-updates", positionUpdateHeaders.getDestination());

   String json = new String((byte[]) positionUpdate.getPayload(), Charset.forName("UTF-8"));
   new JsonPathExpectationsHelper("$.ticker").assertValue(json, "DELL");
   new JsonPathExpectationsHelper("$.shares").assertValue(json, 75);
  }
}
