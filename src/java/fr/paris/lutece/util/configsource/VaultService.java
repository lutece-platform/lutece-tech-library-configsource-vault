package fr.paris.lutece.util.configsource;

import java.util.List;
import java.util.Map;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import com.bettercloud.vault.response.AuthResponse;


/**
 * The Class VaultService.
 */
public class VaultService {

    
    /** The vault. */
    private Vault _vault;

    /**
     * Instantiates a new vault service.
     *
     * @param strAdress the str adress
     * @param strVaultToken the str vault token
     * @throws VaultException the vault exception
     */
    public VaultService(String strAdress, String strVaultToken,String strRoleId,String strSecretId)throws VaultException {
    	
    	if(strVaultToken==null)
    	{
    	  String strRootToken=getRootToken(strAdress, strRoleId, strSecretId);
    	  _vault=initDriver(strAdress, strRootToken);
    	}else
    	{
    	  _vault=initDriver(strAdress, strVaultToken);
    	}

    }

    
	 /**
 	 * Gets the listy of secrets sub path.
 	 *
 	 * @param strSecretPath the str secret path
 	 * @return the all secrets key by path
 	 * @throws VaultException the vault exception
 	 */

    public List<String> getSecretsSubPath(String strSecretPath) throws VaultException{

            return _vault.logical()
                    .list(strSecretPath).getListData();

   }

    
 
    /**
     * Gets the secret value.
     *
     * @param strSecretPath the str secret path
     * @param secretKey the secret key
     * @return the secret value
     * @throws VaultException the vault exception
     */
    public String getSecretValue(String strSecretPath,String secretKey)throws VaultException  {

             String secretKV = _vault.logical()
                    .read( strSecretPath+ "/" + secretKey)
                    .getData()
                    .get(secretKey);
            return secretKV;

        
    }


    
    
    
    
    /**
 	 * Gets the all secrets key by path.
 	 *
 	 * @param strSecretPath the str secret path
 	 * @return the all secrets key by path
 	 * @throws VaultException the vault exception
 	 */

    public Map<String,String> getAllSecretsByPath(String strSecretPath) throws VaultException{
      
            return _vault.logical()
                    .read( strSecretPath)
                    .getData();

   }

    

    
    /**
     * Inits the driver.
     *
     * @param strAdress the str adress
     * @param strRootToken the str root token
     * @return the vault
     * @throws VaultException the vault exception
     */
    private  Vault initDriver(String strAdress, String strRootToken) throws VaultException {

            VaultConfig config = new VaultConfig()
                    .address(strAdress)
                    .token(strRootToken)
                    .engineVersion(1)
                    .build();

            return new Vault(config);

  
    }
    
    
    private String getRootToken(String strAdress,String strRoleId,String strSecretId) throws VaultException
    {
    	
    	
    	 VaultConfig config = new VaultConfig()
                 .address(strAdress)
                 .build();
    	 
    	 Vault vault=new Vault(config);
    	 
    	final AuthResponse response=  vault.auth().loginByAppRole("approle", strRoleId, strSecretId);
    	 
    	 
    	 
    	
    	return response.getAuthClientToken();
    
    }
}



