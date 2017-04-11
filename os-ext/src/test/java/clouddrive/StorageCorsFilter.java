package objectstack.repository.rest;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

public class StorageCorsFilter extends CorsFilter {

	public StorageCorsFilter() {
		super(configurationSource());
	}

	private static UrlBasedCorsConfigurationSource configurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin("http://domain1.com");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}
}