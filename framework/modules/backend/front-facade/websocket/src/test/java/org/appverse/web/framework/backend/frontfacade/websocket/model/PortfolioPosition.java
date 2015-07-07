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

public class PortfolioPosition {

 private String company;

 private String ticker;

 private double price;

 private int shares;

 private long updateTime;


 public PortfolioPosition(String company, String ticker, double price, int shares) {
  this.company = company;
  this.ticker = ticker;
  this.price = price;
  this.shares = shares;
  this.updateTime = System.currentTimeMillis();
 }

 public PortfolioPosition(PortfolioPosition other, int sharesToAddOrSubtract) {
  this.company = other.company;
  this.ticker = other.ticker;
  this.price = other.price;
  this.shares = other.shares + sharesToAddOrSubtract;
  this.updateTime = System.currentTimeMillis();
 }

 private PortfolioPosition() {
 }

 public String getCompany() {
  return this.company;
 }

 public void setCompany(String company) {
  this.company = company;
 }

 public String getTicker() {
  return this.ticker;
 }

 public void setTicker(String ticker) {
  this.ticker = ticker;
 }

 public double getPrice() {
  return this.price;
 }

 public void setPrice(double price) {
  this.price = price;
 }

 public int getShares() {
  return this.shares;
 }

 public void setShares(int shares) {
  this.shares = shares;
 }

 public long getUpdateTime() {
  return this.updateTime;
 }

 public void setUpdateTime(long updateTime) {
  this.updateTime = updateTime;
 }

 @Override
 public String toString() {
  return "PortfolioPosition [company=" + this.company + ", ticker=" + this.ticker
          + ", price=" + this.price + ", shares=" + this.shares + "]";
 }

}
