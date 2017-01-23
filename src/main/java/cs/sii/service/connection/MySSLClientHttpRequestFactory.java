package cs.sii.service.connection;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.http.client.SimpleClientHttpRequestFactory;

public class MySSLClientHttpRequestFactory extends SimpleClientHttpRequestFactory {

//    private final HostnameVerifier verifier;
//    private final String cookie="";

    public MySSLClientHttpRequestFactory(HostnameVerifier verifier) {
       // this.verifier = verifier;
    	mySslVerification(verifier);
    }


    
    private static void mySslVerification(HostnameVerifier verifier) {
	    try
	    {
	        // Create a trust manager that does not validate certificate chains
	        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
	        
	            @Override
	            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	                return null;
	            }

	            @Override
	            public void checkClientTrusted(
	                    java.security.cert.X509Certificate[] certs, String authType) {
	            	System.out.println("ssl client");
	            }

	            @Override
	            public void checkServerTrusted(
	                    java.security.cert.X509Certificate[] certs, String authType)
	                    throws CertificateException {
//	                InputStream inStream = null;
//	                System.out.println("ssl");
//	                try {
//	                    // Loading the CA cert
//	                    URL u = getClass().getResource("classpath:/cac.pem");
//	                    inStream = new FileInputStream(u.getFile());
//		                System.out.println("ssl loaded");
//
//	                    CertificateFactory cf = CertificateFactory.getInstance("X.509");
//	                    X509Certificate ca = (X509Certificate) cf.generateCertificate(inStream);
//	                    inStream.close();
//		                System.out.println("ssl builded");
//
//	                    for (X509Certificate cert : certs) {
//	                        // Verifing by public key
//	                        cert.verify(ca.getPublicKey());
//	    	                System.out.println("ssl verify certs");
//
//	                    }
//		                System.out.println("ssl verified");
//
//	                } catch (Exception ex) {
//	                	System.out.println("erroe validazione cert");
//	                } finally {
//	                    try {
//	                        inStream.close();
//	                    } catch (IOException ex) {
//	                    }
//	                }

	            }
	        }
	        };

	        // Install the all-trusting trust manager
	        SSLContext sc = SSLContext.getInstance("TLS");
	        sc.init(null, trustAllCerts, new java.security.SecureRandom());
	        

	        // Create all-trusting host name verifier
//	        HostnameVerifier allHostsValid = new HostnameVerifier() {
//	            public boolean verify(String hostname, SSLSession session) {
//	                return true;
//	            }
//	        };
	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	        // Install the all-trusting host verifier
	        HttpsURLConnection.setDefaultHostnameVerifier(verifier);
	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    } catch (KeyManagementException e) {
	        e.printStackTrace();
	    }
	}
    
    
}



//    @Override
//    protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
//        if (connection instanceof HttpsURLConnection) {
//            ((HttpsURLConnection) connection).setDefaultHostnameVerifier(verifier);
//            ((HttpsURLConnection) connection).setDefaultSSLSocketFactory(trustSelfSignedSSL().getSocketFactory());
//            ((HttpsURLConnection) connection).setAllowUserInteraction(true);
//        }
//        super.prepareConnection(connection, httpMethod);
//    }

//    public SSLContext trustSelfSignedSSL() {
//        try {
//            X509TrustManager tm = new X509TrustManager() {
//
//                public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
//                }
//
//                public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
//                }
//
//                public X509Certificate[] getAcceptedIssuers() {
//                    return null;
//                }
//            };
//            SSLContext ctx = SSLContext.getInstance("TLS");
//            ctx.init(null, new TrustManager[] { tm }, new java.security.SecureRandom());
//            SSLContext.setDefault(ctx);
//            return ctx;
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return null;
//    }


//
//
//
//
//Creating a custom key manager for SSL
//
//You can create a custom key manager configuration at any management scope and associate the new key manager with a Secure Sockets Layer (SSL) configuration.
//Before you begin
//You must develop, package, and locate a Java Archive (.JAR) file for a custom key manager in the was.install.root/lib/ext directory on WebSphere® Application Server.
//About this task
//Complete the following steps in the administrative console:
//Procedure
//
//    Decide whether you want to create the custom key manager at the cell scope or below the cell scope at the node, server, or cluster, for example.
//    Important: When you create a custom key manager at a level below the cell scope, you can associate it only with a Secure Sockets Layer (SSL) configuration at the same scope or higher. An SSL configuration at a scope lower than the key manager does not see the key manager configuration.
//        To create a custom key manager at the cell scope, click Security > SSL certificate and key management > Key managers. Every SSL configuration in the cell can select the key manager at the cell scope.
//        To create a custom key manager at a scope below the cell level, click Security > SSL certificate and key management > Manage endpoint security configurations > {Inbound | Outbound} > SSL_configuration > Key managers.
//    Click New to create a new key manager.
//    Type a unique key manager name.
//    Select the Custom implementation setting. With the custom setting, you can define a Java class that has an implementation on the Java interface javax.net.ssl.X509KeyManager and, optionally, the com.ibm.wsspi.ssl.KeyManagerExtendedInfo WebSphere Application Server interface. The standard implementation setting applies only when the key manager is already defined in the Java security provider list as a provider and an algorithm, which is not the case for a custom key manager. The typical standard key manager is algorithm = IbmX509, provider = IBMJSSE2.
//    Type a class name. For example, com.ibm.test.CustomKeyManager.
//    Select one of the following actions:
//        Click Apply, then click Custom properties under Additional Properties to add custom properties to the new custom key manager. When you are finished adding custom properties, click OK and Save, then go to the next step.
//        Click OK and Save, then go to the next step.
//    Click SSL certificate and key management in the page navigation at the top of the panel.
//    Select one of the following actions:
//        Click SSL configurations under Related Items for a cell-scoped SSL configuration.
//        Click Manage endpoint security configurations to select an SSL configuration at a lower scope.
//    Click the link for the existing SSL configuration that you want to associate with the new custom key manager. You can create a new SSL configuration instead of associating the custom key manager with an existing configuration. For more information, see Creating a Secure Sockets Layer configuration.
//    Click Trust and Key managers under Additional Properties.
//    Select the new custom key manager in the Key manager drop-down list. If the new custom key manager is not listed, verify that you selected an SSL configuration scope that is at the same level or below the scope that you selected in Step 8.
//    Click OK and Save.
//
//Results
//You have created a custom key manager configuration that references a JAR file in the installation directory of WebSphere Application Server and associates the custom configuration with an SSL configuration during the connection handshake.
//Example
//Example: Developing a custom key manager for custom Secure Sockets Layer key selection
//
//The following example is of a sample custom key manager. This simple key manager returns the configured alias if it is set using the alias properties com.ibm.ssl.keyStoreClientAlias or com.ibm.ssl.keyStoreServerAlias, depending on which side of the connection the key manager is used. The key manager defers to the JSSE default IbmX509 key manager to select an alias if these properties are not set.
//
//After you build and package a custom key manager, you can configure it from either the ssl.client.props file for a pure client or by using the SSLConfiguration KeyManager link in the administrative console. See Key manager control of X.509 certificate identities for more information about key managers.
//Because only one key manager can be configured at a time for any given Secure Sockets Layer (SSL) configuration, the certificate selections on the server side might not work as they would when the default IbmX509 key manager is specified. When a custom key manager is configured, it is up to the owner of that key manager to ensure that the selection of the alias from the SSL configuration supplied is set properly when chooseClientAlias or chooseServerAlias are called. Look for the com.ibm.ssl.keyStoreClientAlias and com.ibm.ssl.keyStoreServerAlias SSL properties.
//Note: This example should only be used as a sample, and is not supported.
//
//package com.ibm.test;
//
//import java.security.cert.X509Certificate;
//import com.ibm.wsspi.ssl.KeyManagerExtendedInfo;
//
//public final class CustomKeyManager 
//	implements javax.net.ssl.X509KeyManager, com.ibm.wsspi.ssl.KeyManagerExtendedInfo
//{
//    private java.util.Properties props = null;
//    private java.security.KeyStore ks = null;
//    private javax.net.ssl.X509KeyManager km = null;
//    private java.util.Properties sslConfig = null;
//    private String clientAlias = null;
//    private String serverAlias = null;
//    private int clientslotnum = 0;
//    private int serverslotnum = 0;
//
//    public CustomKeyManager()
//    {
//    }
//
//    /**
//     * Method called by WebSphere Application Server runtime to set the custom
//     * properties.
//     * 
//     * @param java.util.Properties - custom props
//     */
//    public void setCustomProperties(java.util.Properties customProps)
//    {
//        props = customProps;
//    }
//
//    private java.util.Properties getCustomProperties()
//    {
//        return props;
//    }
//
//    /**
//     * Method called by WebSphere Application Server runtime to set the SSL
//     * configuration properties being used for this connection.
//     * 
//     * @param java.util.Properties - contains a property for the SSL configuration.
//     */
//    public void setSSLConfig(java.util.Properties config)
//    {
//        sslConfig = config;                                   
//    }
//
//    private java.util.Properties getSSLConfig()
//    {
//        return sslConfig;
//    }
//
//    /**
//     * Method called by WebSphere Application Server runtime to set the default
//     * X509KeyManager created by the IbmX509 KeyManagerFactory using the KeyStore
//     * information present in this SSL configuration.  This allows some delegation
//     * to the default IbmX509 KeyManager to occur.
//     * 
//     * @param javax.net.ssl.KeyManager defaultX509KeyManager - default key manager for IbmX509
//     */
//    public void setDefaultX509KeyManager(javax.net.ssl.X509KeyManager defaultX509KeyManager)
//    {
//        km = defaultX509KeyManager;
//    }
//
//    public javax.net.ssl.X509KeyManager getDefaultX509KeyManager()
//    {
//        return km;
//    }
//
//    /**
//     * Method called by WebSphere Application Server runtime to set the SSL
//     * KeyStore used for this connection.
//     * 
//     * @param java.security.KeyStore - the KeyStore currently configured
//     */
//    public void setKeyStore(java.security.KeyStore keyStore)
//    {
//        ks = keyStore;
//    }
//
//    public java.security.KeyStore getKeyStore()
//    {
//        return ks;
//    }
//
// /**
//     * Method called by custom code to set the server alias.
//     * 
//     * @param String - the server alias to use
//     */
//    public void setKeyStoreServerAlias(String alias)
//    {
//        serverAlias = alias;
//    }
//
//    private String getKeyStoreServerAlias()
//    {
//        return serverAlias;
//    }
//
//    /**
//     * Method called by custom code to set the client alias.
//     * 
//     * @param String - the client alias to use
//     */
//    public void setKeyStoreClientAlias(String alias)
//    {
//        clientAlias = alias;
//    }
//    
//    private String getKeyStoreClientAlias()
//    {
//        return clientAlias;
//    }
//
//    /**
//     * Method called by custom code to set the client alias and slot (if necessary).
//     * 
//     * @param String - the client alias to use
//     * @param int - the slot to use (for hardware)
//     */
//    public void setClientAlias(String alias, int slotnum) throws Exception
//    {
//        if ( !ks.containsAlias(alias))
//        {
//            throw new IllegalArgumentException ( "Client alias " + alias + " 
//            not found in keystore." );
//        }
//        this.clientAlias = alias;
//        this.clientslotnum = slotnum;
//    }
//
//    /**
//     * Method called by custom code to set the server alias and slot (if necessary).
//     * 
//     * @param String - the server alias to use
//     * @param int - the slot to use (for hardware)
//     */
//    public void setServerAlias(String alias, int slotnum) throws Exception
//    {
//        if ( ! ks.containsAlias(alias))
//        {
//            throw new IllegalArgumentException ( "Server alias " + alias + " 
//            not found in keystore." );
//        }
//        this.serverAlias = alias;
//        this.serverslotnum = slotnum;
//    }
//
//
//    /**
//     * Method called by JSSE runtime to when an alias is needed for a client
//     * connection where a client certificate is required.
//     * 
//     * @param String keyType
//     * @param Principal[] issuers
//     * @param java.net.Socket socket (not always present)
//     */
//    public String chooseClientAlias(String[] keyType, java.security.Principal[]
//    issuers, java.net.Socket socket)
//    {
//        if (clientAlias != null && !clientAlias.equals(""))
//        {
//            String[] list = km.getClientAliases(keyType[0], issuers);
//            String aliases = "";
//
//            if (list != null)
//            {
//                boolean found=false;
//                for (int i=0; i<list.length; i++)
//                {
//                    aliases += list[i] + " ";
//                    if (clientAlias.equalsIgnoreCase(list[i]))
//                        found=true;
//                }
//
//                if (found)
//                {
//                    return clientAlias;
//                }
//
//            }
//        }
//        
//        // client alias not found, let the default key manager choose.
//        String[] keyArray = new String [] {keyType[0]};
//        String alias = km.chooseClientAlias(keyArray, issuers, null);
//        return alias.toLowerCase();
//    }
//
//    /**
//     * Method called by JSSE runtime to when an alias is needed for a server
//     * connection to provide the server identity.
//     * 
//     * @param String[] keyType
//     * @param Principal[] issuers
//     * @param java.net.Socket socket (not always present)
//     */
//    public String chooseServerAlias(String keyType, java.security.Principal[] 
//    issuers, java.net.Socket socket)
//    {
//        if (serverAlias != null && !serverAlias.equals(""))
//        {
//            // get the list of aliases in the keystore from the default key manager
//            String[] list = km.getServerAliases(keyType, issuers);
//            String aliases = "";
//
//            if (list != null)
//            {
//                boolean found=false;
//                for (int i=0; i<list.length; i++)
//                {
//                    aliases += list[i] + " ";
//                    if (serverAlias.equalsIgnoreCase(list[i]))
//                        found = true;
//                }
//
//                if (found)
//                {
//                    return serverAlias;
//                }
//            }
//        }
//
//        // specified alias not found, let the default key manager choose.
//        String alias = km.chooseServerAlias(keyType, issuers, null);
//        return alias.toLowerCase();
//    }
//
//    public String[] getClientAliases(String keyType, java.security.Principal[] issuers)
//    {
//        return km.getClientAliases(keyType, issuers);
//    }
//
//    public String[] getServerAliases(String keyType, java.security.Principal[] issuers)
//    {
//        return km.getServerAliases(keyType, issuers);
//    }
//
//    public java.security.PrivateKey getPrivateKey(String s)
//    {
//        return km.getPrivateKey(s);
//    }
//
//    public java.security.cert.X509Certificate[] getCertificateChain(String s)
//    {
//        return km.getCertificateChain(s);
//    }
//
//    public javax.net.ssl.X509KeyManager getX509KeyManager()
//    {
//        return km;
//    }
//
//}
//
//What to do next
//You can create a custom key manager for a pure client. For more information, see the keyManagerCommands command group for the AdminTask object.
//
//
//In this information ...
//
//Related concepts
//Key manager control of X.509 certificate identities
//Related reference
//keyManagerCommands command group for the AdminTask object
//	
//IBM Redbooks, demos, education, and more
//
//(Index)
//
//Use IBM Suggests to retrieve related content from ibm.com and beyond, identified for your convenience.
//
//This feature requires Internet access.
//Task topic Task topic    
//Terms and conditions for product documentation |
//
//Last updatedLast updated: Oct 3, 2016 7:15:55 PM CDT
//http://www14.software.ibm.com/webapp/wsbroker/redirect?version=compass&product=was-nd-dist&topic=tsec_sslcreatecuskeymgr
//File name: tsec_sslcreatecuskeymgr.html
//
//v
//s
//p
//z



