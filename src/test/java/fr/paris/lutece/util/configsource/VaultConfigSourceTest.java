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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.bettercloud.vault.VaultException;
import com.sun.net.httpserver.HttpServer;

/**
 * Errors answered by Vault while the config source is built
 */
public class VaultConfigSourceTest
{
    private static final String PATH = "secret/lutece";

    private HttpServer _server;

    @BeforeEach
    public void startVault( ) throws IOException
    {
        _server = HttpServer.create( new InetSocketAddress( "127.0.0.1", 0 ), 0 );
        _server.start( );
        System.setProperty( "configsource.vault.address", "http://127.0.0.1:" + _server.getAddress( ).getPort( ) );
        System.setProperty( "configsource.vault.token", "t" );
        System.setProperty( "configsource.vault.propertiesPath", PATH );
    }

    @AfterEach
    public void stopVault( )
    {
        _server.stop( 0 );
        System.clearProperty( "configsource.vault.address" );
        System.clearProperty( "configsource.vault.token" );
        System.clearProperty( "configsource.vault.propertiesPath" );
    }

    private void answer( String strPath, int nStatus, String strBody )
    {
        _server.createContext( strPath, exchange -> {
            byte [ ] body = strBody.getBytes( StandardCharsets.UTF_8 );
            exchange.getResponseHeaders( ).add( "Content-Type", "application/json" );
            exchange.sendResponseHeaders( nStatus, body.length );
            try ( OutputStream out = exchange.getResponseBody( ) )
            {
                out.write( body );
            }
        } );
    }

    /**
     * A server error on one sub-path is ignored like any other Vault error: the config source is built with the other secrets
     */
    @Test
    public void testServerErrorOnASubPath( )
    {
        answer( "/v1/" + PATH + "/a", 200, "{\"data\":{\"bh.vault.key\":\"x\"}}" );
        answer( "/v1/" + PATH + "/b", 503, "{\"errors\":[\"Vault is sealed\"]}" );
        answer( "/v1/" + PATH, 200, "{\"data\":{\"keys\":[\"a\",\"b\"]}}" );

        VaultConfigSource source = assertDoesNotThrow( ( ) -> new VaultConfigSource( ) );
        assertEquals( "x", source.getValue( "bh.vault.key" ) );
    }

    /**
     * A refusal of Vault (4xx) is an error, not an empty list of secrets
     */
    @Test
    public void testForbiddenIsAnError( ) throws VaultException
    {
        answer( "/v1/" + PATH, 403, "{\"errors\":[\"permission denied\"]}" );

        VaultService service = new VaultService( System.getProperty( "configsource.vault.address" ), "t", null, null );
        assertThrows( VaultException.class, ( ) -> service.getSecretsSubPath( PATH ) );
        assertThrows( VaultException.class, ( ) -> service.getAllSecretsByPath( PATH ) );
    }
}
