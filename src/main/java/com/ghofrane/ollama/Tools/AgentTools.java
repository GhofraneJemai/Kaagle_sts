package com.ghofrane.ollama.Tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class AgentTools {

    @Tool(description = "Get Employee Info (name, salary and seniority)")
    public EmployeeInfo getEmployeeInfo(@ToolParam(description = "the name of the employee") String name) {
        return new EmployeeInfo(name, 9800, 4);
    }
}

record EmployeeInfo(String name, double salary, int seniority) {}
