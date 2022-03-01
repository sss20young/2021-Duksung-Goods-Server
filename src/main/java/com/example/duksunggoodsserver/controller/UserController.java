package com.example.duksunggoodsserver.controller;

import com.example.duksunggoodsserver.config.responseEntity.ResponseData;
import com.example.duksunggoodsserver.model.dto.request.UserLoginRequestDto;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.duksunggoodsserver.model.dto.request.UserRequestDto;
import com.example.duksunggoodsserver.model.dto.response.UserResponseDto;
import com.example.duksunggoodsserver.model.entity.User;
import com.example.duksunggoodsserver.service.UserService;
import io.swagger.annotations.*;
import org.modelmapper.ModelMapper;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    @PostMapping("/signin")
    @ApiOperation(value = "${UserController.signIn}")
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 422, message = "Invalid email/password supplied")})
    public ResponseEntity login(@RequestBody UserLoginRequestDto user) {
        String jwtToken = userService.signIn(user.getEmail(), user.getPassword());
        ResponseData responseData = ResponseData.builder()
                .data(jwtToken)
                .build();
        return ResponseEntity.ok().body(responseData);
    }

    @PostMapping("/signup")
    @ApiOperation(value = "${UserController.signUp}")
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 422, message = "Username is already in use")})
    public ResponseEntity signUp(@ApiParam("Signup User") @RequestBody UserRequestDto user) {
        String jwtToken = userService.signUp(modelMapper.map(user, User.class));
        ResponseData responseData = ResponseData.builder()
                .data(jwtToken)
                .build();
        return ResponseEntity.ok().body(responseData);
    }

    @GetMapping(value = "/me")
    @ApiOperation(value = "${UserController.me}", response = UserResponseDto.class, authorizations = { @Authorization(value="apiKey") })
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public ResponseEntity whoami(HttpServletRequest req) {
        UserResponseDto userResponseDto = modelMapper.map(userService.getCurrentUser(req), UserResponseDto.class);
        ResponseData responseData = ResponseData.builder()
                .data(userResponseDto)
                .build();
        return ResponseEntity.ok().body(responseData);
    }

    @GetMapping("/refresh")
    public ResponseEntity refresh(HttpServletRequest req) {
        String refreshToken = userService.refresh(req.getRemoteUser());
        ResponseData responseData = ResponseData.builder()
                .data(refreshToken)
                .build();
        return ResponseEntity.ok().body(responseData);
    }

}