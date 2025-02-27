package com.moviecatalogservice.resources;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.moviecatalogservice.models.CatalogItem;
import com.moviecatalogservice.models.Movie;
import com.moviecatalogservice.models.Rating;
import com.moviecatalogservice.models.UserRating;

@RestController
@RequestMapping("/catalog")
public class CatalogResource {
	
	@Autowired
	private RestTemplate restTemplate;
	
	@RequestMapping("/{userId}")
	public List<CatalogItem> getCatalog(@PathVariable("userId") String userId){
		
		//return Collections.singletonList(new CatalogItem("Test", "Test Desc", 4));
		
		/*List<Rating> ratingList =Arrays.asList(
                                   new Rating("1234",3),
                                   new Rating("5678",4)
                                  );
		*/
		UserRating userRating = restTemplate.getForObject("http://localhost:8083/ratingsdata/user/"+userId, UserRating.class);
		
		return userRating.getRatings().stream()
				//.map(rating -> new CatalogItem("Name", "Desc", rating.getRating()))
				.map(rating -> {
					Movie movie = restTemplate.getForObject("http://localhost:8082/movies/"+rating.getMovieId(), Movie.class);
					return new CatalogItem(movie.getName(),"Description", rating.getRating());
				})
				.collect(Collectors.toList());
	}

}
