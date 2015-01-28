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

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class HttpClientHelperImpl implements HttpClient {
	public  String post(String target,String message) throws Exception
	{
		String responeMessage = null;
		CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
        	HttpPost httppost = new HttpPost(target);
        	httppost.setHeader("Content-Type","text/xml");
        	httppost.setEntity(new StringEntity(message));

            System.out.println("Executing request " + httppost.getRequestLine());
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());

                // Get hold of the response entity
                HttpEntity entity = response.getEntity();

                // If the response does not enclose an entity, there is no need
                // to bother about connection release
                if (entity != null) {
                    InputStream instream = entity.getContent();
                    try {
                    	responeMessage = IOUtils.toString(instream, "UTF-8");
                        // do something useful with the response
                    } catch (IOException ex) {
                        // In case of an IOException the connection will be released
                        // back to the connection manager automatically
                        throw ex;
                    } finally {
                        // Closing the input stream will trigger connection release
                        instream.close();
                    }
                }
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
        
		return responeMessage;
	}
	
}
