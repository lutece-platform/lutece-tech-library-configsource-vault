/*
 * Copyright (c) 2002-2023, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.util.configsource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.config.spi.ConfigSource;

import com.bettercloud.vault.VaultException;



/**
 * The Class VaultConfigSource.
 */
public class VaultConfigSource implements ConfigSource
{

    /** The configuration. */
    private Configuration _configuration;

    /** The vault properties. */
    private Map<String, String> _vaultProperties = new HashMap<>( );

    private static Logger _logger = LogManager.getLogger( VaultConfigSource.class );
    
    /**
     * Instantiates a new vault config source.
     * @throws VaultException 
     */
    public VaultConfigSource( )
    {
        _configuration = new Configuration( );
        
        _logger.info("vault- init Config Source");
        _logger.info("vault- Address : {} ",_configuration.getVaultAddress( ) );
        _logger.info("vault- Token : {}" ,_configuration.getToken( )!="null"? "xxxxxxxx" : "empty");
        _logger.info("vault- Role Id :{}  " , _configuration.getRoleId( )!="null"? "xxxxxxxx" : "empty");
        _logger.info("vault- Secret Id :{} " ,  _configuration.getSecretId( )!="null"? "xxxxxxxx" : "empty");
        _logger.info("vault- Properties Path : {}" , _configuration.getVaultPropertiesPath( ) );
        
        VaultService vaultService;
		try {
			vaultService = new VaultService(_configuration.getVaultAddress(), _configuration.getToken(),_configuration.getRoleId(),_configuration.getSecretId());
			
	         
	        List<String> listSubPath=vaultService.getSecretsSubPath(_configuration.getVaultPropertiesPath( ));
			
	        if(listSubPath!=null && listSubPath.size()>0)
	        {
	          	               
	            listSubPath.forEach( subPath -> {
	                try {
	                    _logger.info("vault- Subpath found : {}" , _configuration.getVaultPropertiesPath()+"/"+subPath); 
	                    _vaultProperties.putAll(vaultService.getAllSecretsByPath(_configuration.getVaultPropertiesPath()+"/"+subPath));
	                } catch (VaultException e) {
	                    
	                    _logger.error("vault- errror getting properties for subpath: {}", _configuration.getVaultPropertiesPath()+"/"+subPath  , e.getMessage(),e);
	                    throw new RuntimeException(e);
	                }
	            });
	                
	        }
	        else
	        {	
	            try {
	            _vaultProperties.putAll(vaultService.getAllSecretsByPath(_configuration.getVaultPropertiesPath()));
	            } catch (VaultException e) {
	                _logger.error("vault- errror getting properties for path: {}", _configuration.getVaultPropertiesPath()  , e.getMessage(),e);
	                throw e;
	            }
	        }
	
	        
	        
	        _logger.info( "vault- number of vault keys found {} ",_vaultProperties.size());
	        for ( String key : _vaultProperties.keySet( ) )
	        {
	    	   _logger.debug( " vault- found  keys: {}", key  );
	        }
		} catch (VaultException e) {
			_logger.error("Failed to initialize HashiCorp Vault Config Source. It will be ignored. Error details: ", e);		}
    }

    /**
     * Gets the ordinal.
     *
     * @return the ordinal
     */
    @Override
    public int getOrdinal( )
    {
        return 500;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    @Override
    public String getName( )
    {

        return VaultConfigSource.class.getSimpleName( );
    }

    /**
     * Gets the property names.
     *
     * @return the property names
     */
    @Override
    public Set<String> getPropertyNames( )
    {

        return _vaultProperties.keySet( );

    }

    /**
     * Gets the value.
     *
     * @param strProperty
     *            the str property
     * @return the value
     */
    @Override
    public String getValue( String strProperty )
    {
        return _vaultProperties.get( strProperty );
    }

}
