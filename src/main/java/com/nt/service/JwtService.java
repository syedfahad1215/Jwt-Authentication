package com.nt.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.nt.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Service
public class JwtService {
	
	@Value("${jwt.secret}")
	private String secretKey;
	
	private static Logger logger = LoggerFactory.getLogger(JwtService.class);
	
	
	public String generateToken(User user) {
		
		/*
		   This method is use for creating a new key for user 
		 */
		
		 Map<String, Object> claims = new HashMap<>();
		 
		 String token = Jwts
					.builder()
					.addClaims(claims)
					.setSubject(user.getUsername())
					.setIssuer("sf")
					.setIssuedAt(new Date(System.currentTimeMillis()))
					.setExpiration(new Date(System.currentTimeMillis()+ 60*10*1000))
					.signWith(generateKey(), SignatureAlgorithm.HS384)
	                .compact();
		 
		 logger.info("Secret Key length= "+secretKey.getBytes(StandardCharsets.UTF_8).length);
		 
		 logger.info("The token is generated, where token is= "+token);
		 
		return token;
	}
	
	private SecretKey generateKey() {
		/*
		   This method is use for creating a new key for user 
		 */
        byte[] decode
                = Decoders.BASE64.decode(this.secretKey);

        return Keys.hmacShaKeyFor(decode);
    }

	public String extractUserName(String token) {
		try {
	        logger.info("Attempting to extract username from token.");
	        String username = extractClaims(token, Claims::getSubject);
	        logger.info("Username extracted successfully.");
	        return username;
	    } catch (ExpiredJwtException e) {
	        logger.error("Token expired: {}", e.getMessage());
	        // Handle token expiration case, e.g., return null or rethrow as custom exception
	        return null;
	    } catch (UnsupportedJwtException e) {
	        logger.error("Unsupported JWT token: {}", e.getMessage());
	        throw e;
	    } catch (MalformedJwtException e) {
	        logger.error("Malformed JWT token: {}", e.getMessage());
	        throw e;
	    } catch (SignatureException e) {
	        logger.error("Invalid JWT signature: {}", e.getMessage());
	        throw e;
	    } catch (IllegalArgumentException e) {
	        logger.error("Illegal argument token: {}", e.getMessage());
	        throw e;
	    }  catch (Exception e) {
	        logger.error("Invalid JWT token: {} | Exception", e.getMessage());
	        throw e;  // or handle it appropriately
	    }
    }

    public <T> T extractClaims(String token, Function<Claims,T> claimResolver) {
        Claims claims = extractClaims(token);
        logger.info("Extracting the claims using extractClaims(token), here claims= "+claims);
        return claimResolver.apply(claims);
    }

    private Claims extractClaims(String token) {
    	try {
            return Jwts.parserBuilder()
                       .setSigningKey(generateKey())
                       .build()
                       .parseClaimsJws(token)
                       .getBody();
        } catch (JwtException e) {
            logger.error("Error parsing claims from token: " + e.getMessage());
            throw e;  // Handle or rethrow as appropriate
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
    	try {
            final String userName = extractUserName(token);
            boolean isValid = userName != null && userName.equals(userDetails.getUsername()) && !isTokenExpired(token);
            logger.info("Checking whether the token is valid: " + isValid);
            return isValid;
        } catch (Exception e) {
            logger.error("Error validating token: " + e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
    	
    	try {
            boolean isExpired = extractExpiration(token).before(new Date());
            logger.info("Checking whether the token is expired: " + isExpired);
            return isExpired;
        } catch (JwtException e) {
            logger.error("Error checking token expiration: " + e.getMessage());
            return true;  // Consider expired if exception occurs
        }	
    }

    private Date extractExpiration(String token) {
    	try {
            Date expirationDate = extractClaims(token, Claims::getExpiration);
            logger.info("Extracted Expiration Date: " + expirationDate);
            return expirationDate;
        } catch (JwtException e) {
            logger.error("Error extracting expiration date: " + e.getMessage());
            return null;  // Handle as appropriate
        }
    }
	
	
/*
	public String getSecretKey()
	{
		return secretKey = "3acf6f6af11226c010ca335da8177b1c16245ff9cbdca1dc6ad80c01c1809f0c";
	}
*/
}
