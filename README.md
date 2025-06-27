<h1 align="left">microservices 📈</h1>
<p align="left">A monorepo containing multiple microservices for notification, payment, and order processing.</p>

<p align="left">
  <a href="https://github.com/l4yoos/microservices/commits/main">
    <img src="https://img.shields.io/github/last-commit/l4yoos/{microservices}" alt="Last Commit">
  </a>
  <a href="https://github.com/l4yoos/microservices">
    <img src="https://img.shields.io/github/languages/top/l4yoos/{microservices}" alt="Top Language">
  </a>
  <a href="https://github.com/l4yoos/microservices">
    <img src="https://img.shields.io/github/languages/count/l4yoos/{microservices}" alt="Language Count">
  </a>
</p>

<hr/>

<h2 align="left" id="overview">🚀 Overview</h2>
<p align="left">This monorepo contains three microservices: notification-service, payment-service, and order-service.</p>
<ul align="left">
  <li>notification-service: responsible for notification processing</li>
  <li>payment-service: responsible for payment processing</li>
  <li>order-service: responsible for order processing</li>
</ul>

<hr/>

<h2 align="left" id="built-with">📦 Built With</h2>
<p align="left">The following technologies were used to build these microservices:</p>
<ul align="left">
  <li>Spring Boot</li>
  <li>Java 17</li>
  <li>Lombok</li>
  <li>PostgreSQL</li>
  <li>Testcontainers</li>
  <li>Mockito</li>
  <li>Twilio API</li>
</ul>

<hr/>

<h2 align="left" id="table-of-contents">📚 Table of Contents</h2>
<p align="left">This README is organized into the following sections:</p>
<ul align="left">
  <li><a href="#overview">Overview</a></li>
  <li><a href="#built-with">Built With</a></li>
  <li><a href="#table-of-contents">Table of Contents</a></li>
  <li><a href="#architecture">Architecture</a></li>
  <li><a href="#prerequisites">Prerequisites</a></li>
  <li><a href="#installation">Installation</a></li>
  <li><a href="#usage">Usage</a></li>
  <li><a href="#testing">Testing</a></li>
  <li><a href="#demo">Demo</a></li>
</ul>

<hr/>

<h2 align="left" id="architecture">🏗️ Architecture</h2>
<p align="left">The architecture of this monorepo follows a microservices pattern, with each service responsible for a specific domain.</p>
<p align="left">Each service is built using Spring Boot and follows a layered architecture pattern, with separate layers for controllers, services, and repositories.</p>

<hr/>

<h2 align="left" id="prerequisites">✅ Prerequisites</h2>
<p align="left">To run these microservices, you'll need:</p>
<ul align="left">
  <li>Java 17</li>
  <li>Maven</li>
  <li>PostgreSQL</li>
</ul>

<hr/>

<h2 align="left" id="installation">🛠️ Installation</h2>
<p align="left">To install and run each microservice, follow these steps:</p>
<ul align="left">
  <li>Clone the repository</li>
  <li>Run `mvn clean package` to build the project</li>
  <li>Run `java -jar target/ notification-service-1.0.0.jar` (or similar) to start each service</li>
</ul>

<hr/>

<h2 align="left" id="usage">🚀 Usage</h2>
<p align="left">Each microservice has its own usage instructions:</p>
<ul align="left">
  <li>notification-service: send notifications using the `/notifications` endpoint</li>
  <li>payment-service: process payments using the `/payments` endpoint</li>
  <li>order-service: manage orders using the `/orders` endpoint</li>
</ul>

<hr/>

<h2 align="left" id="testing">🧪 Testing</h2>
<p align="left">Each microservice has its own set of tests:</p>
<ul align="left">
  <li>notification-service: see `NotificationControllerTest` and `NotificationServiceTest`</li>
  <li>payment-service: see `PaymentControllerTest` and `PaymentServiceTest`</li>
  <li>order-service: see `OrderControllerTest` and `OrderServiceTest`</li>
</ul>

<hr/>