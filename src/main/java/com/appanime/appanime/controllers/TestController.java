package com.appanime.appanime.controllers;
import com.appanime.appanime.models.Anime;
import com.appanime.appanime.services.AnimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {

  private RestTemplate restTemplate;
    @Autowired
    AnimeService animeService;



  public TestController(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }
 //LLAMAR A API DE ANIME PUBLICA
 @GetMapping("/getAllAnimes")
 public Object callApi() {
   String apiUrl = "https://api.jikan.moe/v4/top/anime"; // URL de la API pública
   Object  response = restTemplate.getForObject(apiUrl,  Object.class);
   return response;
 }






}
