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
- **H2 Database** (modo arquivo para persistência dos dados)
- **ModelMapper**
- **JUnit 5**
- **Mockito**
- **Jacoco** (Cobertura de Código)
- **GitHub Actions** (CI/CD)
- **Swagger/OpenAPI** (Documentação da API)

## 📚 Documentação da API

A documentação interativa da API está disponível através do Swagger UI:

[**Acessar Swagger UI**](http://localhost:8080/swagger-ui.html)

**Endpoints Principais:**

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

Por padrão, a aplicação utiliza o **H2 Database** em modo de arquivo para persistência dos dados. Isso significa que os dados serão salvos em um arquivo local, permitindo que sejam mantidos entre reinicializações da aplicação.

#### Configurações Padrão

As configurações do banco de dados estão definidas no arquivo `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:h2:file:./database
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

spring.profiles.active=dev
```

- **URL do DataSource:** `jdbc:h2:file:./database` indica que o banco de dados será armazenado no arquivo `database` no diretório raiz do projeto.
- **Driver:** `org.h2.Driver` é o driver JDBC para o H2 Database.
- **Usuário e Senha:** Por padrão, o usuário é `sa` e a senha está em branco.
- **DDL-Auto:** `update` permite que o Hibernate atualize o esquema do banco de dados conforme as entidades.
- **Show SQL:** `true` para exibir as consultas SQL nos logs.

#### Verificando e Acessando o Banco de Dados

Você pode verificar os dados persistidos no banco de dados de duas maneiras:

##### 1. Usando o H2 Console

O H2 fornece uma interface web chamada **H2 Console**, que permite acessar e gerenciar o banco de dados.

**Passos para acessar o H2 Console:**

1. **Inicie a aplicação**

   Certifique-se de que a aplicação está em execução. Você pode iniciá-la usando:

   ```bash
   mvn spring-boot:run
   ```

2. **Acesse o H2 Console no navegador**

   Abra o navegador e navegue para:

   ```
   http://localhost:8080/h2-console
   ```

3. **Configure a conexão**

   Na página de login do H2 Console, insira as seguintes informações:

  - **JDBC URL:** `jdbc:h2:file:./database`
  - **User Name:** `sa`
  - **Password:** (deixe em branco)
  - **Driver Class:** `org.h2.Driver` (padrão)

   **Nota:** Certifique-se de que o campo **JDBC URL** corresponde exatamente ao definido em `application.properties`.

4. **Conecte-se ao banco de dados**

   Clique no botão **"Connect"** para estabelecer a conexão.

5. **Explorando o banco de dados**

  - **Visualizar tabelas:** No painel esquerdo, expanda o esquema para ver as tabelas (`CLIENTE`, `ENDERECO`, etc.).
  - **Executar consultas SQL:** Use a área de edição para executar comandos SQL, como:

    ```sql
    SELECT * FROM CLIENTE;
    SELECT * FROM ENDERECO;
    ```

  - **Manipular dados:** Você pode inserir, atualizar ou excluir registros diretamente através de comandos SQL.

**Observações Importantes:**

- **Aplicação em Execução:** O H2 Console requer que a aplicação esteja em execução, pois utiliza o contexto da aplicação para acessar o banco de dados.
- **Segurança:** O H2 Console deve ser usado apenas em ambientes de desenvolvimento. Não é recomendado habilitá-lo em ambientes de produção.

##### 2. Usando um Cliente de Banco de Dados Externo (Opcional)

Ferramentas externas permitem uma experiência mais robusta para gerenciar e visualizar o banco de dados.

**Exemplo com DBeaver:**

1. **Instale o DBeaver**

   Baixe e instale o DBeaver Community Edition em [https://dbeaver.io/download/](https://dbeaver.io/download/).

2. **Crie uma nova conexão**

  - Abra o DBeaver.
  - Vá em **"Database"** > **"New Database Connection"**.
  - Selecione **"H2 Embedded"** e clique em **"Next"**.

3. **Configure a conexão**

  - **Database File:** Clique em **"Browse"** e navegue até o arquivo `database.mv.db` localizado no diretório raiz do seu projeto.
    - Exemplo de caminho:

      ```
      C:\Users\SeuUsuario\Documents\seu-repositorio\database.mv.db
      ```

  - **Username:** `sa`
  - **Password:** (deixe em branco)
  - **Driver Class:** Verifique se o driver H2 está selecionado.

4. **Testar e conectar**

  - Clique em **"Test Connection"** para verificar se a conexão está configurada corretamente.
  - Se o teste for bem-sucedido, clique em **"Finish"**.

5. **Explorar o banco de dados**

  - Navegue pelas tabelas, visualize registros, execute consultas e gerencie os dados conforme necessário.

**Benefícios de Usar um Cliente Externo:**

- **Interface Rica:** Ferramentas como DBeaver oferecem recursos avançados de visualização e manipulação de dados.
- **Gerenciamento Centralizado:** Você pode gerenciar múltiplos bancos de dados a partir de uma única interface.

#### Localização dos Arquivos do Banco de Dados

Os arquivos do banco de dados são armazenados no diretório raiz do projeto:

- **`database.mv.db`**: Arquivo principal que contém os dados do banco de dados.
- **`database.trace.db`**: Arquivo de log de rastreamento (opcional).

**Exemplo de Caminho:**

```
C:\Users\SeuUsuario\Documents\seu-repositorio\database.mv.db
```

#### Considerações de Segurança

- **Protegendo o H2 Console:**

  - Em ambientes de produção, é altamente recomendado desabilitar o H2 Console para evitar potenciais vulnerabilidades.
  - Para desabilitar o H2 Console, adicione ou modifique a seguinte propriedade em `application.properties`:

    ```properties
    spring.h2.console.enabled=false
    ```

- **Controle de Acesso:**

  - Se for necessário manter o H2 Console habilitado, considere implementar autenticação e restrições de acesso.

- **Senhas e Credenciais:**

  - Evite utilizar credenciais padrão em ambientes de produção.
  - Armazene informações sensíveis de forma segura, utilizando variáveis de ambiente ou serviços de gerenciamento de segredos.

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

Por padrão, todos os endpoints estão protegidos e requerem autenticação. Durante o desenvolvimento, pode ser conveniente ajustar as configurações de segurança.

#### Fornecendo Credenciais para Acessar a API

- **Usuário Padrão:** O Spring Security gera um usuário padrão `user` com uma senha aleatória exibida nos logs da aplicação.

  ```plaintext
  Using generated security password: 83053b2f-2f5a-4e96-aecc-cffcc18f5a23
  ```

- **Configurando Credenciais Personalizadas:**

  Você pode definir um usuário e senha fixos no `application.properties`:

  ```properties
  spring.security.user.name=admin
  spring.security.user.password=admin123
  ```

- **Utilizando no Postman:**

  - Vá para a aba **"Authorization"**.
  - Selecione **"Basic Auth"**.
  - Insira o **Username** e **Password** conforme configurado.

#### Desabilitando a Segurança (Opcional)

Para desabilitar a segurança durante o desenvolvimento:

- **Remova a dependência do Spring Security no `pom.xml`:**

  ```xml
  <!-- Comente ou remova esta dependência -->
  <!--
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-security</artifactId>
  </dependency>
  -->
  ```

- **Ou configure uma classe de segurança personalizada para permitir acessos sem autenticação.**

**Nota:** Não é recomendado desabilitar a segurança em ambientes de produção.

## 📊 Monitoramento e Saúde da Aplicação

Com o **Spring Boot Actuator**, a aplicação fornece endpoints para monitoramento e verificação da saúde da aplicação.

### 📈 Endpoints do Actuator

- `GET /actuator/health` - Verificar a saúde da aplicação.
- `GET /actuator/info` - Informações gerais sobre a aplicação.
- `GET /actuator/metrics` - Métricas da aplicação.

**Nota:** Proteja esses endpoints em ambientes de produção para evitar exposições desnecessárias.

## 📥 Testando a API com o Postman

Você pode utilizar o **Postman** para testar os endpoints da API.

### Exemplo de Requisição para Criar um Novo Cliente

- **Endpoint:** `POST /clientes`
- **Headers:**
  - `Content-Type: application/json`
- **Body:**

  ```json
  {
    "nome": "Maria Fernanda",
    "email": "maria.fernanda@example.com",
    "cpf": "123.456.789-10",
    "dataNascimento": "1995-08-15",
    "telefone": "(21) 98765-4321",
    "enderecos": [
      {
        "rua": "Rua das Palmeiras",
        "numero": "150",
        "bairro": "Centro",
        "cidade": "Rio de Janeiro",
        "estado": "RJ",
        "cep": "20010-000"
      }
    ]
  }
  ```

- **Autenticação:**

  Se a segurança estiver habilitada, configure a autenticação:

  - **Type:** Basic Auth
  - **Username:** conforme configurado (`admin` por exemplo)
  - **Password:** conforme configurado (`admin123` por exemplo)

### Outros Endpoints

- **Listar Clientes:**

  - **GET /clientes**

- **Buscar Cliente por ID:**

  - **GET /clientes/{id}**

- **Atualizar Cliente:**

  - **PUT /clientes/{id}**

- **Remover Cliente:**

  - **DELETE /clientes/{id}**

**Nota:** Substitua `{id}` pelo ID real do cliente.