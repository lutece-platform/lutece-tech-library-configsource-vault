package fr.paris.lutece.util.configsource;

import java.util.List;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;



// TODO: Auto-generated Javadoc
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
    public VaultService(String strAdress, String strVaultToken)throws VaultException {
          _vault=initDriver(strAdress, strVaultToken);

    }

    
	 /**
 	 * Gets the all secrets key by path.
 	 *
 	 * @param strSecretPath the str secret path
 	 * @return the all secrets key by path
 	 * @throws VaultException the vault exception
 	 */

    public List<String> getAllSecretsKeyByPath(String strSecretPath) throws VaultException{

            List<String> listAllSecrets = _vault.logical()
                    .list(strSecretPath).getListData();
            return listAllSecrets;

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
                    .build();

            return new Vault(config);

  
    }
}



