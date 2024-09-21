# Gerenciador de Clientes - Projeto Big Data IBMEC

## 📄 Descrição

O **Gerenciador de Clientes** é uma API REST desenvolvida em **Spring Boot** para gerenciar clientes e seus endereços. O projeto permite operações como:

- **Adicionar** um novo cliente.
- **Atualizar** dados de um cliente existente.
- **Remover** um cliente.
- **Adicionar**, **atualizar** e **remover** endereços associados a um cliente.
- **Buscar** clientes e seus respectivos endereços.

Este projeto foi desenvolvido seguindo as melhores práticas de desenvolvimento, garantindo robustez, segurança e facilidade de manutenção.

## 🚀 Funcionalidades

- **CRUD Completo** para Clientes e Endereços.
- **Validações de Dados** rigorosas, incluindo:
    - Formato e unicidade de e-mail e CPF.
    - Validação de idade mínima para clientes.
    - Formato específico para campos de endereço.
- **Relacionamento** entre Cliente e Endereço.
- **Tratamento de Exceções** centralizado com mensagens claras.
- **Cobertura de Testes** com **JUnit** e **Mockito**.
- **Integração Contínua** com **GitHub Actions**.
- **Documentação Interativa** com **Swagger/OpenAPI**.

## 🛠 Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3.3.4**
- **Spring Data JPA**
- **Spring Security**
- **Hibernate**
- **H2 Database** (para desenvolvimento e testes)
- **ModelMapper**
- **JUnit 5**
- **Mockito**
- **Jacoco** (Cobertura de Código)
- **GitHub Actions** (CI/CD)
- **Swagger/OpenAPI** (Documentação da API)

## 📚 Documentação da API

A documentação interativa da API está disponível através do Swagger UI:

[**Acessar Swagger UI**](http://localhost:8080/swagger-ui.html)

**EndPoints Principais:**

### Cliente

- `GET /clientes` - Listar todos os clientes.
- `GET /clientes/{id}` - Buscar um cliente por ID.
- `POST /clientes` - Adicionar um novo cliente.
- `PUT /clientes/{id}` - Atualizar dados de um cliente existente.
- `DELETE /clientes/{id}` - Remover um cliente.

### Endereço

- `GET /clientes/{clienteId}/enderecos` - Listar endereços de um cliente.
- `POST /clientes/{clienteId}/enderecos` - Adicionar um novo endereço para um cliente.
- `PUT /clientes/{clienteId}/enderecos/{enderecoId}` - Atualizar um endereço de um cliente.
- `DELETE /clientes/{clienteId}/enderecos/{enderecoId}` - Remover um endereço de um cliente.

## 🔧 Como Executar o Projeto

### 📝 Pré-requisitos

- **Java 21** instalado. [Download Java](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
- **Maven 3.8.1** ou superior instalado. [Download Maven](https://maven.apache.org/download.cgi)
- **Git** instalado. [Download Git](https://git-scm.com/downloads)

### 📦 Clonando o Repositório

```bash
git clone https://github.com/seu-usuario/seu-repositorio.git
cd seu-repositorio
```

### 🛠 Configuração do Banco de Dados

Por padrão, a aplicação utiliza o **H2 Database** em memória para desenvolvimento e testes. Não há necessidade de configurações adicionais para rodar localmente.

### 🏃 Executando a Aplicação

#### Usando o Maven

```bash
mvn spring-boot:run
```

#### Empacotando a Aplicação

Para gerar o JAR executável:

```bash
mvn clean install
java -jar target/gerenciador-clientes-0.0.1-SNAPSHOT.jar
```

### 🧪 Executando Testes

Para rodar os testes unitários e de integração:

```bash
mvn test
```

Após a execução, os relatórios de cobertura estarão disponíveis em `target/site/jacoco/index.html`.

## 📈 Integração Contínua (CI)

O projeto está configurado para utilizar **GitHub Actions** para integração contínua. A cada **push** ou **pull request** para os branches `master` ou `main`, o workflow executa as seguintes etapas:

1. **Checkout** do código.
2. **Caching** das dependências Maven para otimizar o tempo de build.
3. **Configuração** do JDK 21.
4. **Build** do projeto com Maven.
5. **Execução** dos testes.
6. **Geração** do relatório de cobertura com Jacoco.
7. **Publicação** do relatório de cobertura como artefato.

### 📄 Arquivo de Configuração do GitHub Actions

```yaml
name: CI

on:
  push:
    branches: [ master, main ]
  pull_request:
    branches: [ master, main ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - name: Checar código do repositório
        uses: actions/checkout@v3

      - name: Cache Maven packages
        uses: actions/cache@v3
        with:
          path: ~/.m2/repository
          key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
          restore-keys: |
            ${{ runner.os }}-maven-

      - name: Configurar JDK 21
        uses: actions/setup-java@v3
        with:
          distribution: 'temurin'
          java-version: '21'

      - name: Build com Maven
        run: mvn clean install --batch-mode

      - name: Executar Testes
        run: mvn test --batch-mode

      - name: Gerar Relatório de Cobertura
        run: mvn jacoco:report --batch-mode

      - name: Publicar Relatório de Cobertura
        if: success()
        uses: actions/upload-artifact@v3
        with:
          name: cobertura-de-codigo
          path: target/site/jacoco
```

## 🛡 Segurança

A aplicação utiliza **Spring Security** para proteger os endpoints da API. As configurações de segurança podem ser ajustadas conforme as necessidades do projeto.

### 🔐 Configuração Básica de Segurança

Por padrão, todos os endpoints estão protegidos e requerem autenticação. Para desabilitar a segurança durante o desenvolvimento, você pode ajustar a classe de configuração de segurança ou desabilitar temporariamente.

## 📊 Monitoramento e Saúde da Aplicação

Com o **Spring Boot Actuator**, a aplicação fornece endpoints para monitoramento e verificação da saúde da aplicação.

### 📈 Endpoints do Actuator

- `GET /actuator/health` - Verificar a saúde da aplicação.
- `GET /actuator/info` - Informações gerais sobre a aplicação.
- `GET /actuator/metrics` - Métricas da aplicação.

**Nota:** É recomendado proteger os endpoints do Actuator em ambientes de produção.






