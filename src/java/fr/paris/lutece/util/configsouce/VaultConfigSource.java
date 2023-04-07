package fr.paris.lutece.util.configsouce;
import java.util.Set;

import org.eclipse.microprofile.config.spi.ConfigSource;

public class VaultConfigSource implements ConfigSource{

	@Override
    public int getOrdinal() {
        return 100;
    }
	
	@Override
	public String getName() {

		return VaultConfigSource.class.getSimpleName( );
	}

	@Override
	public Set<String> getPropertyNames() {
		
		//return AppInitPropertiesService.getPropertiesAsMap().keySet();
		return null;
		
	}

	@Override
	public String getValue(String strProperty) {
		
		//return AppInitPropertiesService.getProperty(strProperty);
		return null;
	}

}
