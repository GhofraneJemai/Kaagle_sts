package com.ghofrane.ollama.controllers;


import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RestController;

import com.ghofrane.ollama.agents.AIAgent;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;

@RestController
public class AIAgentController {
	private AIAgent agent;
	
	
    public AIAgentController(AIAgent agent) {
		super();
		this.agent = agent;
	}
    @GetMapping("/chat")
    public String askLLM(String query)
    {
    	return agent.onQuery(query);
    }
    


}
