# Chat Application

A Java-based real-time chat application with a Discord-inspired user interface. This project demonstrates the use of WebSocket technology for real-time communication between a server and multiple clients, along with a modern graphical user interface built using Java Swing.

## Features

- **Discord-like Interface:** Modern, dark-themed UI inspired by Discord
- **Server List Panel:** View and manage different servers
- **Channel List Panel:** Navigate through different channels within a server
- **Real-time Messaging:** Instant message delivery using WebSocket technology
- **Multithreaded Server:** Handles multiple client connections concurrently
- **Dark Theme:** Eye-friendly dark color scheme throughout the application

## Prerequisites

- Java Development Kit (JDK) 8 or higher
- Apache Maven

## Building the Application

To compile the source code and create an executable JAR with dependencies, run:

```bash
mvn clean install
```

This will create two JAR files in the `target` directory:
- `chat-application-1.0-SNAPSHOT.jar`: The basic JAR file
- `chat-application-1.0-SNAPSHOT-jar-with-dependencies.jar`: The JAR file with all dependencies included

## Running the Application

### 1. Start the Server

Open a terminal and run:

```bash
java -cp target/chat-application-1.0-SNAPSHOT-jar-with-dependencies.jar chat.ChatServer
```

The server will start and listen for incoming client connections.

### 2. Start the Client

Open a new terminal for each client instance:

```bash
java -cp target/chat-application-1.0-SNAPSHOT-jar-with-dependencies.jar chat.ChatGUI
```

You can start multiple client instances to simulate different users.

## Project Structure

- `src/main/java/chat/`
  - `ChatServer.java`: The WebSocket server implementation
  - `ChatGUI.java`: Main client GUI application
  - `ChatClient.java`: WebSocket client implementation
  - `ChannelListPanel.java`: UI component for displaying channels
  - `ServerListPanel.java`: UI component for displaying servers
  - `ChatMessageRenderer.java`: Custom renderer for chat messages
  - `DiscordLayout.java`: Layout manager for Discord-like interface
  - `Guild.java`: Server/Guild data model

## Building from Source

1. Clone the repository:
```bash
git clone https://github.com/Jeremiah-Jefry/Chat-Application.git
```

2. Navigate to the project directory:
```bash
cd Chat-Application
```

3. Build with Maven:
```bash
mvn clean install
```

## License

This project is open-source and available under the MIT License.