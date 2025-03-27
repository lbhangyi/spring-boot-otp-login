package com.codingwithtashi.springsecurityjwt.controller;

import com.codingwithtashi.springsecurityjwt.annotation.LongRunningExecution;
import com.codingwithtashi.springsecurityjwt.model.AuthRequest;
import com.codingwithtashi.springsecurityjwt.service.OtpService;
import com.codingwithtashi.springsecurityjwt.service.UserDetailsService;
import com.codingwithtashi.springsecurityjwt.util.JwtUtil;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "api/client/auth/")
@Log4j2
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtTokenUtil;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private OtpService otpService;

    @RequestMapping({ "hello" })
    @LongRunningExecution(request = true,response = true)
    public String firstPage() {
        log.info("User has called the api/client/auth/hello");
        return "Hello World";
    }

    @RequestMapping(value = "requestOtp/{phoneNo}", method = RequestMethod.GET)
    @LongRunningExecution(request = true,response = true,maxExecutionTime = 100)
    public Map<String, Object> getOtp(@PathVariable String phoneNo) {
        Map<String, Object> returnMap = new HashMap<>();
        log.info("User has called the api/client/auth/requestOtp/{}", phoneNo);
        try {
            // generate OTP
            String otp = otpService.generateOtp(phoneNo);
            returnMap.put("otp", otp);
            returnMap.put("status", "success");
            returnMap.put("message", "Otp sent successfully");
            log.info("User requestOtp for {} succeeded", phoneNo);
        } catch (Exception e) {
            returnMap.put("status", "failed");
            returnMap.put("message", e.getMessage());
            log.info("User requestOtp failed {} : {} ", phoneNo, e.getMessage());
        }

        return returnMap;
    }

    @RequestMapping(value = "verifyOtp/", method = RequestMethod.POST)
    @LongRunningExecution(request = true,response = true,maxExecutionTime = 200)
    public ResponseEntity<?> verifyOtp(@RequestBody AuthRequest authenticationRequest) {
        Map<String, Object> returnMap = new HashMap<>();
        var phoneNo = authenticationRequest.getPhoneNo();
        log.info("User has called the api/client/auth/verifyOtp/{}", phoneNo);
        try {
            // verify otp
            if (authenticationRequest.getOtp().equals(otpService.getCacheOtp(phoneNo))) {
                String jwtToken = createAuthenticationToken(authenticationRequest);
                returnMap.put("status", "success");
                returnMap.put("message", "Otp verified successfully");
                returnMap.put("jwt", jwtToken);
                otpService.clearOtp(phoneNo);
            } else {
                var msg = "Otp is either expired or incorrect";
                returnMap.put("status", "failed");
                returnMap.put("message", msg);
                log.info("User requestOtp failed {} : {} ", phoneNo, msg);
                return ResponseEntity.badRequest().body(returnMap);
            }

        } catch (Exception e) {
            var msg = e.getMessage();
            returnMap.put("status", "failed");
            returnMap.put("message", msg);
            log.info("User requestOtp failed {} : {} ", phoneNo, msg);
            return ResponseEntity.internalServerError().body(returnMap);
        }

        return ResponseEntity.ok(returnMap);
    }

    // create auth token
    public String createAuthenticationToken(AuthRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getPhoneNo(), ""));
        } catch (BadCredentialsException e) {
            log.error(e);
            throw new Exception("Incorrect username or password", e);
        }
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getPhoneNo());
        return jwtTokenUtil.generateToken(userDetails);
    }
}
