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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

/**
 * Vault not configured (no address): the credentials are logged as empty and no login to Vault is attempted
 */
public class VaultConfigSourceNotConfiguredTest
{
    private static Object call( String strMethod, Class<?> type, Object value ) throws Exception
    {
        Method method = VaultConfigSource.class.getDeclaredMethod( strMethod, type );
        method.setAccessible( true );
        return method.invoke( null, value );
    }

    @Test
    public void testMaskedCredentials( ) throws Exception
    {
        assertEquals( "empty", call( "mask", String.class, null ) );
        assertEquals( "empty", call( "mask", String.class, "" ) );
        assertEquals( "xxxxxxxx", call( "mask", String.class, "s.token" ) );
    }

    @Test
    public void testNoAddressNoVault( ) throws Exception
    {
        assertFalse( (Boolean) call( "isConfigured", String.class, null ) );
        assertFalse( (Boolean) call( "isConfigured", String.class, "" ) );
        assertTrue( (Boolean) call( "isConfigured", String.class, "http://vault:8200" ) );
        assertTrue( new VaultConfigSource( ).getPropertyNames( ).isEmpty( ) );
    }
}
