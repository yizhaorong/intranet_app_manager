# Spring Boot Https 证书

## 创建目录和文件

```shell
mkdir -p CA/{certs,crl,newcerts,private}
touch CA/index.txt
touch CA/certs.db
touch openssl.cnf
echo 00 > CA/serial
```

## 设置配置

> openssl.cnf

```shell
[ req ]
distinguished_name=req_distinguished_name
req_extensions=v3_req

[ req_distinguished_name ]
countryName=Country Name (2 letter code)
countryName_default=CN
stateOrProvinceName=State or Province Name (full name)
stateOrProvinceName_default=ZheJiang
localityName=Locality Name (eg, city)
localityName_default=HangZhou
organizationalUnitName=Organizational Unit Name (eg, section)
organizationalUnitName_default=Domain Control Validated
commonName=Internet Widgits Ltd
commonName_default=192.168.0.*
commonName_max=64

[ v3_req ]
basicConstraints = CA:FALSE
keyUsage = nonRepudiation, digitalSignature, keyEncipherment
subjectAltName = @alt_names

[ alt_names ]
DNS.1 = 192.168.0.110
DNS.2 = 192.168.0.111
# section for the "default_ca" option
[ca]
default_ca=my_ca_default

# default section for "ca" command options
[my_ca_default]
new_certs_dir=./CA/certs
database=./CA/certs.db
default_md = sha256
policy=my_ca_policy
serial        = ./CA/serial
default_days  = 365

# section for DN field validation and order
[my_ca_policy]
commonName             = supplied
countryName            = optional
stateOrProvinceName    = optional
localityName           = optional
organizationName       = optional
organizationalUnitName = optional
emailAddress           = optional
```

**注意**

```shell
[ alt_names ]
DNS.1 = 192.168.0.110
DNS.2 = 192.168.0.111
```

这里配置需要部署的域名或 IP 地址列表。



## 创建 CA 

### 生成ca.key并自签署

```shell
openssl req -new -x509 -days 3650 -keyout ca.key -out ca.crt -config openssl.cnf
```

## 创建服务器证书

### 生成server.key(名字不重要)

```shell
openssl genrsa -out server.key 2048
```

### 生成证书签名请求

```shell
openssl req -new -key server.key -out server.csr -config openssl.cnf
```

Common Name 这个写主要域名就好了(注意：这个域名也要在openssl.cnf的DNS.x里)

### 使用自签署的CA，签署server.scr

```shell
openssl ca -in server.csr -out server.crt -cert ca.crt -keyfile ca.key -extensions v3_req -config openssl.cnf
```

## 创建 Spring Boot 所需证书

### 导出 pckcs12格式

```shell
openssl pkcs12 -export -in server.crt -inkey server.key -out server.pkcs12
```

### 导出 jks 格式

```shell
keytool -importkeystore -srckeystore server.pkcs12 -destkeystore server.jks -srcstoretype pkcs12
```

## Spring Boot 配置

```properties
# 证书
server.port=443
server.ssl.key-store=classpath:server.pkcs12
server.ssl.key-store-password=123456
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=1
```



### SpringBootApplication

```java
@Bean
    public TomcatServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint constraint = new SecurityConstraint();
                constraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                constraint.addCollection(collection);
                context.addConstraint(constraint);
            }
        };
        tomcat.addAdditionalTomcatConnectors(httpConnector());
        return tomcat;
    }

    @Bean
    public Connector httpConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(9090);
        connector.setSecure(false);
        connector.setRedirectPort(8443);
        return connector;
    }
```



