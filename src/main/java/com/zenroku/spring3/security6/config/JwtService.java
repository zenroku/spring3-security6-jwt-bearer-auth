package com.zenroku.spring3.security6.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
public class JwtService {
    // https://allkeysgenerator.com/Random/Security-Encryption-Key-Generator.aspx
    // jwt minimal requirement is 256-bit, then hex it
    // remember if this app in production make sure its on env file
    private final static String KEYS = "25442A472D4B6150645367566B59703373357638792F423F4528482B4D625165";

    // jwt token if its decoded are split by 3 section : header, payload, and signature
    // here is the example of header (the type of encrypt alg) :
    // {
    //    "alg": "HS256",
    //    "typ": "JWT"
    // }

    // here is the example of payload (basic user info) :
    // {
    //    "sub": "1234567890",
    //    "name": "John Doe",
    //    "iat": 1516239022
    // }

    // here is the example of signature (combining both plus your app secret key):
    // HMACSHA256(
    //  base64UrlEncode(header) + "." +
    //  base64UrlEncode(payload),
    //  your-256-bit-secret
    // )


    public String extractUsername(String token) {
        return extractClaim(token,Claims::getSubject);
    }

    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(),userDetails);
    }

    public String generateToken(
            Map<String, Object> claims,
            UserDetails userDetails
    ){
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                // its mean 1 day
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token,Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims,T> claimResolver){
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey(){
        byte[] keyBytes = Decoders.BASE64.decode(KEYS);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
