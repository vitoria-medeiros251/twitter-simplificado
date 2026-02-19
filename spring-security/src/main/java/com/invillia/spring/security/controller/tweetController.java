package com.invillia.spring.security.controller;

import com.invillia.spring.security.controller.dtos.CreateTweetDto;
import com.invillia.spring.security.controller.dtos.FeedDto;
import com.invillia.spring.security.controller.dtos.FeedItemDto;
import com.invillia.spring.security.domain.RoleEnum;
import com.invillia.spring.security.domain.Tweet;
import com.invillia.spring.security.repositories.TweetRepository;
import com.invillia.spring.security.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("tweets")
public class tweetController {
    @Autowired
    private TweetRepository  tweetRepository;

    @Autowired
    private UserRepository userRepository;



    @PostMapping
    public ResponseEntity<Void>createTweet(@RequestBody CreateTweetDto dto ,  JwtAuthenticationToken token){
        var user = userRepository.findById(UUID.fromString(token.getName()));
        var tweet = new Tweet();
        tweet.setUser(user.get());
        tweet.setContent(dto.content());

        tweetRepository.save(tweet);

        return ResponseEntity.ok().build();


    }

    @GetMapping("/feed")
    public ResponseEntity<FeedDto> feed(@RequestParam(value="page", defaultValue = "0") int page,
                                                     @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
       var tweets = tweetRepository.findAll(PageRequest.of(page,pageSize, Sort.Direction.DESC,"creantionTimestamp" ))
               .map(tweet ->
                       new FeedItemDto(tweet.getTweetId()
                               ,tweet.getContent(),
                               tweet.getUser().getUsername()));
        return ResponseEntity.ok(new FeedDto(tweets.getContent(), page, pageSize, tweets.getTotalPages(), tweets.getTotalElements()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTweet(@PathVariable("id") Long tweetId, JwtAuthenticationToken token){
        var user = userRepository.findById(UUID.fromString(token.getName()));
        var tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var isAdmin = user.get().getRoles() // Verifica se o usuário tem role ADMIN
                .stream().anyMatch(role -> role.getName().equals(RoleEnum.ADMIN));

        var userId = UUID.fromString(token.getName());
        var isOwner = tweet.getUser().getUserId().equals(userId); //Compara se o ID do dono do tweet é igual ao ID do usuário logado usando equals para comparar se é igual ao id do usuario logado
        
        if(!(isAdmin && !isOwner) && !isOwner) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } // se o usuario nao for admin e nao for o dono do tweet retorna 403 forbidden
        
        tweetRepository.deleteById(tweetId);
        return ResponseEntity.ok().build();
    }



}
