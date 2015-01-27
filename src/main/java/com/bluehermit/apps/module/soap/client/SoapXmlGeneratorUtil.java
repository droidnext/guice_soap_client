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
package com.bluehermit.apps.module.soap.client;

import java.io.ByteArrayOutputStream;
import java.lang.annotation.Annotation;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;


public class SoapXmlGeneratorUtil {

	public static String generateSoapXmlRequest(Object request) throws Exception
	{
		String soapRequestXml = null;
		try{
			if(!JAXBElement.class.isInstance(request) && !isXMLRootAnnocationPresnt(request))
			{
				throw new Exception("Incorrect soap request , missing @XMLRootElemnt.");
			}
			
			JAXBContext jaxbContext = null;
			if(request instanceof JAXBElement)
				jaxbContext = JAXBContext.newInstance(((JAXBElement)request).getDeclaredType());
			else
				jaxbContext = JAXBContext.newInstance(request.getClass());
			
			Marshaller marshaller = jaxbContext.createMarshaller();
			MessageFactory msgFactory = MessageFactory.newInstance();
			SOAPMessage message=msgFactory.createMessage();
			SOAPPart soapPart=message.getSOAPPart();
			SOAPEnvelope soapEnvelope=soapPart.getEnvelope();
			SOAPBody soapBody=soapEnvelope.getBody();
			
			marshaller.marshal(request, soapBody);
			message.saveChanges();
			ByteArrayOutputStream writer = new ByteArrayOutputStream();
			message.writeTo(writer);
			soapRequestXml=new String(writer.toByteArray());
			writer.close();
		}catch(Exception e)
		{
			throw new Exception("Failed to generate soap xml request."+e.toString());
		}
		
		return soapRequestXml;
	}
	
	private static boolean isXMLRootAnnocationPresnt(Object request)
	{
		Annotation[] annotations = request.getClass().getAnnotations();
		for(int i=0;i<annotations.length;i++)
		{
			if( annotations[i].annotationType().getName().equals(XmlRootElement.class.getName()))
				return true;
		}
		
		return false;
	}
}
