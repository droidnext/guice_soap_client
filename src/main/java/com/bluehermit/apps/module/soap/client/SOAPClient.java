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

import java.io.StringReader;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import net.sf.saxon.xpath.XPathFactoryImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.google.inject.Inject;

public class SOAPClient<T> {
	
	private static final Logger log = LoggerFactory.getLogger(SOAPClient.class);
	
	private String soapXmlRequest=null;
	private String soapXmlResponse=null;
	private String endPoint=null;
	private static final String SOAP_BODY_XPATH="/*:Envelope/*:Body/*";
	private static final String SOAP_FAULT_CODE_XPATH="/*:Envelope/*:Body/*:Fault/faultcode";
	private T soapResponseObject ; 
	private Class<T> soapResponseType;
	private Fault fault;
	private String packageScan;
	private static ConcurrentHashMap<String, JAXBContext> jaxbContextCache = new ConcurrentHashMap<String, JAXBContext>();
	private Object soapRequestObject;
	@Inject
	private HttpClient httpClient;
	
	public SOAPClient target(String endPoint)
	{
		this.endPoint=endPoint;
		return this;
	}
	
	public SOAPClient request(Object request) throws Exception
	{
		this.soapRequestObject = request;
		this.soapXmlRequest=SoapXmlGeneratorUtil.generateSoapXmlRequest(request);
		return this;
	}
	
	public SOAPClient send() throws Exception
	{
		this.soapXmlResponse=httpClient.post(this.endPoint, this.soapXmlRequest);
		this.marshall();
		return this;
	}
	
	
	public T getEntity()
	{
		return this.soapResponseObject;
	}
	
	public Fault getFault()
	{
		return this.fault;
	}
	
	private void marshall() throws Exception
	{
		String packageScan = getPackageScan();
		JAXBContext ctx = getContext(packageScan);
		
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		Document document = documentBuilderFactory.newDocumentBuilder().parse( new InputSource( new StringReader( this.soapXmlResponse )));
		XPathFactory xPathFactory = XPathFactoryImpl.newInstance();
		XPath xpath= xPathFactory.newXPath();
		
		Node responseNode = null;
		try{
			responseNode = (Node) xpath.evaluate(SOAP_BODY_XPATH, document,XPathConstants.NODE);
			if(responseNode.getNodeName().toLowerCase().contains("fault"))
			{
				this.fault=buildFault();
			}
			else
			{
				Unmarshaller unmarshaller =  ctx.createUnmarshaller();
				this.soapResponseObject = (T)unmarshaller.unmarshal(responseNode);
				if(isJAXBElement(this.soapResponseObject))
				{
					this.soapResponseObject=(T)((JAXBElement)this.soapResponseObject).getValue();
				}
			}
		}catch(Exception exp)
		{
			throw new Exception("Failed to marshall response :"+exp.toString());
		}
	}
	
	public String getPackageScan() {
		if(packageScan!=null)
		{
			if(packageScan.contains(","))
				packageScan.replace(",", ":");
		}
		else
		{
			if(isJAXBElement(this.soapRequestObject))
			{
				packageScan = ((JAXBElement)soapRequestObject).getValue().getClass().getPackage().getName();
			}else
			{
				packageScan = soapXmlRequest.getClass().getPackage().getName();
			}
		}
		
		
		return packageScan;
	}

	public SOAPClient setPackageScan(String packageScan) {
		this.packageScan = packageScan;
		return this;
	}

	public String getXmlResponse()
	{
		return this.soapXmlResponse;
	}
	
	private Fault buildFault()
	{
		Fault fault = new Fault();
		fault.setFaultcode(getTagValue("faultcode"));
		fault.setFaultstring("faultstring");
		fault.setDetail(getTagValue("detail"));
		return fault;
	}
	
	private String getTagValue(String tagName)
	{
		String START_TAG="<"+tagName+">";
		String END_TAG="</"+tagName+">";
		return soapXmlResponse.substring(soapXmlResponse.indexOf(START_TAG)+START_TAG.length(), soapXmlResponse.indexOf(END_TAG));
	}
	
	private boolean isJAXBElement(Object object)
	{
		if(object.getClass().getName().equals(JAXBElement.class.getName()))
			return true;
		else
			return false;
	}
	
	private JAXBContext getContext(String packageScan) throws Exception
	{
		if(jaxbContextCache.get(packageScan)==null)
		{
			JAXBContext ctx = JAXBContext.newInstance(packageScan);
			jaxbContextCache.put(packageScan, ctx);
			return ctx;
		}else
		{
			return jaxbContextCache.get(packageScan);
		}
	}

}
