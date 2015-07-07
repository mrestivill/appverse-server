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

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class TestChannelInterceptor extends ChannelInterceptorAdapter {

  private final BlockingQueue<Message<?>> messages = new ArrayBlockingQueue<>(100);

  private final List<String> destinationPatterns = new ArrayList<>();

  private final PathMatcher matcher = new AntPathMatcher();


  public void setIncludedDestinations(String... patterns) {
   this.destinationPatterns.addAll(Arrays.asList(patterns));
  }

  /**
   * @return the next received message or {@code null} if the specified time elapses
   */
  public Message<?> awaitMessage(long timeoutInSeconds) throws InterruptedException {
   return this.messages.poll(timeoutInSeconds, TimeUnit.SECONDS);
  }

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
   if (this.destinationPatterns.isEmpty()) {
    this.messages.add(message);
   }
   else {
    StompHeaderAccessor headers = StompHeaderAccessor.wrap(message);
    if (headers.getDestination() != null) {
     for (String pattern : this.destinationPatterns) {
      if (this.matcher.match(pattern, headers.getDestination())) {
       this.messages.add(message);
       break;
      }
     }
    }
   }
   return message;
  }

 }

