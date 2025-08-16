# Project-Alakazam 

An intelligent SQL query generator and optimizer that bridges the gap between natural language and complex database operations. Project-Alakazam allows users to ask questions in plain English and receive optimized, schema-aware SQL in return.

## Project Overview

The goal of this project is to democratize data access for non-technical users while providing powerful optimization tools for developers and DBAs. By leveraging modern AI models and a robust Java backend, this project aims to be a comprehensive solution for database interaction and performance management.

## Core Features (Current & Planned)

-   **Natural Language to SQL:** Translates plain English questions into precise PostgreSQL queries.
-   **Schema-Aware Generation:** The AI is grounded in the reality of your database. It reads your live schema to generate valid queries and will refuse to query tables that don't exist.
-   **Automated Database Migrations:** Uses Flyway to manage the database schema and seed test data, ensuring a consistent and repeatable setup.
-   **Integrated Monorepo:** A single repository containing the Spring Boot backend and Next.js frontend, managed by a unified Gradle build system.
-   **(Planned) Query Performance Predictor:** Estimate query execution time and resource usage before running.
-   **(Planned) Automatic Index Suggestions:** Analyze query history to recommend performance-enhancing database indexes.

## System Architecture

The project is built on a modern, decoupled architecture, with a Next.js frontend served by a core Spring Boot application.

```
┌─────────────────┐      ┌───────────────────────────┐      ┌─────────────────┐
│   Web UI        │◄─────►│     Spring Boot App       │◄─────►│   PostgreSQL    │
│   (Next.js)     │      │ (GraphQL API & NLP Core)  │      │    Database     │
└─────────────────┘      └───────────────────────────┘      └─────────────────┘
                                      │
                                      │
                                      ▼
                             ┌────────────────┐
                             │  Google Gemini │
                             │      AI API    │
                             └────────────────┘
```

## Technology Stack

| Category         | Technology                                                                                                   |
| ---------------- | -------------------------------------------------------------------------------------------------------------- |
| **Backend**      | Java 21, Spring Boot 3, Spring for GraphQL, Spring Data JPA                                                  |
| **Frontend**     | Next.js, React, TypeScript, Apollo Client for GraphQL                                                        |
| **Database**     | PostgreSQL, Flyway (for migrations and data seeding)                                                         |
| **AI / ML**      | Google Gemini API (for Natural Language to SQL conversion)                                                   |
| **Build/DevOps** | Gradle, Docker, `node-gradle` plugin (for integrated frontend/backend builds)                                |

## Getting Started

Follow these instructions to get a local copy up and running for development and testing.

### Prerequisites

You must have the following tools installed on your system:
-   **Java (JDK)**: Version 21 or newer.
-   **Node.js**: Version 20.x or newer.
-   **Docker**: For running a local PostgreSQL instance.
-   **Git**: For cloning the repository.

### Installation & Setup

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/your-username/Project-Alakazam.git
    cd Project-Alakazam
    ```

2.  **Configure Your Secrets:**
    This project uses a template system to keep API keys and passwords out of source control.
    -   Navigate to the resources directory: `cd src/main/resources/`
    -   Copy the template file: `cp application.properties.template application.properties`
    -   Open the new `application.properties` file in a text editor.
    -   **Get your API Key:** Go to [Google AI Studio](https://aistudio.google.com/) to create and copy your Gemini API key.
    -   **Fill in the details:** Paste your API key and update your PostgreSQL password.
        ```properties
        # In application.properties
        spring.datasource.password=your_postgres_password
        app.gemini.api.key=PASTE_YOUR_NEW_GEMINI_API_KEY_HERE
        ```
    This `application.properties` file is already listed in `.gitignore` and will not be committed.

3.  **Start the Database:**
    Make sure Docker is running, then start a PostgreSQL instance. If you don't have one, you can use the following command:
    ```bash
    docker run --name querymaster-db -e POSTGRES_PASSWORD=password -p 5432:5432 -d postgres
    ```
    *This will start a PostgreSQL server with the username `postgres` and password `password`.*

4.  **Build and Run the Application:**
    The unified Gradle build will handle everything: installing npm packages, building the frontend, and starting the Spring Boot server.
    ```bash
    ./gradlew clean bootRun
    ```
    The server will start and remain running. The first build may take a few minutes.

5.  **Access the Application:**
    -   **Frontend UI:** Open your browser and navigate to `http://localhost:8080`
    -   **GraphQL API (for testing):** Navigate to `http://localhost:8080/graphiql`

---

## Project Status (As of August 2025)

### What's Working

-   **Foundation:** Fully integrated monorepo with a Spring Boot backend and Next.js frontend.
-   **Database Migrations:** Flyway successfully creates the 5-table schema and seeds it with over 100 rows of test data on the first run.
-   **Core AI Logic:** The application successfully:
    1.  Reads the live PostgreSQL database schema.
    2.  Constructs a detailed prompt with the schema and the user's question.
    3.  Calls the Google Gemini API to generate a SQL query.
    4.  Returns the generated SQL to the user.
-   **Schema Grounding:** The AI is instructed to only use tables present in the provided schema and will return an error if a query is impossible, preventing hallucinations.
-   **Secrets Management:** API keys and database passwords are kept out of source control using a `.properties.template` and `.gitignore`.

### To-Do / Next Steps

-   **UI/UX:** The frontend is functional but lacks proper styling. The immediate next step is to fix the Tailwind CSS build process.
-   **Implement Advanced Features:** Begin work on the planned features from the project roadmap:
    -   Query Performance Predictor
    -   Automatic Index Suggestions
    -   Query Explanation Engine
    -   Real-time Monitoring Dashboard

## Future Roadmap

-   **Multi-Database Support:** Extend beyond PostgreSQL to support MySQL, Oracle, etc.
-   **Context-Aware Conversations:** Allow for follow-up questions that build on the previous query.
-   **Multi-Language Support:** Accept natural language queries in languages other than English.
-   **Enterprise Features:** Implement role-based access control, comprehensive audit logging, and API management.

## How to Contribute

Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1.  Fork the Project
2.  Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3.  Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4.  Push to the Branch (`git push origin feature/AmazingFeature`)
5.  Open a Pull Request

## License

Distributed under the MIT License. See `LICENSE` for more information.
