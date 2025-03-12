package com.moviecatalogservice.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.moviecatalogservice.models.CatalogItem;
import com.moviecatalogservice.models.Movie;
import com.moviecatalogservice.models.Rating;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class MovieInfo {
	
	@Autowired
	private RestTemplate restTemplate;
	
	@CircuitBreaker(name = "movieCatalogService", fallbackMethod = "getFallbackCatalogItem")
	public CatalogItem getCatalogItem(Rating rating) {
		
		Movie movie = restTemplate.getForObject("http://movie-info-service/movies/"+rating.getMovieId(), Movie.class);
		
		String movieUrl = "http://movie-info-service/movies/" + rating.getMovieId();
	    System.out.println("API Call: " + movieUrl);
		
		
		return new CatalogItem(movie.getName(),movie.getDescription(),rating.getRating());
	}
	
	public CatalogItem getFallbackCatalogItem(Rating rating, Throwable t) {
		
		return new CatalogItem("Movie name not found","Fallback response from movieInfo", rating.getRating());
	}

}
