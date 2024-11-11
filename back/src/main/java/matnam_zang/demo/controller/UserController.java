package matnam_zang.demo.controller;

import matnam_zang.demo.dto.RecipeDto;
import matnam_zang.demo.dto.UserRecipeDto;
import matnam_zang.demo.entity.User;
import matnam_zang.demo.entity.Recipe;
import matnam_zang.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    // 회원가입 엔드포인트
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        userService.registerUser(user);
        return ResponseEntity.ok("User registered successfully");
    }

    // 로그인 엔드포인트
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        String token = userService.loginUser(username, password);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

    // 레시피 작성 엔드포인트
    @PostMapping("/create-recipe")
    public ResponseEntity<?> createRecipe(
            @RequestHeader("Authorization") String token,  // JWT 토큰은 Authorization 헤더에서 받음
            @RequestBody UserRecipeDto userRecipeDto) {   // 게시물 작성에 필요한 데이터

        try {
            String bearerToken = token.substring(7);
            // createRecipe 서비스 메서드 호출
            Recipe createdRecipe = userService.createRecipe(bearerToken, userRecipeDto);

            // 성공적인 응답 반환
            return ResponseEntity.ok(createdRecipe);
        } catch (RuntimeException e) {
            // 예외 처리: 예외가 발생하면 400(Bad Request) 상태 코드 반환
            return ResponseEntity.badRequest().body("Error creating recipe: " + e.getMessage());
        }
    }

    @PutMapping("/update-recipe/{recipeId}")
    public ResponseEntity<?> updateRecipe(
            @RequestHeader("Authorization") String token,  // JWT 토큰으로 사용자 인증
            @PathVariable("recipeId") Long recipeId, 
            @RequestBody UserRecipeDto updatedRecipeDto) {

        try {
            String bearerToken = token.substring(7); // "Bearer " 제거
            // 레시피 업데이트
            userService.updateRecipe(bearerToken, recipeId, updatedRecipeDto);

            return ResponseEntity.ok("Recipe updated successfully"); // 수정 성공 메시지
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body("Error updating recipe: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete-recipe/{recipeId}")
    public ResponseEntity<?> deleteRecipe(
            @RequestHeader("Authorization") String token,  // JWT 토큰으로 사용자 인증
            @PathVariable("recipeId") Long recipeId) {

        try {
            String bearerToken = token.substring(7); // "Bearer " 제거
            userService.deleteRecipe(bearerToken, recipeId);
            return ResponseEntity.ok("Recipe deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body("Error deleting recipe: " + e.getMessage());
        }
    }
}
