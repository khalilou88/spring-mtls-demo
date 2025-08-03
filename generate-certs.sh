#!/bin/bash

# Create directories
mkdir -p src/main/resources

# Generate CA private key
openssl genrsa -out ca-key.pem 4096

# Generate CA certificate
openssl req -new -x509 -days 365 -key ca-key.pem -sha256 -out ca.pem -subj "/C=US/ST=CA/L=San Francisco/O=Example CA/CN=Example CA"

# Generate server private key
openssl genrsa -out server-key.pem 4096

# Generate server certificate signing request
openssl req -subj "/C=US/ST=CA/L=San Francisco/O=Example Server/CN=localhost" -new -key server-key.pem -out server.csr

# Generate server certificate signed by CA
openssl x509 -req -days 365 -in server.csr -CA ca.pem -CAkey ca-key.pem -out server-cert.pem -sha256 -CAcreateserial

# Generate client private key
openssl genrsa -out client-key.pem 4096

# Generate client certificate signing request
openssl req -subj "/C=US/ST=CA/L=San Francisco/O=Example Client/CN=client" -new -key client-key.pem -out client.csr

# Generate client certificate signed by CA
openssl x509 -req -days 365 -in client.csr -CA ca.pem -CAkey ca-key.pem -out client-cert.pem -sha256 -CAcreateserial

# Create PKCS12 keystore for server
openssl pkcs12 -export -out server.p12 -inkey server-key.pem -in server-cert.pem -certfile ca.pem -name server-alias -password pass:password

# Create JKS truststore with CA certificate
keytool -import -file ca.pem -alias ca -keystore server-truststore.jks -storepass password -noprompt

# Create JKS truststore with CA certificate for client
keytool -import -file ca.pem -alias ca -keystore client-truststore.jks -storepass password -noprompt

# Create PKCS12 keystore for client (for testing)
openssl pkcs12 -export -out client.p12 -inkey client-key.pem -in client-cert.pem -certfile ca.pem -name client-alias -password pass:password

# Move files to resources directory
mv server.p12 server/src/main/resources/
mv server-truststore.jks server/src/main/resources/
mv client.p12 client/src/main/resources/
mv client-truststore.jks client/src/main/resources/

# Clean up temporary files
rm *.pem *.csr *.srl

echo "Certificates generated successfully!"
echo "Server keystore: src/main/resources/server.p12"
echo "Server truststore: src/main/resources/server-truststore.jks"
echo "Client keystore: src/main/resources/client.p12"