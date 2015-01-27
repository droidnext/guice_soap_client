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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.bluehermit.apps.example.soap.shopping.*;
import com.bluehermit.apps.module.soap.SampleProcessor;
import com.bluehermit.apps.module.soap.client.SoapClientModule;

public class SoapClientTest {

	@Test
	public void testSoapRequst() throws Exception
	{
		Injector injector=Guice.createInjector(new SoapClientModule(),new SampleModule());
		
		List<String> items= new ArrayList<String>();
		items.add("Gateway Laptop");
		items.add("IPhone S 4");
		
		

		SampleProcessor processor = injector.getInstance(SampleProcessor.class);//new SampleProcessor();
		System.out.println("Response="+processor.callService(items));
	}
}
