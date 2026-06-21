package com.travelmind.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

/**
 * Policy specialist agent: uses a QuestionAnswerAdvisor (RAG) over the vector
 * store to answer loyalty/fare questions grounded in the knowledge base.
 */
@Component
public class PolicyAgent {

    private final ChatClient policyClient;

    public PolicyAgent(ChatClient.Builder builder, VectorStore vectorStore) {
        this.policyClient = builder
                .defaultSystem("""
                        You are the policy team. Answer loyalty-program and fare-rule questions
                        using ONLY the retrieved policy documents. If they don't cover it,
                        say you don't have that policy information. Be concise.
                        """)
                .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore).build())
                .build();
    }

    @Tool(description = "Ask the policy team about loyalty rules, tier status, miles, baggage, or EU261 compensation policy")
    public String askPolicyTeam(@ToolParam(description = "the policy or rules question") String query) {
        return policyClient.prompt().user(query).call().content();
    }
}
