// ============================================================
// FILE:
// src/main/java/com/itvedant/StudentManagement/controller/StudentProfileController.java
// ============================================================

package com.itvedant.StudentManagement.controller;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itvedant.StudentManagement.model.Students;
import com.itvedant.StudentManagement.model.Users;
import com.itvedant.StudentManagement.reposatory.StudentRepositiry;
import com.itvedant.StudentManagement.reposatory.UserRepository;

@Controller
@RequestMapping("/student/profile")
public class StudentProfileController {

    private static final int TARGET_SIZE = 400;

    private final UserRepository userRepository;
    private final StudentRepositiry studentRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentProfileController(
            UserRepository userRepository,
            StudentRepositiry studentRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }


    // =========================================================
    // UPDATE STUDENT PROFILE
    // =========================================================

    @PostMapping("/update")
    @Transactional
    public String updateProfile(

            @RequestParam("firstName")
            String firstName,

            @RequestParam("lastName")
            String lastName,

            @RequestParam("email")
            String email,

            @RequestParam("phoneNumber")
            String phoneNumber,

            @RequestParam("address")
            String address,

            Authentication authentication,

            RedirectAttributes redirectAttributes) {


        Users user =
                currentUser(authentication);


        Students student =
                studentRepository
                        .findByEmailIgnoreCase(
                                user.getEmail())
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Student profile was not found."));


        // =====================================================
        // CHECK EMAIL
        // =====================================================

        String newEmail =
                email == null
                        ? ""
                        : email.trim();


        if (newEmail.isEmpty()) {

            redirectAttributes
                    .addFlashAttribute(
                            "error",
                            "Email cannot be empty.");

            return "redirect:/student/profile";
        }


        // =====================================================
        // CHECK DUPLICATE EMAIL
        // =====================================================

        if (studentRepository
                .existsByEmailIgnoreCaseAndIdNot(
                        newEmail,
                        student.getId())) {

            redirectAttributes
                    .addFlashAttribute(
                            "error",
                            "Email is already used by another student.");

            return "redirect:/student/profile";
        }


        // =====================================================
        // UPDATE STUDENT
        // =====================================================

        student.setFirstName(
                firstName.trim());

        student.setLastName(
                lastName.trim());

        student.setEmail(
                newEmail);

        student.setPhoneNumber(
                phoneNumber);

        student.setAddress(
                address);


        studentRepository.save(student);


        // =====================================================
        // UPDATE USER
        //
        // IMPORTANT:
        // currentStudent() uses Users.email to find Students.
        // Therefore both emails must remain synchronized.
        // =====================================================

        user.setFullName(
                firstName.trim()
                        + " "
                        + lastName.trim());

        user.setEmail(
                newEmail);

        user.setPhoneNumber(
                phoneNumber);


        userRepository.save(user);


        redirectAttributes
                .addFlashAttribute(
                        "message",
                        "Profile updated successfully.");


        return "redirect:/student/profile";
    }


    // =========================================================
    // CHANGE PASSWORD
    // =========================================================

    @PostMapping("/password")
    public String changePassword(

            @RequestParam("currentPassword")
            String currentPassword,

            @RequestParam("newPassword")
            String newPassword,

            @RequestParam("confirmPassword")
            String confirmPassword,

            Authentication authentication,

            RedirectAttributes redirectAttributes) {


        Users user =
                currentUser(authentication);


        // =====================================================
        // CURRENT PASSWORD
        // =====================================================

        if (!passwordEncoder.matches(
                currentPassword,
                user.getPassword())) {

            redirectAttributes
                    .addFlashAttribute(
                            "error",
                            "Current password is incorrect.");

            return "redirect:/student/profile";
        }


        // =====================================================
        // CONFIRM PASSWORD
        // =====================================================

        if (!newPassword.equals(
                confirmPassword)) {

            redirectAttributes
                    .addFlashAttribute(
                            "error",
                            "New passwords do not match.");

            return "redirect:/student/profile";
        }


        // =====================================================
        // PASSWORD LENGTH
        // =====================================================

        if (newPassword.length() < 6) {

            redirectAttributes
                    .addFlashAttribute(
                            "error",
                            "Password must contain at least 6 characters.");

            return "redirect:/student/profile";
        }


        // =====================================================
        // SAVE PASSWORD
        // =====================================================

        user.setPassword(
                passwordEncoder.encode(
                        newPassword));


        userRepository.save(user);


        redirectAttributes
                .addFlashAttribute(
                        "message",
                        "Password changed successfully.");


        return "redirect:/student/profile";
    }


    // =========================================================
    // UPLOAD PROFILE IMAGE
    // =========================================================

    @PostMapping("/image")
    @ResponseBody
    public ResponseEntity<String> uploadImage(

            @RequestParam("image")
            MultipartFile file,

            Authentication authentication) {


        if (file.isEmpty()) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Please select an image to upload.");
        }


        String contentType =
                file.getContentType();


        if (contentType == null
                || !contentType.startsWith("image/")) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Please upload a valid image file.");
        }


        try {

            byte[] squareJpegBytes =
                    cropToSquareAndResize(
                            file.getBytes(),
                            TARGET_SIZE);


            Users user =
                    currentUser(authentication);


            user.setProfileImage(
                    squareJpegBytes);


            user.setProfileImageContentType(
                    MediaType.IMAGE_JPEG_VALUE);


            userRepository.save(user);


            return ResponseEntity.ok(
                    "Profile image updated successfully.");

        } catch (IOException e) {

            return ResponseEntity
                    .internalServerError()
                    .body(
                            "Could not process the image.");
        }
    }


    // =========================================================
    // GET PROFILE IMAGE
    // =========================================================

    @GetMapping("/image")
    @ResponseBody
    public ResponseEntity<byte[]> getImage(
            Authentication authentication) {


        Users user =
                currentUser(authentication);


        if (user.getProfileImage() == null) {

            return ResponseEntity
                    .notFound()
                    .build();
        }


        MediaType mediaType =
                MediaType.parseMediaType(

                        user.getProfileImageContentType()
                                != null

                                ? user.getProfileImageContentType()

                                : MediaType.IMAGE_JPEG_VALUE
                );


        return ResponseEntity
                .ok()
                .contentType(mediaType)
                .body(
                        user.getProfileImage());
    }


    // =========================================================
    // CURRENT USER
    // =========================================================

    private Users currentUser(
            Authentication authentication) {

        return userRepository
                .findByUserName(
                        authentication.getName())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Logged-in user was not found."));
    }


    // =========================================================
    // CROP + RESIZE IMAGE
    // =========================================================

    private byte[] cropToSquareAndResize(
            byte[] originalBytes,
            int targetSize)
            throws IOException {


        BufferedImage original =
                ImageIO.read(
                        new ByteArrayInputStream(
                                originalBytes));


        if (original == null) {

            throw new IOException(
                    "Unsupported or corrupt image");
        }


        int width =
                original.getWidth();


        int height =
                original.getHeight();


        int cropSize =
                Math.min(
                        width,
                        height);


        int x =
                (width - cropSize) / 2;


        int y =
                (height - cropSize) / 2;


        BufferedImage squareCrop =
                original.getSubimage(
                        x,
                        y,
                        cropSize,
                        cropSize);


        BufferedImage resized =
                new BufferedImage(
                        targetSize,
                        targetSize,
                        BufferedImage.TYPE_INT_RGB);


        Graphics2D g2d =
                resized.createGraphics();


        g2d.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);


        g2d.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);


        g2d.drawImage(
                squareCrop,
                0,
                0,
                targetSize,
                targetSize,
                null);


        g2d.dispose();


        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();


        ImageIO.write(
                resized,
                "jpg",
                outputStream);


        return outputStream.toByteArray();
    }

}