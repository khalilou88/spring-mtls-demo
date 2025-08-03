# Spring Boot Mutual TLS (mTLS) Demo Guide

This is a step-by-step guide to creating a Spring Boot Mutual TLS (mTLS) demo. We will create two separate Spring Boot applications:

- **Server**: Requires mTLS for incoming connections.
- **Client**: Authenticates itself using a client certificate.

---

## 1. Generating Certificates

We’ll use `OpenSSL` and `keytool` to generate and manage the certificates.

### 📁 Create a directory for the certificates

```bash
mkdir certs
cd certs
```

---

### 🛡️ Generate CA Key and Certificate

```bash
openssl req -x509 -newkey rsa:4096 -keyout ca.key -out ca.pem -days 365 -nodes -subj "/CN=My-CA"
```

---

### 🔐 Generate Server Certificates

```bash
# Generate server private key and CSR
openssl req -newkey rsa:4096 -keyout server.key -out server.csr -nodes -subj "/CN=localhost"

# Sign the server certificate with the CA
openssl x509 -req -in server.csr -CA ca.pem -CAkey ca.key -CAcreateserial -out server.pem -days 365
```

---

### 🔐 Generate Client Certificates

```bash
# Generate client private key and CSR
openssl req -newkey rsa:4096 -keyout client.key -out client.csr -nodes -subj "/CN=My-Client"

# Sign the client certificate with the CA
openssl x509 -req -in client.csr -CA ca.pem -CAkey ca.key -CAcreateserial -out client.pem -days 365
```

---

### 🔄 Convert Certificates to PKCS12 Format

#### For the Server:

```bash
openssl pkcs12 -export -in server.pem -inkey server.key -name "server-alias" -out server.p12 -passout pass:password
```

#### For the Client:

```bash
openssl pkcs12 -export -in client.pem -inkey client.key -name "client-alias" -out client.p12 -passout pass:password
```

---

## 2. Create Truststores

The truststores contain the CA certificate, allowing each side to trust certificates signed by it.

### 🤝 Server's Truststore (trusts the client)

```bash
keytool -import -trustcacerts -file ca.pem -keystore server-truststore.jks -storepass password -alias ca
```

### 🤝 Client's Truststore (trusts the server)

```bash
keytool -import -trustcacerts -file ca.pem -keystore client-truststore.jks -storepass password -alias ca
```


## 3
# Copy the server certs to the server module
cp certs/server.p12 server/src/main/resources/
cp certs/server-truststore.jks server/src/main/resources/

# Copy the client certs to the client module
cp certs/client.p12 client/src/main/resources/
cp certs/client-truststore.jks client/src/main/resources/


## 4 

./mvnw spring-boot:run -pl server

./mvnw spring-boot:run -pl client