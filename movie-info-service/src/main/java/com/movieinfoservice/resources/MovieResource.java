package com.movieinfoservice.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.movieinfoservice.models.Movie;
import com.movieinfoservice.models.MovieSummary;

@RestController
@RequestMapping("/movies")
public class MovieResource {
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${api.key}")
	private String apikey;
	
	@RequestMapping("/{movieId}")
	public Movie getMovieInfo(@PathVariable("movieId") String movieId) {
		
		//return new Movie(movieId, "name for ID"+movieId);
		try {
		
		MovieSummary movieSummary = restTemplate.getForObject("https://api.themoviedb.org/3/movie/" +movieId+ "?api_key="+apikey, MovieSummary.class);
		
		return new Movie(movieId, movieSummary.getTitle(), movieSummary.getOverview());
		} 
		catch (HttpClientErrorException e) {
	        System.err.println("Error fetching movie details: " + e.getMessage());
	        return new Movie(movieId, "Not Found", "Movie details could not be retrieved");
	    }
	}

}
