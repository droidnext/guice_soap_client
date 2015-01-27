/*
 * Copyright 2015 bluehermit.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bluehermit.apps.module.soap;

import java.util.List;

import javax.xml.bind.JAXBElement;

import com.bluehermit.apps.module.soap.client.SOAPClient;
import com.google.inject.Inject;
import com.bluehermit.apps.example.soap.shopping.*;

public class SampleProcessor {
	
	@Inject
	SOAPClient<BuyResponse> soapClient;
	
	//public SayHelloResponse callService(SayHello request)
	public String callService(List<String> items) throws Exception
	{
		ObjectFactory factory= new ObjectFactory();
		Buy buy=new Buy();
		Cart  cart = new Cart();
		cart.getItems().addAll(items);
		buy.setArg0(cart);
		
		JAXBElement<Buy> request=factory.createBuy(buy);

		JAXBElement<BuyResponse> response= (JAXBElement<BuyResponse>) soapClient.setPackageScan("com.bluehermit.apps.example.soap.shopping").target("http://localhost:8080/WS/ShoppingCartService")
				.request(request).send().getEntity();
		
		return ((BuyResponse)response.getValue()).getReturn().getPrintItems();
	}

}
