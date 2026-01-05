# Spring AI 101

A beginner-friendly Spring Boot application that demonstrates how to integrate and use OpenAI's chat capabilities with Spring AI framework.
[Medium Blogs](https://mohankumarsagadevan.medium.com/list/spring-ai-101-078cd80f25a7)

## 📋 Table of Contents

- [Overview](#overview)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Usage](#usage)

## 🎯 Overview

This project shows you how to:
- Create a Spring Boot application
- Connect to OpenAI's API using Spring AI
- explore all the chat functionalities provided by Spring AI

**Technologies Used:**
- Java 17
- Spring Boot 3.3.5
- Spring AI 1.0.1
- Gradle (build tool)
- OpenAI API

## ✅ Prerequisites

Before you start, make sure you have:

1. **Java 17 or higher** installed on your computer
2. **Gradle** (comes with the project as a wrapper)
3. **OpenAI API Key** - Get one from [OpenAI](https://platform.openai.com/api-keys)
4. **Git** (optional, for cloning the repository)

## 🚀 Getting Started

### Step 1: Clone or Download the Project

```bash
git clone <repository-url>
cd spring_ai_101
```

### Step 2: Set Up Your OpenAI API Key

Create a file named `.env` or set an environment variable:

**Option A: Using `.env` file**
```properties
OPENAI_API_KEY=your_openai_api_key_here
```

**Option B: Using application.properties**
Create `src/main/resources/application.properties`:
```properties
spring.ai.openai.api-key=your_openai_api_key_here
spring.ai.openai.chat.options.model=gpt-4
```

### Step 3: Run the Application

Using Gradle:

```bash
./gradlew bootRun
```

Or if you're on Windows:
```bash
gradlew.bat bootRun
```

The application will start at `http://localhost:8080`

## 💬 Usage

Once the application is running, you can interact with the chat API:

### Basic Chat Endpoint

**URL:** `GET /ai/chat/simple`
