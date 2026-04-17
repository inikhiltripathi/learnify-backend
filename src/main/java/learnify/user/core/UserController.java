package learnify.user.core;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import learnify.user.dto.ApiResponse;
import learnify.user.dto.ForgetPassword;
import learnify.user.dto.LoginRequest;
import learnify.user.dto.RegisterRequest;
import learnify.user.dto.ResetPassword;
import learnify.user.dto.ResponseData;
import learnify.user.dto.UserData;
import learnify.user.dto.VerifyEmailRquest;
import learnify.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@AllArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping("/")
    public String messageRequest() {
        return "Welcome to Spring boot Application";
    }

    @GetMapping("/auth/test")
    public String getMethodName() {
        return new String("Hey There! Testing Successful");
    }

    // For Login User will send EMAIL and PASSWORD
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<ResponseData>> login(@Valid @RequestBody LoginRequest dto) {
        ApiResponse<ResponseData> loggedIn = service.doLogin(dto);
        return ResponseEntity.ok(loggedIn);
    }

    // For register user will send : NAME, EMAIL and PASSWORD
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest dto) {
        ApiResponse<Void> registered = service.doRegister(dto);
        return ResponseEntity.ok(registered);
    }

    @PostMapping("verify-email")
    public ResponseEntity<ApiResponse<ResponseData>> verifyEmail(@RequestBody VerifyEmailRquest dto) {
        ApiResponse<ResponseData> verified = service.emailVerification(dto);
        return new ResponseEntity<ApiResponse<ResponseData>>(verified, HttpStatus.CREATED);
    }

    // For forgetting password, User will send EMAIL
    @PostMapping("/forget-password")
    public ResponseEntity<ApiResponse<ForgetPassword>> forgetPassword(@Valid @RequestBody ForgetPassword dto) {
        ApiResponse<ForgetPassword> response = service.otpForForgotPassword(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody ResetPassword dto) {

        ApiResponse<Void> response = service.setNewPassword(dto);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/users")
    public ResponseEntity<ApiResponse<List<UserData>>> getAllUsers() {
        ApiResponse<List<UserData>> response = service.getAllUsers();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/users/teachers")
    public ResponseEntity<ApiResponse<List<UserData>>> getTeachers() {
        ApiResponse<List<UserData>> response = service.getUsersByRole(Role.ROLE_TEACHER);
        return ResponseEntity.ok(response);
    }

    @PutMapping("path/{id}")
    public String putMethodName(@PathVariable String id, @RequestBody String entity) {
        //TODO: process PUT request
        
        return entity;
    }

}
