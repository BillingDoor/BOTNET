package cs.sii.service.connection;

//import java.net.Socket;
//import java.security.Principal;
//import java.security.PrivateKey;
//import java.security.cert.X509Certificate;
//
//import javax.net.ssl.X509KeyManager;
//
//class FilteredKeyManager implements X509KeyManager {
//
//    private final X509KeyManager originatingKeyManager;
//    private final X509Certificate sslCertificate;
//    private final String SSLCertificateKeyStoreAlias;
//
//    /**
//     * @param originatingKeyManager,       original X509KeyManager
//     * @param sslCertificate,              X509Certificate to use
//     * @param SSLCertificateKeyStoreAlias, Alias of the certificate in the provided keystore
//     */
//    public FilteredKeyManager(X509KeyManager originatingKeyManager, X509Certificate sslCertificate, String SSLCertificateKeyStoreAlias) {
//        this.originatingKeyManager = originatingKeyManager;
//        this.sslCertificate = sslCertificate;
//        this.SSLCertificateKeyStoreAlias = SSLCertificateKeyStoreAlias;
//    }
//
//    @Override
//    public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
//        return SSLCertificateKeyStoreAlias;
//    }
//
//    @Override
//    public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
//        return originatingKeyManager.chooseServerAlias(keyType, issuers, socket);
//    }
//
//    @Override
//    public X509Certificate[] getCertificateChain(String alias) {
//        return new X509Certificate[]{ sslCertificate };
//    }
//
//    @Override
//    public String[] getClientAliases(String keyType, Principal[] issuers) {
//        return originatingKeyManager.getClientAliases(keyType, issuers);
//    }
//
//    @Override
//    public String[] getServerAliases(String keyType, Principal[] issuers) {
//        return originatingKeyManager.getServerAliases(keyType, issuers);
//    }
//
//    @Override
//    public PrivateKey getPrivateKey(String alias) {
//        return originatingKeyManager.getPrivateKey(alias);
//    }
//}
//
//








import java.security.cert.X509Certificate;

import org.springframework.beans.factory.annotation.Value;

public final class FilteredKeyManager implements javax.net.ssl.X509KeyManager
{

    private java.util.Properties props = null;
    @Value("${server.ssl.key-store}")
    private java.security.KeyStore ks = null;
    
    private javax.net.ssl.X509KeyManager km = null;
    private java.util.Properties sslConfig = null;
    private String clientAlias = null;
    private String serverAlias = null;
    private int clientslotnum = 0;
    private int serverslotnum = 0;

    public FilteredKeyManager()
    {
    }

    /**
     * Method called by WebSphere Application Server runtime to set the custom
     * properties.
     * 
     * @param java.util.Properties - custom props
     */
    public void setCustomProperties(java.util.Properties customProps)
    {
        props = customProps;
    }

    private java.util.Properties getCustomProperties()
    {
        return props;
    }

    /**
     * Method called by WebSphere Application Server runtime to set the SSL
     * configuration properties being used for this connection.
     * 
     * @param java.util.Properties - contains a property for the SSL configuration.
     */
    public void setSSLConfig(java.util.Properties config)
    {
        sslConfig = config;                                   
    }

    private java.util.Properties getSSLConfig()
    {
        return sslConfig;
    }

    /**
     * Method called by WebSphere Application Server runtime to set the default
     * X509KeyManager created by the IbmX509 KeyManagerFactory using the KeyStore
     * information present in this SSL configuration.  This allows some delegation
     * to the default IbmX509 KeyManager to occur.
     * 
     * @param javax.net.ssl.KeyManager defaultX509KeyManager - default key manager for IbmX509
     */
    public void setDefaultX509KeyManager(javax.net.ssl.X509KeyManager defaultX509KeyManager)
    {
        km = defaultX509KeyManager;
    }

    public javax.net.ssl.X509KeyManager getDefaultX509KeyManager()
    {
        return km;
    }

    /**
     * Method called by WebSphere Application Server runtime to set the SSL
     * KeyStore used for this connection.
     * 
     * @param java.security.KeyStore - the KeyStore currently configured
     */
    public void setKeyStore(java.security.KeyStore keyStore)
    {
        ks = keyStore;
    }

    public java.security.KeyStore getKeyStore()
    {
        return ks;
    }

 /**
     * Method called by custom code to set the server alias.
     * 
     * @param String - the server alias to use
     */
    public void setKeyStoreServerAlias(String alias)
    {
        serverAlias = alias;
    }

    private String getKeyStoreServerAlias()
    {
        return serverAlias;
    }

    /**
     * Method called by custom code to set the client alias.
     * 
     * @param String - the client alias to use
     */
    public void setKeyStoreClientAlias(String alias)
    {
        clientAlias = alias;
    }
    
    private String getKeyStoreClientAlias()
    {
        return clientAlias;
    }

    /**
     * Method called by custom code to set the client alias and slot (if necessary).
     * 
     * @param String - the client alias to use
     * @param int - the slot to use (for hardware)
     */
    public void setClientAlias(String alias, int slotnum) throws Exception
    {
        if ( !ks.containsAlias(alias))
        {
            throw new IllegalArgumentException ( "Client alias " + alias + "not found in keystore." );
        }
        this.clientAlias = alias;
        this.clientslotnum = slotnum;
    }

    /**
     * Method called by custom code to set the server alias and slot (if necessary).
     * 
     * @param String - the server alias to use
     * @param int - the slot to use (for hardware)
     */
    public void setServerAlias(String alias, int slotnum) throws Exception
    {
        if ( ! ks.containsAlias(alias))
        {
            throw new IllegalArgumentException ( "Server alias " + alias + "not found in keystore." );
        }
        this.serverAlias = alias;
        this.serverslotnum = slotnum;
    }


    /**
     * Method called by JSSE runtime to when an alias is needed for a client
     * connection where a client certificate is required.
     * 
     * @param String keyType
     * @param Principal[] issuers
     * @param java.net.Socket socket (not always present)
     */
    public String chooseClientAlias(String[] keyType, java.security.Principal[]
    issuers, java.net.Socket socket)
    {
        if (clientAlias != null && !clientAlias.equals(""))
        {
            String[] list = km.getClientAliases(keyType[0], issuers);
            String aliases = "";

            if (list != null)
            {
                boolean found=false;
                for (int i=0; i<list.length; i++)
                {
                    aliases += list[i] + " ";
                    if (clientAlias.equalsIgnoreCase(list[i]))
                        found=true;
                }

                if (found)
                {
                    return clientAlias;
                }

            }
        }
        
        // client alias not found, let the default key manager choose.
        String[] keyArray = new String [] {keyType[0]};
        String alias = km.chooseClientAlias(keyArray, issuers, null);
        return alias.toLowerCase();
    }

    /**
     * Method called by JSSE runtime to when an alias is needed for a server
     * connection to provide the server identity.
     * 
     * @param String[] keyType
     * @param Principal[] issuers
     * @param java.net.Socket socket (not always present)
     */
    public String chooseServerAlias(String keyType, java.security.Principal[] 
    issuers, java.net.Socket socket)
    {
        if (serverAlias != null && !serverAlias.equals(""))
        {
            // get the list of aliases in the keystore from the default key manager
            String[] list = km.getServerAliases(keyType, issuers);
            String aliases = "";

            if (list != null)
            {
                boolean found=false;
                for (int i=0; i<list.length; i++)
                {
                    aliases += list[i] + " ";
                    if (serverAlias.equalsIgnoreCase(list[i]))
                        found = true;
                }

                if (found)
                {
                    return serverAlias;
                }
            }
        }

        // specified alias not found, let the default key manager choose.
        String alias = km.chooseServerAlias(keyType, issuers, null);
        return alias.toLowerCase();
    }

    public String[] getClientAliases(String keyType, java.security.Principal[] issuers)
    {
        return km.getClientAliases(keyType, issuers);
    }

    public String[] getServerAliases(String keyType, java.security.Principal[] issuers)
    {
        return km.getServerAliases(keyType, issuers);
    }

    public java.security.PrivateKey getPrivateKey(String s)
    {
        return km.getPrivateKey(s);
    }

    public java.security.cert.X509Certificate[] getCertificateChain(String s)
    {
        return km.getCertificateChain(s);
    }

    public javax.net.ssl.X509KeyManager getX509KeyManager()
    {
        return km;
    }

}

//Last updatedLast updated: Oct 3, 2016 7:15:55 PM CDT
//http://www14.software.ibm.com/webapp/wsbroker/redirect?version=compass&product=was-nd-dist&topic=tsec_sslcreatecuskeymgr
//File name: tsec_sslcreatecuskeymgr.html
//
//v
//s
//p
//z