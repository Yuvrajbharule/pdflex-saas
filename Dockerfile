FROM eclipse-temurin:17-jdk-jammy

# Install Ghostscript
RUN apt-get update && apt-get install -y ghostscript && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy jar
COPY target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run application
ENTRYPOINT ["java","-jar","app.jar"]