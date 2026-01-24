package com.mohankumar27.springai.basics;

import com.mohankumar27.springai.advisors.SelfCorrectionAdvisor;
import com.mohankumar27.springai.dto.MovieRecommendation;
import com.mohankumar27.springai.dto.Recipe;
import org.springframework.ai.chat.client.AdvisorParams;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/entity")
public class EntityChatController {

    private final ChatClient chatClient;

    public EntityChatController(ChatClient.Builder builder) {
        this.chatClient = builder.clone()
                .defaultAdvisors(new SimpleLoggerAdvisor()) // Logs requests and responses to LLM in console
                .build();
    }

    @GetMapping("/recipe")
    public Recipe generateRecipe(@RequestParam String ingredients) {
        return this.chatClient.prompt()
                .user(u -> u.text("Create a recipe using these ingredients: {ingredients}")
                        .param("ingredients", ingredients))
                .call()
                .entity(Recipe.class); // <--- The Magic Happens Here
    }

    @GetMapping("/top-recipes")
    public List<Recipe> getTopRecipes() {
        return this.chatClient.prompt()
                .user("Generate 3 unique pasta recipes.")
                .call()
                .entity(new ParameterizedTypeReference<List<Recipe>>() {});
    }

    @GetMapping("/top-movies")
    public List<MovieRecommendation> getTopMovies() {
        return this.chatClient.prompt()
                .user("Recommend 3 must-watch sci-fi movies.")
                .call()
                .entity(new ParameterizedTypeReference<List<MovieRecommendation>>() {});
    }

    @GetMapping("/recipe-advisor")
    public Recipe generateRecipeWithAdvisor(@RequestParam String ingredients) {
        return this.chatClient.prompt()
                .advisors(AdvisorParams.ENABLE_NATIVE_STRUCTURED_OUTPUT)
                .user(u -> u.text("Create a recipe using these ingredients: {ingredients}")
                        .param("ingredients", ingredients))
                .call()
                .entity(Recipe.class);
    }

    @GetMapping("/self-correct-advisor")
    public Recipe generateRecipeWithSelfCorrection(@RequestParam String ingredients) {
        return this.chatClient.prompt()
                .advisors(new SelfCorrectionAdvisor(1))
                .user(u -> u.text("Create a recipe using these ingredients: {ingredients}")
                        .param("ingredients", ingredients))
                .call()
                .entity(Recipe.class);
    }

}
