juice_soap_client
============================

Soap client for easy integration with soap services. Exposed as Juice module though you can directly instantiate SOAPClient


<code>soapClient.setPackageScan("com.bluehermit.apps.example.soap.shopping").target("http://localhost:8080/WS/ShoppingCartService").request(request).send().getEntity();</code>



TODO
--------------
- Add soap signing support


Dependencies included
---------------------
- Guice 4.0
- Apache httpclient 4.3.6
- Saxon-HE 9.4
- slf4j 1.7.7
- Apache lang3 3.1  
- JUnit 4.10

Requirements
------------
- Java 7
- Maven 3

Building
--------
- Make  <code> mvn clean install </code>

Testing
---------------
- Sample soap service for testing ,download  https://github.com/droidnext/sample_soap_shopping.git
- Run SampleSoapServicePublisher to start the soap service 
- Run SoapClientTest which connects to sample soap servcei over http://localhost:8080/WS/ShoppingCartService


