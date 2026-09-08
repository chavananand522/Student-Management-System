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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itvedant.StudentManagement.model.Users;
import com.itvedant.StudentManagement.reposatory.CourseRepository;
import com.itvedant.StudentManagement.reposatory.EnrollmentRepository;
import com.itvedant.StudentManagement.reposatory.StudentRepositiry;
import com.itvedant.StudentManagement.reposatory.UserRepository;

@Controller
@RequestMapping("/profile")
public class ProfileController {

	private static final int TARGET_SIZE = 400; // final stored image is TARGET_SIZE x TARGET_SIZE

	private final UserRepository userRepository;
	private final StudentRepositiry studentRepository;
	private final CourseRepository courseRepository;
	private final EnrollmentRepository enrollmentRepository;
	private final PasswordEncoder passwordEncoder;

	public ProfileController(UserRepository userRepository, StudentRepositiry studentRepository,
			CourseRepository courseRepository, EnrollmentRepository enrollmentRepository,
			PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.studentRepository = studentRepository;
		this.courseRepository = courseRepository;
		this.enrollmentRepository = enrollmentRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@GetMapping
	public String showProfile(Model model, Authentication authentication) {

		Users user = userRepository.findByUserName(authentication.getName())
				.orElseThrow(() -> new RuntimeException("User Not Found"));

		model.addAttribute("user", user);
		model.addAttribute("totalStudents", studentRepository.count());
		model.addAttribute("totalCourses", courseRepository.count());
		model.addAttribute("totalEnrollments", enrollmentRepository.count());

		return "profile";
	}

	@PostMapping("/update")
	public String updateProfile(@RequestParam("fullName") String fullName,
			@RequestParam("email") String email,
			@RequestParam("phoneNumber") String phoneNumber,
			Authentication authentication,
			RedirectAttributes redirectAttributes) {

		Users user = userRepository.findByUserName(authentication.getName())
				.orElseThrow(() -> new RuntimeException("User Not Found"));

		user.setFullName(fullName);
		user.setEmail(email);
		user.setPhoneNumber(phoneNumber);
		userRepository.save(user);

		redirectAttributes.addFlashAttribute("success", "Profile updated successfully");
		return "redirect:/profile";
	}

	@PostMapping("/change-password")
	public String changePassword(@RequestParam("currentPassword") String currentPassword,
			@RequestParam("newPassword") String newPassword,
			@RequestParam("confirmPassword") String confirmPassword,
			Authentication authentication,
			RedirectAttributes redirectAttributes) {

		Users user = userRepository.findByUserName(authentication.getName())
				.orElseThrow(() -> new RuntimeException("User Not Found"));

		if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
			redirectAttributes.addFlashAttribute("error", "Current password is incorrect");
			return "redirect:/profile";
		}

		if (!newPassword.equals(confirmPassword)) {
			redirectAttributes.addFlashAttribute("error", "New password and confirmation do not match");
			return "redirect:/profile";
		}

		if (newPassword.length() < 6) {
			redirectAttributes.addFlashAttribute("error", "New password must be at least 6 characters");
			return "redirect:/profile";
		}

		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);

		redirectAttributes.addFlashAttribute("success", "Password changed successfully");
		return "redirect:/profile";
	}

	@PostMapping("/image")
	@ResponseBody
	public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile file,
			Authentication authentication) {

		if (file.isEmpty()) {
			return ResponseEntity.badRequest().body("Please select an image to upload");
		}

		String contentType = file.getContentType();
		if (contentType == null || !contentType.startsWith("image/")) {
			return ResponseEntity.badRequest().body("Please upload a valid image file");
		}

		try {
			byte[] squareJpegBytes = cropToSquareAndResize(file.getBytes(), TARGET_SIZE);

			Users user = userRepository.findByUserName(authentication.getName())
					.orElseThrow(() -> new RuntimeException("User Not Found"));

			user.setProfileImage(squareJpegBytes);
			user.setProfileImageContentType(MediaType.IMAGE_JPEG_VALUE);
			userRepository.save(user);

			return ResponseEntity.ok("Profile image updated successfully");

		} catch (IOException e) {
			return ResponseEntity.internalServerError().body("Could not process the image");
		}
	}

	@GetMapping("/image")
	@ResponseBody
	public ResponseEntity<byte[]> getImage(Authentication authentication) {

		Users user = userRepository.findByUserName(authentication.getName())
				.orElseThrow(() -> new RuntimeException("User Not Found"));

		if (user.getProfileImage() == null) {
			return ResponseEntity.notFound().build();
		}

		MediaType mediaType = MediaType.parseMediaType(
				user.getProfileImageContentType() != null
						? user.getProfileImageContentType()
						: MediaType.IMAGE_JPEG_VALUE);

		return ResponseEntity.ok().contentType(mediaType).body(user.getProfileImage());
	}

	/**
	 * Safety net: even though the browser sends an already-square crop,
	 * this re-crops (center square) and resizes to a fixed target size on
	 * the server, so every stored image is guaranteed consistent
	 * regardless of what the client sends.
	 */
	private byte[] cropToSquareAndResize(byte[] originalBytes, int targetSize) throws IOException {

		BufferedImage original = ImageIO.read(new ByteArrayInputStream(originalBytes));

		if (original == null) {
			throw new IOException("Unsupported or corrupt image");
		}

		int width = original.getWidth();
		int height = original.getHeight();
		int cropSize = Math.min(width, height);

		int x = (width - cropSize) / 2;
		int y = (height - cropSize) / 2;

		BufferedImage squareCrop = original.getSubimage(x, y, cropSize, cropSize);

		BufferedImage resized = new BufferedImage(targetSize, targetSize, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = resized.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.drawImage(squareCrop, 0, 0, targetSize, targetSize, null);
		g2d.dispose();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ImageIO.write(resized, "jpg", outputStream);

		return outputStream.toByteArray();
	}
}