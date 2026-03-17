AI Log Scanner & Diagnostic Engine
An intelligent, Spring Boot-based tool that parses 
server log files and utilizes Generative AI (Llama 3.3 via Groq) to provide 
automated root cause analysis and suggested fixes for Java exceptions.
 Key FeaturesAI-Powered Diagnostics: Automatically detects Java exceptions and generates human-readable explanations.
 Batch Processing: Implements a grouping logic to bundle multiple errors into a single AI prompt, effectively bypassing API rate limits (429 errors).
 High Performance: Uses Java Streams and HashMaps for $O(n)$ pattern detection in large log files.RESTful API: Provides structured JSON responses for easy integration with frontend dashboards.
 Tech StackLanguage: Java 21Framework: Spring Boot 3.5.x
 AI Orchestration: Spring AILLM Provider: Groq (Llama 3.3 70b)
 Build Tool: Maven🚀 Getting StartedPrerequisitesJava 21 or higherMaven
 A Groq API Key (Get it from Groq Console)
