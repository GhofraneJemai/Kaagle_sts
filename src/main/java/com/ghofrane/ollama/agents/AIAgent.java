package com.ghofrane.ollama.agents;



import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import com.ghofrane.ollama.Tools.AgentTools;


@Service
public class AIAgent {
	private ChatClient chatClient;

	public AIAgent(ChatClient.Builder chatClient, ChatMemory chatMemory,AgentTools agentTools, VectorStore vectorStore) {
		super();
		this.chatClient = chatClient
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                //.defaultTools(agentTools)
                .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore))
                .build();
	}
	
	public String onQuery(String query) {
		return chatClient.prompt()
                .user(query)
                .call()
                .content();
		
	}

}
