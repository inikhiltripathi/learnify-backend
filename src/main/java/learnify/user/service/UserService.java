package learnify.user.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import learnify.user.core.Role;
import learnify.user.core.UserEntity;
import learnify.user.core.UserRepo;
import learnify.user.dto.ApiResponse;
import learnify.user.dto.ForgetPassword;
import learnify.user.dto.LoginRequest;
import learnify.user.dto.RegisterRequest;
import learnify.user.dto.ResetPassword;
import learnify.user.dto.UpdateRole;
import learnify.user.dto.UserData;
import learnify.user.dto.UserSummary;
import learnify.user.dto.VerifyEmailRquest;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final EmailService mail;

    // ** Login Code :-
    public ApiResponse<UserSummary> doLogin(LoginRequest dto) {

        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));
        System.out.println(authentication.toString());

        UserEntity userEntity = (UserEntity) authentication.getPrincipal();
        String jwtToken = jwtService.generateJwtToken(userEntity);

        UserSummary data = UserSummary.map(userEntity, jwtToken);

        return ApiResponse.success("Login Successful", data);
    }

    // ** Register Code :-
    public ApiResponse<Void> doRegister(RegisterRequest dto) {

        String otp = mail.generateOtp();
        UserEntity user = UserEntity.builder().name(dto.getName()).email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .otp(otp).otpexpiry(LocalDateTime.now().plusMinutes(5)).build();

        UserEntity savedEntity = userRepo.save(user);

        mail.sendOtpToEmail(savedEntity.getEmail(), otp);

        return ApiResponse.message("PENDING", "Otp sent to your email for verification.");

    }

    // ** Verify OTP Code :-
    public ApiResponse<UserSummary> emailVerification(VerifyEmailRquest dto) {
        Optional<UserEntity> optionalEntity = userRepo.findByEmail(dto.getEmail());

        if (optionalEntity.isEmpty()) {
            return ApiResponse.failure("Invalid request or OTP.", "USER_NOT_FOUND");
        }

        UserEntity userEntity = optionalEntity.get();

        if (userEntity.getOtp() == null || !userEntity.getOtp().equals(dto.getOtp())) {
            return ApiResponse.failure("Invalid OTP entered.", "INVALID_OTP");
        }
        if (userEntity.getOtpexpiry().isBefore(LocalDateTime.now())) {
            return ApiResponse.failure("OTP has expired. Please request a new one.", "OTP_EXPIRED");
        }

        userEntity.setOtp(null);
        userEntity.setOtpexpiry(null);
        userEntity.setEnabled(true);
        UserEntity savedEntity = userRepo.save(userEntity);
        String jwtToken = jwtService.generateJwtToken(savedEntity);

        UserSummary data = UserSummary.map(savedEntity, jwtToken);

        return ApiResponse.success("User created successfully", data);

    }

    // ** Forget Password Code :-
    public ApiResponse<Void> otpForForgotPassword(ForgetPassword dto) {

        String otp = mail.generateOtp();
        Optional<UserEntity> optionalEntity = userRepo.findByEmail(dto.getEmail());

        if (optionalEntity.isEmpty()) {
            return ApiResponse.failure("If this email is regestered you will receive an OTP", 
            "USER_NOT_FOUND");
        }

        UserEntity userEntity = optionalEntity.get();
        userEntity.setOtp(otp);
        userEntity.setOtpexpiry(LocalDateTime.now().plusMinutes(5));
        userRepo.save(userEntity);

        mail.sendOtpToEmail(userEntity.getEmail(), otp);

        return ApiResponse.message("SUCCESS",
                "If this email is regestered you will receive an OTP");
    }

    // ** Reset Password Code :-
    public ApiResponse<Void> setNewPassword(ResetPassword dto) {
        Optional<UserEntity> optionalEntity = userRepo.findByEmail(dto.getEmail());

        if (optionalEntity.isEmpty()) {
            return ApiResponse.failure("Invalid request or OTP.", "USER_NOT_FOUND");
        }

        UserEntity userEntity = optionalEntity.get();

        if (userEntity.getOtp() == null || !userEntity.getOtp().equals(dto.getOtp())) {
            return ApiResponse.failure("Invalid OTP entered.", "INVALID_OTP");
        }
        if (userEntity.getOtpexpiry().isBefore(LocalDateTime.now())) {
            return ApiResponse.failure("OTP has expired. Please request a new one.", "OTP_EXPIRED");
        }

        userEntity.setPassword(passwordEncoder.encode(dto.getPassword()));
        userEntity.setOtp(null);
        userEntity.setOtpexpiry(null);
        userRepo.save(userEntity);

        return ApiResponse.message("SUCCESS", "Password reset successful.");
    }

    // ** Get all users
    public ApiResponse<List<UserData>> getAllUsers() {
        List<UserData> userList = userRepo.findAll()
                .stream()
                .map(e -> UserData.map(e))
                .toList();
        return ApiResponse.success("Users retrieved successfully", userList);
    }

    // ** Get users by role
    public ApiResponse<List<UserData>> getUsersByRole(Role role) {
        List<UserData> userList = userRepo.findByRole(role)
                .stream()
                .map(e -> UserData.map(e))
                .toList();
        return ApiResponse.success("Users retrieved successfully", userList);
    }


    // ** Update User Role
    public ApiResponse<UserData> updateUserRole(UpdateRole dto) {
        Optional<UserEntity> optionalEntity = userRepo.findByEmail(dto.getEmail());

        if (optionalEntity.isEmpty()) {
            return ApiResponse.failure("User not found with the provided email.", "USER_NOT_FOUND");
        }

        UserEntity userEntity = optionalEntity.get();
        userEntity.setRole(dto.getRole());
        UserEntity updatedUser = userRepo.save(userEntity);

        UserData responseData = UserData.map(updatedUser);
        return ApiResponse.success("User role updated successfully", responseData);
    }

}
