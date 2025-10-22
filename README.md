# 🏥 ConsulToday API

API RESTful desenvolvida em Java com Spring Boot para o sistema de agendamento médico online "ConsulToday". Esta API oferece funcionalidades de gerenciamento de médicos, pacientes, e agendamento/cancelamento de consultas, servindo como backend para aplicações web (PHP) e mobile (Flutter).

## 🚀 Tecnologias Utilizadas

*   **Linguagem:** Java 21
*   **Framework:** Spring Boot 3.x
*   **Gerenciador de Dependências:** Maven
*   **Banco de Dados:** MySQL (produção), H2 (testes)
*   **ORM:** Spring Data JPA / Hibernate
*   **Segurança:** Spring Security, JWT (JSON Web Tokens)
*   **Validação:** Bean Validation
*   **Documentação:** SpringDoc / Swagger UI

## ✨ Funcionalidades

A API ConsulToday gerencia as seguintes entidades e operações:

### Autenticação
*   **Login:** Autentica usuários (Médicos e Pacientes) e gera um JWT.

### Médicos
*   **Cadastro de Médico:** Registra novos profissionais de saúde.
*   **Listagem de Médicos:** Retorna uma lista paginada de médicos ativos.
*   **Atualização de Dados do Médico:** Permite ao médico atualizar suas próprias informações.
*   **Inativação de Médico:** Realiza a exclusão lógica do cadastro do médico.

### Pacientes
*   **Cadastro de Paciente:** Registra novos pacientes.
*   **Listagem de Pacientes:** Retorna uma lista paginada de pacientes ativos.
*   **Atualização de Dados do Paciente:** Permite ao paciente atualizar suas próprias informações.
*   **Inativação de Paciente:** Realiza a exclusão lógica do cadastro do paciente.

### Agendamentos
*   **Agendamento de Consultas:** Permite a pacientes agendar consultas, com validação de disponibilidade de médicos e horários.
*   **Cancelamento de Consultas:** Permite a médicos e pacientes cancelar agendamentos, com regras de antecedência.
*   **Listagem de Agendamentos:** Lista agendamentos para o usuário logado (médico ou paciente).

## 🔒 Segurança

A API utiliza Spring Security com JSON Web Tokens (JWT) para autenticação e autorização.
*   **Autenticação Stateless:** Sessões não são mantidas no servidor.
*   **Controle de Acesso:** Endpoints protegidos por roles (ROLE_MEDICO, ROLE_PACIENTE) e permissões granulares (`@PreAuthorize`).
*   **Criptografia de Senhas:** Senhas armazenadas criptografadas usando BCrypt.

## ⚙️ Configuração do Ambiente de Desenvolvimento

Para rodar o projeto localmente, siga os passos abaixo:

1.  **Pré-requisitos:**
    *   Java Development Kit (JDK) 21
    *   Apache Maven 3.x
    *   Um ambiente de desenvolvimento (IntelliJ IDEA, VS Code, Eclipse)
    *   (Opcional, mas recomendado) Docker e Docker Compose para o banco de dados MySQL.

2.  **Clonar o Repositório:**
    ```bash
    git clone https://github.com/seu-usuario/consultoday-api.git
    cd consultoday-api
    ```

3.  **Configurar o Banco de Dados:**
    *   A API espera uma conexão com um banco de dados MySQL.
    *   **Usando Docker (Recomendado):**
        Crie um arquivo `docker-compose.yml` na raiz do projeto (este arquivo **NÃO** deve ser versionado se contiver senhas).
        ```yaml
        version: '3.8'
        services:
          db:
            image: mysql:8.0
            container_name: consultoday-mysql
            environment:
              MYSQL_ROOT_PASSWORD: root_password # Mudar para algo seguro em produção
              MYSQL_DATABASE: consultoday_db
              MYSQL_USER: consultoday_user # Mudar
              MYSQL_PASSWORD: consultoday_password # Mudar
            ports:
              - "3306:3306"
            # volumes:
            #   - ./mysql-data:/var/lib/mysql # Opcional: para persistir dados
        ```
        Inicie o contêiner do MySQL:
        ```bash
        docker-compose up -d db
        ```
    *   **Instalação Local:**
        Certifique-se de ter um servidor MySQL rodando localmente na porta 3306 e crie um banco de dados chamado `consultoday_db`.

4.  **Configurar `application.properties` (Variáveis de Ambiente / Profile Local):**
    *   A API usa variáveis de ambiente para a chave JWT e credenciais do banco.
    *   **Opção 1 (Variáveis de Ambiente - Produção/Deploy):**
        Defina as seguintes variáveis antes de executar a aplicação:
        ```bash
        export DB_URL="jdbc:mysql://localhost:3306/consultoday_db"
        export DB_USERNAME="consultoday_user"
        export DB_PASSWORD="consultoday_password"
        export JWT_SECRET="sua_chave_secreta_muito_longa_e_aleatoria" # **CRÍTICO: Mude isto para uma chave forte!**
        ```
    *   **Opção 2 (Profile Local - Desenvolvimento):**
        Crie um arquivo `src/main/resources/application-local.properties` (este arquivo **deve ser adicionado ao `.gitignore`** para não versionar credenciais sensíveis) com o seguinte conteúdo:
        ```properties
        spring.datasource.url=jdbc:mysql://localhost:3306/consultoday_db
        spring.datasource.username=consultoday_user
        spring.datasource.password=consultoday_password
        api.security.token.secret=DEV_SECRET_CONSUL_TODAY_APENAS_PARA_DEV # **Mude este para um valor forte para testes mais realistas**
        ```

5.  **Compilar e Rodar a Aplicação:**
    ```bash
    mvn clean install
    # Para rodar com variáveis de ambiente:
    java -jar target/consultoday-api-0.0.1-SNAPSHOT.jar
    # Para rodar com profile local (se você criou application-local.properties):
    java -jar target/consultoday-api-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
    ```

    A API estará acessível em `http://localhost:8080`.

## 📖 Documentação da API (Swagger UI)

Após iniciar a aplicação, a documentação interativa da API estará disponível em:
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## 🧪 Executar Testes

Para executar os testes automatizados e gerar o relatório de cobertura JaCoCo:
```bash
mvn clean test
```

## 🤝 Contribuições
Contribuições são bem-vindas! Se você tiver sugestões, melhorias ou encontrar bugs, sinta-se à vontade para abrir uma issue ou enviar um pull request.

## 📄 Licença
Este projeto está licenciado sob a Licença MIT.
