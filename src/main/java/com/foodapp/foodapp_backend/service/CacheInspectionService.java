package com.foodapp.foodapp_backend.service;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.cache.Cache;


@Service
public class CacheInspectionService {
	
	@Autowired
	private CacheManager cacheManager;
	
	public void getContent(String name) {
		
		Cache cache = cacheManager.getCache(name);
		
		if(cache != null) {
			System.out.println(Objects.requireNonNull(cache.getNativeCache().toString()));
		}

	}
}
	
