# Chat program over sockets

## Run the thing

### Java

- Client

    ```zsh
    mvn package
    java -jar server/target/chat-server-<version>.jar
    ```

- Server

    ```zsh
    mvn package
    java -jar client/target/chat-client-<version>.jar
    ```

### Nix

- Server

    ```zsh
    nix run .#server
    ```

- Client

    ```zsh
    nix run .#client
    ```
