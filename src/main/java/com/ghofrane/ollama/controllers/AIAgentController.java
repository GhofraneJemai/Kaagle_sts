package com.ghofrane.ollama.controllers;

import com.ghofrane.ollama.agents.AIAgent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AIAgentController {
    private final AIAgent agent;

    public AIAgentController(AIAgent agent) {
        this.agent = agent;
    }

    @GetMapping("/chat")
    public String askLLM(@RequestParam String query) {
        return agent.onQuery(query);
    }
}
