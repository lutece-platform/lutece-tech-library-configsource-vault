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

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

/**
 * The Class Configuration.
 */
public class Configuration
{

    /** The config. */
    private Config config = ConfigProviderResolver.instance( ).getBuilder( ).addDefaultSources( ).build( );

    /** The vault host. */
    private String vaultHost = getConfigValue( "configsource.vault.host", "" );

    /** The vault port. */
    private int vaultPort = Integer.valueOf( getConfigValue( "configsource.vault.port", "8500" ) );

    /** The token. */
    private String token = getConfigValue( "configsource.vault.token", null );

    /** The vault properties path. */
    private String vaultPropertiesPath = getConfigValue( "configsource.vault.propertiesPath", null );

    /**
     * Gets the vault properties path.
     *
     * @return the vault properties path
     */
    public String getVaultPropertiesPath( )
    {
        return vaultPropertiesPath;
    }

    /**
     * Gets the token.
     *
     * @return the token
     */
    public String getToken( )
    {
        return token;
    }

    /**
     * Gets the vault host.
     *
     * @return the vault host
     */
    public String getVaultHost( )
    {
        return vaultHost;
    }

    /**
     * Gets the vauly port.
     *
     * @return the vauly port
     */
    public int getVaulyPort( )
    {
        return vaultPort;
    }

    /**
     * Gets the config value.
     *
     * @param key
     *            the key
     * @param defaultValue
     *            the default value
     * @return the config value
     */
    private String getConfigValue( String key, String defaultValue )
    {
        return config.getOptionalValue( key, String.class ).orElse( defaultValue );
    }

}
