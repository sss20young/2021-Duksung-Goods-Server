package com.example.duksunggoodsserver.controller;

import com.example.duksunggoodsserver.config.responseEntity.ResponseData;
import com.example.duksunggoodsserver.model.dto.request.UserLoginRequestDto;
import com.example.duksunggoodsserver.model.dto.request.UserRequestDto;
import com.example.duksunggoodsserver.model.dto.response.UserResponseDto;
import com.example.duksunggoodsserver.model.entity.User;
import com.example.duksunggoodsserver.service.UserService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    @PostMapping("/login")
    @ApiOperation(value = "로그인")
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
    @ApiOperation(value = "회원가입")
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 422, message = "Username is already in use")})
    public ResponseEntity signUp(@ApiParam("Signup User") @RequestBody UserRequestDto user) throws Exception {
        String jwtToken = userService.signUp(modelMapper.map(user, User.class));
        ResponseData responseData = ResponseData.builder()
                .data(jwtToken)
                .build();
        return ResponseEntity.ok().body(responseData);
    }

    @GetMapping(value = "/me")
    @ApiOperation(value = "사용자 정보 조회", response = UserResponseDto.class, authorizations = { @Authorization(value="apiKey") })
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public ResponseEntity whoami(HttpServletRequest req) {
        UserResponseDto userResponseDto = modelMapper.map(userService.getCurrentUser(req).get(), UserResponseDto.class);
        ResponseData responseData = ResponseData.builder()
                .data(userResponseDto)
                .build();
        return ResponseEntity.ok().body(responseData);
    }

    @GetMapping("/refresh")
    @ApiOperation(value = "토큰 갱신")
    public ResponseEntity refresh(HttpServletRequest req) {
        String refreshToken = userService.refresh(req.getRemoteUser());
        ResponseData responseData = ResponseData.builder()
                .data(refreshToken)
                .build();
        return ResponseEntity.ok().body(responseData);
    }
}