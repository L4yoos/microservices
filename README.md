<h1 align="left">payment-service 📈</h1>
<p align="left">Microservice responsible for payment processing</p>

<p align="left">
  <a href="https://github.com/{user}/{project}/commits/main">
    <img src="https://img.shields.io/github/last-commit/{user}/{project}" alt="Last Commit">
  </a>
  <a href="https://github.com/{user}/{project}">
    <img src="https://img.shields.io/github/languages/top/{user}/{project}" alt="Top Language">
  </a>
  <a href="https://github.com/{user}/{project}">
    <img src="https://img.shields.io/github/languages/count/{user}/{project}" alt="Language Count">
  </a>
</p>

<hr/>

<h2 align="left">🚀 Overview</h2>
<p align="left">payment-service is a microservice responsible for payment processing. It uses Spring Boot, PostgreSQL, and RabbitMQ for event-driven communication.</p>
<ul align="left">
  <li>Spring Boot for building the RESTful API</li>
  <li>PostgreSQL for data storage</li>
  <li>RabbitMQ for event-driven communication</li>
</ul>

<hr/>

<h2 align="left">📦 Built With</h2>
<p align="left">The following tools and frameworks were used to build this project:</p>
<ul align="left">
  <li>Spring Boot 3.2.0</li>
  <li>PostgreSQL 42.7.1</li>
  <li>RabbitMQ</li>
  <li>Lombok 1.18.30</li>
  <li>Testcontainers 1.19.3</li>
</ul>

<hr/>

<h2 align="left">📚 Table of Contents</h2>
<p align="left">This README is organized as follows:</p>
<ul align="left">
  <li>Overview</li>
  <li>Built With</li>
  <li>Table of Contents</li>
  <li>Prerequisites</li>
  <li>Installation</li>
  <li>Usage</li>
  <li>Testing</li>
  <li>Demo</li>
</ul>

<hr/>

<h2 align="left">✅ Prerequisites</h2>
<p align="left">The following prerequisites are required to run this project:</p>
<ul align="left">
  <li>Java 17</li>
  <li>Maven 3.13.0</li>
  <li>PostgreSQL 42.7.1</li>
  <li>RabbitMQ</li>
</ul>

<hr/>

<h2 align="left">🛠️ Installation</h2>
<p align="left">To install this project, follow these steps:</p>
<ul align="left">
  <li>Clone the repository</li>
  <li>Run `mvn clean package` to build the project</li>
  <li>Run `java -jar target/payment-service-1.0.0.jar` to start the application</li>
</ul>

<hr/>

<h2 align="left">🚀 Usage</h2>
<p align="left">This microservice provides a RESTful API for payment processing.</p>
<ul align="left">
  <li>POST /payments to create a new payment</li>
  <li>GET /payments/{id} to retrieve a payment by ID</li>
</ul>

<hr/>

<h2 align="left">🧪 Testing</h2>
<p align="left">This project includes unit tests and integration tests using JUnit, Mockito, and Testcontainers.</p>
<ul align="left">
  <li>RabbitMQEventPublisherTest.java</li>
  <li>PaymentControllerTest.java</li>
  <li>PaymentServiceTest.java</li>
</ul>

<hr/>

<h2 align="left">🎬 Demo</h2>
<p align="left">A demo of this microservice is available upon request.</p>

> 📝 **Note**: Replace {user} and {project} in the badge URLs with your actual GitHub username and repository name.