import com.example.userauthservice.dto.ApiResponse;
import com.example.userauthservice.model.User;
import com.example.userauthservice.security.core.CurrentUser;
import com.example.userauthservice.security.core.UserPrincipal;
import com.example.userauthservice.security.oauth2.service.CustomOAuth2UserService;
import com.example.userauthservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// Create LinkAccountController.java
@RestController
@RequestMapping("/auth/link")
@RequiredArgsConstructor
public class LinkAccountController {

    private final UserService userService;
    private final CustomOAuth2UserService oAuth2UserService;

    @PostMapping("/{provider}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> linkAccount(
            @PathVariable("provider") String providerName,
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody Map<String, Object> providerUserInfo) {

        try {
            User.AuthProvider provider = User.AuthProvider.valueOf(providerName.toUpperCase());
            boolean linked = userService.linkUserAccount(
                    userPrincipal.getId(),
                    provider,
                    providerUserInfo
            );

            if (linked) {
                return ResponseEntity.ok(new ApiResponse(true,
                        "Account successfully linked with " + providerName));
            } else {
                return ResponseEntity.badRequest().body(
                        new ApiResponse(false, "Failed to link account"));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse(false, "Invalid provider: " + providerName));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse(false, e.getMessage()));
        }
    }
}