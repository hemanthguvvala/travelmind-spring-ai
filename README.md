# TravelMind-AI ✈️

A **multi-agent travel assistant** built with **Spring AI** on **AWS Bedrock**. An orchestrator agent delegates a traveler's request to specialist agents (flight, hotel, policy), each with its own tools and an LLM, then synthesizes one answer — with conversation memory, RAG over a knowledge base, and production-grade resilience.

> Rebuild of an agentic "TravelMind" use case in Java/Spring AI, designed as a production-shaped system (cost, resilience, error handling, observability) rather than a demo.

---

## What it does
Handles airline flight disruptions end to end. Example:

> **"Flight BA117 was cancelled. What am I owed, what's an alternative flight, and find me a hotel near JFK for 1 night?"**

The orchestrator delegates to the right specialists and returns a single grounded reply: compensation (EU261), alternative flights, and hotels.

---

## Architecture
```
            Client ─► API (POST /chat)
                         │  validation + RFC-7807 errors
                  ┌──────▼───────────────┐
                  │  ORCHESTRATOR (Nova   │  delegation + synthesis
                  │  Lite) + memory       │  Resilience4j: retry · CB · fallback
                  └──┬───────┬───────┬────┘
            askFlight│  askHotel│  askPolicy│   (specialist agents = tools)
            ┌────────▼┐ ┌──────▼──┐ ┌──────▼─────┐
            │ Flight  │ │ Hotel   │ │ Policy     │
            │ Agent   │ │ Agent   │ │ Agent      │
            │ tools   │ │ tools   │ │ RAG advisor│
            └─────────┘ └─────────┘ └─────┬──────┘
                                          │ retrieves from
                                    ┌─────▼───────────┐
                                    │ SimpleVectorStore│  (local ONNX embeddings)
                                    │  loyalty policies │
                                    └───────────────────┘
```

## Features
- **Multi-agent orchestration** — orchestrator + 3 specialist agents (agents-as-tools).
- **Tool calling** — agents call Java methods for live data (flight status, alternatives, hotels).
- **Conversation memory** — remembers context across turns per conversation id.
- **RAG** — policy agent answers loyalty/fare questions grounded in a vector store.
- **Resilience4j** — retry + circuit breaker + graceful fallback around every LLM call.
- **Validation + RFC-7807 error handling** — clean `ProblemDetail` responses.
- **JWT authentication** — stateless Spring Security; protected endpoints require a Bearer token.
- **Observability** — request/latency logging, `/actuator/health`.
- **Prompt-injection guard** + grounding prompts against hallucination.

## Tech stack
Java 21 · Spring Boot 3.4 · **Spring AI 1.0** · AWS Bedrock (Converse API, **Nova Lite**) · local **ONNX/Transformers** embeddings · SimpleVectorStore · Resilience4j · **Spring Security (JWT)** · Maven.

## Running it
**Prerequisites:** Java 21, an AWS account with **Bedrock Nova** access, and AWS credentials in `~/.aws/credentials` (region `us-east-1`).

```bash
mvn spring-boot:run
```
**1. Get a JWT** (the `/chat` endpoint is protected):
```bash
curl.exe -X POST http://localhost:8080/token -H "Content-Type: application/json" ^
  -d "{\"username\":\"hemanth\",\"password\":\"password\"}"
```
**2. Call `/chat` with the token:**
```bash
curl.exe -X POST http://localhost:8080/chat -H "Content-Type: application/json" ^
  -H "Authorization: Bearer <token-from-step-1>" ^
  -d "{\"message\":\"Flight BA117 was cancelled. Compensation, an alternative, and a hotel near JFK for 1 night?\"}"
```
Health check (open, no token): `GET http://localhost:8080/actuator/health`

## Configuration
Secrets are read from **environment variables** (with safe dev defaults) — no real secrets in the repo:

| Env var | Purpose | Dev default |
|---|---|---|
| `JWT_SECRET` | HMAC signing key for JWTs (≥ 32 chars) | a dev placeholder — **override in prod** |
| `DEMO_USERNAME` / `DEMO_PASSWORD` | demo login for `/token` | `hemanth` / `password` |
| AWS credentials | Bedrock access | read from `~/.aws/credentials` (never committed) |

## Key engineering decisions (the interesting part)
- **Model tiering:** the orchestrator needs reasoning to chain tools — Nova *Micro* hallucinated/skipped tools, so it runs on Nova *Lite*. Tested down to the cheapest model that stays reliable.
- **Grounding over hallucination:** strict system prompts ("use only tool data, never invent") + low temperature stopped the model inventing flights/hotels.
- **Embedding-provider swap:** Bedrock Titan embeddings hit a framework schema bug, so I switched to a **local ONNX embedder** — free, offline, and a one-line change thanks to Spring AI's `EmbeddingModel`/`VectorStore` abstractions.
- **Resilience for AI calls:** LLM calls are slow/flaky/throttled, so each is wrapped in retry + circuit breaker + a friendly fallback (degrade, don't 500).

## Project structure
```
ChatController        → REST endpoint + input validation
OrchestratorService   → orchestrator ChatClient + memory + Resilience4j
FlightAgent/HotelAgent/PolicyAgent → specialist agents (ChatClients exposed as @Tool)
FlightTools/HotelTools → @Tool methods (mock data; swap for real APIs)
KnowledgeBaseConfig   → SimpleVectorStore seeded with policy docs (RAG)
SecurityConfig        → Spring Security filter chain + JWT decode/encode
AuthController        → /token endpoint that issues JWTs
GlobalExceptionHandler→ RFC-7807 ProblemDetail responses
```

## Roadmap
- [x] Chat, memory, multi-tool, multi-agent, RAG, resilience, validation/error handling, observability, JWT auth
- [ ] Persistent memory + pgvector (production vector store)
- [ ] Containerize + deploy (ECS/EKS)

---
*Tools use mock data for demonstration; the architecture is the point.*
