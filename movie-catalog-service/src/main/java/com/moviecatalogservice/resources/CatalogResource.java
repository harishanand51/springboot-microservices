package com.moviecatalogservice.resources;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.moviecatalogservice.models.CatalogItem;
import com.moviecatalogservice.models.Movie;
import com.moviecatalogservice.models.UserRating;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;

@RestController
@RequestMapping("/catalog")
public class CatalogResource {
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	MovieInfo movieInfo;
	
	@Autowired
	UserRatingInfo userRatingInfo;
	
	@RequestMapping("/{userId}")
	@CircuitBreaker(name = "movieCatalogService", fallbackMethod = "getFallbackCatalog")
	@Bulkhead(name = "movieCatalogService", type = Bulkhead.Type.THREADPOOL)
	public List<CatalogItem> getCatalog(@PathVariable("userId") String userId){
		
		UserRating userRating = userRatingInfo.getUserRating(userId);
		//UserRating userRating = restTemplate.getForObject("http://ratings-data-service/ratingsdata/user/"+userId, UserRating.class);
		
		return userRating.getRatings().stream()
				.map(rating -> movieInfo.getCatalogItem(rating))
				.collect(Collectors.toList());
		/*return userRating.getRatings().stream()
				.map(rating -> {
					Movie movie = restTemplate.getForObject("http://movie-info-service/movies/"+rating.getMovieId(), Movie.class);
					return new CatalogItem(movie.getName(),movie.getDescription(), rating.getRating());
				})
				.collect(Collectors.toList());*/
	}
	
		public List<CatalogItem> getFallbackCatalog(@PathVariable("userId") String userId, Throwable t) {
			return List.of(new CatalogItem("No Movie Available", "Fallback Response", 0));
		}

}
