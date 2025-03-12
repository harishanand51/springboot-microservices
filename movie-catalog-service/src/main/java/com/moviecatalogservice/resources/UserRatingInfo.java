package com.moviecatalogservice.resources;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import com.moviecatalogservice.models.Rating;
import com.moviecatalogservice.models.UserRating;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class UserRatingInfo {
	
	@Autowired
	private RestTemplate restTemplate;
	
	@CircuitBreaker(name = "userRatingService", fallbackMethod = "getFallbackUserRating")
	public UserRating getUserRating(String userId) {
		
		return restTemplate.getForObject("http://ratings-data-service/ratingsdata/user/"+userId, UserRating.class);
	}
	
	public UserRating getFallbackUserRating(String userId, Throwable t) {
		
		UserRating userRating = new UserRating();
		userRating.setUserId(userId);
		userRating.setRatings(Arrays.asList(
				   new Rating("Fallback Movie",0)
				));
		
		return userRating;
		
	}
	
	

}
