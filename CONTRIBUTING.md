# Contributing to Data-Oriented Programming Holiday API

## 🛠️ Development Setup

### Prerequisites

This project requires **Java 25** for development. Here's how to install it:

#### Option 1: Using SDKMAN (Recommended)

```bash
# Install SDKMAN if not already installed
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# Install Java 25 (Amazon Corretto)
sdk install java 25-amzn
sdk use java 25-amzn

# Verify installation
java --version
```

#### Option 2: Using asdf

```bash
# Install asdf if not already installed
git clone https://github.com/asdf-vm/asdf.git ~/.asdf --branch v0.14.0

# Add to your shell profile (.bashrc, .zshrc, etc.)
echo '. "$HOME/.asdf/asdf.sh"' >> ~/.bashrc
echo '. "$HOME/.asdf/completions/asdf.bash"' >> ~/.bashrc

# Restart shell or source profile
source ~/.bashrc

# Install Java plugin and Java 25
asdf plugin add java
asdf install java corretto-25.0.0.37.1
asdf global java corretto-25.0.0.37.1

# Verify installation
java --version
```

#### Option 3: Manual Installation

1. Download Amazon Corretto 25 from: <https://aws.amazon.com/corretto/>
2. Install following the platform-specific instructions
3. Set `JAVA_HOME` environment variable
4. Add Java to your `PATH`

### Additional Requirements

- **Maven 3.9.11+** (included via Maven Wrapper)
- **Docker & Docker Compose**
- **Make** (optional, for convenience commands)

### Quick Development Start

```bash
# Clone the repository
git clone https://github.com/vagnerclementino/api-holiday.git
cd api-holiday

# Start development
./mvnw spring-boot:run
```

The application uses `spring-boot-docker-compose` to automatically start MongoDB when needed.

## 🎯 Types of Contributions Welcome

We maintain an open mind! There are many ways to contribute:

- **📚 Documentation**: Improve documentation, write tutorials or posts
- **🐛 Bug Reports**: Identify and report problems
- **✨ New Features**: Suggest and implement improvements
- **🧪 Tests**: Add or improve existing tests
- **🎨 DOP Examples**: Create new Data-Oriented Programming examples
- **🔧 Refactoring**: Improve existing code following DOP principles

## 🚫 Contributions NOT Accepted

- Technical support issues (use GitHub Discussions)
- Changes that break Data-Oriented Programming principles
- Malicious code or security vulnerabilities
- Changes that don't follow project quality standards

## 📋 Basic Rules

### Technical Responsibilities

- **✅ Compatibility**: Ensure compatibility with Java 25 and Spring Boot 3.5.6
- **🧪 Tests**: All code must have unit and integration tests
- **📐 DOP Standards**: Follow the 4 principles of Data-Oriented Programming v1.1
- **🎨 Style**: Maintain consistency with Checkstyle and existing formatting
- **📝 Documentation**: Document significant changes

### Behavioral Responsibilities

- Be respectful and constructive in all interactions
- Create issues for significant changes before implementing
- Keep PRs small and focused on one functionality
- Be receptive to feedback and improvement suggestions

## 🛠️ Como Contribuir

### Para Mudanças Pequenas (correções óbvias)

Correções pequenas como erros de digitação, formatação ou comentários podem ser enviadas diretamente via PR:

1. Fork o repositório
2. Faça a correção
3. Envie um pull request

### Para Mudanças Significativas

1. **📝 Crie uma Issue**: Descreva a mudança proposta
2. **🍴 Fork o Repositório**: Crie sua própria cópia
3. **🌿 Crie uma Branch**: `git checkout -b feature/minha-funcionalidade`
4. **💻 Desenvolva**: Implemente sua mudança seguindo os padrões
5. **🧪 Teste**: Execute todos os testes localmente
6. **📋 Qualidade**: Verifique style checks e formatação
7. **📤 Pull Request**: Envie seu PR com descrição detalhada

### Comandos de Desenvolvimento

```bash
# Setup completo do ambiente
make setup

# Executar testes
make test-all

# Verificar qualidade do código
make quality

# Verificar estilo
make style-check

# Corrigir formatação
make format-fix
```

## 🧪 Guia de Testes

O projeto utiliza uma estratégia de testes categorizada com **JUnit 5 tags** para separar testes unitários e de integração.

### Categorias de Testes

#### 🔵 Testes Unitários (`@Tag("unit")`)

Testes rápidos e isolados que não requerem dependências externas:

**Características:**

- ⚡ Execução rápida (< 1 segundo por teste)
- 🔒 Isolados (sem dependências externas)
- 🎯 Focados (testam unidades individuais)
- 🔄 Repetíveis (mesmo resultado sempre)

**Exemplos:**

- Testes de objetos de domínio (Holiday, Location)
- Validação de DTOs
- Testes de mappers com dados mock
- Testes de classes utilitárias

#### 🟢 Testes de Integração (`@Tag("integration")`)

Verificam que diferentes componentes funcionam juntos corretamente:

**Características:**

- 🐳 Usa TestContainers para instâncias reais do MongoDB
- 🌐 Testa contexto completo da aplicação
- 📊 Verifica funcionalidade end-to-end
- ⏱️ Execução mais lenta (vários segundos por teste)

**Exemplos:**

- Testes de carregamento do contexto Spring Boot
- Testes de integração com banco de dados
- Testes de endpoints da API
- Testes de integração da camada de serviço

### Executando Testes

#### Todos os Testes (Padrão)

```bash
# Executar todos os testes (unitários + integração)
./mvnw test

# Ou explicitamente usar o profile all-tests
./mvnw test -Pall-tests
```

#### Apenas Testes Unitários

```bash
# Execução rápida - apenas testes unitários
./mvnw test -Punit-tests
```

#### Apenas Testes de Integração

```bash
# Execução mais lenta - apenas testes de integração
./mvnw test -Pintegration-tests
```

#### Testes Específicos

```bash
# Executar uma classe de teste específica
./mvnw test -Dtest=HolidayOperationsTest

# Executar múltiplas classes de teste
./mvnw test -Dtest=HolidayOperationsTest,CreateHolidayRequestTest
```

### Adicionando Novos Testes

#### Para Testes Unitários

```java
@Tag("unit")
class MyServiceTest {
    
    @Test
    void shouldDoSomething() {
        // Implementação do teste
    }
}
```

#### Para Testes de Integração

```java
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@Tag("integration")
class MyIntegrationTest {
    
    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8");
    
    @Test
    void shouldIntegrateCorrectly() {
        // Implementação do teste de integração
    }
}
```

### Boas Práticas de Testes

#### Testes Unitários

- ✅ Use anotação `@Tag("unit")`
- ✅ Mock dependências externas
- ✅ Teste unidades individuais de funcionalidade
- ✅ Mantenha testes rápidos (< 1 segundo)
- ✅ Use nomes descritivos para testes
- ✅ Siga o padrão AAA (Arrange, Act, Assert)

#### Testes de Integração

- ✅ Use anotação `@Tag("integration")`
- ✅ Use TestContainers para bancos de dados reais
- ✅ Teste interações entre componentes
- ✅ Use `@SpringBootTest` para contexto completo
- ✅ Limpe recursos após os testes
- ✅ Use dados de teste realistas

#### Diretrizes Gerais

- 📝 Escreva testes antes ou junto com o código (TDD/BDD)
- 🎯 Busque alta cobertura de testes (>80%)
- 🔄 Mantenha testes independentes e repetíveis
- 📚 documente cenários de teste complexos
- 🚀 Execute testes frequentemente durante desenvolvimento

## 📮 Testes com Postman

O projeto inclui **coleções Postman abrangentes** para testar todos os endpoints da API e padrões DOP.

### 🚀 Início Rápido com Postman

#### 1. Importar Coleções

1. Abra o Postman
2. Clique em **Import**
3. Selecione `postman/DOP-Holiday-API.postman_collection.json`
4. Selecione `postman/DOP-Holiday-API.postman_environment.json`

#### 2. Configurar Ambiente

1. Selecione **🎯 DOP Holiday API - Master Environment** no dropdown de ambiente
2. Certifique-se que a API está rodando em `http://localhost:8080`
3. Atualize variáveis se necessário (porta diferente, URL, etc.)

#### 3. Executar Testes

- **Testes Individuais**: Clique em qualquer request e pressione **Send**
- **Testes por Categoria**: Clique com botão direito em uma pasta e selecione **Run folder**
- **Suite Completa**: Clique na coleção e selecione **Run collection**

### 📊 Estrutura da Coleção

A coleção está organizada em **5 categorias principais** com **23 testes abrangentes**:

#### 🟢 1. Operações CRUD Básicas (13 requests)

Operações padrão de Create, Read, Update, Delete para feriados

- Criar feriados
- Recuperar feriados por ID
- Atualizar feriados existentes
- Deletar feriados
- Listar todos os feriados

#### 🔵 2. Filtragem e Recuperação Avançada (1 request)

Consultas complexas, filtragem e padrões de recuperação de dados

- Filtrar por país, estado, cidade
- Filtrar por tipo de feriado
- Filtragem por intervalo de datas
- Correspondência de padrões de nome

#### 🟡 3. Validação e Tratamento de Erros (4 requests)

Validação de entrada, cenários de erro e condições de contorno

- Validação de dados inválidos
- Validação de campos obrigatórios
- Validação de datas
- Teste de respostas de erro

#### 🟠 4. Tipos Específicos DOP (4 subcategorias)

Tipos de feriados específicos de Data-Oriented Programming

##### 📅 Feriados Fixos (2 requests)

Feriados de data fixa (Natal, Ano Novo, etc.)

##### 👁️ Feriados Observados (1 request)

Feriados com datas observadas e regras de segunda-feira

##### 🔄 Feriados Móveis (1 request)

Feriados calculados (Páscoa, Ação de Graças, etc.)

##### 🔗 Feriados Móveis Baseados (1 request)

Feriados calculados a partir de outros feriados

#### 🔴 5. Testes de Performance (1 request)

Testes de performance, operações em lote e testes de stress

### 🌍 Variáveis de Ambiente

| Variável | Valor Padrão | Descrição |
|----------|--------------|-----------|
| `baseUrl` | `http://localhost:8080` | URL base da Holiday API |
| `apiVersion` | `v1` | Versão da API |
| `contentType` | `application/json` | Tipo de conteúdo padrão |
| `testCountry` | `BR` | País padrão para testes |
| `testState` | `SP` | Estado padrão para testes |
| `testCity` | `São Paulo` | Cidade padrão para testes |
| `currentYear` | `2024` | Ano atual para testes |
| `timeout` | `5000` | Timeout de request em milissegundos |

### 📈 Ordem de Execução dos Testes

Para melhores resultados, execute os testes nesta ordem:

1. **🟢 Operações CRUD Básicas** - Estabelecer funcionalidade base
2. **🟠 Tipos Específicos DOP** - Testar funcionalidades específicas do domínio
3. **🔵 Filtragem Avançada** - Testar capacidades de consulta
4. **🟡 Validação e Tratamento de Erros** - Testar casos extremos
5. **🔴 Testes de Performance** - Testar performance do sistema

### 🎯 Melhores Práticas para Postman

1. **Sempre use a coleção principal** para novos testes
2. **Execute a suite completa** antes de releases importantes
3. **Atualize variáveis de ambiente** para diferentes ambientes
4. **Adicione novos testes** às categorias apropriadas
5. **Use nomes descritivos** para novos requests
6. **Inclua asserções de teste** para todos os requests
7. **Documente cenários complexos** nas descrições dos requests

## 🐛 Como Reportar Bugs

### ⚠️ Vulnerabilidades de Segurança

**NÃO abra uma issue pública para vulnerabilidades de segurança.** Entre em contato diretamente via email.

### 🐞 Bugs Gerais

Ao reportar um bug, inclua:

1. **Versão do Java**: Qual versão você está usando?
2. **Sistema Operacional**: Windows, macOS, Linux?
3. **O que você fez**: Passos para reproduzir
4. **O que esperava**: Comportamento esperado
5. **O que aconteceu**: Comportamento atual
6. **Logs/Erros**: Inclua mensagens de erro relevantes

## ✨ Como Sugerir Funcionalidades

### Filosofia do Projeto

Este projeto demonstra **Data-Oriented Programming v1.1** em Java, focando em:

- Dados imutáveis e transparentes
- Separação entre dados e operações
- Estados ilegais impossíveis de representar
- Modelagem precisa do domínio

### Processo de Sugestão

1. **🔍 Verifique**: Se a funcionalidade já foi sugerida
2. **📝 Crie Issue**: Use o template de feature request
3. **🎯 Descreva**: Por que é necessária e como deveria funcionar
4. **🏗️ Considere**: Como se alinha com os princípios DOP

## 🔍 Processo de Code Review

### Workflow de Qualidade Obrigatório

Todos os PRs devem passar pelo **workflow de qualidade automatizado**:

- **🏗️ Build Application**: Compilação bem-sucedida
- **🎨 Code Style Check**: Verificação de estilo com Checkstyle
- **🧪 Unit Tests**: Todos os testes unitários passando
- **🔗 Integration Tests**: Testes de integração com MongoDB
- **🚪 Quality Gate**: Agregação de todos os resultados

### Proteção de Branch

A branch `main` está protegida e requer:

- ✅ Todos os status checks passando
- ✅ Branch atualizada antes do merge
- ✅ Aprovação de code review
- ✅ Histórico linear (recomendado)

### Cronograma de Review

- **Reviews iniciais**: Dentro de 2-3 dias úteis
- **Feedback esperado**: Resposta em até 1 semana
- **PRs inativos**: Podem ser fechados após 2 semanas sem atividade

## 🏛️ Convenções do Projeto

### Estilo de Código

- **Java 25**: Use preview features quando apropriado
- **Records**: Para todos os objetos de domínio
- **Pattern Matching**: Para operações com sealed interfaces
- **Checkstyle**: Configuração em `checkstyle.xml`
- **Spotless**: Formatação automática configurada

### Mensagens de Commit

Use o formato:

```
tipo(escopo): descrição breve

Descrição mais detalhada se necessário.

Fixes #123
```

Tipos: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`

### Labels de Issues

- **`bug`**: Problemas confirmados
- **`enhancement`**: Novas funcionalidades
- **`documentation`**: Melhorias na documentação
- **`good first issue`**: Adequado para iniciantes
- **`help wanted`**: Precisa de ajuda da comunidade
- **`dop-example`**: Novos exemplos de DOP

## 🌟 Reconhecimento

Contribuidores são reconhecidos:

- **README.md**: Lista de contribuidores
- **Releases**: Menção em notas de release
- **Issues**: Crédito em issues relacionadas

## 📞 Comunidade

- **GitHub Discussions**: Para discussões gerais
- **Issues**: Para bugs e feature requests
- **Pull Requests**: Para contribuições de código

---

**Lembre-se**: Este é um projeto educacional focado em demonstrar Data-Oriented Programming. Toda contribuição deve alinhar-se com este objetivo pedagógico.
